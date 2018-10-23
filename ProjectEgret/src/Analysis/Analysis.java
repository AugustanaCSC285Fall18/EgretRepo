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

public class Analysis {

	private static ProjectData data;

	public static void main(String[] args) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File dataFile = new File("final_data_file");
		data = ProjectData.loadFromFile(dataFile);
		exportToCSV();
		totalDistanceTraveled();
	}

	public Analysis(ProjectData data) {
		this.data = data;
	}

	public static void exportToCSV() throws IOException {
		FileWriter fileWriter = new FileWriter(new File("test1.csv"));
		StringBuilder s = new StringBuilder();
		int maxNumTimePoints = 0;

		for (int i = 0; i < data.getAnimalTracksList().size(); i++) {
			System.out.println(data.getAnimalTracksList().get(i).getFirstTimePoint());
			System.out.println(data.getAnimalTracksList().get(i).getFinalTimePoint());
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
			s.append("Time");
			s.append(",");
			s.append("X-Position");
			s.append(",");
			s.append("Y-Position");
			s.append(",,");
		}
		s.append('\n');

		for (int i = 0; i < maxNumTimePoints; i++) {
			for (int j = 0; j < data.getAnimalTracksList().size(); j++) {
				AnimalTrack currentTrack = data.getAnimalTracksList().get(j);
				if (currentTrack.hasTimePointAtIndex(i)) {
					TimePoint currentTP = currentTrack.getTimePointAtIndex(i);
					s.append(currentTP.getFrameNum());
					s.append(",");
					s.append(currentTP.getX());
					s.append(",");
					s.append(currentTP.getY());
					s.append(",,");
				} else {
					s.append(",,,,");
				}
			}
			s.append('\n');
		}
		fileWriter.append(s);
		fileWriter.close();
		System.out.println("done!");
	}

	public static void totalDistanceTraveled() {
		double[] distanceTraveledList = new double[data.getAnimalTracksList().size()];
		for (int i = 0; i < data.getAnimalTracksList().size(); i++) {
			AnimalTrack currentTrack = data.getAnimalTracksList().get(i);
			for (int j = 1; j < currentTrack.getNumPoints() - 1; j++) {
				double tempDistance = currentTrack.getTimePointAtIndex(j)
						.getDistanceTo(currentTrack.getTimePointAtIndex(j - 1));
				distanceTraveledList[i] += tempDistance;
			}
		}
		System.out.println(Arrays.toString(distanceTraveledList));
	}

	public static void averageDistance() {
		int numOfTracks = data.getAnimalTracksList().size();
		double[][] name = new double[numOfTracks][numOfTracks];
		for(int i = 0; i < numOfTracks-1; i++) {
			AnimalTrack currentTrack = data.getAnimalTracksList().get(i);
			for(int j = i+1; j < numOfTracks; j++) {
				AnimalTrack comparingToTrack = data.getAnimalTracksList().get(j);
				int minNumberPoints = Math.min(currentTrack.getNumPoints(), currentTrack.getNumPoints());
				for(int k = 0; k < minNumberPoints; k++) {
					//if()
				}
			}
		}
	}

}
