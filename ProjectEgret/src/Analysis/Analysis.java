/**
 * Description: Intakes a ProjectData object and exports CSV documents for its data and some basic analysis of the data
 */

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
	private static int frameRate;
	private static double ratio;

	/**
	 * Runs all of the analysis; makes a CSV file for each type of analysis (Total
	 * Distance/Average Distance and list of points)
	 * 
	 * @param dataInformation - ProjectData object to get the information from
	 * @param curFrameImage   - the imageview so that they can have a save dialog
	 *                        box
	 * @throws IOException if the user gives a file that cannot be read to
	 */
	public static void runAnalysis(ProjectData dataInformation, ImageView curFrameImage, double videoRatio) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		data = dataInformation;
		currentFrameImage = curFrameImage;
		frameRate = (int) data.getVideo().getFrameRate();
		ratio = videoRatio;
		exportToCSV();
		averageDistanceBetweenPointsAndTotalDistanceToCSV();
	}

	/**
	 * Exports the tracks data (TimePoints of each AnimalTrack object in the
	 * ProjectData object, data)
	 * 
	 * @throws IOException if the user gives a file that cannot be read to
	 */
	public static void exportToCSV() throws IOException {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Data CSV");
		fileChooser.setInitialFileName(data.getVideo().getFilePathJustName() + ".csv");
		Window mainWindow = currentFrameImage.getScene().getWindow();
		File chosenFile = fileChooser.showSaveDialog(mainWindow);
		if (!chosenFile.getPath().contains(".csv")) {
			chosenFile = new File(chosenFile.getPath() + ".csv");
		}
		FileWriter fileWriter = new FileWriter(chosenFile);

		StringBuilder s = new StringBuilder();

		for (int i = 0; i < data.getAnimalTracksList().size(); i++) {
			s.append(data.getAnimalTracksList().get(i).getName());
			s.append(",,,,");
		}
		s.append('\n');

		for (int i = 0; i < data.getAnimalTracksList().size(); i++) {
			s.append("Time,X-Position,Y-Position,,");
		}
		s.append('\n');

		int endFrameNum = data.getVideo().getEndFrameNum();
		int startFrameNum = data.getVideo().getStartFrameNum();
		for (int i = startFrameNum; i < endFrameNum; i += frameRate) {
			for (int j = 0; j < data.getAnimalTracksList().size(); j++) {
				AnimalTrack currentTrack = data.getAnimalTracksList().get(j);
				TimePoint currentTP = currentTrack.getClosestPointToFrameNumber(i);
				s.append(i/frameRate + ",");
				s.append(currentTP.getX()/ratio + ",");
				s.append(currentTP.getY()/ratio + ",,");
			}
			s.append('\n');
		}
		fileWriter.append(s);
		fileWriter.close();
	}


	/**
	 * @return an array containing the total distance traveled for each AnimalTrack
	 *         object in data (ProjectData)
	 */
	public static double[] totalDistanceTraveled() {
		double[] distanceTraveledList = new double[data.getAnimalTracksList().size()];
		for (int i = 0; i < data.getAnimalTracksList().size(); i++) {
			AnimalTrack currentTrack = data.getAnimalTracksList().get(i);
			for (int j = 1; j < currentTrack.getNumPoints() - 1; j++) {
				TimePoint newTP = currentTrack.getTimePointAtIndex(j);
				newTP.setX(newTP.getX()/ratio);
				newTP.setY(newTP.getY()/ratio);
				TimePoint oldTP = currentTrack.getTimePointAtIndex(j - 1); 
				oldTP.setX(oldTP.getX()/ratio);
				oldTP.setY(oldTP.getY()/ratio);
				double tempDistance = newTP.getDistanceTo(oldTP);
				distanceTraveledList[i] += tempDistance;
			}
		}
		return distanceTraveledList;
	}

	/**
	 * 
	 * @throws IOException
	 */
	public static void averageDistanceBetweenPointsAndTotalDistanceToCSV() throws IOException {
		double[] distanceTraveledList = totalDistanceTraveled();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Other Analysis CSV");
		fileChooser.setInitialFileName(
				"Total Distance and Average Distance " + data.getVideo().getFilePathJustName() + ".csv");
		Window mainWindow = currentFrameImage.getScene().getWindow();
		File chosenFile = fileChooser.showSaveDialog(mainWindow);
		if (!chosenFile.getPath().contains(".csv")) {
			chosenFile = new File(chosenFile.getPath() + ".csv");
		}
		FileWriter fileWriter = new FileWriter(chosenFile);
		StringBuilder s = new StringBuilder();

		int numOfTracks = data.getAnimalTracksList().size();

		for (int i = 0; i < numOfTracks; i++) {
			AnimalTrack currentAnimal = data.getAnimalTracksList().get(i);
			s.append("Total Distance " + currentAnimal.getName() + " Traveled: " + distanceTraveledList[i]);
			s.append('\n');
		}
		s.append('\n');

		for (int i = 0; i < numOfTracks - 1; i++) {
			AnimalTrack currentTrack = data.getAnimalTracksList().get(i);
			for (int j = i + 1; j < numOfTracks; j++) {
				s.append(data.getAnimalTracksList().get(i).getName() + " to "
						+ data.getAnimalTracksList().get(j).getName());
				s.append('\n');
				s.append("Time");
				s.append(",");
				s.append("Distance");
				s.append('\n');
				AnimalTrack comparingTrack = data.getAnimalTracksList().get(j);
				for (int k = 0; k < currentTrack.getNumPoints(); k++) {
					if (comparingTrack.hasTimePointAtTime(currentTrack.getTimePointAtIndex(k).getFrameNum())) {
						TimePoint currentTrackCurrentTP = currentTrack.getTimePointAtIndex(k);
						TimePoint comparingTrackCurrentTP = comparingTrack
								.getTimePointAtTime(currentTrackCurrentTP.getFrameNum());
						s.append(currentTrackCurrentTP.getFrameNum());
						s.append(",");
						double distance = Math
								.sqrt(Math.pow(currentTrackCurrentTP.getX() - comparingTrackCurrentTP.getX(), 2)
										+ Math.pow(currentTrackCurrentTP.getY() - comparingTrackCurrentTP.getY(), 2));
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