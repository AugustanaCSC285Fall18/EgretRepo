package edu.augustana.csc285.Egret;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import datamodel.ProjectData;
import datamodel.Video;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class PreviewWindowController {

    @FXML
    private ImageView currentFrameImage;

    @FXML
    private Slider sliderSeekBar;

    @FXML
    private MenuItem returnOption;

    @FXML
    private MenuItem closeOption;

    @FXML
    private MenuItem settingsOption;

    @FXML
    private Button browseBtn;

    @FXML
    private Button continueBtn;
    
    ProjectData data;
    
    
    
    @FXML
    private void handleBrowse(ActionEvent event) throws FileNotFoundException {
    	FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		Window mainWindow = currentFrameImage.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		if (chosenFile != null) {
			String fileName = chosenFile.toURI().toString();
			data = new ProjectData(fileName);
			data.getVideo().setVidCap(new VideoCapture());
			startVideo();
		};
    }
    
    
    protected void startVideo() {
 		// start the video capture
		double numFrame = data.getVideo().getVidCap().get(Videoio.CV_CAP_PROP_FRAME_COUNT);
		// totalFrameArea.appendText("Total frames: " + (int) numFrame + "\n"); //prints
		// total number of frames
		sliderSeekBar.setDisable(false);
		// this can be repurposed to allow the client to jump to specific time stamp
		// jumpToFrameArea.setDisable(false); //allows client to jump to specific frame
		updateFrameView();
		sliderSeekBar.setMax((int) numFrame - 1);
		sliderSeekBar.setMaxWidth((int) numFrame - 1);
 	}
    
    @FXML
    void exitWindow(ActionEvent event) {

    }

    @FXML
    void openExportWindow(MouseEvent event) {

    }

    @FXML
    void openSettingsWindow(ActionEvent event) {

    }
    
    @FXML
    void openEditingWindow(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("EditingWindow.fxml"));
		BorderPane root = (BorderPane)loader.load();
		EditingWindowController nextController = loader.getController();
		
		Scene nextScene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		Stage primary = (Stage) continueBtn.getScene().getWindow();
		primary.setScene(nextScene);
    }

    @FXML
    void returnToPrevWindow(MouseEvent event) {

    }
    
    public ImageView getCurrentFrameImage() {
    	return currentFrameImage;
    }

 	/**
	 * Get a frame from the opened video stream (if any)
	 *
	 * @return the {@link Mat} to show
	 */
	private Mat grabFrame() {
		// init everything
		Mat frame = new Mat();
 		// check if the capture is open
		System.out.println("Create new mat");
		if (this.data.getVideo().getVidCap().isOpened()) {
			System.out.println("Opened video");
			try {
				// read the current frame
				this.data.getVideo().getVidCap().read(frame);
 			} catch (Exception e) {
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
			
		} else {
			System.out.println("Failed to open video");
		}
 		return frame;
	}
	
    public void updateFrameView() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// effectively grab and process a single frame
				Mat frame = grabFrame();
				// convert and show the frame
				Image imageToShow = Utils.mat2Image(frame);
				currentFrameImage.setImage(imageToShow);
			}
		});
 	}
}
