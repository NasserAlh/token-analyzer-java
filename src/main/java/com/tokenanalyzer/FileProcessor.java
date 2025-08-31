package com.tokenanalyzer;

import com.tokenanalyzer.models.FileMetadata;
import com.tokenanalyzer.models.FormatMetadata;
import com.tokenanalyzer.models.ProcessedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.List;
import java.util.ArrayList;

public class FileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);
    private static final int MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB limit
    
    public CompletableFuture<String> processFile(Path filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File file = filePath.toFile();
                validateFile(file);
                
                String content = extractText(file);
                logger.info("Successfully processed file: {} ({} characters)", 
                    filePath.getFileName(), content.length());
                return content;
                
            } catch (Exception e) {
                logger.error("Error processing file: {}", filePath, e);
                throw new RuntimeException("File processing failed: " + filePath, e);
            }
        });
    }
    
    public String extractText(File file) throws IOException {
        validateFile(file);
        
        String fileName = file.getName().toLowerCase();
        String extension = getFileExtension(fileName);
        
        return switch (extension) {
            case "txt", "md" -> extractPlainText(file);
            case "html", "htm" -> extractHtmlText(file);
            case "pdf" -> extractPdfText(file);
            case "docx" -> extractDocxText(file);
            case "zip" -> extractZipText(file);
            case "java", "py", "js", "css", "xml", "json" -> extractPlainText(file); // Code files
            default -> throw new UnsupportedOperationException(
                "Unsupported file type: " + extension);
        };
    }
    
    public FileMetadata getMetadata(File file) throws IOException {
        String extension = getFileExtension(file.getName());
        long size = file.length();
        String mimeType = Files.probeContentType(file.toPath());
        
        // Extract format-specific metadata
        var specificMetadata = switch (extension) {
            case "pdf" -> getPdfMetadata(file);
            case "docx" -> getDocxMetadata(file);
            case "html", "htm" -> getHtmlMetadata(file);
            default -> new FormatMetadata(null, null, 0);
        };
        
        return new FileMetadata(
            file.getName(),
            extension,
            size,
            mimeType,
            file.lastModified(),
            specificMetadata.author(),
            specificMetadata.title(),
            specificMetadata.pageCount()
        );
    }
    
    private void validateFile(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }
        
        if (!file.canRead()) {
            throw new IOException("Cannot read file: " + file.getAbsolutePath());
        }
        
        if (file.length() > MAX_FILE_SIZE) {
            throw new IOException("File too large: " + file.length() + 
                " bytes (max: " + MAX_FILE_SIZE + ")");
        }
    }
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) return "";
        return fileName.substring(lastDot + 1).toLowerCase();
    }
    
    private String extractPlainText(File file) throws IOException {
        logger.debug("Processing plain text file: {}", file.getName());
        return Files.readString(file.toPath());
    }
    
    private String extractHtmlText(File file) throws IOException {
        logger.debug("Processing HTML file: {}", file.getName());
        
        Document doc = Jsoup.parse(file, "UTF-8");
        
        // Remove script and style elements
        doc.select("script, style, noscript").remove();
        
        // Extract text with some structure preservation
        String text = doc.body().text();
        
        // Also extract title and meta description if available
        String title = doc.title();
        String description = doc.select("meta[name=description]").attr("content");
        
        StringBuilder result = new StringBuilder();
        if (!title.isEmpty()) {
            result.append("Title: ").append(title).append("\n\n");
        }
        if (!description.isEmpty()) {
            result.append("Description: ").append(description).append("\n\n");
        }
        result.append(text);
        
        return result.toString();
    }
    
    private String extractPdfText(File file) throws IOException {
        logger.debug("Processing PDF file: {}", file.getName());
        
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setStartPage(1);
            stripper.setEndPage(document.getNumberOfPages());
            
            return stripper.getText(document);
        }
    }
    
    private String extractDocxText(File file) throws IOException {
        logger.debug("Processing DOCX file: {}", file.getName());
        
        StringBuilder text = new StringBuilder();
        
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            // Extract paragraphs
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String paragraphText = paragraph.getText();
                if (paragraphText != null && !paragraphText.trim().isEmpty()) {
                    text.append(paragraphText).append("\n\n");
                }
            }
            
            // Extract tables if needed
            document.getTables().forEach(table -> {
                table.getRows().forEach(row -> {
                    row.getTableCells().forEach(cell -> {
                        String cellText = cell.getText();
                        if (cellText != null && !cellText.trim().isEmpty()) {
                            text.append(cellText).append("\t");
                        }
                    });
                    text.append("\n");
                });
                text.append("\n");
            });
        }
        
        return text.toString();
    }
    
    private String extractZipText(File file) throws IOException {
        logger.debug("Processing ZIP file: {}", file.getName());
        
        StringBuilder allText = new StringBuilder();
        List<String> processedFiles = new ArrayList<>();
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String entryName = entry.getName();
                    String extension = getFileExtension(entryName);
                    
                    // Only process supported text formats
                    if (isSupported(extension)) {
                        // Create temp file for processing
                        Path tempFile = Files.createTempFile("zip_extract_", "." + extension);
                        try {
                            Files.copy(zis, tempFile, StandardCopyOption.REPLACE_EXISTING);
                            
                            allText.append("\n=== File: ").append(entryName).append(" ===\n");
                            allText.append(extractText(tempFile.toFile()));
                            allText.append("\n\n");
                            
                            processedFiles.add(entryName);
                        } finally {
                            Files.deleteIfExists(tempFile);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
        
        if (processedFiles.isEmpty()) {
            throw new IOException("No supported files found in ZIP archive");
        }
        
        // Add summary at the beginning
        String summary = String.format("Processed %d files from archive:\n%s\n",
            processedFiles.size(),
            String.join("\n", processedFiles));
        
        return summary + allText.toString();
    }
    
    public boolean isSupported(Path filePath) {
        String fileName = filePath.getFileName().toString().toLowerCase();
        return isSupported(getFileExtension(fileName));
    }
    
    private boolean isSupported(String extension) {
        return switch (extension) {
            case "txt", "md", "html", "htm", "pdf", "docx", "zip",
                 "java", "py", "js", "css", "xml", "json" -> true;
            default -> false;
        };
    }
    
    private FormatMetadata getPdfMetadata(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            var info = document.getDocumentInformation();
            return new FormatMetadata(
                info.getAuthor(),
                info.getTitle(),
                document.getNumberOfPages()
            );
        }
    }
    
    private FormatMetadata getDocxMetadata(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            var props = document.getProperties().getCoreProperties();
            return new FormatMetadata(
                props.getCreator(),
                props.getTitle(),
                document.getProperties().getExtendedProperties().getPages()
            );
        }
    }
    
    private FormatMetadata getHtmlMetadata(File file) throws IOException {
        Document doc = Jsoup.parse(file, "UTF-8");
        
        String author = doc.select("meta[name=author]").attr("content");
        String title = doc.title();
        
        return new FormatMetadata(author, title, 0);
    }
    
    public List<ProcessedFile> batchProcess(List<File> files) {
        List<ProcessedFile> results = new ArrayList<>();
        
        for (File file : files) {
            try {
                String text = extractText(file);
                FileMetadata metadata = getMetadata(file);
                results.add(new ProcessedFile(file, text, metadata, null));
            } catch (Exception e) {
                logger.error("Error processing file {}: {}", file.getName(), e.getMessage());
                results.add(new ProcessedFile(file, null, null, e.getMessage()));
            }
        }
        
        return results;
    }
    
    public List<File> findFiles(File directory, boolean recursive) {
        List<File> files = new ArrayList<>();
        findFilesRecursive(directory, files, recursive);
        return files;
    }
    
    private void findFilesRecursive(File dir, List<File> files, boolean recursive) {
        File[] contents = dir.listFiles();
        if (contents != null) {
            for (File file : contents) {
                if (file.isDirectory() && recursive) {
                    findFilesRecursive(file, files, recursive);
                } else if (file.isFile() && isSupported(getFileExtension(file.getName()))) {
                    files.add(file);
                }
            }
        }
    }
}