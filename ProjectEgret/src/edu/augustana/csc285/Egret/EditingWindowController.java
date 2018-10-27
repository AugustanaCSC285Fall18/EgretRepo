package edu.augustana.csc285.Egret;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.videoio.Videoio;

import Analysis.Analysis;
import datamodel.AnimalTrack;
import datamodel.ProjectData;
import datamodel.TimePoint;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class EditingWindowController {
	//FXML Objects
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private MenuItem closeOption;
	@FXML
	private MenuItem saveOption;
	@FXML
	private MenuItem undoOption;
	@FXML
	private ToggleButton modifyToggleBtn;
	@FXML
	private Button undoBtn;
	@FXML
	private Button reviewTrackBtn;
	@FXML
	private Button saveBtn;
	@FXML
	private Button previousFrameBtn;
	@FXML
	private Button nextFrameBtn;
	@FXML
	private Button finishEditingBtn;
	@FXML
	ImageView currentFrameImage;
	@FXML
	private Canvas canvas;
	@FXML
	private Slider sliderSeekBar;
	@FXML
	private TextField timeField;
	@FXML
	private Label timeLabel;
	@FXML
	private ChoiceBox<String> pickUnassignedAnimalTrackBtn = new ChoiceBox<>();
	@FXML
	private ComboBox<String> animalTrackObjectComboBox;
	@FXML 
	private TextField timeStepField;
	private GraphicsContext gc;
	
	//Data Fields 
	public double totalNumFrame;
	static ProjectData data;
	private int animalCounter = 0;
	private TimePoint previousPoint;
	private boolean modifyToggleActive = false;
	
	// Constants for Drawing and Frame Changing
	private static final int drawX = 5;
	private static final int drawY = 5;
	
	
	// from calibration: random assignment at the moment
	private int totalAmountOfAnimals = 2;
	private int startFrame = 850;
	private int endFrame = 1500;
	private int oldCurrentFrame = 0;
	private int frameJumpModifier = 2;
	
	// Fields that make the program run faster rather
	// than continuously calling for an int value. 
	private static int frameRate;
	private int currentFrameNumber = startFrame;

	/**
	 * 
	 * @throws FileNotFoundException
	 */
	void loadData() throws FileNotFoundException {
		File dataFile = new File("full_auto_tracker_data");
		data = ProjectData.loadFromFile(dataFile);
		for (int i = 0; i < data.getUnassignedSegments().size(); i++) {
			if (data.getUnassignedSegments().get(i).getNumPoints() < frameRate * frameJumpModifier * 2) {
				data.getUnassignedSegments().remove(i);
				i--;
			}
		}
		
		// Prints out unassigned tracks
		for (AnimalTrack track : data.getUnassignedSegments()) {
			System.out.println(track.getName() + "Num of Points " + track.getNumPoints() + " first point: "
					+ track.getFirstTimePoint() + " last point: " + track.getFinalTimePoint());
		}
	}

	/*
	 * Aims to draw a circle around the track that is currently being focused by the
	 * animalTrack combo box. Currently does not work because the field immediately
	 * has nothing in it, unsure how to fix. TODO: fix this too.
	 */
//	public void showSpecifiedAnimalTrack() {
//		int curAnimalIndex = animalTrackObjectComboBox.getSelectionModel().getSelectedIndex();
//		AnimalTrack curAnimal = data.getAnimalTracksList().get(curAnimalIndex);
//		gc.setFill(data.getColorArrayForAnimalTracks().get(curAnimalIndex));
//		gc.strokeOval(curAnimal.getTimePointAtTime(currentFrameNumber).getX() - (drawX * 3), curAnimal.getTimePointAtTime(currentFrameNumber).getY() - (drawY * 3), drawX * 3, drawY *3);
//	}

	/**
	 * Updates the ImageView to the given frame number.
	 * @param numOfFrame - given frame number
	 */
	void jumpToFrame(int numOfFrame) {
		System.out.println(canvas.getWidth());
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		currentFrameNumber = numOfFrame;
		frameJumpHelper();
		//showSpecifiedAnimalTrack();

	}

	/**
	 * Changes the current frame based on a given frame step. 
	 * @param numOfFrameChange - how many frames to move
	 */
	void frameChanger(double numOfFrameChange) {
		if (currentFrameNumber + numOfFrameChange > endFrame) {
			animalCounter++;
			if (animalCounter > totalAmountOfAnimals) {
				saveFinishedProject();
				makeAlert("Tracking Complete","You have completed the tracking! Your file has been saved as \"" + data.getVideo().getFilePathJustName() + "\" Hit the finish button to recieve csv files and analysis");
			} else {
				setAnimalTrackObjectComboBox();
				makeAlert("Tracking New Chicken","You are now adding data for chicken " + data.getAnimalTracksList().get(animalCounter).getName());
				jumpToFrame(startFrame);
			}
		} else {
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			currentFrameNumber += numOfFrameChange;
			frameJumpHelper();
		}
	}
	
	/**
	 * Allows the user to review the chosen track with a click of a button. 
	 */
	void reviewTimeChanger() {
		if (currentFrameNumber < endFrame - frameRate) {
			currentFrameNumber += frameRate;
			frameReviewJumpHelper();
			new java.util.Timer().schedule(
				    new java.util.TimerTask() {
				        @Override
				        public void run() {
				            Platform.runLater(() -> reviewTimeChanger());
				        }
				    }, 
				    50);   // the delay time in milliseconds
		} else {
			jumpToFrame(oldCurrentFrame);
			makeAlert("Review Complete", "Reviewing process is finished");
	
		}
	}
	
	/**
	 * Changes the frame back a static amount. 
	 */
	void frameStepBack() {
		frameChanger(-frameRate);

	}
	
	/**
	 * The FXML method to get a mouse click from the previous button
	 * to go back in the video. 
	 * @param event - click on the Previous Button
	 */
	@FXML
	void frameStepBack(MouseEvent event) {
		frameStepBack();
	}

	/**
	 * Changes the frame forward a static amount. 
	 */
	void frameStepForward() {
		frameChanger(frameRate);
	}

	/**
	 * The FXML method to get a mouse click from the next button
	 * to go back in the video. 
	 * @param event - click on the Next button
	 */
	@FXML
	void frameStepForward(MouseEvent event) {
		frameStepForward();
	}

	/**
	 * Draws the point of the current animal at the current frame. 
	 */
	protected void drawPointsAtCurrentFrame() {
		setAnimalCounter();
		AnimalTrack currentAnimal = data.getAnimalTracksList().get(animalCounter);
		if (currentAnimal.hasTimePointAtTime(currentFrameNumber)) {
			TimePoint curAnimalPoint = currentAnimal.getTimePointAtTime(currentFrameNumber);
			gc.setStroke(Color.GREEN);
			gc.fillOval(curAnimalPoint.getX(), curAnimalPoint.getY(), drawX, drawY);
		}
	}
	
	/**
	 * Clears the canvas, sets the video to the new current frame number, and displays the current
	 * points, and the past/future points within a certain interval. Also updates the Time Box and
	 * slider bar to be the new time. 
	 */
	void frameJumpHelper() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		data.getVideo().getVidCap().set(Videoio.CAP_PROP_POS_FRAMES, currentFrameNumber);
		updateFrameView();
		drawPointsAtCurrentFrame();
		displayFutureTracks();
		displayPastTracks();
		updateTextAndSlider();
	}
	
	/**
	 * Clears the canvas, sets the video to the new current frame number, and displays the current
	 * points, and the past points within a certain interval. Also updates the Time Box and
	 * slider bar to be the new time. 
	 */
	void frameReviewJumpHelper() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		data.getVideo().getVidCap().set(Videoio.CAP_PROP_POS_FRAMES, currentFrameNumber);
		updateFrameView();
		drawPointsAtCurrentFrame();
		displayPastTracks();
		updateTextAndSlider();
	}

	/**
	 * Checks for a click on the Manual Edit button.
	 * @param event - mouse click on the modify event button
	 */
	@FXML
	void toggleManualEdit(MouseEvent event) {
		modifyToggleActive = !modifyToggleActive;
	}

	/**
	 * Gets a click on the canvas and creates a point and adds it to the
	 * current AnimalTrack
	 * @param event - click on the canvas
	 */
	@FXML
	void addOrModifyDataPoint(MouseEvent event) {
		double xCord = event.getX();
		double yCord = event.getY();
		Point centerPoint = new Point(xCord, yCord);
		setAnimalCounter();
		AnimalTrack currentAnimal = data.getAnimalTracksList().get(animalCounter);
		if (modifyToggleActive) {
			if(currentAnimal.getTimePointAtTime(currentFrameNumber) != null) {
				previousPoint = currentAnimal.getTimePointAtTime(currentFrameNumber);
				modifyDataPointHelper(currentAnimal, centerPoint, previousPoint);
				gc.fillOval(xCord, yCord, drawX, drawY);
			} else {
				setAnimalTrackObjectComboBox();
				makeAlert("Modify Location Error", "No data point to modify");
			}
			
		} else {
			addDataPointHelper(currentAnimal, centerPoint);
			frameStepForward();
			gc.setFill(Color.DARKGREEN);
			gc.fillOval(xCord, yCord, drawX, drawY);
		}
	}

	/*
	 * Takes a new point and changes a previous point (Modifies the data) of the current AnimalTrack
	 */
	void modifyDataPointHelper(AnimalTrack currentAnimal, Point newPoint, TimePoint undoPoint) {
		gc.clearRect(previousPoint.getX(), previousPoint.getY(), drawX, drawY);
		currentAnimal.setTimePointAtTime(newPoint, currentFrameNumber);
	}

	/*
	 * Adds a new data point to the current AnimalTrack
	 */
	void addDataPointHelper(AnimalTrack currentAnimal, Point newPoint) {
		currentAnimal.addLocation(newPoint, currentFrameNumber);
	}
	
	/**
	 * Adds an unassigned segment to the current AnimalTrack. Gets the track value
	 * from the unassigned Track Choice box and the AnimalTrack from the Chick Combo Box
	 * @param event - mouse click on the Choice box for unassigned tracks
	 */
	@FXML
	void addTrack(MouseEvent event) {
		if (pickUnassignedAnimalTrackBtn.getValue() != null) {
			String chosenValueByUser = new String(pickUnassignedAnimalTrackBtn.getValue());
			String nameOfCurrentTrack = ("");
			AnimalTrack chosenTrack = null;
			boolean foundTrack = false;
			int trackCounter = 1;
			int i = 0;
			int chosenTrackNumber = 0;
			while (i < data.getUnassignedSegments().size() && !foundTrack) {
				AnimalTrack currentTrack = data.getUnassignedSegments().get(i);
				// displays a track that is within 10 times the frame rate
				if (Math.abs(currentTrack.getFirstTimePoint().getFrameNum() - currentFrameNumber) < frameRate
						* frameJumpModifier * 5) {
					nameOfCurrentTrack = ("Track " + trackCounter);
					trackCounter++;
				}
				if (nameOfCurrentTrack.equals(chosenValueByUser)) {
					chosenTrack = currentTrack;
					chosenTrackNumber = i;
					foundTrack = true;
				}
				i++;
			}
			setAnimalCounter();
			data.getAnimalTracksList().get(animalCounter).addTrackSegment(chosenTrack);
			data.removeUnassignedSegment(chosenTrackNumber);
			frameChanger(chosenTrack.getFinalTimePoint().getFrameNum() + 1 - currentFrameNumber);
		}
	}

	/**
	 * Displays the unassigned segments that are within a certain amount. 
	 */
	void displayFutureTracks() {
		int trackCounter = 1;
		ObservableList<String> listOfTracksDisplayed = FXCollections.observableArrayList();
		for (int i = 0; i < data.getUnassignedSegments().size(); i++) {
			AnimalTrack currentTrack = data.getUnassignedSegments().get(i);
			// displays a track that is within x times the frame rate
			if (Math.abs(currentTrack.getFirstTimePoint().getFrameNum() - currentFrameNumber) < frameRate
					* frameJumpModifier * 5) {
				for (int j = 0; j < currentTrack.getNumPoints(); j++) {
					TimePoint currentTimePoint = currentTrack.getTimePointAtIndex(j);
					gc.setFill(Color.BLACK);
					gc.fillOval(currentTimePoint.getX(), currentTimePoint.getY(), drawX, drawY);
				}
				gc.setFill(Color.DARKBLUE);
				gc.fillText("Track " + trackCounter, currentTrack.getFirstTimePoint().getX() + 10,
						currentTrack.getFirstTimePoint().getY() + 10);
				listOfTracksDisplayed.add("Track " + trackCounter);
				trackCounter++;
			}
		}
		if (listOfTracksDisplayed != null) {
			pickUnassignedAnimalTrackBtn.setItems(listOfTracksDisplayed);
		}
	}

	/**
	 * Draws the past tracks of the current animal within a certain interval. 
	 */
	void displayPastTracks() {
		setAnimalCounter();
		AnimalTrack currentTrack = data.getAnimalTracksList().get(animalCounter);
		if( currentTrack.getTimePointAtTime(currentFrameNumber) != null) {
			TimePoint currentTP = currentTrack.getTimePointAtTime(currentFrameNumber);
			gc.setFill(Color.AQUAMARINE);
			gc.fillOval(currentTP.getX(), currentTP.getY(), drawX, drawY);
		}
	}

	/**
	 * Exits the program when the window is closed. 
	 * @param event
	 */
	@FXML
	void closeWindow(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	void redoEdit(MouseEvent event) {
	}

	/**
	 * Undos the most recent point. Only works during the adding points section. 
	 * @param event
	 */
	@FXML
	void undoEdit(MouseEvent event) {
		if(!modifyToggleActive) {
			setAnimalCounter();
			AnimalTrack currentAnimal = data.getAnimalTracksList().get(animalCounter);
			if (currentAnimal.getTimePointAtIndex(currentAnimal.getNumPoints() - 1) != null) {
				TimePoint previousTP = currentAnimal.getTimePointAtIndex(currentAnimal.getNumPoints() - 1);
				gc.clearRect(previousTP.getX(), previousTP.getY(), drawX, drawY);
				currentAnimal.removeLocation(currentAnimal.getNumPoints() - 1);
				if(currentAnimal.getFinalTimePoint() != null) {
					jumpToFrame(currentAnimal.getFinalTimePoint().getFrameNum());
				}
			} else {
				makeAlert("Undo Error", "No more points to undo for this chicken." );
			}
		}else {
			makeAlert("Undo Error", "Undo Does Not Work in Modify Mode" );
		}
	}
	
	/**
	 * goes through the video displaying the points of the current animal track. Once it goes through the video,
	 * it goes back to where the user first clicked the review button.
	 * @param event - click on the Review button 
	 * @throws InterruptedException - if the thread that goes through the video ends. 
	 */
	@FXML 
	void reviewTrack(MouseEvent event) throws InterruptedException {
		oldCurrentFrame = currentFrameNumber;
		currentFrameNumber = startFrame;
		reviewTimeChanger();
	}

	/**
	 * Allows the user to change the frame view using the text field 
	 */
	@FXML
	public void changeFrameWithTextField() {
		int newTime = getSecondsFromMinuteSeconds();
		int maxTime = (int) Math.floor((data.getVideo().getTotalNumFrames() / frameRate));
		if (newTime > maxTime) {
			makeAlert("Time Change Error", "The video is between 0 and " + maxTime + " seconds long.");
		}
		int frameNumber = (int) Math.round((newTime * frameRate));
		jumpToFrame(frameNumber);
	}

	/**
	 * Puts all of the AnimalTrack names into the AnimalTrack Combo Box
	 */
	public void initializeAnimalTrackObjectComboBox() {
		for (int i = 0; i <= totalAmountOfAnimals; i++) {
			String name = data.getAnimalTracksList().get(i).getName();
			animalTrackObjectComboBox.getItems().add(name);
		}
		animalTrackObjectComboBox.getSelectionModel().select(0);
	}
	
	/**
	 * Sets the animal counter to the current animal Track
	 */
	public void setAnimalCounter() {
		animalCounter = animalTrackObjectComboBox.getSelectionModel().getSelectedIndex();
	}
	
	
	public void setAnimalTrackObjectComboBox() {
		animalTrackObjectComboBox.getSelectionModel().select(animalCounter);
	}

	/**
	 * Allows the user to use the slider bar to change the image view. 
	 */
	@FXML
	public void runSliderSeekBar() {
		int frameNum = (int) sliderSeekBar.getValue();
		jumpToFrame(frameNum);
	}

	/**
	 * Updates the Text field with the time and the slider bar to the correct frame.
	 */
	public void updateTextAndSlider() {
		timeField.setText("" + getTimeInMinuteSecond());
		sliderSeekBar.setValue(currentFrameNumber);
	}
	
	/**
	 * Allows the user to have shortcuts when editing the data.
	 * D - Presses the next button
	 * A - Presses the previous button
	 * Q - Presses the undo button
	 * @param e - the key that was pressed
	 */
    @FXML
    public void keyPressed(KeyEvent e) {
    	if(e.getCode() == KeyCode.D) {
    		frameStepForward();
    	}else if(e.getCode() == KeyCode.A) {
    		frameStepBack();
    	}else if(e.getCode() == KeyCode.Q) {
    		undoEdit(null);
    	}
    }
    
    /*
     * Makes the alert with a given title and message. 
     */
    private void makeAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
		// Cited https://code.makery.ch/blog/javafx-dialogs-official/
    }

