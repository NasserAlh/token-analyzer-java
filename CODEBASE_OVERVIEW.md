# Claude Token Analyzer - Codebase Overview

A comprehensive guide to all Java files in the Claude Token Analyzer project, detailing their purpose, functionality, and importance in the overall architecture.

## 📁 Project Structure

```
src/main/
├── java/com/tokenanalyzer/
│   ├── TokenAnalyzer.java          # Main JavaFX Application
│   ├── TokenEngine.java            # Core tokenization engine
│   ├── FileProcessor.java          # Multi-format file processor
│   ├── controllers/
│   │   └── MainController.java     # Primary GUI controller
│   ├── models/                     # Data models (Java 21 records)
│   │   ├── AnalysisResult.java
│   │   ├── ComparisonResult.java
│   │   ├── DensityMetrics.java
│   │   ├── FileMetadata.java
│   │   ├── FormatMetadata.java
│   │   ├── ProcessedFile.java
│   │   ├── ReadabilityMetrics.java
│   │   ├── TextMetrics.java
│   │   ├── TokenInfo.java
│   │   └── TokenMetrics.java
│   ├── exceptions/                 # Custom exception hierarchy
│   │   ├── FileProcessingException.java
│   │   ├── TokenAnalysisException.java
│   │   └── UnsupportedFormatException.java
│   └── utils/
│       ├── ErrorHandler.java       # Comprehensive error handling
│       └── ClaudePricing.java      # Claude pricing calculations
└── resources/
    ├── fxml/
    │   └── main.fxml              # Primary UI layout definition
    └── styles/
        └── application.css        # Application styling and themes
```

---

## 🚀 Main Application Files

### **TokenAnalyzer.java**
- **Purpose**: Main JavaFX application entry point
- **Description**: Initializes the JavaFX application, loads FXML layouts, sets up the primary stage
- **Key Features**:
  - Application lifecycle management (start/stop)
  - FXML loader configuration
  - CSS stylesheet loading
  - Window sizing and constraints
- **Importance**: ⭐⭐⭐⭐⭐ **Critical** - Entry point for entire application

### **TokenEngine.java**
- **Purpose**: Core tokenization and text analysis engine
- **Description**: Handles all Claude-compatible tokenization using JTokkit library
- **Key Features**:
  - CL100K_BASE encoding (Claude standard)
  - Comprehensive Claude model mapping (Opus 4.1, Sonnet 4, Haiku 3.5, etc.)
  - Advanced text analysis (readability, complexity, density)
  - Asynchronous processing with CompletableFuture
  - Token frequency analysis and statistics
- **Importance**: ⭐⭐⭐⭐⭐ **Critical** - Heart of the tokenization functionality

### **FileProcessor.java**
- **Purpose**: Multi-format file processing and content extraction
- **Description**: Handles reading and extracting text from various file formats
- **Key Features**:
  - Multi-format support (TXT, PDF, DOCX, HTML, ZIP, code files)
  - Batch file processing capabilities
  - File validation and size limits (50MB max)
  - Asynchronous processing
  - ZIP archive extraction with nested file support
- **Importance**: ⭐⭐⭐⭐⭐ **Critical** - Essential for multi-format file support

---

## 🎮 User Interface

### **MainController.java**
- **Purpose**: Primary JavaFX controller managing the entire GUI
- **Description**: Handles all user interactions, UI updates, and coordinates between backend and frontend
- **Key Features**:
  - Single file and batch processing workflows
  - Real-time progress tracking with progress bars
  - Claude-optimized encoding selection (CL100K_BASE locked)
  - Comprehensive error handling with user-friendly messages
  - Batch summary statistics calculation
  - Tabbed interface (Single File / Batch Results)
  - Claude cost estimation integration
- **Importance**: ⭐⭐⭐⭐⭐ **Critical** - Main user interaction hub

---

## 📊 Data Models (Java 21 Records)

### **AnalysisResult.java**
- **Purpose**: Primary data structure for text analysis results
- **Description**: Immutable record containing all analysis metrics
- **Key Data**: Token counts, readability scores, processing time, model info, Claude costs
- **Importance**: ⭐⭐⭐⭐ **High** - Core data structure for results

### **TokenMetrics.java**
- **Purpose**: Token-specific analysis data
- **Description**: Detailed tokenization statistics and frequency analysis
- **Key Data**: Total/unique tokens, ratios, frequency distributions, most common tokens
- **Importance**: ⭐⭐⭐⭐ **High** - Essential for token analysis

### **ReadabilityMetrics.java**
- **Purpose**: Text readability and complexity analysis
- **Description**: Flesch reading scores, complexity metrics, sentence analysis
- **Key Data**: Flesch scores, complexity levels, sentence lengths
- **Importance**: ⭐⭐⭐ **Medium** - Important for content analysis

### **DensityMetrics.java**
- **Purpose**: Content density and lexical diversity analysis
- **Description**: Measures text efficiency and vocabulary richness
- **Key Data**: Content density, lexical diversity, whitespace ratios
- **Importance**: ⭐⭐⭐ **Medium** - Valuable for content quality assessment

### **FileMetadata.java**
- **Purpose**: File system and format-specific metadata
- **Description**: File properties, creation dates, author info, page counts
- **Key Data**: File names, sizes, timestamps, MIME types, format-specific data
- **Importance**: ⭐⭐ **Low** - Useful for file tracking and organization

### **ComparisonResult.java**
- **Purpose**: Model comparison analysis (future feature)
- **Description**: Compares tokenization results between different models
- **Key Data**: Model names, efficiency ratios, token overlaps
- **Importance**: ⭐⭐ **Low** - Future enhancement feature

