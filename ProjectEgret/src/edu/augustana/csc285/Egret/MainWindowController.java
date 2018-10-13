package edu.augustana.csc285.Egret;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

import autotracking.AutoTrackListener;
import autotracking.AutoTracker;
import datamodel.AnimalTrack;
import datamodel.ProjectData;
import datamodel.TimePoint;
import datamodel.Video;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.augustana.csc285.Egret.Utils;

public class MainWindowController implements AutoTrackListener {

	@FXML private Button btnBrowse;
	@FXML private ImageView videoView;
	@FXML private Slider sliderVideoTime;
	@FXML private TextField textFieldCurFrameNum;

	@FXML private TextField textfieldStartFrame;
	@FXML private TextField textfieldEndFrame;
	@FXML private Button btnAutotrack;
	@FXML private ProgressBar progressAutoTrack;

	
	private AutoTracker autotracker;
	private ProjectData project;
	private Stage stage;
	private Video video;
	
	@FXML public void initialize() {
		
		//FIXME: this quick loading of a specific file and specific settings 
		//       is for debugging purposes only, since there's no way to specify
		//       the settings in the GUI right now...
		//loadVideo("/home/forrest/data/shara_chicks_tracking/sample1.mp4");
		loadVideo("S:/class/cs/285/sample_videos/sample1.mp4");		
		project.getVideo().setXPixelsPerCm(6.5); //  these are just rough estimates!
		project.getVideo().setYPixelsPerCm(6.7);

//		loadVideo("/home/forrest/data/shara_chicks_tracking/lowres/lowres2.avi");
		//loadVideo("S:/class/cs/285/sample_videos/lowres2.mp4");		
//		project.getVideo().setXPixelsPerCm(5.5); //  these are just rough estimates!
//		project.getVideo().setYPixelsPerCm(5.5);
		
		sliderVideoTime.valueProperty().addListener((obs, oldV, newV) -> showFrameAt(newV.intValue())); 
	}
	
	public void initializeWithStage(Stage stage) {
		this.stage = stage;
		
		// bind it so whenever the Scene changes width, the videoView matches it
		// (not perfect though... visual problems if the height gets too large.)
		videoView.fitWidthProperty().bind(videoView.getScene().widthProperty());  
	}
	
	@FXML
	public void handleBrowse()  {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		File chosenFile = fileChooser.showOpenDialog(stage);
		if (chosenFile != null) {
			loadVideo(chosenFile.getPath());
		}		
	}
	
	public void loadVideo(String filePath) {
		try {
			project = new ProjectData(filePath);
			video = project.getVideo();
			sliderVideoTime.setMax(video.getTotalNumFrames()-1);
			showFrameAt(0);
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}

	}
	
	public void showFrameAt(int frameNum) {
		if (autotracker == null || !autotracker.isRunning()) {
			project.getVideo().setCurrentFrameNum(frameNum);
			Image curFrame = Utils.matToJavaFXImage(project.getVideo().readFrame());
			videoView.setImage(curFrame);
			textFieldCurFrameNum.setText(String.format("%05d",frameNum));
			
		}		
	}
	
	@FXML
	public void handleStartAutotracking() throws InterruptedException {
		if (autotracker == null || !autotracker.isRunning()) {
			Video video = project.getVideo();
			video.setStartFrameNum(Integer.parseInt(textfieldStartFrame.getText()));
			video.setEndFrameNum(Integer.parseInt(textfieldEndFrame.getText()));
			autotracker = new AutoTracker();
			// Use Observer Pattern to give autotracker a reference to this object, 
			// and call back to methods in this class to update progress.
			autotracker.addAutoTrackListener(this);
			
			// this method will start a new thread to run AutoTracker in the background
			// so that we don't freeze up the main JavaFX UI thread.
			autotracker.startAnalysis(video);
			btnAutotrack.setText("CANCEL auto-tracking");
		} else {
			autotracker.cancelAnalysis();
			btnAutotrack.setText("Start auto-tracking");
		}
		 
	}

