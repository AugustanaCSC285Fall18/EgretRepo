package edu.augustana.csc285.Egret;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import datamodel.AnimalTrack;
import datamodel.ProjectData;
import datamodel.Video;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableIntegerArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
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
    private ComboBox<String> chicksComboBox;

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
    private Label instructLabel;
    
    @FXML
    private Button loadBtn;

    @FXML
    private Button callibrateBtn;
    
    @FXML
    private Button setLengthsBtn;
    
    @FXML
    private Button continueBtn;
    
    @FXML
    private TextField startField;

    @FXML
    private TextField endField;
    
    @FXML
    private ChoiceBox<Integer> timeStepBox;

//    private Button continueBtn;

  
	
	//private String fileName = null;
	public double numFrame;
	
	ProjectData data = new ProjectData();
	
	boolean pointsCalibrated=false;
	Point upperLeftCorner = new Point();
	Point lowerRightCorner = new Point();
	Point lowerLeftCorner = new Point();
	Point origin = new Point();
	int step = 0;

	private List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
	private ObservableList items = FXCollections.observableList(list);

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
		//runSliderSeekBar();
		
		startField.setText("0:00");
		int endTime = data.getVideo().getTimeInSeconds(data.getVideo().getEndFrameNum());
		endField.setText(endTime/60 + ":" + endTime%60);
		//runJumpTo(); //prints out which frame you are at
    }
	
	public void keyIgnore(KeyEvent event) {
		char character = event.getCharacter().charAt(0);
    	if(Character.isAlphabetic(character)) {
    		((TextField)event.getSource()).deletePreviousChar();
    	}
	}
    
    protected void startVideo() {
		// start the video capture
		numFrame = data.getVideo().getVidCap().get(Videoio.CV_CAP_PROP_FRAME_COUNT);
		updateFrameView();
		//totalFrameArea.appendText("Total frames: " + (int) numFrame + "\n"); //prints total number of frames
		//this can be repurposed to allow the client to jump to specific time stamp
		//jumpToFrameArea.setDisable(false); //allows client to jump to specific frame
		sliderSeekBar.setMax((int) numFrame -1);
		sliderSeekBar.setMaxWidth((int) numFrame -1);
		sliderSeekBar.setDisable(false);
		jumpToFrame(0);
	}
    
    public ImageView getCurrentFrameImage() {
    	return currentFrameImage;
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
    
    public void setTimeStep() {
    	data.getVideo().setTimeStep(timeStepBox.getSelectionModel().getSelectedItem());
    }

    @FXML
	public void initialize() {
		timeStepBox.setItems(items);
	}
    
	//event handlers
    @FXML
    void handleAddChickBtn(MouseEvent event) {
    	String suggestedInput = "Chick #" + (chicksComboBox.getItems().size() + 1);
		TextInputDialog dialog = new TextInputDialog(suggestedInput);
		dialog.setTitle("Add Chick:");
		dialog.setHeaderText(null);
		dialog.setContentText("Enter Chick Name:");

		//
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String chickName = result.get();
			data.getAnimalTracksList().add(new AnimalTrack(chickName));
			int index = chicksComboBox.getSelectionModel().getSelectedIndex();
			chicksComboBox.getItems().add(chickName);
			chicksComboBox.getSelectionModel().select(chickName);
		}
    }
    
    @FXML
    private void handleBrowse(MouseEvent event) throws FileNotFoundException {
    	browseForVideoFile();
    }
	
	@FXML
	// handles first step of calibration, handleCanvasClick handles the rest
    void handleCallibration(MouseEvent event) {
		if(step==0) {
			step = 1;
			instructLabel.setText("Please select the upper left corner of the box.");
		}
    }
    
    @FXML
    void handleCanvasClick(MouseEvent event) throws InterruptedException {
    	Rectangle rect = new Rectangle();
    	if (step==1) {
    		upperLeftCorner.setLocation(event.getX(), event.getY());
    		rect.add(upperLeftCorner);
    		
    		step=2;
    		instructLabel.setText("Please select the lower right hand corner of the box");
    	}else if (step==2) {
    		lowerRightCorner.setLocation(event.getX(), event.getY());
    		rect.add(lowerRightCorner);
    		data.getVideo().setArenaBounds(rect);
    		
    		step=3;
    		instructLabel.setText("Please select the lower left hand corner of the box.");
    	}else if (step==3) {
    		lowerLeftCorner.setLocation(event.getX(), event.getY());
    		
    		step=4;
    		instructLabel.setText("Please select where you would like your origin to be located.");
    	}else if (step==4) {
    		pointsCalibrated=true;
    		origin.setLocation(event.getX(), event.getY());
    		data.getVideo().setOriginPoint(origin);
    		openEmptyFrameDialog();
    		endCalibration();
    	}
    }
    
    public void openEmptyFrameDialog() {
    	TextInputDialog dialog = new TextInputDialog("0:00");
    	dialog.setTitle("Callibration");
    	dialog.setHeaderText("Empty Box Callibration");
    	dialog.setContentText("Please enter a time in which the box has no chickens: ");
    	
    	Optional<String> result = dialog.showAndWait();
//    	String text = dialog.getContentText();
//    	if(! Pattern.matches("``^[a-zA-Z]+$`", result.get())) {
//    		
//    	}
    	if (result.isPresent()){
    		String string = result.get();
    		int index = string.indexOf(":");
        	int mins = Integer.valueOf(string.substring(0, index));
        	int secs = Integer.valueOf(string.substring(index+1));
        	int emptyFrame = data.getVideo().getTimeInFrames(mins*60+secs);
        	data.getVideo().setEmptyFrameNum(emptyFrame);
    	}
    }
    
    public void endCalibration() {
    	step=0;
		instructLabel.setText("You are done calibrating! Press calibrate again to reset calibration.");
		//wait 2 seconds
		new java.util.Timer().schedule(
			    new java.util.TimerTask() {
			        @Override
			        public void run() {
			            Platform.runLater(() -> instructLabel.setText(""));
			        }
			    }, 
			    2000);   // the delay time in milliseconds
    }

    @FXML
    void handleClose(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Confirmation Dialog");
    	alert.setHeaderText("Close Window");
    	alert.setContentText("Are you sure you want to close? Any unsaved data will be lost.");

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		Platform.exit();
    	} else {
    	    alert.close();
    	}
    }
    
    @FXML
    void handleContinueBtn(MouseEvent event) throws IOException {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Continue");
    	alert.setHeaderText("You are about to leave the Preview Window");
    	alert.setContentText("Please review your calibration.\n"
    			+ "Once you continue you will not be able to make any changes.\n Would you like to continue?");

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
    		BorderPane root = (BorderPane)loader.load();
    		EditingWindowController nextController = loader.getController();
    		
    		Scene nextScene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
    		nextScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
    		
    		Stage primary = (Stage) continueBtn.getScene().getWindow();
    		primary.setScene(nextScene);
    	} else {
    	    alert.close();
    	}
    }
    
  //Avery... still working on it
    @FXML //throw exception if non number is entered.... or prevent it from being entered
    void handleEndTime(KeyEvent event) {
    	keyIgnore(event);

    	String result = event.getText();
    	int index = result.indexOf(":");
    	int mins = Integer.valueOf(result.substring(0, index));
    	int secs = Integer.valueOf(result.substring(index));
    	int endFrame = data.getVideo().getTimeInFrames(mins*60+secs);
    	data.getVideo().setStartFrameNum(endFrame);
    }

    //replaces video in window... need to make sure 
    @FXML
    void handleLoadVideo(MouseEvent event) throws FileNotFoundException {
    	browseForVideoFile();
    }
    
    @FXML //throw exception if non number is entered... or prevent it from being entered
    void handleStartTime(KeyEvent event) {
    	keyIgnore(event);
    	
    	String result = event.getText();
    	int index = result.indexOf(":");
    	int mins = Integer.valueOf(result.substring(0, index));
    	int secs = Integer.valueOf(result.substring(index));
    	int startFrame = data.getVideo().getTimeInFrames(mins*60+secs);
    	data.getVideo().setStartFrameNum(startFrame);
    	//sliderSeekBar.setValue();
    	
    }

    @FXML
    void handleSettings(ActionEvent event) {

    }
    
    @FXML
    void handleSetLengthsBtn(MouseEvent event) {
    	if(pointsCalibrated) {
    		openFirstDialog();
        	openSecondDialog();
    	}
    }
    
    public void openFirstDialog() {
    	TextInputDialog dialog = new TextInputDialog("0");
    	dialog.setTitle("Additional Callibration");
    	dialog.setHeaderText("Length Callibration");
    	dialog.setContentText("Please enter the height of the box in cm: ");

    	// Get the response value.
    	Optional<String> result = dialog.showAndWait();
//    	String text = dialog.getContentText();
//    	if(! Pattern.matches("``^[a-zA-Z]+$`", result.get())) {
//    		
//    	}
    	double length = Double.valueOf(result.get());
    	if (result.isPresent()){
    		data.getVideo().setYPixelsPerCm(length, upperLeftCorner, lowerLeftCorner);
    	}
    	//citation: 
    }
    
    public void openSecondDialog() {
    	TextInputDialog dialog = new TextInputDialog("0");
    	dialog.setTitle("Additional Callibration");
    	dialog.setHeaderText("Length Callibration");
    	dialog.setContentText("Please enter the width of the box in cm: ");
    	ButtonType buttonTypeNext = new ButtonType("Next", ButtonData.NEXT_FORWARD);
    	ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

    	//dialog.getButtonTypes().setAll(buttonTypeNext, buttonTypeCancel);

    	// Get the response value.
    	Optional<String> result = dialog.showAndWait();
//    	String text = dialog.getContentText();
//    	if(! Pattern.matches("``^[a-zA-Z]+$`", result.get())) {
//    		
//    	}
    	double length = Double.valueOf(result.get());
    	if (result.isPresent()){
    		data.getVideo().setXPixelsPerCm(length, lowerRightCorner, lowerLeftCorner);
    	}
    	//citation: 
    }
    
    @FXML
    void handleRemoveChickBtn(MouseEvent event) {
    	TextInputDialog dialog = new TextInputDialog();
    	dialog.setHeaderText("Note: Names are case sensitive.");
    	
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String chickName = result.get();
			data.getAnimalTracksList().remove(new AnimalTrack(chickName));
			chicksComboBox.getItems().remove(chickName);
			chicksComboBox.getSelectionModel().select(chickName);
		}
    }
    
	@FXML
	public void runSliderSeekBar() {
		int frameNum = (int) sliderSeekBar.getValue();
		jumpToFrame(frameNum);
	}
    
	void jumpToFrame(int numOfFrame) {
		data.getVideo().getVidCap().set(Videoio.CAP_PROP_POS_FRAMES, numOfFrame);
		updateFrameView();

	}
	
}

