package com.tokenanalyzer.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Claude model pricing information based on Anthropic's official pricing
 * Prices are in USD per million tokens (MTok)
 * Source: https://docs.anthropic.com/en/docs/about-claude/pricing
 */
public class ClaudePricing {
    
    // Pricing per million tokens (MTok) in USD
    private static final Map<String, ModelPricing> MODEL_PRICES = new HashMap<>();
    
    static {
        // Claude 4 Models (Latest)
        MODEL_PRICES.put("claude-opus-4-1", new ModelPricing(15.0, 75.0, "Claude Opus 4.1 - Most capable"));
        MODEL_PRICES.put("claude-opus-4", new ModelPricing(15.0, 75.0, "Claude Opus 4"));
        MODEL_PRICES.put("claude-sonnet-4", new ModelPricing(3.0, 15.0, "Claude Sonnet 4 - High performance"));
        MODEL_PRICES.put("claude-sonnet-3.7", new ModelPricing(3.0, 15.0, "Claude Sonnet 3.7"));
        MODEL_PRICES.put("claude-haiku-3.5", new ModelPricing(0.80, 4.0, "Claude Haiku 3.5 - Fastest"));
        MODEL_PRICES.put("claude-haiku-3", new ModelPricing(0.25, 1.25, "Claude Haiku 3"));
        
        // Legacy Claude 3 Models
        MODEL_PRICES.put("claude-3-opus", new ModelPricing(15.0, 75.0, "Claude 3 Opus (deprecated)"));
        MODEL_PRICES.put("claude-3-sonnet", new ModelPricing(3.0, 15.0, "Claude 3 Sonnet"));
        MODEL_PRICES.put("claude-3-haiku", new ModelPricing(0.25, 1.25, "Claude 3 Haiku"));
        
        // OpenAI Models (for compatibility)
        MODEL_PRICES.put("gpt-4", new ModelPricing(30.0, 60.0, "GPT-4"));
        MODEL_PRICES.put("gpt-3.5-turbo", new ModelPricing(0.5, 1.5, "GPT-3.5 Turbo"));
        MODEL_PRICES.put("text-davinci-003", new ModelPricing(20.0, 20.0, "Text Davinci 003"));
    }
    
    public static ModelPricing getPricing(String modelName) {
        return MODEL_PRICES.getOrDefault(modelName, 
            new ModelPricing(3.0, 15.0, "Unknown model (using Sonnet 4 pricing)"));
    }
    
    public static double calculateInputCost(String modelName, long tokenCount) {
        ModelPricing pricing = getPricing(modelName);
        return (tokenCount / 1_000_000.0) * pricing.inputPrice();
    }
    
    public static double calculateOutputCost(String modelName, long tokenCount) {
        ModelPricing pricing = getPricing(modelName);
        return (tokenCount / 1_000_000.0) * pricing.outputPrice();
    }
    
    public static String formatCost(double cost) {
        if (cost < 0.001) {
            return String.format("$%.6f", cost);
        } else if (cost < 0.01) {
            return String.format("$%.4f", cost);
        } else {
            return String.format("$%.2f", cost);
        }
    }
    
    public static String getModelDescription(String modelName) {
        ModelPricing pricing = getPricing(modelName);
        return pricing.description();
    }
    
    public static record ModelPricing(
        double inputPrice,   // Price per million input tokens
        double outputPrice,  // Price per million output tokens
        String description   // Model description
    ) {}
}