package edu.augustana.csc285.Egret;

import java.io.File;
import java.io.FileNotFoundException;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import datamodel.AnimalTrack;
import datamodel.ProjectData;
import datamodel.TimePoint;
import datamodel.Video;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
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
	private Canvas canvas;

	@FXML
	private Slider sliderSeekBar;

	// a timer for acquiring the video stream
	// private ScheduledExecutorService timer;
	private VideoCapture capture = new VideoCapture();
	private String fileName = null;
	private int curFrameNum;
	public double numFrame;

	private double xCord;
	private double yCord;
	ProjectData data = new ProjectData();
	private int animalCounter = 0;
	private Video videoObject;
	private GraphicsContext gc;
	private boolean modifyToggleActive = false;
	private int drawX = 5;
	private int drawY = 5;

	private TimePoint previousPoint;

	@FXML
	void closeWindow(ActionEvent event) {

	}

	@FXML
	void frameStepBack(MouseEvent event) {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		animalCounter = 0;
		curFrameNum -= Math.floor(videoObject.getFrameRate() * 3);
		capture.set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);
		updateFrameView();
		frameAdjustHelper();
	}

	@FXML
	void frameStepForward(MouseEvent event) {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		animalCounter = 0;
		// change hard coded number into what the user wants to measure
		curFrameNum += Math.floor(videoObject.getFrameRate() * 3);
		capture.set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);
		updateFrameView();
		frameAdjustHelper();
	}

	protected void frameAdjustHelper() {
		AnimalTrack currentAnimal = data.getAnimalTracksList().get(animalCounter);
		// modifyToggleActive = currentAnimal.isTimePointAtTime(curFrameNum);
		// System.out.println(modifyToggleActive);
		if (currentAnimal.hasTimePointAtTime(curFrameNum)) {
			for (int i = 0; i < data.getAnimalTracksList().size(); i++) {
				TimePoint curAnimalPoint = data.getAnimalTracksList().get(i).getTimePointAtTime(curFrameNum);
				gc.fillOval(curAnimalPoint.getX(), curAnimalPoint.getY(), drawX, drawY);
			}
		}
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
		modifyToggleActive = !modifyToggleActive;
		animalCounter = 0;
	}

	@FXML // TODO: figure out mechanism for how to modify animal data
	void addOrModifyDataPoint(MouseEvent event) {
		xCord = event.getX();
		yCord = event.getY();
		Point centerPoint = new Point(xCord, yCord);
		AnimalTrack currentAnimal = data.getAnimalTracksList().get(animalCounter);

		if (modifyToggleActive) {
			modifyDataPointHelper(currentAnimal, centerPoint);
		} else {
			addDataPointHelper(currentAnimal, centerPoint);
		}
		animalCounter++;
		System.out.println(data.getAnimalTracksList());
		gc.fillOval(xCord, yCord, drawX, drawY);
	}

	void modifyDataPointHelper(AnimalTrack currentAnimal, Point newPoint) {
		previousPoint = currentAnimal.getTimePointAtTime(curFrameNum);
		System.out.println("Old point: " + previousPoint);
		gc.clearRect(previousPoint.getX(), previousPoint.getY(), drawX, drawY);
		currentAnimal.setTimePointAtTime(newPoint, curFrameNum);
		System.out.println("New Point: " + newPoint);
	}

	void addDataPointHelper(AnimalTrack currentAnimal, Point newPoint) {
		currentAnimal.addLocation(newPoint, curFrameNum);
	}

	@FXML
	void undoEdit(MouseEvent event) {
		if (animalCounter > 0) {
			animalCounter--;
			AnimalTrack currentAnimal = data.getAnimalTracksList().get(animalCounter);

			gc.clearRect(currentAnimal.getX(), currentAnimal.getY(), drawX, drawY);
			currentAnimal.removeLocation();
			for (int i = 0; i < data.getAnimalTracksList().size(); i++) {
				System.out.println(data.getAnimalTracksList().get(i));
			}
		} else {
			Popup popup = new Popup();
			// TODO: make a popup that says nothing to undo instead of nothing happening.

		}
	}

	@FXML
	public void initialize() {
		sliderSeekBar.setDisable(true);
		gc = canvas.getGraphicsContext2D();
		runSliderSeekBar();
	}

	@FXML
	public void handleBrowse() throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Image File");
		Window mainWindow = currentFrameImage.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		if (chosenFile != null) {
			fileName = chosenFile.toURI().toString();
			videoObject = new Video(fileName);
			capture = videoObject.getVidCap();
			startVideo();
		}
		;
		runSliderSeekBar();
		// runJumpTo(); //prints out which frame you are at
	}

	protected void startVideo() {

		// start the video capture
		numFrame = this.capture.get(Videoio.CV_CAP_PROP_FRAME_COUNT);
		// totalFrameArea.appendText("Total frames: " + (int) numFrame + "\n"); //prints
		// total number of frames
		sliderSeekBar.setDisable(false);
		// this can be repurposed to allow the client to jump to specific time stamp
		// jumpToFrameArea.setDisable(false); //allows client to jump to specific frame
		updateFrameView();
		sliderSeekBar.setMax((int) numFrame - 1);
		sliderSeekBar.setMaxWidth((int) numFrame - 1);

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
				// currentFrameArea.appendText("Current frame: " + ((int)
				// Math.round(newValue.doubleValue())) + "\n");

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