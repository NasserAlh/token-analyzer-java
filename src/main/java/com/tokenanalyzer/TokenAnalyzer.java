package com.tokenanalyzer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenAnalyzer extends Application {
    private static final Logger logger = LoggerFactory.getLogger(TokenAnalyzer.class);
    private static final String TITLE = "Token Analyzer";
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting Token Analyzer application");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            BorderPane root = loader.load();
            
            Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
            
            primaryStage.setTitle(TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();
            
            logger.info("Token Analyzer application started successfully");
            
        } catch (Exception e) {
            logger.error("Failed to start Token Analyzer application", e);
            throw new RuntimeException("Application startup failed", e);
        }
    }

    @Override
    public void stop() {
        logger.info("Token Analyzer application stopping");
    }

    public static void main(String[] args) {
        launch(args);
    }
}