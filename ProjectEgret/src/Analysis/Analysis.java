package Analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileNotFoundException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import datamodel.AnimalTrack;
import datamodel.ProjectData;
import datamodel.TimePoint;
import datamodel.Video;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class Analysis {

	private static ProjectData data;
	private static ImageView currentFrameImage;
	
	/**
	 * Runs all of the analysis; makes a CSV file for each type of analysis (Total Distance/Average Distance and 
	 * list of points)
	 * @param dataInformation - ProjectData object to get the information from
	 * @param curFrameImage - the imageview so that they can have a save dialog box
	 * @throws IOException if the user gives a file that cannot be read to
	 */
	public static void runAnalysis(ProjectData dataInformation, ImageView curFrameImage) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		data = dataInformation;
		currentFrameImage = curFrameImage;
		exportToCSV();
		averageDistanceBetweenPointsAndTotalDistanceToCSV();
	}

	/**
	 * Exports the tracks data (TimePoints of each AnimalTrack object in the ProjectData object, data)
	 * @throws IOException if the user gives a file that cannot be read to
	 */
	public static void exportToCSV() throws IOException {
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		Window mainWindow = currentFrameImage.getScene().getWindow();
		File chosenFile = fileChooser.showSaveDialog(mainWindow);

		FileWriter fileWriter = new FileWriter(chosenFile);
		
		StringBuilder s = new StringBuilder();
		int maxNumTimePoints = 0;

		for (int i = 0; i < data.getAnimalTracksList().size(); i++) {
			if (data.getAnimalTracksList().get(i).getNumPoints() > maxNumTimePoints) {
				maxNumTimePoints = data.getAnimalTracksList().get(i).getNumPoints();
			}
		}

		for (int i = 0; i < data.getAnimalTracksList().size(); i++) {
			s.append(data.getAnimalTracksList().get(i).getName());
			s.append(",,,,");
		}
		s.append('\n');

		for (int i = 0; i < data.getAnimalTracksList().size(); i++) {
			s.append("Time,X-Position,Y-Position,,");
		}
		s.append('\n');

		//for (int i = 0; i < maxNumTimePoints; i++) {
		int maxSeconds = (int) (data.getVideo().getEndFrameNum() / data.getVideo().getFrameRate());
		for (int i = 0; i < maxSeconds; i++) {
			for (int j = 0; j < data.getAnimalTracksList().size(); j++) {
				AnimalTrack currentTrack = data.getAnimalTracksList().get(j);
				if (currentTrack.hasTimePointAtIndex(i)) {
					//TimePoint currentTP = currentTrack.getTimePointAtIndex(i);
					TimePoint currentTP = getPointAtSpecificSecond(currentTrack, i);
					//s.append(currentTP.getFrameNum() + ",");
					s.append(i + ",");
					s.append(currentTP.getX()+ ",");
					s.append(currentTP.getY()+ ",");
				} else {
					s.append(",,,,");
				}
			}
			s.append('\n');
		}
		fileWriter.append(s);
		fileWriter.close();
	}
	
	private static TimePoint getPointAtSpecificSecond(AnimalTrack animal, int second) {
		for (TimePoint pt : animal.getLocations()) {
			if (pt.getFrameNum() == second * data.getVideo().getFrameRate()) {
				return pt;
			}
		}
		return null;
	}

	public static double[] totalDistanceTraveled() {
		double[] distanceTraveledList = new double[data.getAnimalTracksList().size()];
		for (int i = 0; i < data.getAnimalTracksList().size(); i++) {
			AnimalTrack currentTrack = data.getAnimalTracksList().get(i);
			for (int j = 1; j < currentTrack.getNumPoints() - 1; j++) {
				double tempDistance = currentTrack.getTimePointAtIndex(j)
						.getDistanceTo(currentTrack.getTimePointAtIndex(j - 1));
				distanceTraveledList[i] += tempDistance;
			}
		}
		return distanceTraveledList;
	}

	public static void averageDistanceBetweenPointsAndTotalDistanceToCSV() throws IOException {	
		double[] distanceTraveledList = totalDistanceTraveled();
		FileWriter fileWriter = new FileWriter(new File("Total Distance and Average Distance " + data.getVideo().getFilePathJustName() + ".csv"));
		StringBuilder s = new StringBuilder();
		
		int numOfTracks = data.getAnimalTracksList().size();

		for(int i = 0; i < numOfTracks; i++) {
			AnimalTrack currentAnimal = data.getAnimalTracksList().get(i);
			s.append("Total Distance " + currentAnimal.getName() + " Traveled: " + distanceTraveledList[i]);
			s.append('\n');
		}
		s.append('\n');

		for(int i = 0; i < numOfTracks-1; i++) {
			AnimalTrack currentTrack = data.getAnimalTracksList().get(i);
			for(int j = i+1; j < numOfTracks; j++) {
				s.append(data.getAnimalTracksList().get(i).getName() + " to " + data.getAnimalTracksList().get(j).getName());
				s.append('\n');
				s.append("Time");
				s.append(",");
				s.append("Distance");
				s.append('\n');
				AnimalTrack comparingTrack = data.getAnimalTracksList().get(j);
				for(int k = 0; k < currentTrack.getNumPoints(); k++) {
					if(comparingTrack.hasTimePointAtTime(currentTrack.getTimePointAtIndex(k).getFrameNum())) {
						TimePoint currentTrackCurrentTP = currentTrack.getTimePointAtIndex(k);
						TimePoint comparingTrackCurrentTP = comparingTrack.getTimePointAtTime(currentTrackCurrentTP.getFrameNum());
						s.append(currentTrackCurrentTP.getFrameNum());
						s.append(",");
						double distance = Math.sqrt(Math.pow(currentTrackCurrentTP.getX() - comparingTrackCurrentTP.getX(), 2) + Math.pow(currentTrackCurrentTP.getY() - comparingTrackCurrentTP.getY(),2));
						s.append(distance);
						s.append(",,");
						s.append('\n');
					}
				}
				s.append('\n');
			}
		}
		fileWriter.append(s);
		fileWriter.close();
	}

}