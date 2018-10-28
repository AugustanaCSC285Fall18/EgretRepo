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
import javafx.scene.control.ChoiceDialog;
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
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;


public class PreviewWindowController {
    
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

    @FXML
    private Slider sliderSeekBar;
    
    @FXML
    private Label instructLabel;
    
    @FXML
    private Button loadBtn;

    @FXML
    private Button calibrateBtn;
    
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

    private GraphicsContext gc;
  
	
	//private String fileName = null;
	public double numFrame;
	
	ProjectData data;
	
	boolean pointsCalibrated = false;
	Point upperLeftCorner = new Point();
	Point lowerRightCorner = new Point();
	Point lowerLeftCorner = new Point();
	Point origin = new Point();
	int step = 0;

	private List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
	private ObservableList items = FXCollections.observableList(list);
	
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
    	data.getVideo().setTimeStepIndex(timeStepBox.getSelectionModel().getSelectedIndex());
    }

    @FXML
	public void initialize() {
		timeStepBox.setItems(items);
		timeStepBox.getSelectionModel().select(0);
		gc = canvas.getGraphicsContext2D();
		disableButtons();
	}
    
    /*
     * Disables all of the buttons and boxes and slider
     */
    private void disableButtons() {
    	addChickBtn.setDisable(true);
    	removeChickBtn.setDisable(true);
    	calibrateBtn.setDisable(true);
    	setLengthsBtn.setDisable(true);
    	continueBtn.setDisable(true);
    	sliderSeekBar.setDisable(true);
    	chicksComboBox.setDisable(true);
    }

    /*
     * Enables all of the buttons and boxes and slider
     */
    private void enableButtons() {
    	addChickBtn.setDisable(false);
    	removeChickBtn.setDisable(false);
    	calibrateBtn.setDisable(false);
    	setLengthsBtn.setDisable(false);
    	continueBtn.setDisable(false);
    	sliderSeekBar.setDisable(false);
    	chicksComboBox.setDisable(false);
    }
    
    @FXML
    private void handleBrowse() throws FileNotFoundException {
    	loadBtn.setText("Load Different Video");
    	FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		Window mainWindow = currentFrameImage.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		if (chosenFile != null) {
			String fileName = chosenFile.toURI().toString();
			data = new ProjectData(fileName);
			data.getVideo().setTimeStep(1);
			startVideo();
		};
		startField.setText("0:00");
		int endTime = data.getVideo().getTimeInSeconds(data.getVideo().getEndFrameNum());
		endField.setText(endTime/60 + ":" + endTime%60);
		enableButtons();
    }
	
	@FXML
	// handles first step of calibration, handleCanvasClick handles the rest
	private void handleCalibration(MouseEvent event) {
		if(step==0) {
			step = 1;
			instructLabel.setText("Please select the upper left corner of the box.");
		}
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setFill(Color.BLUEVIOLET);
    }
    
	@FXML
	private void handleCanvasClick(MouseEvent event) throws InterruptedException {
		Rectangle rect = new Rectangle();
		if (data == null) {
			makeAlert(AlertType.INFORMATION, "Calibrate", null, "Select a video before attempting to calibrate.");
		} else if (step == 0) {
			makeAlert(AlertType.INFORMATION, "Calibrate", null, "Press Calibrate first.");
		} else {
			gc.fillOval(event.getX() - (15/2), event.getY() - (15/2), 15, 15);	
			System.out.println(step);
			if (step==1) {
				upperLeftCorner.setLocation(event.getX(), event.getY());
				rect.add(upperLeftCorner);
				changeStepAndInstructLabel("Please select the lower right hand corner of the box");
			} else if (step==2) {
				lowerRightCorner.setLocation(event.getX(), event.getY());
				rect.add(lowerRightCorner);
				data.getVideo().setArenaBounds(rect);
				changeStepAndInstructLabel("Please select the lower left hand corner of the box.");
			} else if (step==3) {
				lowerLeftCorner.setLocation(event.getX(), event.getY());
				changeStepAndInstructLabel("Please select where you would like your origin to be located.");
				gc.setFill(Color.AQUA);
			} else if (step==4) {
				pointsCalibrated=true;
				origin.setLocation(event.getX(), event.getY());
				data.getVideo().setOriginPoint(origin);
				openEmptyFrameDialog();
				step=0;
				endCalibration();
			}
		}
    }
	
	private void changeStepAndInstructLabel(String instructions) {
		step++;
		instructLabel.setText(instructions);
	}
    
    public void openEmptyFrameDialog() {
    	TextInputDialog dialog = new TextInputDialog("0:00");
    	dialog.setTitle("Calibration");
    	dialog.setHeaderText("Empty Box Calibration");
    	dialog.setContentText("Please enter a time in which the box has no chickens: ");
    	
    	Optional<String> result = dialog.showAndWait();
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
    private void handleClose(ActionEvent event) {
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
    private void handleContinueBtn(MouseEvent event) throws IOException {
    	if (data.getVideo().getArenaBounds() == null) {
    		makeAlert(AlertType.INFORMATION, "Continue", null, "Set Arena Bounds First (Press Calibration button)");
    	} else if (data.getVideo().getYPixelsPerCm() == 0) {
    		makeAlert(AlertType.INFORMATION, "Continue", null, "Set Box Width and Height first (Press Set Box Lengths)");
    	} else if (data.getAnimalTracksList().size() == 0) {
    		makeAlert(AlertType.INFORMATION, "Continue", null, "Add Chicks first (Press Add Chicken)");
    	} else {
    		Alert alert = makeAlert(AlertType.CONFIRMATION,"Continue", "You are about to leave the Preview Window", "Please review your calibration.\n"
	    			+ "Once you continue you will not be able to make any changes.\n Would you like to continue?");
    		
    		Optional<ButtonType> result = alert.showAndWait();
        	if (result.get() == ButtonType.OK){
    			showAutoTrackWindow();
        	} else {
        	    alert.close();
        	}
    	}
    }
    
    private void showAutoTrackWindow() throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("AutoTrackWindow.fxml"));
		BorderPane root = (BorderPane)loader.load();
		AutoTrackController controller = loader.getController();
		Scene scene = new Scene(root,root.getPrefWidth(),root.getPrefHeight());
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage primary = (Stage) continueBtn.getScene().getWindow();
		primary.setScene(scene);
		primary.centerOnScreen();
		primary.setResizable(false);
		controller.initializeWithStage(primary);
		controller.loadVideo(data.getVideo().getFilePath(), data);
		primary.show();
    }
    
    @SuppressWarnings("static-access")
	private Alert makeAlert(AlertType alertType, String title, String header, String text) {
    	Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(text);
		if (!alertType.equals(alertType.CONFIRMATION)) {
			alert.showAndWait();
		}
		return alert;
    }
    
  //Avery... still working on it
    @FXML //throw exception if non number is entered.... or prevent it from being entered
    private void handleEndTime(KeyEvent event) {
    	keyIgnore(event);

    	String result = endField.getText();
    	int index = result.indexOf(":");
    	if(index!=-1) {
    		String minsString =result.substring(0, index);
        	String secsString = result.substring(index+1);
	    	if(!(minsString.equals("")) && minsString!=null && !(secsString.equals("")) && secsString!=null) {
	    		int mins = Integer.valueOf(minsString);
	    		int secs = Integer.valueOf(secsString);
	    		int endFrame = data.getVideo().getTimeInFrames(mins*60+secs);
	    		data.getVideo().setEndFrameNum(endFrame);
	    		jumpToFrame(data.getVideo().getEndFrameNum());
	    	}
    	}
    }
    
    @FXML //throw exception if non number is entered... or prevent it from being entered
    private void handleStartTime(KeyEvent event) {
    	keyIgnore(event);
    	
    	String result = startField.getText();
    	int index = result.indexOf(':');
    	if(index!=-1) {
    		String minsString =result.substring(0, index);
        	String secsString = result.substring(index+1);
	    	if(!(minsString.equals("")) && minsString!=null && !(secsString.equals("")) && secsString!=null) {
	    		int mins = Integer.valueOf(minsString);
	    		int secs = Integer.valueOf(secsString);
	    		int startFrame = data.getVideo().getTimeInFrames(mins*60+secs);
	    		data.getVideo().setStartFrameNum(startFrame);
	    		jumpToFrame(data.getVideo().getStartFrameNum());
	    	}
    	}
    }

    @FXML
    private void handleSettings(ActionEvent event) {

    }
    
    //For sample1.mp4: Height ~43 cm; Width ~ 71cm
    @FXML
    private void handleSetLengthsBtn(MouseEvent event) {
    	if(pointsCalibrated) {
    		openFirstDialog();
        	openSecondDialog();
    	} else {
    		makeAlert(AlertType.INFORMATION, "Calibration", null, "Set Calibration first (Press Calibrate");
    	}
    }
    
    public void openFirstDialog() {
    	TextInputDialog dialog = new TextInputDialog("0");
    	dialog.setTitle("Additional Calibration");
    	dialog.setHeaderText("Length Calibration");
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
    	dialog.setTitle("Additional Calibration");
    	dialog.setHeaderText("Length Calibration");
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
   private void handleAddChickBtn(MouseEvent event) {
    	String suggestedInput = "Chick #" + (chicksComboBox.getItems().size() + 1);
		TextInputDialog dialog = new TextInputDialog(suggestedInput);
		dialog.setTitle("Add Chick:");
		dialog.setHeaderText(null);
		dialog.setContentText("Enter Chick Name:");
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
    private void handleRemoveChickBtn(MouseEvent event) {
    	if (chicksComboBox.getItems().size() == 0) {
    		makeAlert(AlertType.INFORMATION, "Remove Chick", null, "You must add a chick before removing. (Press Add Chick)");
    	} else {
			ChoiceDialog<String> dialog = new ChoiceDialog<String>(chicksComboBox.getItems().get(0), chicksComboBox.getItems());
			dialog.setTitle("Remove Chick");
			dialog.setHeaderText("What chick do you want to remove?");
			dialog.setContentText("Choose Chick Name:");
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				String chickName = result.get();
				data.getAnimalTracksList().remove(new AnimalTrack(chickName));
				chicksComboBox.getItems().remove(chickName);
			}	
			//credit: https://code.makery.ch/blog/javafx-dialogs-official/
    	}
    }
    
	@FXML
	public void runSliderSeekBar() {
		int frameNum = (int) sliderSeekBar.getValue();
		jumpToFrame(frameNum);
	}
    
	private void jumpToFrame(int numOfFrame) {
		data.getVideo().getVidCap().set(Videoio.CAP_PROP_POS_FRAMES, numOfFrame);
		updateFrameView();

	}
	
}

