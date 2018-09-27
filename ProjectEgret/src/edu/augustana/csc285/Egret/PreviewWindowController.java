package edu.augustana.csc285.Egret;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class PreviewWindowController {

    @FXML
    private ImageView currentFrameImage;

    @FXML
    private Slider sliderSeekBar;

    @FXML
    private MenuItem returnOption;

    @FXML
    private MenuItem closeOption;

    @FXML
    private MenuItem settingsOption;

    @FXML
    private Button returnBtn;

    @FXML
    private Button continueBtn;

    @FXML
    void exitWindow(ActionEvent event) {

    }

    @FXML
    void openExportWindow(MouseEvent event) {

    }

    @FXML
    void openSettingsWindow(ActionEvent event) {

    }

    @FXML
    void returnToPrevWindow(MouseEvent event) {

    }

}