### **ProcessedFile.java**
- **Purpose**: Batch processing file tracking
- **Description**: Links files to their processing results and metadata
- **Key Data**: File references, content, metadata, error messages
- **Importance**: ⭐⭐⭐ **Medium** - Important for batch operations

### **TextMetrics.java**
- **Purpose**: Basic text statistics
- **Description**: Fundamental text measurements (characters, words, etc.)
- **Key Data**: Character counts, word counts, line counts
- **Importance**: ⭐⭐ **Low** - Basic supporting metrics

### **TokenInfo.java**
- **Purpose**: Individual token information
- **Description**: Represents single token with frequency and metadata
- **Key Data**: Token text, ID, frequency count
- **Importance**: ⭐⭐ **Low** - Supporting data structure

### **FormatMetadata.java**
- **Purpose**: Format-specific document metadata
- **Description**: PDF pages, DOCX authors, HTML titles, etc.
- **Key Data**: Author names, titles, page counts, format-specific fields
- **Importance**: ⭐⭐ **Low** - Nice-to-have metadata

---

## ⚠️ Exception Handling

### **FileProcessingException.java**
- **Purpose**: File processing error handling
- **Description**: Custom exception for file-related errors
- **Use Cases**: File not found, permission denied, format errors, size limits
- **Importance**: ⭐⭐⭐ **Medium** - Essential for robust file handling

### **TokenAnalysisException.java**
- **Purpose**: Tokenization process error handling
- **Description**: Custom exception for analysis-related errors
- **Use Cases**: Encoding failures, memory issues, analysis timeouts
- **Importance**: ⭐⭐⭐ **Medium** - Important for analysis reliability

### **UnsupportedFormatException.java**
- **Purpose**: Unsupported file format handling
- **Description**: Thrown when encountering unsupported file types
- **Use Cases**: Unknown extensions, corrupted files, unsupported encodings
- **Importance**: ⭐⭐ **Low** - User experience enhancement

---

## 🎨 User Interface Resources

### **main.fxml**
- **Purpose**: Primary JavaFX FXML layout definition
- **Description**: Declarative UI structure using FXML markup language
- **Key Components**:
  - **MenuBar**: File operations (Open File, Open Multiple Files) and Help menu
  - **SplitPane**: Vertical split with input area (top) and results area (bottom)
  - **Input Section**: Text area, Claude encoding combo box, action buttons
  - **Tabbed Results**: Single File Results and Batch Results tabs
  - **Batch Summary**: Statistics section with progress indicators
  - **Status Bar**: Bottom status label for user feedback
- **Architecture**: Uses MVC pattern with `MainController` as controller class
- **Importance**: ⭐⭐⭐⭐⭐ **Critical** - Defines entire user interface structure

### **application.css**
- **Purpose**: Application-wide styling and visual theme
- **Description**: Custom CSS styles for professional appearance
- **Key Styling**:
  - **Color Scheme**: Blue accent (#007acc), clean whites and grays
  - **Typography**: Segoe UI font family, consistent sizing
  - **Component Styles**: Buttons, tables, text areas, combo boxes
  - **Interactive States**: Hover effects, focus indicators, selection highlighting
  - **Layout**: Proper spacing, padding, borders, and visual hierarchy
- **Design Philosophy**: Clean, professional, accessibility-focused
- **Importance**: ⭐⭐⭐ **Medium** - Essential for professional user experience

---

## 🛠️ Utility Classes

### **ErrorHandler.java**
- **Purpose**: Centralized error handling and user message generation
- **Description**: Converts technical exceptions into user-friendly messages
- **Key Features**:
  - User-friendly error message generation
  - Comprehensive logging with context
  - Error categorization and handling strategies
- **Importance**: ⭐⭐⭐⭐ **High** - Critical for user experience

### **ClaudePricing.java**
- **Purpose**: Claude API cost calculation and pricing information
- **Description**: Real-time cost estimation based on official Anthropic pricing
- **Key Features**:
  - All Claude models pricing (Opus 4.1, Sonnet 4, Haiku 3.5, etc.)
  - Input/output cost calculations
  - Model descriptions and capabilities
  - Cost formatting utilities
- **Importance**: ⭐⭐⭐⭐ **High** - Core differentiating feature

---

## 📈 Importance Legend

- ⭐⭐⭐⭐⭐ **Critical**: Core functionality, application won't work without it
- ⭐⭐⭐⭐ **High**: Major features, significant impact on user experience
- ⭐⭐⭐ **Medium**: Important supporting functionality
- ⭐⭐ **Low**: Nice-to-have features, minimal impact if removed

---

## 🔄 Data Flow

1. **User Input** → `MainController` → `FileProcessor` (if file) → `TokenEngine`
2. **TokenEngine** → `AnalysisResult` + various metrics records → `MainController`
3. **MainController** → `ClaudePricing` → Display results with costs
4. **Error Flow**: Any exception → `ErrorHandler` → User-friendly message → `MainController`

---

## 🎯 Key Integration Points

- **JavaFX FXML**: `MainController` bridges UI and backend
- **JTokkit Library**: `TokenEngine` uses CL100K_BASE encoding
- **File I/O**: `FileProcessor` handles multiple formats
- **Claude Integration**: `ClaudePricing` provides real-time costs
- **Error Handling**: `ErrorHandler` ensures smooth user experience

This architecture provides a robust, extensible foundation for Claude-optimized tokenization and cost analysis with comprehensive multi-file processing capabilities.