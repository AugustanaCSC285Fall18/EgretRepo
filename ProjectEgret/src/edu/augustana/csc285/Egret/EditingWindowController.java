package edu.augustana.csc285.Egret;

import java.io.File;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import datamodel.ProjectData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class EditingWindowController {

	@FXML
	private AnchorPane anchorPane;
	
	@FXML
    private MenuItem closeOption;

    @FXML
    private MenuItem saveOption;

    @FXML
    private MenuItem undoOption;

    @FXML
    private MenuItem redoOption;

    @FXML
    private ToggleButton modifyToggleBtn;

    @FXML
    private Button undoBtn;

    @FXML
    private Button redoBtn;

    @FXML
    private Button previousFrameBtn;

    @FXML
    private Button nextFrameBtn;

    @FXML
    private Button finishEditingBtn;

    @FXML
    private ImageView currentFrameImage;

    @FXML
    private Slider sliderSeekBar;
    
	// a timer for acquiring the video stream
	// private ScheduledExecutorService timer;
	private VideoCapture capture = new VideoCapture();
	private String fileName = null;
	private int curFrameNum;
	public double numFrame;
	
	private double xPixelsPerCm;
	private double yPixelsPerCm;	
	private Integer startFrameNum;
	//private Integer curFrameNum;
	private Integer endFrameNum;
	private Integer totalFrames;
	
	private double videoLength;
	private int currentTimeStamp; //CHANGE: to frame

	private ArrayList<Integer> flagMarkers;
	private double timeInterval;
	private VideoCapture vidCap = new VideoCapture();
	
	private double xCord;
	private double yCord;
	private int animals = 3;
    
    

    @FXML
    void closeWindow(ActionEvent event) {

    }

    @FXML
    void frameStepBack(MouseEvent event) {

    }

    @FXML
    void frameStepForward(MouseEvent event) {

    }

    @FXML
    void openPopUp(MouseEvent event) {

    }

    @FXML
    void redoEdit(MouseEvent event) {

    }

    @FXML
    void saveProject(ActionEvent event) {

    }

    @FXML
    void toggleManualEdit(MouseEvent event) {

    	currentFrameImage.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
    		xCord = e.getSceneX();
    		yCord = e.getSceneY();
    		Point centerPoint = new Point(xCord,yCord);
    	 });
    }

    @FXML
    void undoEdit(MouseEvent event) {

    }
    
	@FXML
	public void initialize() {
		ProjectData data = new ProjectData();
		sliderSeekBar.setDisable(true);
		File newFile = new File("S:\\CLASS\\CS\\285\\sample_videos\\sample1.mp4");
		File chosenFile = newFile;
		if (chosenFile != null) {
			fileName = chosenFile.toURI().toString();
			startVideo();
		};
		runSliderSeekBar();
		//jumpToFrameArea.setDisable(true);
		
	}
	
	@FXML
	public void handleBrowse() {
//		FileChooser fileChooser = new FileChooser();
//		fileChooser.setTitle("Open Image File");
//		Window mainWindow = currentFrameImage.getScene().getWindow();
//		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		File newFile = new File("S:\\CLASS\\CS\\285\\sample_videos\\sample1.mp4");
		File chosenFile = newFile;
		if (chosenFile != null) {
			fileName = chosenFile.toURI().toString();
			startVideo();
		}
		;
		runSliderSeekBar();
		//runJumpTo();
	}

	protected void startVideo() {

		// start the video capture
		this.capture.open(fileName);
		numFrame = this.capture.get(Videoio.CV_CAP_PROP_FRAME_COUNT);
		//totalFrameArea.appendText("Total frames: " + (int) numFrame + "\n");
		sliderSeekBar.setDisable(false);
		//jumpToFrameArea.setDisable(false);
		updateFrameView();
		sliderSeekBar.setMax((int) numFrame -1);
		sliderSeekBar.setMaxWidth((int) numFrame -1);

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
		if (this.capture.isOpened()) {
			try {
				// read the current frame
				this.capture.read(frame);

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
    
   
	
	
	
	
	
	
	
	
	
	

}