//	private void loadWelcomeWindow() {
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeWindow.fxml"));
//		try {
//			AnchorPane root = (AnchorPane) loader.load();
//			Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//			Stage primary = (Stage) saveBtn.getScene().getWindow();
//			primary.setScene(scene);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}
    
    /*
     * Gets the time from the current frame number in minutes and seconds MM:SS formate
     */
	private String getTimeInMinuteSecond() {
		int startTime = (int)(currentFrameNumber / data.getVideo().getFrameRate());
		int startTimeMinutes = startTime / 60;
		int startTimeSeconds = startTime % 60;
		return startTimeMinutes + ":" + startTimeSeconds;
	}
	
	/*
	 * Gets the seconds from MM:SS format.
	 */
	private int getSecondsFromMinuteSeconds() {
		int colonIndex = timeField.getText().indexOf(':');
		int timeInSeconds = (Integer.parseInt(timeField.getText().substring(0, colonIndex)) * 60) + Integer.parseInt(timeField.getText().substring(colonIndex + 1)); 
		return timeInSeconds;
	}
	
	/**
	 * Saves the project when the finish button is pressed. 
	 */
	void saveFinishedProject() {
		File finalDataFile = new File(data.getVideo().getFilePathJustName());
		try {
			data.saveToFile(finalDataFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();	
		}
	}
	
	/**
	 * Analyzes the project data
	 */
	@FXML
	void analyzeProjectData() {
		try {
			Analysis.runAnalysis(data);
			makeAlert("Analysis Complete", "CSV Files and Analysis have been added to your computer.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Saves the unfinished project data after the user has finished filling in the gaps from the 
	 * auto tracker.  
	 */
	@FXML
	private void saveUnfinishedProject() {
		File tempDataFile = new File("Unfinished" + data.getVideo().getFilePathJustName());
		try {
			data.saveToFile(tempDataFile);
			makeAlert("Tracking Saved","You have saved your project! Your file has been saved as \"Unfinished " + data.getVideo().getFilePathJustName() + "\". Hit the finish button to recieve CSV files and analysis");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize() throws FileNotFoundException {
	}

	/**
	 * Initializes the EditingWindow with the ProjectData
	 * @param projectData - the project data of the current project
	 * @throws FileNotFoundException if the video from project data was missing. 
	 */
	public void initializeWithProjectData(ProjectData projectData) throws FileNotFoundException {
		data = projectData;
		gc = canvas.getGraphicsContext2D();
		// runSliderSeekBar();
		frameRate = (int) Math.floor(data.getVideo().getFrameRate());
		initializeAnimalTrackObjectComboBox();
		timeField.setText(getTimeInMinuteSecond());
		timeStepField.setText("" + data.getVideo().getFrameRate());

		startVideo();
	}

	/**
	 * Starts the video by showing the start frame and setting the slider bar to the correct length and
	 * enabling it. 
	 */
	protected void startVideo() {
		totalNumFrame = data.getVideo().getVidCap().get(Videoio.CV_CAP_PROP_FRAME_COUNT);

		updateFrameView();
		sliderSeekBar.setMax((int) totalNumFrame - 1);
		sliderSeekBar.setMaxWidth((int) totalNumFrame - 1);

		sliderSeekBar.setDisable(false);

		updateTextAndSlider();

		jumpToFrame(startFrame);
	}

	/**
	 * Get a frame from the opened video stream (if any)
	 * 
	 * @return the {@link Mat} to show
	 */
	private Mat grabFrame() {
		Mat frame = new Mat();
		// check if the capture is open
		if (data.getVideo().getVidCap().isOpened()) {
			try {
				// read the current frame
				data.getVideo().getVidCap().read(frame);

			} catch (Exception e) {
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		return frame;
	}

	/**
	 * Changes the image view to the current frame number. 
	 */
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

	/**
	 * @return the current ImageView
	 */
	public ImageView getCurrentFrameImage() {
		return currentFrameImage;
	}
}