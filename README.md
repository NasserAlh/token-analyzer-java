# Claude Token Analyzer

A comprehensive text analysis tool built with Java 21 and JavaFX, **optimized for Claude AI models**. Features advanced tokenization, multi-format file processing, detailed text metrics, and **real-time Claude API cost estimation**.

## Features

### 🔤 **Claude-Optimized Tokenization**
- **CL100K_BASE encoding** - same tokenization as Claude models use
- **All Claude models supported**: Opus 4.1, Opus 4, Sonnet 4, Sonnet 3.7, Haiku 3.5, Haiku 3
- **Real-time cost estimation** based on official Anthropic pricing
- Token frequency analysis and detailed statistics
- Accurate token counting for Claude API workflows

### 📄 **Multi-Format File Support**
- **Text Files**: TXT, MD
- **Documents**: PDF, DOCX
- **Web**: HTML, HTM
- **Code**: Java, Python, JavaScript, CSS, XML, JSON
- **Archives**: ZIP (processes nested files)

### 📊 **Comprehensive Analysis**
- **Token Metrics**: Total, unique, ratios, frequency distribution
- **Claude Cost Analysis**: Input costs, model-specific pricing, cost per file
- **Content Analysis**: Density, lexical diversity, whitespace ratios
- **Readability**: Flesch Reading Ease scores, complexity analysis
- **Text Statistics**: Word count, sentence analysis, vocabulary richness

### 🎨 **Modern JavaFX GUI**
- Clean, intuitive interface optimized for Claude workflows
- Real-time analysis results with **Claude cost estimation**
- **Multi-file batch processing** with progress indicators
- **Batch summary statistics** with total costs and averages
- Tabbed results view (Single File / Batch Results)
- Comprehensive error handling
- Resizable result tables

## Requirements

- **Java 21** or later
- **Maven 3.6+**
- **JavaFX 21** (included via Maven)

## Installation

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd token-analyzer-java
   ```

2. **Compile the project:**
   ```bash
   mvn clean compile
   ```

3. **Run the application:**
   ```bash
   mvn javafx:run
   ```

## Usage

### GUI Application
1. Launch the application using `mvn javafx:run`
2. Choose your input method:
   - Type or paste text directly into the input area
   - Use **File → Open File** to load a single document
   - Use **File → Open Multiple Files** for batch processing
3. **Claude encoding is automatically selected** (CL100K_BASE)
4. Click **Analyze** (single file) or **Analyze Batch** (multiple files)
5. View results with **Claude cost estimates** in the appropriate tab

### Supported File Formats
- **Plain Text**: `.txt`, `.md`
- **Documents**: `.pdf`, `.docx`
- **Web Files**: `.html`, `.htm`
- **Source Code**: `.java`, `.py`, `.js`, `.css`, `.xml`, `.json`
- **Archives**: `.zip` (extracts and analyzes text files within)

## Architecture

### Core Components
- **TokenEngine**: JTokkit-powered tokenization with advanced text analysis
- **FileProcessor**: Multi-format file processing with comprehensive error handling
- **Models**: Java 21 records for type-safe data representation
- **Controllers**: JavaFX FXML controllers with reactive UI updates

### Key Classes
```
src/main/java/com/tokenanalyzer/
├── TokenAnalyzer.java          # Main JavaFX Application
├── TokenEngine.java            # Core tokenization engine
├── FileProcessor.java          # Multi-format file processor
├── controllers/
│   └── MainController.java     # Primary GUI controller
├── models/                     # Data models (Java 21 records)
│   ├── AnalysisResult.java
│   ├── TokenMetrics.java
│   ├── ReadabilityMetrics.java
│   └── ...
├── exceptions/                 # Custom exception hierarchy
└── utils/
    └── ErrorHandler.java       # Comprehensive error handling
```

### Batch Processing Features
- **Multi-file selection**: Choose multiple files simultaneously
- **Asynchronous processing**: Non-blocking UI with real-time progress
- **Individual file results**: Comprehensive table showing per-file metrics
- **Batch summary statistics**: Total tokens, average costs, success rates
- **Claude cost tracking**: See estimated costs for entire batch
- **Error handling**: Graceful handling of individual file failures
- **Mixed format support**: Process different file types in same batch

## Analysis Metrics

### Token Analysis
- **Total Tokens**: Raw token count using CL100K_BASE encoding (Claude standard)
- **Unique Tokens**: Distinct token count
- **Token/Word Ratio**: Efficiency of tokenization
- **Average Token Length**: Mean character length per token
- **Estimated Input Cost**: Real-time Claude API cost calculation

### Content Metrics
- **Content Density**: Ratio of non-whitespace to total characters
- **Lexical Diversity**: Unique words vs. total words
- **Whitespace Ratio**: Proportion of whitespace characters

### Readability Analysis
- **Flesch Reading Ease**: Standard readability score (0-100)
- **Complexity Score**: Custom metric combining multiple factors
- **Average Sentence Length**: Words per sentence
- **Reading Level**: Categorized difficulty (Very Easy to Very Difficult)

## Building

### Quick Start (Windows)
```bash
# Using batch files (Windows)
build.bat           # Clean and compile
run.bat            # Run application
build-and-run.bat  # Build and run in sequence
```

### Development Build
```bash
mvn clean compile
mvn javafx:run
```

### Production JAR
```bash
mvn clean package
```

### Run Tests
```bash
mvn test
```

## Configuration

The application uses Maven for dependency management and JavaFX Maven Plugin for execution. Key dependencies:

- **JTokkit**: Claude-compatible tokenization (CL100K_BASE)
- **Apache PDFBox**: PDF processing
- **Apache POI**: DOCX document processing  
- **JSoup**: HTML parsing
- **JavaFX 21**: Modern GUI framework
- **SLF4J + Logback**: Comprehensive logging

### Claude Pricing Integration
- **Real-time cost calculation** based on official Anthropic pricing
- **All Claude models supported** with accurate pricing
- **Batch cost summaries** for large-scale analysis projects

## Error Handling

Comprehensive error handling includes:
- **File Processing Errors**: Size limits, format validation, permission checks
- **Analysis Errors**: Encoding failures, memory issues
- **User-Friendly Messages**: Clear explanations without technical jargon
- **Detailed Logging**: Full context for debugging

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- **Anthropic** for Claude AI models and transparent pricing
- **JTokkit** for CL100K_BASE tokenization compatibility
- **Apache Foundation** for document processing libraries
- **JavaFX Community** for the modern GUI framework