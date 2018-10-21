package edu.augustana.csc285.Egret;

import java.io.File;
import java.io.IOException;

import org.opencv.videoio.VideoCapture;

import datamodel.ProjectData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class WelcomeWindowController {

    @FXML
    private MenuItem closeOption;

    @FXML
    private Button openProjectBtn;

    @FXML
    private Button newProjectBtn;
    
    @FXML
    private Button nextBtn;
    @FXML
    private TextField fileNameTextField;

    @FXML
    void createProject(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("PreviewWindow.fxml"));
		BorderPane root = (BorderPane)loader.load();
		PreviewWindowController previewController = loader.getController();
		
		Scene nextScene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		Stage primary = (Stage) openProjectBtn.getScene().getWindow();
		primary.setScene(nextScene);

//    	FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
//		BorderPane root = (BorderPane)loader.load();
////		EditingWindowController nextController = loader.getController();
//		
//		Scene nextScene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
//		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//		
//		Stage primary = (Stage) newProjectBtn.getScene().getWindow();
//		primary.setScene(nextScene);
    }

    @FXML
    void exitWindow(ActionEvent event) {

    }

    @FXML
    void openProject(ActionEvent event) {
    	//TODO: Get openExistingProject working
    }

}

