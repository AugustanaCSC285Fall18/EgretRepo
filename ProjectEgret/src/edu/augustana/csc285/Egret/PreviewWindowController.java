/**
 * Description: Houses the video calibration and originally creates the ProjectData object.
 */

package edu.augustana.csc285.Egret;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.opencv.core.Mat;
import org.opencv.videoio.Videoio;

import datamodel.AnimalTrack;
import datamodel.ProjectData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import javafx.stage.Stage;
import javafx.stage.Window;

public class PreviewWindowController {
	// FXML GUI Fields
	@FXML
	private ComboBox<String> chicksComboBox;

	@FXML
	private Button addChickBtn;

	@FXML
	private Button removeChickBtn;

	@FXML
	private ImageView currentFrameImage;

	@FXML
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

	private GraphicsContext gc;

	// Data Fields
	private static ProjectData data;
	boolean pointsCalibrated = false;
	Point upperLeftCorner = new Point();
	Point lowerRightCorner = new Point();
	Point lowerLeftCorner = new Point();
	Point origin = new Point();
	int step = 0;

	/**
	 * Ignores a key press if it is a letter
	 * 
	 * @param event - key press
	 */
	public void keyIgnore(KeyEvent event) {
		char character = event.getCharacter().charAt(0);
		if (Character.isAlphabetic(character)) {
			((TextField) event.getSource()).deletePreviousChar();
		}
	}

	/**
	 * Starts the video: sets the slider bar to the correct size and updates the
	 * frame view.
	 */
	protected void startVideo() {
		// start the video capture
		double totalNumFrames = data.getVideo().getVidCap().get(Videoio.CV_CAP_PROP_FRAME_COUNT);
		updateFrameView();
		sliderSeekBar.setMax((int) totalNumFrames - 1);
		sliderSeekBar.setMaxWidth((int) totalNumFrames - 1);
		sliderSeekBar.setDisable(false);
		jumpToFrame(0);
	}

	/**
	 * @return the current image view
	 */
	public ImageView getCurrentFrameImage() {
		return currentFrameImage;
	}

	/**
	 * Get a frame from the opened video stream (if any)
	 * 
	 * @return the {@link Mat} to show
	 */
	@SuppressWarnings("static-access")
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

	/**
	 * Updates the frame view.
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
	 * Sets the time step of the ProjectData video object
	 */
	public void setTimeStep() {
		data.getVideo().setTimeStep(timeStepBox.getSelectionModel().getSelectedItem());
	}

	/**
	 * Initializes the timeStepBox items, the graphics context, and disables the
	 * buttons.
	 */
	@FXML
	public void initialize() {
		List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
		ObservableList<Integer> items = FXCollections.observableList(list);
		timeStepBox.setItems(items);
		timeStepBox.getSelectionModel().select(0);
		gc = canvas.getGraphicsContext2D();
		disableButtons();
	}

