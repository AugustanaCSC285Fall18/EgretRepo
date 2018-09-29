package  edu.augustana.csc285.Egret;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class MainWindowController {

	@FXML private ImageView videoView;
	@FXML private Button browseButton;
	@FXML private VideoCapture vidCap = new VideoCapture();
	@FXML private Slider slider;
	@FXML private TextArea currentFrameArea;
	@FXML private TextArea totalFrameArea;
	@FXML private TextField jumpToFrameArea;
	private Image currentFrameImage;
	private Video video;
	private String fileName;
	
	@FXML 
	public void runSlider() {
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				video.setCurFrameNum(Math.round(newValue.doubleValue()));
				vidCap.set(Videoio.CAP_PROP_POS_FRAMES, video.getCurFrameNum());
				videoView.setImage(currentFrameImage);
				getNewImage(newValue);
				currentFrameArea.appendText("Current Frame: " + ((int)video.getCurFrameNum()) + "\n");
				

			}
		});
		
	}
	
	@FXML public void initialize() {
		slider.setDisable(true);
		jumpToFrameArea.setDisable(true);
	}
	
	public void handleBrowse()  {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		Window mainWindow = videoView.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		
		
		if (chosenFile != null) {
			
			startVideo();
		}
		getNewImage(0);
		
	}
	
	private void startVideo() {
		vidCap.open(fileName);
		video.setNumFrame((int) vidCap.get(Videoio.CV_CAP_PROP_FRAME_COUNT));
		totalFrameArea.appendText("Total Frames: " + (int) video.getNumFrame()+ "\n");
		slider.setDisable(false);
		jumpToFrameArea.setDisable(false);
		
	}

	public void getNewImage(Number newValue) {
		Mat mat = new Mat();
		vidCap.set(Videoio.CAP_PROP_POS_FRAMES, newValue.doubleValue());
		vidCap.read(mat);
		
		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".png", mat, buffer);

		currentFrameImage = new Image(new ByteArrayInputStream(buffer.toArray()));

		videoView.setImage(currentFrameImage);
	}
	
	public void updateFrameView() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				videoView.setImage(currentFrameImage);
			}
		});
	}

}
