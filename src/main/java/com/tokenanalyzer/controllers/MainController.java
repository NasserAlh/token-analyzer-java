package com.tokenanalyzer.controllers;

import com.knuddels.jtokkit.api.EncodingType;
import com.tokenanalyzer.FileProcessor;
import com.tokenanalyzer.TokenEngine;
import com.tokenanalyzer.models.AnalysisResult;
import com.tokenanalyzer.utils.ErrorHandler;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    
    @FXML private TextArea inputTextArea;
    @FXML private ComboBox<EncodingType> encodingComboBox;
    @FXML private Button analyzeButton;
    @FXML private Button clearButton;
    @FXML private TableView<MetricRow> resultsTable;
    @FXML private TableColumn<MetricRow, String> metricColumn;
    @FXML private TableColumn<MetricRow, String> valueColumn;
    @FXML private Label statusLabel;
    
    private final TokenEngine tokenEngine = new TokenEngine();
    private final FileProcessor fileProcessor = new FileProcessor();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEncodingComboBox();
        setupResultsTable();
        setupEventHandlers();
        
        logger.info("MainController initialized with comprehensive error handling");
    }
    
    private void setupEncodingComboBox() {
        encodingComboBox.setItems(FXCollections.observableArrayList(
            EncodingType.CL100K_BASE,
            EncodingType.P50K_BASE,
            EncodingType.R50K_BASE,
            EncodingType.P50K_EDIT
        ));
        encodingComboBox.setValue(EncodingType.CL100K_BASE);
    }
    
    private void setupResultsTable() {
        metricColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().metric()));
        valueColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().value()));
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
            new MetricRow("Token Count", String.valueOf(result.totalTokens())),
            new MetricRow("Unique Tokens", String.valueOf(result.uniqueTokens())),
            new MetricRow("Character Count", String.valueOf(result.fileSize())),
            new MetricRow("Word Count", calculateWordCount(result)),
            
            // Token metrics
            new MetricRow("Token/Word Ratio", String.format("%.3f", result.tokenWordRatio())),
            new MetricRow("Avg Token Length", String.format("%.2f", result.avgTokenLength())),
            new MetricRow("Tokens per Character", String.format("%.4f", result.getTokensPerCharacter())),
            new MetricRow("Uniqueness Ratio", String.format("%.3f", result.getUniquenessRatio())),
            
            // Content analysis
            new MetricRow("Content Density", String.format("%.3f", result.contentDensity())),
            new MetricRow("Lexical Diversity", String.format("%.3f", result.lexicalDiversity())),
            new MetricRow("Whitespace Ratio", String.format("%.3f", result.whitespaceRatio())),
            
            // Readability
            new MetricRow("Flesch Score", String.format("%.1f", result.fleschScore())),
            new MetricRow("Reading Level", result.getReadingLevel()),
            new MetricRow("Complexity Score", String.format("%.3f", result.complexityScore())),
            new MetricRow("Complexity Level", result.getComplexityLevel()),
            new MetricRow("Avg Sentence Length", String.format("%.1f", result.avgSentenceLength())),
            
            // Processing info
            new MetricRow("Model/Encoding", result.model()),
            new MetricRow("Processing Time", result.getFormattedProcessingTime())
        );
        
        resultsTable.setItems(data);
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
        statusLabel.setText("Ready");
        analyzeButton.setDisable(true);
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
            "A comprehensive text analysis tool with token counting and metrics.\n\n" +
            "Features:\n" +
            "• Multi-format file support (TXT, PDF, DOCX, HTML, ZIP)\n" +
            "• Multiple tokenization models (GPT-3.5, GPT-4, etc.)\n" +
            "• Advanced text metrics and readability analysis\n" +
            "• Comprehensive error handling\n\n" +
            "Built with Java 21 and JavaFX."
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
    
    public record MetricRow(String metric, String value) {}
}