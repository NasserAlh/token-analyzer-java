package com.tokenanalyzer.models;

public record AnalysisResult(
    String fileName,
    long fileSize,
    String model,
    int totalTokens,
    int uniqueTokens,
    double tokenWordRatio,
    double avgTokenLength,
    double contentDensity,
    double lexicalDiversity,
    double whitespaceRatio,
    double fleschScore,
    double complexityScore,
    double avgSentenceLength,
    long processingTime
) {
    public double getTokensPerCharacter() {
        return fileSize > 0 ? (double) totalTokens / fileSize : 0.0;
    }
    
    public double getCompressionRatio() {
        return fileSize > 0 ? (double) totalTokens / fileSize * 100 : 0.0;
    }
    
    public double getUniquenessRatio() {
        return totalTokens > 0 ? (double) uniqueTokens / totalTokens : 0.0;
    }
    
    public String getReadingLevel() {
        if (fleschScore >= 90) return "Very Easy";
        if (fleschScore >= 80) return "Easy";
        if (fleschScore >= 70) return "Fairly Easy";
        if (fleschScore >= 60) return "Standard";
        if (fleschScore >= 50) return "Fairly Difficult";
        if (fleschScore >= 30) return "Difficult";
        return "Very Difficult";
    }
    
    public String getComplexityLevel() {
        if (complexityScore >= 0.8) return "Very Complex";
        if (complexityScore >= 0.6) return "Complex";
        if (complexityScore >= 0.4) return "Moderate";
        if (complexityScore >= 0.2) return "Simple";
        return "Very Simple";
    }
    
    public String getFormattedProcessingTime() {
        if (processingTime < 1000) return processingTime + " ms";
        return String.format("%.2f s", processingTime / 1000.0);
    }
}