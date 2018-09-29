package edu.augustana.csc285.Egret;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class WelcomeWindowController {

    @FXML
    private MenuItem closeOption;

    @FXML
    private Button openProjectBtn;

    @FXML
    private Button newProjectBtn;

    @FXML
    void createProject(ActionEvent event) throws IOException{
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("EditingWindow.fxml"));
		BorderPane root = (BorderPane)loader.load();
//		EditingWindowController nextController = loader.getController();
		
		Scene nextScene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		Stage primary = (Stage) newProjectBtn.getScene().getWindow();
		primary.setScene(nextScene);
    }

    @FXML
    void exitWindow(ActionEvent event) {

    }

    @FXML
    void openProject(ActionEvent event) {

    }

}

