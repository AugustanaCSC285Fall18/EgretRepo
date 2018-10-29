/**
 * Description: Allows the user to either create a new project or load a previous project.
 */

package edu.augustana.csc285.Egret;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.opencv.videoio.VideoCapture;

import datamodel.ProjectData;
import datamodel.Video;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class WelcomeWindowController {

	@FXML
	private Button openProjectBtn;

	@FXML
	private Button newProjectBtn;

	@FXML
	private Button nextBtn;

	@FXML
	private TextField fileNameTextField;

	@FXML
	private AnchorPane currentFrameImage;

	private Stage stage;

	public void initializeWithStage(Stage primaryStage) {
		stage = primaryStage;

	}

	/**
	 * Allows the user to create a ProjectData object (sends them to the next window
	 * to do so).
	 * 
	 * @param event - mouse click on CreateProject button
	 * @throws IOException if there is an error when loading the next window
	 */
	@FXML
	public void createProject(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PreviewWindow.fxml"));
		BorderPane root = (BorderPane) loader.load();
		PreviewWindowController previewController = loader.getController();
		Scene nextScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage primary = (Stage) openProjectBtn.getScene().getWindow();
		primary.setScene(nextScene);
		primary.centerOnScreen();
		primary.setResizable(false);
	}

	/**
	 * Allows the user to load a file that contains a JSON String.
	 * 
	 * @param event - mouse click on the OpenProject button
	 * @throws IOException if there is an error when loading the next window
	 */
	@FXML
	public void openProject(ActionEvent event) throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		File chosenFile = fileChooser.showOpenDialog(stage);
		if (chosenFile != null) {
			try {
				ProjectData chosenProject = ProjectData.loadFromFile(chosenFile);
				FXMLLoader loader = new FXMLLoader(getClass().getResource("EditingWindow.fxml"));
				BorderPane root = (BorderPane) loader.load();
				Scene nextScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
				nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				Stage primary = (Stage) openProjectBtn.getScene().getWindow();
				primary.setScene(nextScene);
				primary.centerOnScreen();
				primary.setResizable(false);
				EditingWindowController nextController = loader.getController();
				nextController.initializeWithProjectData(chosenProject);
			} catch (NoSuchElementException e) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Open Project");
				alert.setHeaderText(null);
				alert.setContentText("Did not select a proper file. Make sure you are not loading a video. Load a "
						+ "ProjectData file (usually .txt or .project or .JSON or .file)");
			}
		}
	}

	/**
	 * Shows the about message in an Alert.
	 */
	@FXML
	public void showAboutMessage() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About Message");
		alert.setHeaderText(null);
		String names = "Team Egret: Kathryn Clark, Brent Pierce, Avery Vanopdorp\nProject Supervisor: Dr. Forrest Stonedahl";
		String className = "Project for CSC 285 at Augustana College";
		String acknowledgements = "Thanks to all of the teams that we conversed with about the project (shout out to team Curlew for"
				+ " help with the resizing of images and Team Dowitcher for help with the scaling ratio, the Q&A site to help get "
				+ "stuff sorted out, Luigi De Russis for a way to start the project from Lab 3, Code.Makery for help with the "
				+ "alert messages, and everything else that may need a mention that we have left out. ";
		String usedLibraries = "Credit to the libraries: OpenCV, JavaFX, GSON, JSON";
		alert.setContentText(names + "\n" + className + "\n\n" + acknowledgements + "\n\n" + usedLibraries);
		alert.showAndWait();
	}
}