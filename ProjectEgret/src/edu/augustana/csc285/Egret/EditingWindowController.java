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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
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
 	@FXML ImageView currentFrameImage;
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
 	// a timer for acquiring the video stream
	// private ScheduledExecutorService timer;
	//private VideoCapture capture = new VideoCapture();
	//private String fileName = null; //What does this do??? 
	private int curFrameNum;
	public double totalNumFrame;
	//ProjectData data = new ProjectData();
	ProjectData data;
	private int animalCounter = 0;
	private GraphicsContext gc;
	private boolean modifyToggleActive = false;
	private static final int drawX = 5;
	private static final int drawY = 5;
	private static int halfDrawX = drawX / 2;
	private static int halfDrawY = drawY / 2;
	private int frameJumpModifier = 2;
 	private TimePoint previousPoint;
 	
 	//from calibration: random assignment at the moment
	private int totalAmountOfAnimals = 2; 
	private int startFrame = 850;
	private int endFrame = 7500;

	
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
		//Prints out unassigned tracks
		for (AnimalTrack track: data.getUnassignedSegments()) {
			System.out.println(track.getName() + "Num of Points " + track.getNumPoints() + " first point: " + track.getFirstTimePoint() + " last point: " + track.getFinalTimePoint());
		}
		
		data.getAnimalTracksList().add(new AnimalTrack("Chick 1"));
		data.getAnimalTracksList().get(0).add(new TimePoint(100,200,5));
		data.getAnimalTracksList().add(new AnimalTrack("Chick 2"));
		data.getAnimalTracksList().get(1).add(new TimePoint(150,200,5));
		data.getAnimalTracksList().add(new AnimalTrack("Chick 3"));
		data.getAnimalTracksList().get(2).add(new TimePoint(250,300,10));
	}
	
	@FXML
	void closeWindow(ActionEvent event) {
		Platform.exit();
 	}
	
	void frameChanger(double numOfFrameChange) {
		if(curFrameNum + numOfFrameChange > endFrame) {
			animalCounter++;
			if(animalCounter > totalAmountOfAnimals) {
				saveData();
				//TODO: make code to end the manual tracking screen
			} else {
				jumpToFrame(startFrame);
			}
		} else {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		System.out.println("old frame " + curFrameNum);
		curFrameNum += numOfFrameChange;
		data.getVideo().getVidCap().set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);
		updateFrameView();
		frameAdjustHelper();
		displayFutureTracks();
		displayPastTracks();
		updateTextAndSlider();
		System.out.println("current frame " + curFrameNum);
		System.out.println("chicken 0 data " + data.getAnimalTracksList().get(0));
		System.out.println("chicken 1 data " + data.getAnimalTracksList().get(1));
		System.out.println("chicken 2 data " + data.getAnimalTracksList().get(2));
		System.out.println("AnimalCounter: " + animalCounter);
		}
 	}
 	void jumpToFrame(int numOfFrame) {
		System.out.println("old frame num " + curFrameNum);
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		curFrameNum = numOfFrame;
		System.out.println("new frame num " + curFrameNum);
		data.getVideo().getVidCap().set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);
		updateFrameView();
		frameAdjustHelper();
		displayFutureTracks();
		updateTextAndSlider();
//		showSpecifiedAnimalTrack();
		System.out.println("chicken 0 data " + data.getAnimalTracksList().get(0));
		System.out.println("chicken 1 data " + data.getAnimalTracksList().get(1));
		System.out.println("chicken 2 data " + data.getAnimalTracksList().get(2));
		
	}
	
 	/*
 	 * Aims to draw a circle around the track that is currently being focused by the animalTrack combo box. 
 	 * Currently does not work because the field immediately has nothing in it, unsure how to fix.
 	 * TODO: fix this too.
 	 */