	/**
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
		timeStepBox.setDisable(true);
		startField.setDisable(true);
		endField.setDisable(true);
	}

	/**
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
		timeStepBox.setDisable(false);
		startField.setDisable(false);
		endField.setDisable(false);
	}

	/**
	 * Allows the user to select a video; initializes the ProjectData object;
	 * enables the buttons if the user has selected a video; Makes an alert if the
	 * user selects a non-video object.
	 */
	@FXML
	private void handleBrowse() throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		Window mainWindow = currentFrameImage.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		if (chosenFile != null) {
			loadBtn.setText("Load Different Video");
			String fileName = chosenFile.toURI().toString();
			try {
				data = new ProjectData(fileName);
				data.getVideo().setTimeStep(1);
				startVideo();
				startField.setText("0:00");
				int endTime = data.getVideo().getTimeInSeconds(data.getVideo().getEndFrameNum());
				endField.setText(endTime / 60 + ":" + endTime % 60);
				enableButtons();
			} catch (FileNotFoundException e) {
				makeAlert(AlertType.INFORMATION, "Load Video", null, "Please select an appropriate video file.");
			}
		} else {
			makeAlert(AlertType.INFORMATION, "Load Video", null, "Please load a video.");
		}
	}

	/**
	 * handles first step of calibration, handleCanvasClick handles the rest
	 */
	@FXML
	private void handleCalibration(MouseEvent event) {
		if (step == 0) {
			step = 1;
			instructLabel.setText("Please select the upper LEFT corner of the box.");
		}
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setFill(Color.BLUEVIOLET);
	}

	/**
	 * handles the canvas clicks: first click the upper left, then the lower left,
	 * then the lower right, then the origin.
	 */
	@FXML
	private void handleCanvasClick(MouseEvent event) throws InterruptedException {
		Rectangle rect = new Rectangle();
		if (data == null) {
			makeAlert(AlertType.INFORMATION, "Calibrate", null, "Select a video before attempting to calibrate.");
		} else if (step == 0) {
			makeAlert(AlertType.INFORMATION, "Calibrate", null, "Press Calibrate first.");
		} else {
			gc.fillOval(event.getX() - (15 / 2), event.getY() - (15 / 2), 15, 15);
			System.out.println(step);
			if (step == 1) {
				upperLeftCorner.setLocation(event.getX(), event.getY());
				rect.add(upperLeftCorner);
				changeStepAndInstructLabel("Please select the lower LEFT hand corner of the box.");
			} else if (step == 2) {
				lowerLeftCorner.setLocation(event.getX(), event.getY());
				changeStepAndInstructLabel("Please select the lower RIGHT hand corner of the box");
			} else if (step == 3) {
				lowerRightCorner.setLocation(event.getX(), event.getY());
				rect.add(lowerRightCorner);
				data.getVideo().setArenaBounds(rect);
				changeStepAndInstructLabel("Please select where you would like your origin to be located.");
				gc.setFill(Color.AQUA);
			} else if (step == 4) {
				pointsCalibrated = true;
				origin.setLocation(event.getX(), event.getY());
				data.getVideo().setOriginPoint(origin);
				openEmptyFrameDialog();
				endCalibration();
			}
		}
	}

	/**
	 * Updates the step and instruction label
	 * 
	 * @param instructions - the next direction step for the instruction label
	 */
	private void changeStepAndInstructLabel(String instructions) {
		step++;
		instructLabel.setText(instructions);
	}

	/**
	 * Opens the empty frame dialog box to get the empty frame.
	 */
	public void openEmptyFrameDialog() {
		TextInputDialog dialog = new TextInputDialog("0:00");
		dialog.setTitle("Calibration");
		dialog.setHeaderText("Empty Box Calibration");
		dialog.setContentText("Please enter a time in which the box has no chickens: ");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String string = result.get();
			if (string.indexOf(":") != -1) {
				int index = string.indexOf(":");
				int mins = Integer.valueOf(string.substring(0, index));
				int secs = Integer.valueOf(string.substring(index + 1));
				int emptyFrame = data.getVideo().getTimeInFrames(mins * 60 + secs);
				data.getVideo().setEmptyFrameNum(emptyFrame);
			}
		}
	}

	/**
	 * Resets calibration step and tells the user that they have finished
	 * calibrating.
	 */
	public void endCalibration() {
		step = 0;
		instructLabel.setText("You are done calibrating! Press calibrate again to reset calibration.");
		// wait 2 seconds
		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> instructLabel.setText(""));
			}
		}, 2000); // the delay time in milliseconds
	}

	/**
	 * Allows the user to progress to the next window: AutoTrackWindow.
	 * 
	 * @param event - the user clicking on the continue button
	 * @throws IOException if there is an error when loading the next window
	 */
	@FXML
	private void handleContinueBtn(MouseEvent event) throws IOException {
		if (data.getVideo().getArenaBounds() == null) {
			makeAlert(AlertType.INFORMATION, "Continue", null, "Set Arena Bounds First (Press Calibration button)");
		} else if (data.getVideo().getYPixelsPerCm() == 0) {
			makeAlert(AlertType.INFORMATION, "Continue", null,
					"Set Box Width and Height first (Press Set Box Lengths)");
		} else if (data.getAnimalTracksList().size() == 0) {
			makeAlert(AlertType.INFORMATION, "Continue", null, "Add Chicks first (Press Add Chicken)");
		} else {
			Alert alert = makeAlert(AlertType.CONFIRMATION, "Continue", "You are about to leave the Preview Window",
					"Please review your calibration.\n"
							+ "Once you continue you will not be able to make any changes.\n Would you like to continue?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				showAutoTrackWindow();
			} else {
				alert.close();
			}
		}
	}

	/**
	 * Actually sends the user to the next window.
	 * 
	 * @throws IOException - if there is an error when loading the next window
	 */
	private void showAutoTrackWindow() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("AutoTrackWindow.fxml"));
		BorderPane root = (BorderPane) loader.load();
		AutoTrackController controller = loader.getController();
		Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage primary = (Stage) continueBtn.getScene().getWindow();
		primary.setScene(scene);
		controller.initializeWithStage(primary);
		controller.loadVideo(data.getVideo().getFilePath(), data);
		primary.show();
	}

	/**
	 * Makes an alert window. Returns an alert if the alert needs to be shown and
	 * waited on for a result.
	 * 
	 * @param alertType - the type of alert (ie AlertType.INFORMATION)
	 * @param title     - the title for the alert
	 * @param header    - the header for the alert (usually null)
	 * @param text      - the body of the alert
	 * @return the alert
	 */
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

	/**
	 * Allows the user to input an end time. Makes an alert box if they type in a
	 * non number.
	 */
	@FXML
	private void handleEndTime(KeyEvent event) {
		keyIgnore(event);
		String result = endField.getText();
		int index = result.indexOf(":");
		if (index != -1) {
			String minsString = result.substring(0, index);
			String secsString = result.substring(index + 1);
			if (!(minsString.equals("")) && minsString != null && !(secsString.equals("")) && secsString != null) {
				try {
					int mins = Integer.valueOf(minsString);
					int secs = Integer.valueOf(secsString);
					int endFrame = data.getVideo().getTimeInFrames(mins * 60 + secs);
					data.getVideo().setEndFrameNum(endFrame);
				} catch (NumberFormatException e) {
					((TextField) event.getSource()).deletePreviousChar();
					makeAlert(AlertType.INFORMATION, "End Time", null, "Please enter a number for the end time.");
				}
				jumpToFrame(data.getVideo().getEndFrameNum());
			}
		}
	}

	/**
	 * Allows the user to input a start time. Makes a alert box if they type in a
	 * non number.
	 */
	@FXML
	private void handleStartTime(KeyEvent event) {
		keyIgnore(event);

		String result = startField.getText();
		int index = result.indexOf(':');
		if (index != -1) {
			String minsString = result.substring(0, index);
			String secsString = result.substring(index + 1);
			if (!(minsString.equals("")) && minsString != null && !(secsString.equals("")) && secsString != null) {
				try {
					int mins = Integer.valueOf(minsString);
					int secs = Integer.valueOf(secsString);
					int startFrame = data.getVideo().getTimeInFrames(mins * 60 + secs);
					data.getVideo().setStartFrameNum(startFrame);
				} catch (NumberFormatException e) {
					((TextField) event.getSource()).deletePreviousChar();
					makeAlert(AlertType.INFORMATION, "Start Time", null, "Please enter a number for the start time.");
				}

				jumpToFrame(data.getVideo().getStartFrameNum());
			}
		}
	}

	/**
	 * Allows the user to calibrate the length of the box.
	 */
	@FXML
	private void handleSetLengthsBtn(MouseEvent event) {
		if (pointsCalibrated) {
			for (int i = 0; i < 2; i++) {
				TextInputDialog dialog;
				if (i == 0) {
					dialog = openTextInputDialog("0", "Additional Calibration", "Length Calibration",
							"Please enter the height of the box in cm: ");
				} else {
					dialog = openTextInputDialog("0", "Additional Calibration", "Length Calibration",
							"Please enter the width of the box in cm: ");
				}
				Optional<String> result = dialog.showAndWait();
				if (result != null) {
					double length = Double.valueOf(result.get());
					if (result.isPresent()) {
						if (i == 0) {
							data.getVideo().setYPixelsPerCm(length, upperLeftCorner, lowerLeftCorner);
						} else {
							data.getVideo().setXPixelsPerCm(length, lowerRightCorner, lowerLeftCorner);
						}

					}
				}
			}
			// citation: https://code.makery.ch/blog/javafx-dialogs-official/
		} else {
			makeAlert(AlertType.INFORMATION, "Calibration", null, "Set Calibration first (Press Calibrate");
		}
	}

	/**
	 * Makes a TextInputDialog box. Returns if the dialog box needs to be shown and
	 * waited on for a result.
	 * 
	 * @param initial - initial text to display
	 * @param title   - title of the dialog box
	 * @param header  - header of the dialog box (can be null)
	 * @param content - body text of the dialog box
	 * @return the TextInputDialogBox
	 */
	private TextInputDialog openTextInputDialog(String initial, String title, String header, String content) {
		TextInputDialog dialog = new TextInputDialog(initial);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		return dialog;
	}

	/**
	 * Allows the user to add a chick to the ProjectData.
	 * 
	 * @param event - Add Chicken btn was pressed
	 */
	@FXML
	private void handleAddChickBtn(MouseEvent event) {
		// Ensures that there is a chick of each number if they use the suggested
		// inputs.
		int number = 1;
		String suggestedInput = "Chick #" + number;
		while (chicksComboBox.getItems().contains(suggestedInput)) {
			suggestedInput = "Chick #" + (number++);
		}

		TextInputDialog dialog = openTextInputDialog(suggestedInput, "Add Chick:", null, "Enter Chick Name:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String chickName = result.get();
			data.getAnimalTracksList().add(new AnimalTrack(chickName));
			chicksComboBox.getItems().add(chickName);
			chicksComboBox.getSelectionModel().select(chickName);
		}
	}

	/**
	 * Allows the user to remove a chick from the ProjectData
	 * 
	 * @param event - Remove Chicken btn was pressed
	 */
	@FXML
	private void handleRemoveChickBtn(MouseEvent event) {
		if (chicksComboBox.getItems().size() == 0) {
			makeAlert(AlertType.INFORMATION, "Remove Chick", null,
					"You must add a chick before removing. (Press Add Chick)");
		} else {
			ChoiceDialog<String> dialog = new ChoiceDialog<String>(chicksComboBox.getItems().get(0),
					chicksComboBox.getItems());
			dialog.setTitle("Remove Chick");
			dialog.setHeaderText("What chick do you want to remove?");
			dialog.setContentText("Choose Chick Name:");
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				String chickName = result.get();
				data.getAnimalTracksList().remove(new AnimalTrack(chickName));
				chicksComboBox.getItems().remove(chickName);
			}
			// credit: https://code.makery.ch/blog/javafx-dialogs-official/
		}
	}

	/**
	 * Changes the image view using the slider bar
	 */
	@FXML
	public void runSliderSeekBar() {
		int frameNum = (int) sliderSeekBar.getValue();
		jumpToFrame(frameNum);
	}

	/**
	 * Changes the image view to the specified frame number.
	 * 
	 * @param framenum - the frame number to change to
	 */
	private void jumpToFrame(int framenum) {
		data.getVideo().getVidCap().set(Videoio.CAP_PROP_POS_FRAMES, framenum);
		updateFrameView();

	}

}
