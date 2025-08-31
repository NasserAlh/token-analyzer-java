package com.tokenanalyzer.models;

public record FormatMetadata(
    String author,
    String title,
    int pageCount
) {
    public boolean hasAuthor() {
        return author != null && !author.trim().isEmpty();
    }
    
    public boolean hasTitle() {
        return title != null && !title.trim().isEmpty();
    }
    
    public boolean hasPageCount() {
        return pageCount > 0;
    }
}