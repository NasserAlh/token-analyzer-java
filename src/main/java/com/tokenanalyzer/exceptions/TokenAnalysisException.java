package com.tokenanalyzer.exceptions;

public class TokenAnalysisException extends Exception {
    private final String operation;
    private final String details;
    
    public TokenAnalysisException(String message) {
        super(message);
        this.operation = null;
        this.details = null;
    }
    
    public TokenAnalysisException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.details = null;
    }
    
    public TokenAnalysisException(String operation, String details, Throwable cause) {
        super(String.format("Token analysis failed during %s: %s", operation, details), cause);
        this.operation = operation;
        this.details = details;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getDetails() {
        return details;
    }
}