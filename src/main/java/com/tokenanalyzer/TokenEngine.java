package com.tokenanalyzer;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import com.knuddels.jtokkit.api.EncodingType;
import com.tokenanalyzer.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.BreakIterator;
import java.util.*;
import com.knuddels.jtokkit.api.IntArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TokenEngine {
    private static final Logger logger = LoggerFactory.getLogger(TokenEngine.class);
    
    private final EncodingRegistry registry;
    private final Map<String, ModelType> modelMap;
    private final TextAnalyzer textAnalyzer;
    
    public TokenEngine() {
        this.registry = Encodings.newDefaultEncodingRegistry();
        this.modelMap = initializeModelMap();
        this.textAnalyzer = new TextAnalyzer();
        logger.info("TokenEngine initialized with comprehensive analysis capabilities");
    }
    
    private Map<String, ModelType> initializeModelMap() {
        Map<String, ModelType> map = new HashMap<>();
        map.put("gpt-3.5-turbo", ModelType.GPT_3_5_TURBO);
        map.put("gpt-4", ModelType.GPT_4);
        map.put("gpt-4-turbo", ModelType.GPT_4);
        map.put("text-embedding-ada-002", ModelType.TEXT_EMBEDDING_ADA_002);
        map.put("text-davinci-003", ModelType.TEXT_DAVINCI_003);
        // Add Claude models (using GPT-4 encoding as approximation)
        map.put("claude-3-opus", ModelType.GPT_4);
        map.put("claude-3-sonnet", ModelType.GPT_4);
        map.put("claude-3-haiku", ModelType.GPT_4);
        return map;
    }
    
    public CompletableFuture<AnalysisResult> analyzeText(String text, EncodingType encodingType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Starting comprehensive text analysis with encoding: {}", encodingType);
                long startTime = System.currentTimeMillis();
                
                Encoding encoding = registry.getEncoding(encodingType);
                String modelName = getModelNameFromEncoding(encodingType);
                
                TokenMetrics tokenMetrics = calculateTokenMetrics(text, modelName);
                DensityMetrics densityMetrics = textAnalyzer.calculateDensity(text);
                ReadabilityMetrics readabilityMetrics = textAnalyzer.calculateReadability(text);
                
                long processingTime = System.currentTimeMillis() - startTime;
                
                var result = new AnalysisResult(
                    "direct-input",
                    text.length(),
                    modelName,
                    tokenMetrics.totalTokens(),
                    tokenMetrics.uniqueTokens(),
                    tokenMetrics.tokenWordRatio(),
                    tokenMetrics.avgTokenLength(),
                    densityMetrics.contentDensity(),
                    densityMetrics.lexicalDiversity(),
                    densityMetrics.whitespaceRatio(),
                    readabilityMetrics.fleschScore(),
                    readabilityMetrics.complexityScore(),
                    readabilityMetrics.avgSentenceLength(),
                    processingTime
                );
                
                logger.debug("Analysis completed: {} tokens, {} processing time ms", 
                    tokenMetrics.totalTokens(), processingTime);
                return result;
                
            } catch (Exception e) {
                logger.error("Error analyzing text", e);
                throw new RuntimeException("Text analysis failed", e);
            }
        });
    }
    
    public AnalysisResult analyze(File file) throws IOException {
        long startTime = System.currentTimeMillis();
        
        String content = Files.readString(file.toPath());
        String modelName = "gpt-3.5-turbo"; // Default model
        
        TokenMetrics tokenMetrics = calculateTokenMetrics(content, modelName);
        DensityMetrics densityMetrics = textAnalyzer.calculateDensity(content);
        ReadabilityMetrics readabilityMetrics = textAnalyzer.calculateReadability(content);
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        return new AnalysisResult(
            file.getName(),
            file.length(),
            modelName,
            tokenMetrics.totalTokens(),
            tokenMetrics.uniqueTokens(),
            tokenMetrics.tokenWordRatio(),
            tokenMetrics.avgTokenLength(),
            densityMetrics.contentDensity(),
            densityMetrics.lexicalDiversity(),
            densityMetrics.whitespaceRatio(),
            readabilityMetrics.fleschScore(),
            readabilityMetrics.complexityScore(),
            readabilityMetrics.avgSentenceLength(),
            processingTime
        );
    }
    
    public TokenMetrics calculateTokenMetrics(String text, String modelName) {
        ModelType modelType = modelMap.getOrDefault(modelName, ModelType.GPT_3_5_TURBO);
        Encoding encoding = registry.getEncodingForModel(modelType);
        
        // Encode text to get tokens
        IntArrayList tokensList = encoding.encode(text);
        int totalTokens = tokensList.size();
        
        // Convert to List for compatibility
        List<Integer> tokens = tokensList.boxed();
        
        // Calculate unique tokens
        Set<Integer> uniqueTokenSet = new HashSet<>(tokens);
        int uniqueTokens = uniqueTokenSet.size();
        
        // Calculate word count for ratio
        String[] words = text.split("\\s+");
        int wordCount = words.length;
        double tokenWordRatio = wordCount > 0 ? (double) totalTokens / wordCount : 0;
        
        // Calculate average token length
        double avgTokenLength = calculateAverageTokenLength(tokens, encoding);
        
        // Token frequency distribution
        Map<Integer, Long> tokenFrequency = tokens.stream()
            .collect(Collectors.groupingBy(
                t -> t,
                Collectors.counting()
            ));
        
        return new TokenMetrics(
            totalTokens,
            uniqueTokens,
            tokenWordRatio,
            avgTokenLength,
            tokenFrequency,
            findMostFrequentTokens(tokenFrequency, encoding, 10)
        );
    }
    
    private double calculateAverageTokenLength(List<Integer> tokens, Encoding encoding) {
        if (tokens.isEmpty()) return 0;
        
        double totalLength = tokens.stream()
            .mapToInt(token -> {
                IntArrayList singleToken = new IntArrayList();
                singleToken.add(token);
                return encoding.decode(singleToken).length();
            })
            .sum();
        
        return totalLength / tokens.size();
    }
    
    private List<TokenInfo> findMostFrequentTokens(Map<Integer, Long> frequency, 
                                                   Encoding encoding, int limit) {
        return frequency.entrySet().stream()
            .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                IntArrayList singleToken = new IntArrayList();
                singleToken.add(entry.getKey());
                String tokenText = encoding.decode(singleToken);
                return new TokenInfo(tokenText, entry.getKey(), entry.getValue());
            })
            .collect(Collectors.toList());
    }
    
    public ComparisonResult compareModels(String text, String model1, String model2) {
        TokenMetrics metrics1 = calculateTokenMetrics(text, model1);
        TokenMetrics metrics2 = calculateTokenMetrics(text, model2);
        
        double efficiency = (double) metrics1.totalTokens() / metrics2.totalTokens();
        
        return new ComparisonResult(
            model1,
            model2,
            metrics1,
            metrics2,
            efficiency,
            calculateTokenOverlap(metrics1, metrics2)
        );
    }
    
    private double calculateTokenOverlap(TokenMetrics m1, TokenMetrics m2) {
        // Calculate Jaccard similarity of token sets
        Set<Integer> tokens1 = m1.tokenFrequency().keySet();
        Set<Integer> tokens2 = m2.tokenFrequency().keySet();
        
        Set<Integer> intersection = new HashSet<>(tokens1);
        intersection.retainAll(tokens2);
        
        Set<Integer> union = new HashSet<>(tokens1);
        union.addAll(tokens2);
        
        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }
    
    public List<AnalysisResult> batchAnalyze(List<File> files, ProgressCallback callback) {
        List<AnalysisResult> results = new ArrayList<>();
        int total = files.size();
        
        for (int i = 0; i < total; i++) {
            try {
                results.add(analyze(files.get(i)));
                if (callback != null) {
                    callback.onProgress((i + 1) / (double) total, files.get(i).getName());
                }
            } catch (IOException e) {
                logger.error("Error processing {}: {}", files.get(i).getName(), e.getMessage());
            }
        }
        
        return results;
    }
    
    private String getModelNameFromEncoding(EncodingType encodingType) {
        return switch (encodingType) {
            case CL100K_BASE -> "gpt-3.5-turbo";
            case P50K_BASE -> "text-davinci-003";
            case R50K_BASE -> "gpt-3";
            case P50K_EDIT -> "text-davinci-edit-001";
            default -> "gpt-3.5-turbo";
        };
    }
    
    @FunctionalInterface
    public interface ProgressCallback {
        void onProgress(double progress, String currentFile);
    }
    
    static class TextAnalyzer {
        private static final Pattern WORD_PATTERN = Pattern.compile("\\b\\w+\\b");
        private static final Pattern SENTENCE_PATTERN = Pattern.compile("[.!?]+\\s+");
        
        public DensityMetrics calculateDensity(String text) {
            int totalChars = text.length();
            int nonWhitespace = text.replaceAll("\\s", "").length();
            double contentDensity = totalChars > 0 ? (double) nonWhitespace / totalChars : 0;
            
            // Calculate lexical diversity
            List<String> words = extractWords(text.toLowerCase());
            Set<String> uniqueWords = new HashSet<>(words);
            double lexicalDiversity = words.isEmpty() ? 0 : 
                (double) uniqueWords.size() / words.size();
            
            // Whitespace ratio
            int whitespaceCount = totalChars - nonWhitespace;
            double whitespaceRatio = totalChars > 0 ? 
                (double) whitespaceCount / totalChars : 0;
            
            return new DensityMetrics(contentDensity, lexicalDiversity, whitespaceRatio);
        }
        
        public ReadabilityMetrics calculateReadability(String text) {
            List<String> words = extractWords(text);
            List<String> sentences = extractSentences(text);
            
            if (words.isEmpty() || sentences.isEmpty()) {
                return new ReadabilityMetrics(0, 0, 0);
            }
            
            // Calculate average sentence length
            double avgSentenceLength = (double) words.size() / sentences.size();
            
            // Calculate syllable count (simplified)
            int totalSyllables = words.stream()
                .mapToInt(this::countSyllables)
                .sum();
            double avgSyllablesPerWord = (double) totalSyllables / words.size();
            
            // Flesch Reading Ease Score
            double fleschScore = 206.835 - 1.015 * avgSentenceLength - 84.6 * avgSyllablesPerWord;
            fleschScore = Math.max(0, Math.min(100, fleschScore)); // Clamp to 0-100
            
            // Complexity score (custom metric)
            double complexityScore = calculateComplexity(words, avgSentenceLength);
            
            return new ReadabilityMetrics(fleschScore, complexityScore, avgSentenceLength);
        }
        
        private List<String> extractWords(String text) {
            List<String> words = new ArrayList<>();
            var matcher = WORD_PATTERN.matcher(text);
            while (matcher.find()) {
                words.add(matcher.group());
            }
            return words;
        }
        
        private List<String> extractSentences(String text) {
            // Use BreakIterator for better sentence detection
            BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
            iterator.setText(text);
            
            List<String> sentences = new ArrayList<>();
            int start = iterator.first();
            for (int end = iterator.next(); end != BreakIterator.DONE; 
                 start = end, end = iterator.next()) {
                String sentence = text.substring(start, end).trim();
                if (!sentence.isEmpty()) {
                    sentences.add(sentence);
                }
            }
            return sentences;
        }
        
        private int countSyllables(String word) {
            // Simplified syllable counting
            word = word.toLowerCase().replaceAll("[^a-z]", "");
            int count = 0;
            boolean previousWasVowel = false;
            
            for (char c : word.toCharArray()) {
                boolean isVowel = "aeiou".indexOf(c) >= 0;
                if (isVowel && !previousWasVowel) {
                    count++;
                }
                previousWasVowel = isVowel;
            }
            
            // Adjust for silent 'e'
            if (word.endsWith("e") && count > 1) {
                count--;
            }
            
            return Math.max(1, count);
        }
        
        private double calculateComplexity(List<String> words, double avgSentenceLength) {
            // Custom complexity metric based on:
            // - Average word length
            // - Sentence length
            // - Vocabulary richness
            
            double avgWordLength = words.stream()
                .mapToInt(String::length)
                .average()
                .orElse(0);
            
            Set<String> uniqueWords = new HashSet<>(words);
            double vocabularyRichness = (double) uniqueWords.size() / words.size();
            
            // Normalize and combine metrics
            double complexity = (avgWordLength / 10.0) * 0.3 +
                              (avgSentenceLength / 30.0) * 0.4 +
                              (1 - vocabularyRichness) * 0.3;
            
            return Math.min(1.0, complexity);
        }
    }
}