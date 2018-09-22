package edu.augustana.csc285.Egret;

import  edu.augustana.csc285.Egret.Utils;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.core.Mat;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.util.ArrayList;

public class Video {
	
	@FXML
	private ImageView currentFrameImage;
	@FXML
	private Slider sliderSeekBar;
	@FXML
	private TextArea currentFrameArea;
	@FXML
	private TextArea totalFrameArea;
	@FXML
	private TextField jumpToFrameArea;
	
	private double frameRate;
	private double widthPixels;
	private double heightPixels;	

	private double xPixelsPerCm;
	private double yPixelsPerCm;	
	private double startFrameNum;
	private double endFrameNum;
	
	private int videoLength;
	private int currentTimeStamp;
	private String formatType;
	//Integer because the time steps are in seconds most likely else we could maybe do them in milliseconds or something
	private ArrayList<Integer> flagMarkers;
	private int timeInterval;
	private int numTracks;
	

	// a timer for acquiring the video stream
	// private ScheduledExecutorService timer;
	private VideoCapture capture = new VideoCapture();
	private String fileName = null;
	private int curFrameNum;
	public double numFrame;

	public Video() { //what parameters might we need?
		//insert constructor stuff here
		
		
		
	}
	
	@FXML
	public void initialize() {
		sliderSeekBar.setDisable(true);
		jumpToFrameArea.setDisable(true);

	}

	@FXML
	public void handleBrowse() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Image File");
		Window mainWindow = currentFrameImage.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);

		if (chosenFile != null) {
			fileName = chosenFile.toURI().toString();
			captureInfo();
			startVideo();
		}
		;
		runSliderSeekBar();
		runJumpTo();
	}

	protected void captureInfo() {
		this.capture.open(fileName);
		widthPixels = this.capture.get(Videoio.CV_CAP_PROP_FRAME_WIDTH);
		heightPixels = this.capture.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT);		
		frameRate = this.capture.get(Videoio.CV_CAP_PROP_FPS); 
		numFrame = this.capture.get(Videoio.CV_CAP_PROP_FRAME_COUNT);
		startFrameNum = 0;
		endFrameNum = numFrame; 
		

	}
	
	protected void startVideo() {

		// start the video capture
		this.capture.open(fileName);
		totalFrameArea.appendText("Total frames: " + (int) numFrame + "\n");
		sliderSeekBar.setDisable(false);
		jumpToFrameArea.setDisable(false);
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
				currentFrameArea.appendText("Current frame: " + ((int) Math.round(newValue.doubleValue())) + "\n");

				curFrameNum = (int) Math.round(newValue.doubleValue());
				capture.set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);

				updateFrameView();
			}

		});
	}

	private void runJumpTo() {
		
		jumpToFrameArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int realValue = Integer.parseInt(newValue);
				currentFrameArea.appendText("Current frame: " + (realValue) + "\n");
				sliderSeekBar.setValue(realValue);
				curFrameNum = realValue;
				capture.set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);
				updateFrameView();
			}

		});

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

	public double getFrameRate() {
		return frameRate;
	}

	public double getxPixelsPerCm() {
		return xPixelsPerCm;
	}

	public double getyPixelsPerCm() {
		return yPixelsPerCm;
	}

	public double getStartFrameNum() {
		return startFrameNum;
	}

	public double getEndFrameNum() {
		return endFrameNum;
	}

	public int getVideoLength() {
		return videoLength;
	}

	public int getCurrentTimeStamp() {
		return currentTimeStamp;
	}

	
}
