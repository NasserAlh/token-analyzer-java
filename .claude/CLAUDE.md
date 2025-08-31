# Token Analyzer Java 21 Implementation

## Project Overview
Converting Python Token Analyzer to Java 21 with JavaFX GUI.

## Architecture Guidelines
- Use Java 21 features: records, pattern matching, virtual threads
- JavaFX for GUI (not Swing)
- Maven for dependency management
- JTokkit for tokenization (OpenAI compatibility)

## Key Dependencies
- JTokkit: Token counting
- Apache PDFBox: PDF processing
- Apache POI: DOCX processing
- JSoup: HTML parsing
- JavaFX 21: GUI framework

## File Structure
src/main/java/com/tokenanalyzer/
  - TokenAnalyzer.java (Main GUI)
  - TokenEngine.java (Core logic)
  - FileProcessor.java (File handling)
  - models/ (Data classes)
  - controllers/ (JavaFX controllers)
  - utils/ (Utilities)

## Reference Files
All reference implementations are in docs/reference/

## Testing Requirements
- Unit tests for all core functionality
- Use JUnit 5
- Minimum 80% coverage

## Build Commands
mvn clean compile
mvn javafx:run
mvn test
mvn package