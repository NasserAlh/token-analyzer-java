# Token Analyzer

A comprehensive text analysis tool built with Java 21 and JavaFX, featuring advanced tokenization, multi-format file processing, and detailed text metrics.

## Features

### 🔤 **Advanced Tokenization**
- Multiple encoding models (GPT-3.5, GPT-4, Claude approximations)
- JTokkit integration for OpenAI-compatible token counting
- Token frequency analysis and statistics
- Model comparison capabilities

### 📄 **Multi-Format File Support**
- **Text Files**: TXT, MD
- **Documents**: PDF, DOCX
- **Web**: HTML, HTM
- **Code**: Java, Python, JavaScript, CSS, XML, JSON
- **Archives**: ZIP (processes nested files)

### 📊 **Comprehensive Analysis**
- **Token Metrics**: Total, unique, ratios, frequency distribution
- **Content Analysis**: Density, lexical diversity, whitespace ratios
- **Readability**: Flesch Reading Ease scores, complexity analysis
- **Text Statistics**: Word count, sentence analysis, vocabulary richness

### 🎨 **Modern JavaFX GUI**
- Clean, intuitive interface
- Real-time analysis results
- Progress indicators
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
2. Either:
   - Type or paste text directly into the input area
   - Use **File → Open File** to load supported documents
3. Select your preferred encoding model
4. Click **Analyze** to get comprehensive metrics

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

## Analysis Metrics

### Token Analysis
- **Total Tokens**: Raw token count using selected encoding
- **Unique Tokens**: Distinct token count
- **Token/Word Ratio**: Efficiency of tokenization
- **Average Token Length**: Mean character length per token

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

- **JTokkit**: OpenAI-compatible tokenization
- **Apache PDFBox**: PDF processing
- **Apache POI**: DOCX document processing  
- **JSoup**: HTML parsing
- **JavaFX 21**: Modern GUI framework
- **SLF4J + Logback**: Comprehensive logging

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

- **JTokkit** for OpenAI-compatible tokenization
- **Apache Foundation** for document processing libraries
- **JavaFX Community** for the modern GUI framework