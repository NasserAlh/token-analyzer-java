package com.tokenanalyzer.models;

public record TokenInfo(
    String text,
    int tokenId,
    long frequency
) {
    public double getRelativeFrequency(int totalTokens) {
        return totalTokens > 0 ? (double) frequency / totalTokens : 0.0;
    }
}