package com.tokenanalyzer.utils;

import com.tokenanalyzer.exceptions.FileProcessingException;
import com.tokenanalyzer.exceptions.TokenAnalysisException;
import com.tokenanalyzer.exceptions.UnsupportedFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletionException;

public class ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
    
    public static String getUserFriendlyMessage(Throwable throwable) {
        if (throwable instanceof CompletionException && throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        
        return switch (throwable) {
            case FileProcessingException fpe -> handleFileProcessingError(fpe);
            case TokenAnalysisException tae -> handleTokenAnalysisError(tae);
            case UnsupportedFormatException ufe -> handleUnsupportedFormatError(ufe);
            case IOException ioe -> handleIOError(ioe);
            case OutOfMemoryError oome -> "File is too large to process. Please try with a smaller file.";
            case SecurityException se -> "Access denied. Please check file permissions.";
            default -> "An unexpected error occurred: " + throwable.getMessage();
        };
    }
    
    private static String handleFileProcessingError(FileProcessingException fpe) {
        String baseMessage = "Failed to process file";
        if (fpe.getFile() != null) {
            baseMessage += " '" + fpe.getFile().getName() + "'";
        }
        
        if (fpe.getFileSize() > 50 * 1024 * 1024) {
            return baseMessage + ": File is too large (maximum 50MB supported)";
        }
        
        return baseMessage + ": " + fpe.getMessage();
    }
    
    private static String handleTokenAnalysisError(TokenAnalysisException tae) {
        if (tae.getOperation() != null && tae.getDetails() != null) {
            return String.format("Token analysis failed during %s: %s", tae.getOperation(), tae.getDetails());
        }
        return "Token analysis failed: " + tae.getMessage();
    }
    
    private static String handleUnsupportedFormatError(UnsupportedFormatException ufe) {
        return "Unsupported file format. Supported formats: " + 
            String.join(", ", ufe.getSupportedFormats());
    }
    
    private static String handleIOError(IOException ioe) {
        String message = ioe.getMessage();
        if (message.contains("No such file")) {
            return "File not found. Please check the file path.";
        }
        if (message.contains("Access denied") || message.contains("Permission denied")) {
            return "Access denied. Please check file permissions.";
        }
        if (message.contains("too large")) {
            return "File is too large to process.";
        }
        return "File access error: " + message;
    }
    
    public static void logError(String operation, Throwable throwable, Object... context) {
        StringBuilder contextStr = new StringBuilder();
        for (int i = 0; i < context.length; i += 2) {
            if (i + 1 < context.length) {
                contextStr.append(context[i]).append(": ").append(context[i + 1]).append(", ");
            }
        }
        
        logger.error("Error during {}: {} [Context: {}]", 
            operation, throwable.getMessage(), contextStr.toString(), throwable);
    }
    
    public static FileProcessingException wrapFileError(String operation, File file, Throwable cause) {
        return new FileProcessingException(
            "Failed to " + operation + " file: " + file.getName(), 
            file, 
            cause
        );
    }
    
    public static TokenAnalysisException wrapAnalysisError(String operation, String details, Throwable cause) {
        return new TokenAnalysisException(operation, details, cause);
    }
}