//	public void showSpecifiedAnimalTrack() {
//		int curAnimalIndex = animalTrackObjectComboBox.getSelectionModel().getSelectedIndex();
//		AnimalTrack curAnimal = data.getAnimalTracksList().get(curAnimalIndex);
//		gc.setFill(data.getColorArrayForAnimalTracks().get(curAnimalIndex));
//		gc.strokeOval(curAnimal.getTimePointAtTime(curFrameNum).getX() - (drawX * 3), curAnimal.getTimePointAtTime(curFrameNum).getY() - (drawY * 3), drawX * 3, drawY *3);
//	}

	void frameStepBack() {
		frameChanger(-Math.floor(data.getVideo().getFrameRate() * frameJumpModifier));
		
	}
 	
	@FXML
	void frameStepBack(MouseEvent event) {
		frameStepBack();
	}
	
 	void frameStepForward() {
		frameChanger(Math.floor(data.getVideo().getFrameRate() * frameJumpModifier));
	}
 	
	@FXML
	void frameStepForward(MouseEvent event) {
		frameStepForward();
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
		double xCord = event.getX();
		double yCord = event.getY();
		Point centerPoint = new Point(xCord, yCord);
		AnimalTrack currentAnimal = data.getAnimalTracksList().get(animalCounter);
 		if (modifyToggleActive) {
 			Point newPoint = currentAnimal.getPointAtTime(curFrameNum);
 			previousPoint = new TimePoint(newPoint, curFrameNum);
			modifyDataPointHelper(currentAnimal, centerPoint, previousPoint);
		} else {
			addDataPointHelper(currentAnimal, centerPoint);
		}
 		
		System.out.println("chicken 0 data " + data.getAnimalTracksList().get(0));
		System.out.println("chicken 1 data " + data.getAnimalTracksList().get(1));
		System.out.println("chicken 2 data " + data.getAnimalTracksList().get(2));
		//gc.fillOval(xCord, yCord, drawX, drawY);
		frameStepForward();
	}
		
 	void modifyDataPointHelper(AnimalTrack currentAnimal, Point newPoint, TimePoint undoPoint) {
		gc.clearRect(previousPoint.getX() - halfDrawX, previousPoint.getY() - halfDrawY, drawX, drawY);
		currentAnimal.setTimePointAtTime(newPoint, curFrameNum);
	}
 	
 	void addDataPointHelper(AnimalTrack currentAnimal, Point newPoint) {
		currentAnimal.addLocation(newPoint, curFrameNum);
	}
	
 	@FXML
 	void undoEdit(MouseEvent event) {
		if(previousPoint.getFrameNum() < curFrameNum) {
			AnimalTrack currentAnimal = data.getAnimalTracksList().get(animalCounter);
			gc.clearRect(currentAnimal.getX() - halfDrawX, currentAnimal.getY() - halfDrawY, drawX, drawY);
			if (modifyToggleActive) {
				TimePoint undoPoint = currentAnimal.getTimePointAtTime(curFrameNum);
				gc.clearRect(undoPoint.getX() - halfDrawX, undoPoint.getY() - halfDrawY, drawX, drawY);
				gc.fillOval(previousPoint.getX() - halfDrawX, previousPoint.getY() - halfDrawY, drawX, drawY);
				modifyDataPointHelper(currentAnimal, previousPoint.getPointOpenCV(), undoPoint);
				currentAnimal.setTimePointAtTime(previousPoint.getPointOpenCV(), curFrameNum);
			} else {
				currentAnimal.removeLocation();
			}
			System.out.println(data.getAnimalTracksList().get(animalCounter));
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Undo Attempt");
			alert.setHeaderText(null);
			alert.setContentText("No more points to undo.");
			alert.showAndWait();
			
			//Cited https://code.makery.ch/blog/javafx-dialogs-official/

		}
	}
 	
