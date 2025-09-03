package com.tokenanalyzer.controllers;

import com.knuddels.jtokkit.api.EncodingType;
import com.tokenanalyzer.FileProcessor;
import com.tokenanalyzer.TokenEngine;
import com.tokenanalyzer.models.AnalysisResult;
import com.tokenanalyzer.utils.ErrorHandler;
import com.tokenanalyzer.utils.ClaudePricing;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class MainController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    
    @FXML private TextArea inputTextArea;
    @FXML private ComboBox<EncodingType> encodingComboBox;
    @FXML private Button analyzeButton;
    @FXML private Button analyzeBatchButton;
    @FXML private Button clearButton;
    @FXML private TableView<MetricRow> resultsTable;
    @FXML private TableColumn<MetricRow, String> metricColumn;
    @FXML private TableColumn<MetricRow, String> valueColumn;
    @FXML private TableColumn<MetricRow, String> descriptionColumn;
    @FXML private Label statusLabel;
    
    // Batch processing components
    @FXML private TabPane resultsTabPane;
    @FXML private Tab singleResultTab;
    @FXML private Tab batchResultTab;
    @FXML private TableView<BatchResultRow> batchResultsTable;
    @FXML private TableColumn<BatchResultRow, String> fileNameColumn;
    @FXML private TableColumn<BatchResultRow, String> fileTokensColumn;
    @FXML private TableColumn<BatchResultRow, String> fileUniqueTokensColumn;
    @FXML private TableColumn<BatchResultRow, String> fileReadabilityColumn;
    @FXML private TableColumn<BatchResultRow, String> fileComplexityColumn;
    @FXML private TableColumn<BatchResultRow, String> fileStatusColumn;
    @FXML private ProgressBar batchProgressBar;
    @FXML private Label progressLabel;
    
    // Batch summary components
    @FXML private VBox batchSummarySection;
    @FXML private Label summaryFilesProcessed;
    @FXML private Label summaryTotalTokens;
    @FXML private Label summaryAvgTokens;
    @FXML private Label summaryAvgReadability;
    @FXML private Label summarySuccessRate;
    
    private final TokenEngine tokenEngine = new TokenEngine();
    private final FileProcessor fileProcessor = new FileProcessor();
    private List<File> selectedFiles = null;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEncodingComboBox();
        setupResultsTable();
        setupBatchResultsTable();
        setupEventHandlers();
        
        logger.info("MainController initialized with multi-file processing support");
    }
    
    private void setupEncodingComboBox() {
        // Focus on Claude-compatible encodings
        encodingComboBox.setItems(FXCollections.observableArrayList(
            EncodingType.CL100K_BASE  // Used by Claude 4, Sonnet, Haiku, GPT-4
            // Note: All Claude models use CL100K_BASE encoding
            // Keeping other encodings for compatibility with legacy OpenAI models
            // EncodingType.P50K_BASE,   // Legacy GPT-3.5
            // EncodingType.R50K_BASE,   // Legacy GPT-3
            // EncodingType.P50K_EDIT    // Legacy edit models
        ));
        encodingComboBox.setValue(EncodingType.CL100K_BASE);
        // Disable combo box since Claude models only use CL100K_BASE
        encodingComboBox.setDisable(true);
    }
    
    private void setupResultsTable() {
        metricColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().metric()));
        valueColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().value()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().description()));
    }
    
    private void setupBatchResultsTable() {
        fileNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().fileName()));
        fileTokensColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().tokens()));
        fileUniqueTokensColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().uniqueTokens()));
        fileReadabilityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().readability()));
        fileComplexityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().complexity()));
        fileStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status()));
    }
    
    private void setupEventHandlers() {
        inputTextArea.textProperty().addListener((obs, oldText, newText) -> {
            analyzeButton.setDisable(newText == null || newText.trim().isEmpty());
        });
    }
    
    @FXML
    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Supported", "*.txt", "*.pdf", "*.docx", "*.html", "*.htm", "*.java", "*.py", "*.js", "*.md", "*.css", "*.xml", "*.json", "*.zip"),
            new FileChooser.ExtensionFilter("Text files", "*.txt", "*.md"),
            new FileChooser.ExtensionFilter("PDF files", "*.pdf"),
            new FileChooser.ExtensionFilter("Word documents", "*.docx"),
            new FileChooser.ExtensionFilter("HTML files", "*.html", "*.htm"),
            new FileChooser.ExtensionFilter("Code files", "*.java", "*.py", "*.js", "*.css", "*.xml", "*.json"),
            new FileChooser.ExtensionFilter("Archives", "*.zip"),
            new FileChooser.ExtensionFilter("All files", "*.*")
        );
        
        Stage stage = (Stage) inputTextArea.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            loadFile(selectedFile);
        }
    }
    
    private void loadFile(File file) {
        statusLabel.setText("Loading file...");
        analyzeButton.setDisable(true);
        
        fileProcessor.processFile(file.toPath())
            .thenAccept(content -> Platform.runLater(() -> {
                inputTextArea.setText(content);
                statusLabel.setText("File loaded: " + file.getName() + " (" + content.length() + " characters)");
                analyzeButton.setDisable(false);
                // Reset batch mode
                selectedFiles = null;
                analyzeBatchButton.setVisible(false);
                batchResultTab.setDisable(true);
                resultsTabPane.getSelectionModel().select(singleResultTab);
            }))
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    String userMessage = ErrorHandler.getUserFriendlyMessage(throwable);
                    ErrorHandler.logError("file loading", throwable, "file", file.getName());
                    
                    showError("File Load Error", userMessage);
                    statusLabel.setText("Error loading file");
                    analyzeButton.setDisable(false);
                });
                return null;
            });
    }
    
    @FXML
    private void handleAnalyzeBatch() {
        if (selectedFiles == null || selectedFiles.isEmpty()) {
            return;
        }
        
        EncodingType encoding = encodingComboBox.getValue();
        statusLabel.setText("Analyzing batch...");
        analyzeBatchButton.setDisable(true);
        
        // Clear previous results
        batchResultsTable.getItems().clear();
        
        // Show progress indicators
        batchProgressBar.setVisible(true);
        progressLabel.setVisible(true);
        batchProgressBar.setProgress(0);
        
        AtomicInteger processed = new AtomicInteger(0);
        int totalFiles = selectedFiles.size();
        
        // Process files asynchronously
        CompletableFuture<Void> batchFuture = CompletableFuture.runAsync(() -> {
            for (File file : selectedFiles) {
                try {
                    // Process file
                    String content = fileProcessor.processFile(file.toPath()).join();
                    AnalysisResult result = tokenEngine.analyzeText(content, encoding).join();
                    
                    // Update UI on JavaFX thread
                    Platform.runLater(() -> {
                        BatchResultRow row = new BatchResultRow(
                            file.getName(),
                            String.valueOf(result.totalTokens()),
                            String.valueOf(result.uniqueTokens()),
                            String.format("%.1f", result.fleschScore()),
                            result.getComplexityLevel(),
                            "Completed"
                        );
                        batchResultsTable.getItems().add(row);
                        
                        int currentProgress = processed.incrementAndGet();
                        double progress = (double) currentProgress / totalFiles;
                        batchProgressBar.setProgress(progress);
                        progressLabel.setText(String.format("Processing %d/%d files...", currentProgress, totalFiles));
                    });
                    
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        BatchResultRow errorRow = new BatchResultRow(
                            file.getName(),
                            "N/A",
                            "N/A",
                            "N/A",
                            "N/A",
                            "Error: " + e.getMessage()
                        );
                        batchResultsTable.getItems().add(errorRow);
                        
                        int currentProgress = processed.incrementAndGet();
                        double progress = (double) currentProgress / totalFiles;
                        batchProgressBar.setProgress(progress);
                        progressLabel.setText(String.format("Processing %d/%d files...", currentProgress, totalFiles));
                    });
                    
                    logger.error("Error processing file {}: {}", file.getName(), e.getMessage());
                }
            }
        });
        
        batchFuture.thenRun(() -> Platform.runLater(() -> {
            // Calculate and display batch summary
            calculateAndDisplayBatchSummary();
            
            statusLabel.setText("Batch analysis complete - " + totalFiles + " files processed");
            analyzeBatchButton.setDisable(false);
            batchProgressBar.setVisible(false);
            progressLabel.setVisible(false);
            logger.info("Batch analysis completed for {} files", totalFiles);
        })).exceptionally(throwable -> {
            Platform.runLater(() -> {
                String userMessage = ErrorHandler.getUserFriendlyMessage(throwable);
                ErrorHandler.logError("batch analysis", throwable, "fileCount", totalFiles);
                
                showError("Batch Analysis Error", userMessage);
                statusLabel.setText("Batch analysis failed");
                analyzeBatchButton.setDisable(false);
                batchProgressBar.setVisible(false);
                progressLabel.setVisible(false);
            });
            return null;
        });
    }
    
    @FXML
    private void handleOpenMultipleFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Multiple Files");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Supported", "*.txt", "*.pdf", "*.docx", "*.html", "*.htm", "*.java", "*.py", "*.js", "*.md", "*.css", "*.xml", "*.json", "*.zip"),
            new FileChooser.ExtensionFilter("Text files", "*.txt", "*.md"),
            new FileChooser.ExtensionFilter("PDF files", "*.pdf"),
            new FileChooser.ExtensionFilter("Word documents", "*.docx"),
            new FileChooser.ExtensionFilter("HTML files", "*.html", "*.htm"),
            new FileChooser.ExtensionFilter("Code files", "*.java", "*.py", "*.js", "*.css", "*.xml", "*.json"),
            new FileChooser.ExtensionFilter("Archives", "*.zip"),
            new FileChooser.ExtensionFilter("All files", "*.*")
        );
        
        Stage stage = (Stage) inputTextArea.getScene().getWindow();
        selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            inputTextArea.setText("Selected " + selectedFiles.size() + " files for batch processing:\n" +
                selectedFiles.stream()
                    .map(File::getName)
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse(""));
            
            analyzeBatchButton.setVisible(true);
            analyzeBatchButton.setDisable(false);
            analyzeButton.setDisable(true);
            
            batchResultTab.setDisable(false);
            resultsTabPane.getSelectionModel().select(batchResultTab);
            
            statusLabel.setText("Ready to analyze " + selectedFiles.size() + " files");
            logger.info("Selected {} files for batch processing", selectedFiles.size());
        }
    }
    
    @FXML
    private void handleAnalyze() {
        String text = inputTextArea.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        
        EncodingType encoding = encodingComboBox.getValue();
        statusLabel.setText("Analyzing text...");
        analyzeButton.setDisable(true);
        
        tokenEngine.analyzeText(text, encoding)
            .thenAccept(result -> Platform.runLater(() -> {
                displayResults(result);
                statusLabel.setText("Analysis complete (" + result.getFormattedProcessingTime() + ")");
                analyzeButton.setDisable(false);
            }))
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    String userMessage = ErrorHandler.getUserFriendlyMessage(throwable);
                    ErrorHandler.logError("text analysis", throwable, 
                        "encoding", encoding, "textLength", text.length());
                    
                    showError("Analysis Error", userMessage);
                    statusLabel.setText("Analysis failed");
                    analyzeButton.setDisable(false);
                });
                return null;
            });
    }
    
    private void displayResults(AnalysisResult result) {
        ObservableList<MetricRow> data = FXCollections.observableArrayList(
            // Basic metrics
            new MetricRow("Token Count", String.valueOf(result.totalTokens()), "Total number of tokens (smallest units of text) identified by the tokenizer"),
            new MetricRow("Unique Tokens", String.valueOf(result.uniqueTokens()), "Number of distinct tokens, indicating vocabulary diversity"),
            new MetricRow("Character Count", String.valueOf(result.fileSize()), "Total number of characters including spaces and punctuation"),
            new MetricRow("Word Count", calculateWordCount(result), "Estimated number of words based on token analysis"),
            
            // Token metrics
            new MetricRow("Token/Word Ratio", String.format("%.3f", result.tokenWordRatio()), "Average number of tokens per word - higher values indicate more complex tokenization"),
            new MetricRow("Avg Token Length", String.format("%.2f", result.avgTokenLength()), "Average character length per token - indicates granularity of tokenization"),
            new MetricRow("Tokens per Character", String.format("%.4f", result.getTokensPerCharacter()), "Token density - how many tokens per character of text"),
            new MetricRow("Uniqueness Ratio", String.format("%.3f", result.getUniquenessRatio()), "Ratio of unique tokens to total tokens - measures vocabulary repetition"),
            
            // Content analysis
            new MetricRow("Content Density", String.format("%.3f", result.contentDensity()), "Ratio of meaningful content to total text - excludes whitespace and common words"),
            new MetricRow("Lexical Diversity", String.format("%.3f", result.lexicalDiversity()), "Measure of vocabulary richness - higher values indicate more varied word choice"),
            new MetricRow("Whitespace Ratio", String.format("%.3f", result.whitespaceRatio()), "Proportion of text that consists of spaces, tabs, and line breaks"),
            
            // Readability
            new MetricRow("Flesch Score", String.format("%.1f", result.fleschScore()), "Reading ease score (0-100) - higher scores indicate easier readability"),
            new MetricRow("Reading Level", result.getReadingLevel(), "Educational level required to understand the text based on complexity"),
            new MetricRow("Complexity Score", String.format("%.3f", result.complexityScore()), "Overall text complexity based on sentence structure and vocabulary"),
            new MetricRow("Complexity Level", result.getComplexityLevel(), "Categorized complexity level from simple to very complex"),
            new MetricRow("Avg Sentence Length", String.format("%.1f", result.avgSentenceLength()), "Average number of words per sentence - longer sentences increase complexity"),
            
            // Processing info
            new MetricRow("Model/Encoding", result.model(), "Tokenization model used for analysis (Claude Sonnet 4, etc.)"),
            new MetricRow("Processing Time", result.getFormattedProcessingTime(), "Time taken to complete the text analysis"),
            
            // Claude Pricing Information
            new MetricRow("Est. Input Cost", ClaudePricing.formatCost(ClaudePricing.calculateInputCost(result.model(), result.totalTokens())), 
                "Estimated cost for input tokens based on Claude pricing"),
            new MetricRow("Model Description", ClaudePricing.getModelDescription(result.model()), 
                "Claude model capabilities and use case")
        );
        
        resultsTable.setItems(data);
        
        logger.info("Analysis results displayed with Claude pricing: {} tokens ≈ {}", 
            result.totalTokens(), 
            ClaudePricing.formatCost(ClaudePricing.calculateInputCost(result.model(), result.totalTokens())));
    }
    
    private String calculateWordCount(AnalysisResult result) {
        // Estimate word count from token ratio
        if (result.tokenWordRatio() > 0) {
            int estimatedWords = (int) (result.totalTokens() / result.tokenWordRatio());
            return String.valueOf(estimatedWords);
        }
        return "N/A";
    }
    
    @FXML
    private void handleClear() {
        inputTextArea.clear();
        resultsTable.getItems().clear();
        batchResultsTable.getItems().clear();
        statusLabel.setText("Ready");
        analyzeButton.setDisable(true);
        
        // Reset batch mode
        selectedFiles = null;
        analyzeBatchButton.setVisible(false);
        batchResultTab.setDisable(true);
        resultsTabPane.getSelectionModel().select(singleResultTab);
        
        // Hide progress indicators
        batchProgressBar.setVisible(false);
        progressLabel.setVisible(false);
        
        // Hide batch summary
        batchSummarySection.setVisible(false);
    }
    
    private void calculateAndDisplayBatchSummary() {
        if (batchResultsTable.getItems().isEmpty()) {
            return;
        }
        
        var items = batchResultsTable.getItems();
        int totalFiles = items.size();
        int successfulFiles = 0;
        long totalTokens = 0;
        double totalReadability = 0;
        int readabilityCount = 0;
        
        for (var item : items) {
            if ("Completed".equals(item.status())) {
                successfulFiles++;
                
                // Parse tokens (handle "N/A" case)
                try {
                    totalTokens += Long.parseLong(item.tokens());
                } catch (NumberFormatException e) {
                    // Skip if not a number
                }
                
                // Parse readability (handle "N/A" case)
                try {
                    double readability = Double.parseDouble(item.readability());
                    totalReadability += readability;
                    readabilityCount++;
                } catch (NumberFormatException e) {
                    // Skip if not a number
                }
            }
        }
        
        // Calculate averages
        double avgTokens = successfulFiles > 0 ? (double) totalTokens / successfulFiles : 0;
        double avgReadability = readabilityCount > 0 ? totalReadability / readabilityCount : 0;
        double successRate = totalFiles > 0 ? (double) successfulFiles / totalFiles * 100 : 0;
        
        // Update summary labels
        summaryFilesProcessed.setText(String.valueOf(totalFiles));
        summaryTotalTokens.setText(String.format("%,d", totalTokens));
        summaryAvgTokens.setText(String.format("%.0f", avgTokens));
        summaryAvgReadability.setText(String.format("%.1f", avgReadability));
        summarySuccessRate.setText(String.format("%.1f%%", successRate));
        
        // Show summary section
        batchSummarySection.setVisible(true);
        
        logger.info("Batch summary calculated: {} files, {} tokens total, {:.1f}% success rate", 
            totalFiles, totalTokens, successRate);
    }
    
    @FXML
    private void handleExit() {
        Platform.exit();
    }
    
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Token Analyzer");
        alert.setHeaderText("Token Analyzer v1.0.0");
        alert.setContentText(
            "A comprehensive text analysis tool optimized for Claude AI models.\n\n" +
            "Features:\n" +
            "• Multi-file batch processing with progress tracking\n" +
            "• Multi-format file support (TXT, PDF, DOCX, HTML, ZIP)\n" +
            "• Claude model tokenization and cost estimation\n" +
            "• Advanced text metrics and readability analysis\n" +
            "• Batch summary statistics\n" +
            "• Comprehensive error handling\n\n" +
            "Built with Java 21 and JavaFX for Claude AI workflows."
        );
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Make the dialog resizable for long error messages
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(450, 200);
        
        alert.showAndWait();
    }
    
    public record MetricRow(String metric, String value, String description) {}
    
    public record BatchResultRow(String fileName, String tokens, String uniqueTokens, 
                                String readability, String complexity, String status) {}
}