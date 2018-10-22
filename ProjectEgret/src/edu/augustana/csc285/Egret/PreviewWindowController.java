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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
>>>>>>> branch 'master' of https://github.com/AugustanaCSC285Fall18/EgretRepo.git
import javafx.fxml.FXML;
<<<<<<< HEAD
import javafx.scene.canvas.Canvas;
=======
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
>>>>>>> branch 'master' of https://github.com/AugustanaCSC285Fall18/EgretRepo.git
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
<<<<<<< HEAD
import javafx.scene.layout.BorderPane;
=======
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;


public class PreviewWindowController {

    @FXML
    private Button browseBtn;

    @FXML
    private MenuItem advancedSettings;

    @FXML
    private MenuItem closeOption;

    @FXML
    private ImageView currentFrameImage;

    @FXML
  //this was also added with continueBtn but unlike the button this will not conflict with my edits
    private Canvas canvas;

    private Button browseBtn;

    @FXML
    private Slider sliderSeekBar;
    
    @FXML
    private Button loadBtn;

    @FXML
    private Button callibrateBtn;
    
    @FXML
    private TextField startField;

    @FXML
    private TextField endField;

//    private Button continueBtn;
    
    //this was also added with continueBtn but unlike the button this will not conflict with my edits
    ProjectData data;
    
    private Video videoObject;
    private VideoCapture capture = new VideoCapture();


	private int curFrameNum;
	
	private String fileName = null;
	private int curFrameNum;
	public double numFrame;
	
	private double xCord;
	private double yCord;
	ProjectData data = new ProjectData();
	private int animalCounter = 0;
	private Video videoObject;
	private GraphicsContext gc;
    
    @FXML
    private void handleBrowse(ActionEvent event) throws FileNotFoundException {
    	FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		Window mainWindow = currentFrameImage.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		if (chosenFile != null) {
			String fileName = chosenFile.toURI().toString();
			videoObject = new Video(fileName);
			capture = videoObject.getVidCap();
			//data = new ProjectData(fileName);
			//data.getVideo().setVidCap(new VideoCapture());
			startVideo();
		};
		runSliderSeekBar();
    }

	@FXML
	public void initialize() {
		sliderSeekBar.setDisable(true);
		gc = canvas.getGraphicsContext2D();
		runSliderSeekBar();
	}
	
    @FXML
    void handleBrowse(MouseEvent event) {
    	FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Image File");
		Window mainWindow = currentFrameImage.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		if (chosenFile != null) {
			fileName = chosenFile.toURI().toString();
			videoObject = new Video(fileName);
			capture = videoObject.getVidCap();
			startVideo();
		};
		runSliderSeekBar();
		//runJumpTo(); //prints out which frame you are at
    }

	private void runSliderSeekBar() {

		sliderSeekBar.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				//currentFrameArea.appendText("Current frame: " + ((int) Math.round(newValue.doubleValue())) + "\n");

				curFrameNum = (int) Math.round(newValue.doubleValue());
				capture.set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);

				updateFrameView();
			}

		});
	}
	

//	private void runJumpTo() {
//		
//		jumpToFrameArea.textProperty().addListener(new ChangeListener<String>() {
//			@Override
//			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//				int realValue = Integer.parseInt(newValue);
//				currentFrameArea.appendText("Current frame: " + (realValue) + "\n");
//				sliderSeekBar.setValue(realValue);
//				curFrameNum = realValue;
//				capture.set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);
//				updateFrameView();
//			}
//

//		});
//
//	}

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

    @FXML
    void handleCallibration(MouseEvent event) {

    }

    @FXML
    void handleClose(ActionEvent event) {

    }

    @FXML
    void handleLoadVideo(MouseEvent event) {

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
    void handleSettings(ActionEvent event) {

    }
    

    @FXML //throw exception if non number is entered... or prevent it from being entered
    void handleStartTime(KeyEvent event) {

    }
    
    @FXML //throw exception if non number is entered.... or prevent it from being entered
    void handleEndTime(KeyEvent event) {
//    	int i = key.getKeyCode();
//        if (i >= 65 && i <= 90)
//        {
//           ((TextField)event.getSource()).cancelKey();
//        }
    }
    
    protected void startVideo() {

		// start the video capture
		numFrame = this.capture.get(Videoio.CV_CAP_PROP_FRAME_COUNT);
		//totalFrameArea.appendText("Total frames: " + (int) numFrame + "\n"); //prints total number of frames
		sliderSeekBar.setDisable(false);
		//this can be repurposed to allow the client to jump to specific time stamp
		//jumpToFrameArea.setDisable(false); //allows client to jump to specific frame
		updateFrameView();
		sliderSeekBar.setMax((int) numFrame -1);
		sliderSeekBar.setMaxWidth((int) numFrame -1);

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
		if (this.capture.isOpened()) {
			System.out.println("Opened video");
			try {
				// read the current frame
				this.capture.read(frame);
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

