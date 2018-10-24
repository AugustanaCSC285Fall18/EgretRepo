package edu.augustana.csc285.Egret;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics;
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

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;


public class PreviewWindowController {

    @FXML
    private MenuItem advancedSettings;

    @FXML
    private MenuItem closeOption;
    
    @FXML
    private ComboBox<?> chicksComboBox;

    @FXML
    private Button addChickBtn;

    @FXML
    private Button removeChickBtn;

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
    
    @FXML
    private ChoiceBox<?> timeStepBox;

//    private Button continueBtn;
    
   private VideoCapture capture = new VideoCapture();
	
	//private String fileName = null;
	private int curFrameNum;
	public double numFrame;
	
	private double xCord;
	private double yCord;
	ProjectData data = new ProjectData();
	private int animalCounter = 0;
	private GraphicsContext gc;
	
	Point upperLeftCorner = new Point();
	Point lowerRightCorner = new Point();
	Point origin = new Point();
	int step = 0;
	Alert alert = new Alert(AlertType.INFORMATION);
	
	//doesn't currently work
//    @FXML
//    private void handleBrowse(MouseEvent event) throws FileNotFoundException {
//    	FileChooser fileChooser = new FileChooser();
//		fileChooser.setTitle("Open Video File");
//		Window mainWindow = currentFrameImage.getScene().getWindow();
//		File chosenFile = fileChooser.showOpenDialog(mainWindow);
//		if (chosenFile != null) {
//			String fileName = chosenFile.toURI().toString();
//			videoObject = new Video(fileName);
//			capture = videoObject.getVidCap();
//			//data = new ProjectData(fileName);
//			//data.getVideo().setVidCap(new VideoCapture());
//			startVideo();
//		};
//		runSliderSeekBar();
//    }
    
	//various methods
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
	
	//might use this
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
	
	public void browseForVideoFile() throws FileNotFoundException{
    	FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		Window mainWindow = currentFrameImage.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		if (chosenFile != null) {
			String fileName = chosenFile.toURI().toString();
			data.setVideo(new Video(fileName));
			startVideo();
		};
		runSliderSeekBar();
		//runJumpTo(); //prints out which frame you are at
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
    
    //doesn't currently work
// 	/**
//	 * Get a frame from the opened video stream (if any)
//	 *
//	 * @return the {@link Mat} to show
//	 */
//	private Mat grabFrame() {
//		// init everything
//		Mat frame = new Mat();
// 		// check if the capture is open
//		System.out.println("Create new mat");
//		if (this.capture.isOpened()) {
//			System.out.println("Opened video");
//			try {
//				// read the current frame
//				this.capture.read(frame);
// 			} catch (Exception e) {
//				// log the error
//				System.err.println("Exception during the image elaboration: " + e);
//			}
//			
//		} else {
//			System.out.println("Failed to open video");
//		}
// 		return frame;
//	}
	
	/**
	 * Get a frame from the opened video stream (if any)
	 * @return the {@link Mat} to show
	 */
	private Mat grabFrame() {
		// init everything
		Mat frame = new Mat();
 		// check if the capture is open
		if (this.data.getVideo().getVidCap().isOpened()) {
			try {
				// read the current frame
				this.data.getVideo().getVidCap().read(frame);
 				// if the frame is not empty, process it to black and white color
				/*
				 * if (!frame.empty()) { Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
				 * }
				 */
 			} catch (Exception e) {
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
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
    
    public void setBoxArena() {
    	alert.initModality(Modality.WINDOW_MODAL);
		alert.setTitle("Callibration");
		step = 1;
		alert.setHeaderText("Please initialize the box's area.");
		alert.setContentText("Please select the upper left corner of the box.");
		alert.showAndWait();
    }
    
    public void setLengthMeasurements() {
    	
    }
    
    public void setEmptyFrame() {
    	
    }
    
    public void setTimeStep() {
    	
    }

//note for another time ProgressMonitor in JOption Pane
	
	//event handlers
    @FXML
    void handleAddChickBtn(MouseEvent event) {

    }
    
    @FXML
    private void handleBrowse(MouseEvent event) throws FileNotFoundException {
    	browseForVideoFile();
    }
	
	@FXML
    void handleCallibration(MouseEvent event) {
    	setBoxArena();
    	//setLengthMeasurements();
    	//setEmptyFrame();
    }
    
    @FXML
    void handleCanvasClick(MouseEvent event) {
    	Rectangle rect = new Rectangle();
    	if (step==1) {
    		System.out.println(step);
    		upperLeftCorner.setLocation(event.getX(), event.getY());
    		rect.add(upperLeftCorner);
    		System.out.println(upperLeftCorner.getX() + " " + upperLeftCorner.getY());
    		
    		step=2;
    		alert.setContentText("Please select the lower right hand corner of the box");
    	}else if (step==2) {
    		System.out.println(step);
    		lowerRightCorner.setLocation(event.getX(), event.getY());
    		System.out.println(lowerRightCorner.getX() + " " + lowerRightCorner.getY());
    		rect.add(lowerRightCorner);
    		data.getVideo().setArenaBounds(rect);
    		
    		step=3;
    		alert.setContentText("Please select where you would like your origin to be located.");
    	}else if (step==3) {
    		System.out.println(step);
    		origin.setLocation(event.getX(), event.getY());
    		System.out.println(origin.getX() + " " + origin.getY());
    		data.getVideo().setOriginPoint(origin);
    	}
    }

    @FXML
    void handleClose(ActionEvent event) {
    	Platform.exit();
    }
    
  //Avery... still working on it
    @FXML //throw exception if non number is entered.... or prevent it from being entered
    void handleEndTime(KeyEvent event) {
//    	int i = key.getKeyCode();
//        if (i >= 65 && i <= 90)
//        {
//           ((TextField)event.getSource()).cancelKey();
//        }
    }
    
    @FXML
	public void initialize() {
		sliderSeekBar.setDisable(true);
		gc = canvas.getGraphicsContext2D();
		runSliderSeekBar();
	}

    //replaces video in window... need to make sure 
    @FXML
    void handleLoadVideo(MouseEvent event) throws FileNotFoundException {
    	browseForVideoFile();
    }
    
    @FXML //throw exception if non number is entered... or prevent it from being entered
    void handleStartTime(KeyEvent event) {

    }

    @FXML
    void handleSettings(ActionEvent event) {

    }
    
    @FXML
    void handleRemoveChickBtn(MouseEvent event) {

    }
    
    @FXML
    void openEditingWindow(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("EditingWindow.fxml"));
		BorderPane root = (BorderPane)loader.load();
		EditingWindowController nextController = loader.getController();
		
		Scene nextScene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		//Stage primary = (Stage) continueBtn.getScene().getWindow();
		//primary.setScene(nextScene);
    }
}

