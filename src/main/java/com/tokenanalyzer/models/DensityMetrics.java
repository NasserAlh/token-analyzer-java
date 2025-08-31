package com.tokenanalyzer.models;

public record DensityMetrics(
    double contentDensity,
    double lexicalDiversity,
    double whitespaceRatio
) {
    public double getInformationDensity() {
        // Higher content density and lexical diversity indicate more information
        return (contentDensity * 0.6) + (lexicalDiversity * 0.4);
    }
    
    public String getDensityCategory() {
        double density = getInformationDensity();
        if (density >= 0.8) return "Very High";
        if (density >= 0.6) return "High";
        if (density >= 0.4) return "Medium";
        if (density >= 0.2) return "Low";
        return "Very Low";
    }
}