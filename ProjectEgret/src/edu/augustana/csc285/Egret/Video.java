package edu.augustana.csc285.Egret;

import  edu.augustana.csc285.Egret.Utils;
import java.io.File;
import java.io.FileNotFoundException;

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
	
	
	//you want these
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
	
	
	
	
	
	
	
	// a timer for acquiring the video stream
	// private ScheduledExecutorService timer;
	private VideoCapture capture = new VideoCapture();
	private String fileName = null;
	private int curFrameNum;
	public double numFrame;

	
	
		
//Delete these:
//	private double frameRate;
//	private double widthPixels;
//	private double heightPixels;
//	private String formatType;	
//	private int numTracks;
//	public double numFrame;
//	private int curFrameNum;
		
		
		
		

		
		
		
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


//	public Video(String fileName) throws FileNotFoundException { 
//		this.fileName = fileName;
//		this.vidCap = new VideoCapture(fileName);
//		if(!vidCap.isOpened()) {
//			throw new FileNotFoundException("Unable to open video file: " + fileName);
//		}
//		
//	}
	
	public double getFrameRate() {
		return this.vidCap.get(Videoio.CV_CAP_PROP_FPS);
	}

	public double getwidthPixels() {
		return this.vidCap.get(Videoio.CV_CAP_PROP_FRAME_WIDTH);
	}

	public double getheightPixels() {
		return this.vidCap.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT);	
	}

	public void setStartFrameNum(double startFrame) {
		startFrameNum = (int) startFrame;
	}
	
	public Integer getStartFrameNum() {
		return startFrameNum;
	}

	public void setEndFrameNum(double endFrame) {
		endFrameNum = (int) endFrame;
	}
	
	public Integer getEndFrameNum() {
		return endFrameNum;
	}

	public void setCurFrameNum(double curFrameNum) {
		this.curFrameNum = (int) curFrameNum;
	}
	
	public Integer getCurFrameNum() {
		return curFrameNum;
	}

	public double getVideoLength() {
		return videoLength;
	}

	public int getCurrentTimeStamp() {
		return currentTimeStamp;
	}
	
	public int getTotalNumberofFrames() {
		return endFrameNum - startFrameNum;
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
			startVideo();
		}
		;
		runSliderSeekBar();
		runJumpTo();
	}

	protected void startVideo() {

		// start the video capture
		this.capture.open(fileName);
		numFrame = this.capture.get(Videoio.CV_CAP_PROP_FRAME_COUNT);
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

}

	
	
