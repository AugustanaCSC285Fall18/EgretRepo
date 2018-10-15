package edu.augustana.csc285.Egret;
 import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
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
	
	@FXML
	private ChoiceBox<String> PickAnimalTrackBtn = new ChoiceBox<>();
 	// a timer for acquiring the video stream
	// private ScheduledExecutorService timer;
	private VideoCapture capture = new VideoCapture();
	private String fileName = null;
	private int curFrameNum;
	public double numFrame;
 	private double xCord;
	private double yCord;
	//ProjectData data = new ProjectData();
	ProjectData data;
	private int animalCounter = 0;
	private int totalAmountOfAnimals = 2;
	private Video videoObject;
	private GraphicsContext gc;
	private boolean modifyToggleActive = false;
	private int drawX = 5;
	private int drawY = 5;
	private int frameJumpModifier = 2;
 	private TimePoint previousPoint;
	
	private int startFrame = 850;
	private int endFrame = 2000;
 	void loadData() throws FileNotFoundException {
		File dataFile = new File("full_auto_tracker_data");
		data = ProjectData.loadFromFile(dataFile);
		//for (AnimalTrack track: data.getUnassignedSegments()) {
		//	System.out.println(track.getName() + "Num of Points " + track.getNumPoints() + " first point: " + track.getFirstTimePoint() + " last point: " + track.getFinalTimePoint());
		//}
 		//removes a data string that is less than 4*frame rate
		for(int i = 0; i < data.getUnassignedSegments().size(); i++) {
			if(data.getUnassignedSegments().get(i).getNumPoints() < data.getVideo().getFrameRate()* frameJumpModifier*2 ) {
				data.getUnassignedSegments().remove(i);
				i--;
			}
		}
		
		for (AnimalTrack track: data.getUnassignedSegments()) {
			System.out.println(track.getName() + "Num of Points " + track.getNumPoints() + " first point: " + track.getFirstTimePoint() + " last point: " + track.getFinalTimePoint());
		}
	
		data.getAnimalTracksList().add(new AnimalTrack("Chick 1"));
		data.getAnimalTracksList().add(new AnimalTrack("Chick 2"));
		data.getAnimalTracksList().get(1).add(new TimePoint(150,200,5));
		data.getAnimalTracksList().add(new AnimalTrack("Chick 3"));
		data.getAnimalTracksList().get(2).add(new TimePoint(250,300,10));
		
	}
	
	@FXML
	void closeWindow(ActionEvent event) {
 	}
	
	void frameChanger(double numOfFrameChange) {
		if(curFrameNum + numOfFrameChange > endFrame) {
			animalCounter++;
			if(animalCounter > totalAmountOfAnimals) {
				saveData();
				//TODO: make code to end the manual tracking screen
			}else {
				jumpToFrame(startFrame);
			}
		}else {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		System.out.println("old frame " + curFrameNum);
		curFrameNum += numOfFrameChange;
		capture.set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);
		updateFrameView();
		frameAdjustHelper();
		displayFutureTracks();
		System.out.println("current frame " + curFrameNum);
		System.out.println("chicken 0 data " + data.getAnimalTracksList().get(0));
		System.out.println("chicken 1 data " + data.getAnimalTracksList().get(1));
		System.out.println("chicken 2 data " + data.getAnimalTracksList().get(2));
		}
 	}
 	void jumpToFrame(int numOfFrame) {
		System.out.println("old frame num " + curFrameNum);
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		curFrameNum = numOfFrame;
		System.out.println("new frame num " + curFrameNum);
		capture.set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);
		updateFrameView();
		frameAdjustHelper();
		displayFutureTracks();
		System.out.println("chicken 0 data " + data.getAnimalTracksList().get(0));
		System.out.println("chicken 1 data " + data.getAnimalTracksList().get(1));
		System.out.println("chicken 2 data " + data.getAnimalTracksList().get(2));
	}
	
	void frameStepBack() {
		frameChanger(-Math.floor(videoObject.getFrameRate() * frameJumpModifier));
	}
	@FXML
	void frameStepBack(MouseEvent event) {
		frameChanger(-Math.floor(videoObject.getFrameRate() * frameJumpModifier));
	}
 	void frameStepForward() {
		frameChanger(Math.floor(videoObject.getFrameRate() * frameJumpModifier));
	}
	@FXML
	void frameStepForward(MouseEvent event) {
		frameChanger(Math.floor(videoObject.getFrameRate() * frameJumpModifier));
	}
 	protected void frameAdjustHelper() {
		AnimalTrack currentAnimal = data.getAnimalTracksList().get(animalCounter);
		// modifyToggleActive = currentAnimal.isTimePointAtTime(curFrameNum);
		// System.out.println(modifyToggleActive);
		if (currentAnimal.hasTimePointAtTime(curFrameNum)) {
			//data.getAnimalTracksList().size()
			for (int i = 0; i < 1; i++) {
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
		System.out.println("chicken 0 data " + data.getAnimalTracksList().get(0));
		System.out.println("chicken 1 data " + data.getAnimalTracksList().get(1));
		System.out.println("chicken 2 data " + data.getAnimalTracksList().get(2));
		gc.fillOval(xCord, yCord, drawX, drawY);
		frameStepForward();
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
			//animalCounter--;
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
	void addTrack(MouseEvent event) {
		if(PickAnimalTrackBtn.getValue() != null) {
			String chosenValueByUser = new String(PickAnimalTrackBtn.getValue());	
			String nameOfCurrentTrack = ("");
			AnimalTrack chosenTrack = null;
			boolean foundTrack = false;
			int trackCounter = 1;
			int i = 0;
			int chosenTrackNumber = 0;
			while(i < data.getUnassignedSegments().size() && !foundTrack) {
				AnimalTrack currentTrack = data.getUnassignedSegments().get(i);
				//displays a track that is within 10 times the frame rate
				if(Math.abs(currentTrack.getFirstTimePoint().getFrameNum() - curFrameNum) < data.getVideo().getFrameRate()*frameJumpModifier*5) {
					nameOfCurrentTrack = ("Track "+trackCounter);
					trackCounter++;
				}
			System.out.println("name of cur track " + nameOfCurrentTrack);
			System.out.println("chosenValueByUser " + chosenValueByUser);
			if(nameOfCurrentTrack.equals(chosenValueByUser)) {
				chosenTrack = currentTrack;
				chosenTrackNumber = i;
				foundTrack = true;
			}
			i++;
			
			}
			data.getAnimalTracksList().get(animalCounter).addTrackSegment(chosenTrack);
			data.removeUnassignedSegment(chosenTrackNumber);
			frameChanger(chosenTrack.getFinalTimePoint().getFrameNum()+1-curFrameNum);
			
		}
 	}
	
	void displayFutureTracks() {
		int trackCounter = 1;
		ObservableList<String> listOfTracksDisplayed = FXCollections.observableArrayList();
 		for(int i = 0; i < data.getUnassignedSegments().size(); i++) {
			AnimalTrack currentTrack = data.getUnassignedSegments().get(i);
			//displays a track that is within 10 times the frame rate
			if(Math.abs(currentTrack.getFirstTimePoint().getFrameNum() - curFrameNum) < data.getVideo().getFrameRate()*frameJumpModifier*5) {
				for(int j = 0; j < currentTrack.getNumPoints();j++) {
					TimePoint currentTimePoint = currentTrack.getTimePointAtIndex(j);
					gc.setFill(Color.BLACK);
					gc.fillOval(currentTimePoint.getX(), currentTimePoint.getY(), drawX, drawY);
				}
				gc.setFill(Color.DARKRED);
				gc.fillText("Track "+ trackCounter, currentTrack.getFirstTimePoint().getX()+10, currentTrack.getFirstTimePoint().getY()+10);
				listOfTracksDisplayed.add("Track "+trackCounter);
				trackCounter++;
			}
		}
		if(listOfTracksDisplayed != null) {
			PickAnimalTrackBtn.setItems(listOfTracksDisplayed);
		}
 	}
	
	private void saveData() {
		File finalDataFile = new File("final_data_file");
		try {
			data.saveToFile(finalDataFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
 	@FXML
	public void initialize() throws FileNotFoundException {
		loadData();
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
		jumpToFrame(startFrame);
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