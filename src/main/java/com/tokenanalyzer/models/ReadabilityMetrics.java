package com.tokenanalyzer.models;

public record ReadabilityMetrics(
    double fleschScore,
    double complexityScore,
    double avgSentenceLength
) {
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
}