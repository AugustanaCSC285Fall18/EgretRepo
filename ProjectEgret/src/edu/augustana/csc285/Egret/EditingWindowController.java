package edu.augustana.csc285.Egret;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.videoio.Videoio;
import datamodel.AnimalTrack;
import datamodel.ProjectData;
import datamodel.TimePoint;
import datamodel.Video;
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

	public double totalNumFrame;
	ProjectData data;
	private int animalCounter = 0;
	private GraphicsContext gc;
	private boolean modifyToggleActive = false;
	private static final int drawX = 5;
	private static final int drawY = 5;
	private TimePoint previousPoint;
	private int frameJumpModifier = 2;
	
	// from calibration: random assignment at the moment
	private int totalAmountOfAnimals = 2;
	private int startFrame = 850;
	private int endFrame = 1000;

	// THIS EXISTS SO THE FRAME JUMPS A CONSISTANT AMOUNT, ITS BULLSHIT
	// BUT ITS THE ONLY THING I FOUND FIXES THE MODIFY FUNCTION
	// Also, setting current frame number and frame jump amount beforehand
	// really reduces lag in the program
	private int frameJumpAmount;
	private int currentFrameNumber = startFrame;

	//

	void loadData() throws FileNotFoundException {
		File dataFile = new File("full_auto_tracker_data");
		data = ProjectData.loadFromFile(dataFile);
		for (int i = 0; i < data.getUnassignedSegments().size(); i++) {
			if (data.getUnassignedSegments().get(i).getNumPoints() < frameJumpAmount * frameJumpModifier * 2) {
				data.getUnassignedSegments().remove(i);
				i--;
			}
		}
		
		// Prints out unassigned tracks
		for (AnimalTrack track : data.getUnassignedSegments()) {
			System.out.println(track.getName() + "Num of Points " + track.getNumPoints() + " first point: "
					+ track.getFirstTimePoint() + " last point: " + track.getFinalTimePoint());
		}

		data.getAnimalTracksList().add(new AnimalTrack("Chick 1"));
		data.getAnimalTracksList().get(0).add(new TimePoint(100, 200, 5));
		data.getAnimalTracksList().add(new AnimalTrack("Chick 2"));
		data.getAnimalTracksList().get(1).add(new TimePoint(150, 200, 5));
		data.getAnimalTracksList().add(new AnimalTrack("Chick 3"));
		data.getAnimalTracksList().get(2).add(new TimePoint(250, 300, 10));
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

	void jumpToFrame(int numOfFrame) {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		currentFrameNumber = numOfFrame;
		data.getVideo().getVidCap().set(Videoio.CAP_PROP_POS_FRAMES, currentFrameNumber);
		updateFrameView();
		frameAdjustHelper();
		displayFutureTracks();
		displayPastTracks();
		updateTextAndSlider();
		//showSpecifiedAnimalTrack();

	}

	void frameChanger(double numOfFrameChange) {
		if (currentFrameNumber + numOfFrameChange > endFrame) {
			animalCounter++;
			if (animalCounter > totalAmountOfAnimals) {
				saveFinishedProject();
				makeAlert("Analysis Complete","You have completed the analysis!");
			} else {
				setAnimalTrackObjectComboBox();
				makeAlert("Tracking New Chicken","You are now adding data for chicken " + data.getAnimalTracksList().get(animalCounter).getName());
			}
		} else {
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			//System.out.println("old frame number: " + data.getVideo().getCurrentFrameNum());
			System.out.println("Old Frame: " + currentFrameNumber );
			//data.getVideo().setCurrentFrameNum(data.getVideo().getCurrentFrameNum() + numOfFrameChange);
			currentFrameNumber += numOfFrameChange;
			//System.out.println("new frame number: " + data.getVideo().getCurrentFrameNum());
			System.out.println("New Frame: " + currentFrameNumber );
			updateFrameView();
			frameAdjustHelper();
			displayFutureTracks();
			displayPastTracks();
			updateTextAndSlider();
		}
	}

	void frameStepBack() {
		frameChanger(-frameJumpAmount);

	}

	@FXML
	void frameStepBack(MouseEvent event) {
		frameStepBack();
	}

	void frameStepForward() {
		frameChanger(frameJumpAmount);
	}

	@FXML
	void frameStepForward(MouseEvent event) {
		frameStepForward();
	}

	protected void frameAdjustHelper() {
		setAnimalCounter();
		AnimalTrack currentAnimal = data.getAnimalTracksList().get(animalCounter);
		if (currentAnimal.hasTimePointAtTime(currentFrameNumber)) {
			TimePoint curAnimalPoint = currentAnimal.getTimePointAtTime(currentFrameNumber);
			gc.fillOval(curAnimalPoint.getX(), curAnimalPoint.getY(), drawX, drawY);
		}
	}

	@FXML
	void toggleManualEdit(MouseEvent event) {
		modifyToggleActive = !modifyToggleActive;
	}

	@FXML
	void addOrModifyDataPoint(MouseEvent event) {
		double xCord = event.getX();
		double yCord = event.getY();
		Point centerPoint = new Point(xCord, yCord);
		setAnimalCounter();
		AnimalTrack currentAnimal = data.getAnimalTracksList().get(animalCounter);
		System.out.println(currentAnimal.getTimePointAtIndex(1));
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
			gc.fillOval(xCord, yCord, drawX, drawY);
		}
		System.out.println("final time point for current animal " + data.getAnimalTracksList().get(animalCounter).getName() + data.getAnimalTracksList().get(animalCounter).getFinalTimePoint() );
	}

	void modifyDataPointHelper(AnimalTrack currentAnimal, Point newPoint, TimePoint undoPoint) {
		gc.clearRect(previousPoint.getX(), previousPoint.getY(), drawX, drawY);
		currentAnimal.setTimePointAtTime(newPoint, currentFrameNumber);
	}

	void addDataPointHelper(AnimalTrack currentAnimal, Point newPoint) {
		currentAnimal.addLocation(newPoint, currentFrameNumber);
	}

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
				if (Math.abs(currentTrack.getFirstTimePoint().getFrameNum() - currentFrameNumber) < frameJumpAmount
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

	void displayFutureTracks() {
		int trackCounter = 1;
		ObservableList<String> listOfTracksDisplayed = FXCollections.observableArrayList();
		for (int i = 0; i < data.getUnassignedSegments().size(); i++) {
			AnimalTrack currentTrack = data.getUnassignedSegments().get(i);
			// displays a track that is within 10 times the frame rate
			if (Math.abs(currentTrack.getFirstTimePoint().getFrameNum() - currentFrameNumber) < frameJumpAmount
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

	void displayPastTracks() {
		setAnimalCounter();
		AnimalTrack currentTrack = data.getAnimalTracksList().get(animalCounter);
		gc.setFill(Color.DARKRED);
		// draw this segments recent past & near future locations
		for (TimePoint prevPt : currentTrack.getTimePointsWithinInterval(
				currentFrameNumber - frameJumpAmount * frameJumpModifier * 3,
				currentFrameNumber + frameJumpAmount * frameJumpModifier * 3)) {
			gc.fillOval(prevPt.getX(), prevPt.getY(), drawX, drawY);
		}

	}

	@FXML
	void closeWindow(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	void openPopUp(MouseEvent event) {
	}

	@FXML
	void redoEdit(MouseEvent event) {
	}

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

	@FXML
	public void changeFrameWithTextField() {
		int newTime = Integer.parseInt(timeField.getText());
		int maxTime = (int) Math.floor((data.getVideo().getTotalNumFrames() / frameJumpAmount));
		if (newTime > maxTime) {
			makeAlert("Time Change Error", "The video is between 0 and " + maxTime + " seconds long.");
		}
		int frameNumber = (int) Math.round((newTime * frameJumpAmount));
		jumpToFrame(frameNumber);
	}

	public void initializeAnimalTrackObjectComboBox() {
		for (int i = 0; i <= totalAmountOfAnimals; i++) {
			String name = data.getAnimalTracksList().get(i).getName();
			animalTrackObjectComboBox.getItems().add(name);
		}
		animalTrackObjectComboBox.getSelectionModel().select(0);
	}
	
	public void setAnimalCounter() {
		animalCounter = animalTrackObjectComboBox.getSelectionModel().getSelectedIndex();
	}
	
	public void setAnimalTrackObjectComboBox() {
		animalTrackObjectComboBox.getSelectionModel().select(animalCounter);
	}

	@FXML
	public void runSliderSeekBar() {
		int frameNum = (int) sliderSeekBar.getValue();
		jumpToFrame(frameNum);
	}

	public void updateTextAndSlider() {
		timeField.setText("" + (int) (currentFrameNumber / frameJumpAmount));
		sliderSeekBar.setValue(currentFrameNumber);
	}
	
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
    
    private void makeAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
		// Cited https://code.makery.ch/blog/javafx-dialogs-official/
    }

	private void loadWelcomeWindow() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeWindow.fxml"));
		try {
			AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage primary = (Stage) saveBtn.getScene().getWindow();
			primary.setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void saveFinishedProject() {
		File finalDataFile = new File("finished_data_file");
		try {
			data.saveToFile(finalDataFile);
		} catch (FileNotFoundException e) {
			// TODO: Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	private void saveUnfinishedProject() {
		File tempDataFile = new File("not_finished_data_file2");
		try {
			data.saveToFile(tempDataFile);
			loadWelcomeWindow();
		} catch (FileNotFoundException e) {
			// TODO: Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: doMoreStuff();
	}

	@FXML
	public void initialize() throws FileNotFoundException {
//		loadData();
//		sliderSeekBar.setDisable(true);
//		gc = canvas.getGraphicsContext2D();
//		runSliderSeekBar();
//		data.getVideo().setVidCap();
//		setAnimalTrackObjectComboBox();
	}

	public void initializeWithProjectData(ProjectData projectData) throws FileNotFoundException {
		data = projectData;
		frameJumpAmount = (int) Math.floor(data.getVideo().getFrameRate());
		gc = canvas.getGraphicsContext2D();
		// runSliderSeekBar();
		initializeAnimalTrackObjectComboBox();

		startVideo();
	}

	@FXML
	public void handleBrowse() throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		Window mainWindow = currentFrameImage.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		if (chosenFile != null) {
			String fileName = chosenFile.toURI().toString();
			data.setVideo(new Video(fileName));
			startVideo();
		}
		;
		// runSliderSeekBar();
		// changeFrameWithTextField();
		// runJumpTo(); //prints out which frame you are at
	}

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

	public ImageView getCurrentFrameImage() {
		return currentFrameImage;
	}
}