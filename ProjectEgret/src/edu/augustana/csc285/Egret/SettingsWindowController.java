package edu.augustana.csc285.Egret;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;

public class SettingsWindowController {

	@FXML
	private MenuItem closeOption;

	@FXML
	private RadioButton multSamplesOption;

	@FXML
	private CheckBox velocityDataOption;

	@FXML
	private CheckBox positionDataOption;

	@FXML
	private Spinner<?> samplesSelection;

	@FXML
	private TextField intervalBox;

	@FXML
	private TextField startTimeBox;

	@FXML
	private TextField endTimeBox;

	@FXML
	void exitWindow(ActionEvent event) {

	}

	@FXML
	void passNumSamples(InputMethodEvent event) {

	}

	@FXML
	void recordPosition(ActionEvent event) {

	}

	@FXML
	void recordVelocity(ActionEvent event) {

	}

	@FXML
	void takeMultSamples(ActionEvent event) {

	}

	@FXML
	void updateEndFrame(InputMethodEvent event) {

	}

	@FXML
	void updateFrameInterval(InputMethodEvent event) {

	}

	@FXML
	void updateStartFrame(InputMethodEvent event) {

	}

}