//	@FXML
//	void undoEdit(MouseEvent event) {
//		if (animalCounter >= 0) {
//			//animalCounter--;
//			AnimalTrack currentAnimal = data.getAnimalTracksList().get(animalCounter);
// 			gc.clearRect(currentAnimal.getX(), currentAnimal.getY(), drawX, drawY);
//			currentAnimal.removeLocation();
//			for (int i = 0; i < data.getAnimalTracksList().size(); i++) {
//				System.out.println(data.getAnimalTracksList().get(i));
//			}
//		} else {
//			Alert alert = new Alert(AlertType.INFORMATION);
//			alert.setTitle("Undo Attempt");
//			alert.setHeaderText(null);
//			alert.setContentText("No more points to undo.");
//			alert.showAndWait();
//			
//			//Cited https://code.makery.ch/blog/javafx-dialogs-official/
// 		}
//	}
	
	@FXML
	void addTrack(MouseEvent event) {
		if(pickUnassignedAnimalTrackBtn.getValue() != null) {
			String chosenValueByUser = new String(pickUnassignedAnimalTrackBtn.getValue());	
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
				gc.setFill(Color.DARKBLUE);
				gc.fillText("Track "+ trackCounter, currentTrack.getFirstTimePoint().getX()+10, currentTrack.getFirstTimePoint().getY()+10);
				listOfTracksDisplayed.add("Track " + trackCounter);
				trackCounter++;
			}
		}
		if(listOfTracksDisplayed != null) {
			pickUnassignedAnimalTrackBtn.setItems(listOfTracksDisplayed);
		}
 	}
	
	void displayPastTracks() {
		AnimalTrack currentTrack = data.getAnimalTracksList().get(animalCounter);
			gc.setFill(Color.DARKRED);
			// draw this segments recent past & near future locations 
			for (TimePoint prevPt : currentTrack.getTimePointsWithinInterval(curFrameNum-data.getVideo().getFrameRate()*frameJumpModifier*3, curFrameNum+data.getVideo().getFrameRate()*frameJumpModifier*3)) {
				gc.fillOval(prevPt.getX(), prevPt.getY(), drawX, drawY);
			}

	}

	//is this going to be different than the saveProject method?? if so let's get rid of saveProject.
 	@FXML
 	public void changeFrameWithTextField() {
 		int newTime = Integer.parseInt(timeField.getText());
 		int maxTime = (int) Math.round(data.getVideo().getTotalNumFrames() / data.getVideo().getFrameRate());
 		if (newTime > maxTime) {
 			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Time Change");
			alert.setHeaderText(null);
			alert.setContentText("The video is between 0 and " + maxTime + " seconds long.");
			alert.showAndWait();
			
			//Cited https://code.makery.ch/blog/javafx-dialogs-official/
 		}
 		int frameNumber = (int) Math.round((newTime * data.getVideo().getFrameRate()));
 		jumpToFrame(frameNumber);
 	}
 	
 	
 	public void setAnimalTrackObjectComboBox() {
 		for (int i = 0; i <= totalAmountOfAnimals; i++) {
 			String name = data.getAnimalTracksList().get(i).getName();
 			animalTrackObjectComboBox.getItems().add(name);
 		}
 	}
 	
 	@FXML
 	public void runSliderSeekBar() {
 		int frameNum = (int) sliderSeekBar.getValue();
 		jumpToFrame(frameNum);
 	}
	
 	public void updateTextAndSlider() {
 		timeField.setText("" + (int) (curFrameNum/data.getVideo().getFrameRate())); 
 		sliderSeekBar.setValue(curFrameNum);
 	}
 	
	private void saveData() {
		File finalDataFile = new File("final_data_file");
		try {
			data.saveToFile(finalDataFile);
		} catch (FileNotFoundException e) {
			// TODO: Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: doMoreStuff();
	}
	
 	@FXML
	public void initialize() throws FileNotFoundException {
		loadData();
		sliderSeekBar.setDisable(true);
		gc = canvas.getGraphicsContext2D();
		//runSliderSeekBar();
		setAnimalTrackObjectComboBox();
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
		};
		//runSliderSeekBar();
		//changeFrameWithTextField();
		// runJumpTo(); //prints out which frame you are at
	}
 	

 	
 	protected void startVideo() {
 		// start the video capture
		totalNumFrame = this.data.getVideo().getVidCap().get(Videoio.CV_CAP_PROP_FRAME_COUNT);
		// totalFrameArea.appendText("Total frames: " + (int) numFrame + "\n"); //prints
		// total number of frames
		sliderSeekBar.setDisable(false);
		// this can be repurposed to allow the client to jump to specific time stamp
		// jumpToFrameArea.setDisable(false); //allows client to jump to specific frame
		updateFrameView();
		timeField.setText("" + data.getVideo().getTimeInSeconds(startFrame));
		sliderSeekBar.setMax((int) totalNumFrame - 1);
		sliderSeekBar.setMaxWidth((int) totalNumFrame - 1);
		jumpToFrame(startFrame);
 	}
 	
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
	
//	//Are we making a new listener every time we call this method? 
// 	private void runSliderSeekBar() {
// 		sliderSeekBar.valueProperty().addListener(new ChangeListener<Number>() {
//			@Override
//			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//				// currentFrameArea.appendText("Current frame: " + ((int)
//				// Math.round(newValue.doubleValue())) + "\n");
// 				timeField.setText("" + data.getVideo().getTimeInSeconds(curFrameNum));
//				curFrameNum = (int) Math.round(newValue.doubleValue());
//				data.getVideo().getVidCap().set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);
// 				updateFrameView();
//			}
// 		});
// 		
//	}

 	
 	/*
 	 * This method is supposed to change the displayed frame with the new frame inputted by the timeField
 	 * For some reason, there cannot be any input. Unsure why.
 	 */
// 	private void changeFrameWithTextField() {
// 		timeField.textProperty().addListener(new ChangeListener<String>() {
// 			@Override
// 			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
// 				System.out.println(newValue);
// 				int newTime = Integer.parseInt(newValue);
// 				timeField.appendText("" + newTime);
// 				int newTimeInFrames = data.getVideo().getTimeInFrames(newTime);
// 				//sliderSeekBar.setValue(newTimeInFrames);
// 				jumpToFrame(curFrameNum);
// 				data.getVideo().getVidCap().set(Videoio.CAP_PROP_POS_FRAMES, curFrameNum);
// 				updateFrameView();
// 			}
// 		});
// 	}
 	
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

	public ImageView getCurrentFrameImage() {
		return currentFrameImage;
	}
 }