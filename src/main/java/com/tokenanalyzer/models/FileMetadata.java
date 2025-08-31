package com.tokenanalyzer.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record FileMetadata(
    String fileName,
    String extension,
    long size,
    String mimeType,
    long lastModified,
    String author,
    String title,
    int pageCount
) {
    public String getFormattedSize() {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }
    
    public String getFormattedLastModified() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(lastModified), 
            ZoneId.systemDefault()
        );
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    public String getFileType() {
        return extension.toUpperCase() + " File";
    }
}