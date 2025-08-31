package com.tokenanalyzer.exceptions;

import java.io.File;

public class FileProcessingException extends Exception {
    private final File file;
    private final String fileType;
    private final long fileSize;
    
    public FileProcessingException(String message, File file) {
        super(message);
        this.file = file;
        this.fileType = getFileExtension(file.getName());
        this.fileSize = file.length();
    }
    
    public FileProcessingException(String message, File file, Throwable cause) {
        super(message, cause);
        this.file = file;
        this.fileType = getFileExtension(file.getName());
        this.fileSize = file.length();
    }
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) return "unknown";
        return fileName.substring(lastDot + 1).toLowerCase();
    }
    
    public File getFile() {
        return file;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public String getFormattedFileSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        if (fileSize < 1024 * 1024 * 1024) return String.format("%.1f MB", fileSize / (1024.0 * 1024));
        return String.format("%.1f GB", fileSize / (1024.0 * 1024 * 1024));
    }
}