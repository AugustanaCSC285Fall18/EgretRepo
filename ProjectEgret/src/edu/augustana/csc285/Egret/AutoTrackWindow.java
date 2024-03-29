/**
 * @author Dr. Forrest Stonedahl
 * Dr. Stonedahl wrote this class. (Team Egret wrote all the other classes that don't have a comment with an author in it)
 */

package edu.augustana.csc285.Egret;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

public class AutoTrackWindow extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("AutoTrackWindow.fxml"));
			BorderPane root = (BorderPane) loader.load();
			AutoTrackController controller = loader.getController();

			Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			controller.initializeWithStage(primaryStage);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}
}