	// this method will get called repeatedly by the Autotracker after it analyzes each frame
	@Override
	public void handleTrackedFrame(Mat frame, int frameNumber, double fractionComplete) {
		Image imgFrame = Utils.matToJavaFXImage(frame);
		// this method is being run by the AutoTracker's thread, so we must
		// ask the JavaFX UI thread to update some visual properties
		Platform.runLater(() -> { 
			videoView.setImage(imgFrame);
			progressAutoTrack.setProgress(fractionComplete);
			sliderVideoTime.setValue(frameNumber);
			textFieldCurFrameNum.setText(String.format("%05d",frameNumber));
		});		
	}

	@Override
	public void trackingComplete(List<AnimalTrack> trackedSegments){
		project.getUnassignedSegments().clear();
		project.getUnassignedSegments().addAll(trackedSegments);

		for (AnimalTrack track: trackedSegments) {
			System.out.println(track.getName() + "Num of Points " + track.getNumPoints() + " first point: " + track.getFirstTimePoint() + " last point: " + track.getFinalTimePoint());
//			System.out.println("  " + track.getPositions());
		}
		Platform.runLater(() -> { 
			progressAutoTrack.setProgress(1.0);
			btnAutotrack.setText("Start auto-tracking");
		});	
		
		try {
			File autoTrackData = new File("full_auto_tracker_data");
			project.saveToFile(autoTrackData);
		} catch (FileNotFoundException e) {
		}
		
		//mergeTrackObjects(trackedSegments);
		
	}
	
	
//	public void mergeTrackObjects(List<AnimalTrack> trackedSegments) {
//		int startFrame = video.getStartFrameNum();
//		List<AnimalTrack> finalAnimalList = new ArrayList<AnimalTrack>();
//		for(int i = 0; i < trackedSegments.size(); i++) {
//			if(trackedSegments.get(i).getFirstFrame() == startFrame) {
//				AnimalTrack tempAnimalTrack = trackedSegments.get(i); 
//				finalAnimalList.add(tempAnimalTrack);
//				trackedSegments.remove(i);
//			}
//		}
////		for(int i = 0; i < trackedSegments.size(); i++) {
////			if(trackedSegments.get(i).getFirstFrame() == startFrame) {
////				trackedSegments.remove(i);
////			}
////		}
//		for (AnimalTrack track: finalAnimalList) {
//			System.out.println("final: " + track.getName() + "Num of Points " + track.getNumPoints() + " first point: " + track.getFirstTimePoint() + " last point: " + track.getFinalTimePoint());
//		}
//		
//		for (AnimalTrack track: trackedSegments) {
//			System.out.println("unknown: " + track.getName() + "Num of Points " + track.getNumPoints() + " first point: " + track.getFirstTimePoint() + " last point: " + track.getFinalTimePoint());
//		}
//		for(int i = 0; i<finalAnimalList.size(); i++) {
//			//int trackedSegmentsSize = trackedSegments.size();
//			int j = 0;
//			boolean foundData = false;
//			while(!foundData ||  j > trackedSegments.size()) {
//				System.out.println(j);
//				AnimalTrack currentFinalAnimal = finalAnimalList.get(i);
//				AnimalTrack currentUnknownTrack = trackedSegments.get(j);
//				System.out.println(currentFinalAnimal.getName() + currentFinalAnimal.getFirstFrame());
//				System.out.println(currentUnknownTrack.getName() + currentUnknownTrack.getFirstFrame());
//				if(currentUnknownTrack.getFirstFrame() - currentFinalAnimal.getLastFrame() < 50) {
//					System.out.println("hi");
//					if(Math.sqrt(Math.pow(currentFinalAnimal.getTimePointAtIndex(currentFinalAnimal.getNumPoints()).getX()- currentUnknownTrack.getTimePointAtIndex(0).getX(), 2) + 
//							Math.pow(currentFinalAnimal.getTimePointAtIndex(currentFinalAnimal.getNumPoints()).getY()- currentUnknownTrack.getTimePointAtIndex(0).getY(), 2)) < 40) {
//						currentFinalAnimal.addTrackSegment(currentUnknownTrack);
//						//trackedSegments.remove(j);
//						foundData = true;
//					}
//				}
//				j++;
//			}
//		}
//		for (AnimalTrack track: finalAnimalList) {
//			System.out.println(track.getName() + "Num of Points " + track.getNumPoints() + " first point: " + track.getFirstTimePoint() + " last point: " + track.getFinalTimePoint());
//		}
//		
//		
//	}
	
	
	
}
