package com.tokenanalyzer.models;

import java.util.List;
import java.util.Map;

public record TokenMetrics(
    int totalTokens,
    int uniqueTokens,
    double tokenWordRatio,
    double avgTokenLength,
    Map<Integer, Long> tokenFrequency,
    List<TokenInfo> mostFrequent
) {
    public double getUniquenessRatio() {
        return totalTokens > 0 ? (double) uniqueTokens / totalTokens : 0.0;
    }
    
    public int getVocabularySize() {
        return uniqueTokens;
    }
}