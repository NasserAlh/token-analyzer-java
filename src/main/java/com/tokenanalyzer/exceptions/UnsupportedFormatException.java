package com.tokenanalyzer.exceptions;

import java.util.List;

public class UnsupportedFormatException extends FileProcessingException {
    private final List<String> supportedFormats;
    
    public UnsupportedFormatException(String fileType, List<String> supportedFormats) {
        super(String.format("Unsupported file format: %s. Supported formats: %s", 
            fileType, String.join(", ", supportedFormats)), null);
        this.supportedFormats = supportedFormats;
    }
    
    public List<String> getSupportedFormats() {
        return supportedFormats;
    }
}