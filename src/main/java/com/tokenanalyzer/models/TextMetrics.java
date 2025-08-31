package com.tokenanalyzer.models;

public record TextMetrics(
    int characterCount,
    int wordCount,
    int lineCount,
    int sentenceCount,
    double averageWordLength
) {
    public double getReadabilityScore() {
        if (sentenceCount == 0 || wordCount == 0) {
            return 0.0;
        }
        
        double avgSentenceLength = (double) wordCount / sentenceCount;
        double avgSyllableCount = averageWordLength * 0.5; // Rough approximation
        
        return 206.835 - (1.015 * avgSentenceLength) - (84.6 * avgSyllableCount);
    }
}