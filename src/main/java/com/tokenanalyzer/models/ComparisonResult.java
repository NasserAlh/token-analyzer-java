package com.tokenanalyzer.models;

public record ComparisonResult(
    String model1,
    String model2,
    TokenMetrics metrics1,
    TokenMetrics metrics2,
    double efficiency,
    double tokenOverlap
) {
    public String getEfficiencyDescription() {
        if (efficiency < 0.8) return model1 + " is significantly more efficient";
        if (efficiency < 0.9) return model1 + " is more efficient";
        if (efficiency <= 1.1) return "Models have similar efficiency";
        if (efficiency <= 1.25) return model2 + " is more efficient";
        return model2 + " is significantly more efficient";
    }
    
    public String getOverlapDescription() {
        if (tokenOverlap >= 0.8) return "Very high token overlap";
        if (tokenOverlap >= 0.6) return "High token overlap";
        if (tokenOverlap >= 0.4) return "Moderate token overlap";
        if (tokenOverlap >= 0.2) return "Low token overlap";
        return "Very low token overlap";
    }
}