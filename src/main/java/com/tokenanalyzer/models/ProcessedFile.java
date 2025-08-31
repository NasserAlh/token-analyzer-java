package com.tokenanalyzer.models;

import java.io.File;

public record ProcessedFile(
    File file,
    String text,
    FileMetadata metadata,
    String error
) {
    public boolean isSuccess() {
        return error == null && text != null;
    }
    
    public boolean hasText() {
        return text != null && !text.trim().isEmpty();
    }
    
    public int getTextLength() {
        return text != null ? text.length() : 0;
    }
    
    public String getFileName() {
        return file.getName();
    }
}