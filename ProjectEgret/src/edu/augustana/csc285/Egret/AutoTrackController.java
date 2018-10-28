package edu.augustana.csc285.Egret;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

import autotracking.AutoTrackListener;
import autotracking.AutoTracker;
import datamodel.AnimalTrack;
import datamodel.ProjectData;
import datamodel.TimePoint;
import datamodel.Video;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.augustana.csc285.Egret.Utils;

public class AutoTrackController implements AutoTrackListener {

	@FXML private Button btnBrowse;
	@FXML private ImageView videoView;
	@FXML private Slider sliderVideoTime;
	@FXML private TextField textFieldCurFrameNum;

	@FXML private TextField textfieldStartFrame;
	@FXML private TextField textfieldEndFrame;
	@FXML private Button btnAutotrack;
	@FXML private ProgressBar progressAutoTrack;
	@FXML private Button continueBtn;

	
	private AutoTracker autotracker;
	private ProjectData project;
	private Stage stage;
	private Video video;
	
	@FXML public void initialize() {
		
		sliderVideoTime.valueProperty().addListener((obs, oldV, newV) -> showFrameAt(newV.intValue())); 
	}
	
	public void initializeWithStage(Stage stage) {
		this.stage = stage;
		
		// bind it so whenever the Scene changes width, the videoView matches it
		// (not perfect though... visual problems if the height gets too large.)
		videoView.fitWidthProperty().bind(videoView.getScene().widthProperty());  
	}
	
	public void loadVideo(String filePath, ProjectData data) {
		project = data;
		textfieldStartFrame.setText(""+ data.getVideo().getStartFrameNum());
		textfieldEndFrame.setText(""+ data.getVideo().getEndFrameNum());
		video = project.getVideo();
		sliderVideoTime.setMax(video.getTotalNumFrames()-1);
		showFrameAt(0);
	}
	
	public void showFrameAt(int frameNum) {
		if (autotracker == null || !autotracker.isRunning()) {
			project.getVideo().setCurrentFrameNum(frameNum);
			Image curFrame = Utils.matToJavaFXImage(project.getVideo().readFrame());
			videoView.setImage(curFrame);
			textFieldCurFrameNum.setText(String.format("%05d",frameNum));
			
		}		
	}
	
	@FXML
	public void continueToEditingWindow() throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("EditingWindow.fxml"));
		BorderPane root = (BorderPane)loader.load();
		Scene nextScene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage primary = (Stage) continueBtn.getScene().getWindow();
		primary.setScene(nextScene);
		EditingWindowController nextController = loader.getController();
		nextController.initializeWithProjectData(project);
	}
	
	@FXML
	public void handleStartAutotracking() throws InterruptedException {
		if (autotracker == null || !autotracker.isRunning()) {
			Video video = project.getVideo();
			video.setStartFrameNum(Integer.parseInt(textfieldStartFrame.getText()));
			video.setEndFrameNum(Integer.parseInt(textfieldEndFrame.getText()));
			autotracker = new AutoTracker();
			// Use Observer Pattern to give autotracker a reference to this object, 
			// and call back to methods in this class to update progress.
			autotracker.addAutoTrackListener(this);
			// this method will start a new thread to run AutoTracker in the background
			// so that we don't freeze up the main JavaFX UI thread.
			autotracker.startAnalysis(video);
			btnAutotrack.setText("CANCEL auto-tracking");
		} else {
			autotracker.cancelAnalysis();
			btnAutotrack.setText("Start auto-tracking");
		}
		 
	}

	// this method will get called repeatedly by the Autotracker after it analyzes each frame
	@Override
	public void handleTrackedFrame(Mat frame, int frameNumber, double fractionComplete) {
		Image imgFrame = Utils.matToJavaFXImage(frame);
		// this method is being run by the AutoTracker's thread, so we must
		// ask the JavaFX UI thread to update some visual properties
		Platform.runLater(() -> { 
			videoView.setImage(imgFrame);
			progressAutoTrack.setProgress(fractionComplete);
			sliderVideoTime.setValue(frameNumber);
			textFieldCurFrameNum.setText(String.format("%05d",frameNumber));
		});		
	}

	@Override
	public void trackingComplete(List<AnimalTrack> trackedSegments){
		project.getUnassignedSegments().clear();
		project.getUnassignedSegments().addAll(trackedSegments);

		Platform.runLater(() -> { 
			progressAutoTrack.setProgress(1.0);
			btnAutotrack.setText("Start auto-tracking");
		});	
		
		try {
			File autoTrackData = new File("full_auto_tracker_data");
			project.saveToFile(autoTrackData);
		} catch (FileNotFoundException e) {
		}
				
	}
	
}