package datamodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.scene.paint.Color;

public class ProjectData {
	
	//Data Fields
	private Video video;
	private List<AnimalTrack> animalTracksList;
	private List<AnimalTrack> unassignedSegments;
	private List<Color> colorArrayForAnimalTracks = new ArrayList<Color>();

	//Constructor
	public ProjectData(String videoFilePath) throws FileNotFoundException {
		video = new Video(videoFilePath);
		animalTracksList = new ArrayList<>();
		unassignedSegments = new ArrayList<>();
		addColorsToColorArray();

	}
	
	/*
	 * Adds a list of colors to the ColorArray for displaying different colors
	 */
	private void addColorsToColorArray() {
		colorArrayForAnimalTracks.add(Color.BLACK);
		colorArrayForAnimalTracks.add(Color.RED);
		colorArrayForAnimalTracks.add(Color.BLUE);
		colorArrayForAnimalTracks.add(Color.ORANGE);
		colorArrayForAnimalTracks.add(Color.GREEN);
	}

	/**
	 * @return the list of AnimalTracks
	 */
	public List<AnimalTrack> getAnimalTracksList() {
		return animalTracksList;
	}

	/**
	 * Remove the given track from unassignedSegments
	 * @param assignedTrack - the track to be removed
	 */
	public void removeUnassignedSegment(int assignedTrack) {
		unassignedSegments.remove(assignedTrack);
	}
	
	/**
	 * @param animalTracks - the list of animal tracks to assign the
	 * AnimalTracksList to
	 */
	public void setAnimalTracksList(List<AnimalTrack> animalTracks) {
		this.animalTracksList = animalTracks;
	}

	/**
	 * @return the video object
	 */
	public Video getVideo() {
		return video;
	}

	/**
	 * @param video - the video object to set the data field Video to 
	 */
	public void setVideo(Video video) {
		this.video = video;
	}

	/**
	 * @return the list of unassignedSegments
	 */
	public List<AnimalTrack> getUnassignedSegments() {
		return unassignedSegments;
	}

	/**
	 * sets the unassignedSegments
	 * @param untrackedTracks - the new list of unassigned segments
	 */
	public void setUnassignedSegments(List<AnimalTrack> untrackedTracks) {
		this.unassignedSegments = untrackedTracks;
	}

	/**
	 * @return the list that has colors for the different AnimalTracks
	 */
	public List<Color> getColorArrayForAnimalTracks() {
		return colorArrayForAnimalTracks;
	}
	
	/**
	 * This method returns the unassigned segment that contains a TimePoint (between
	 * startFrame and endFrame) that is closest to the given x,y location
	 * 
	 * @param x          - x coordinate to search near
	 * @param y          - y coordinate to search near
	 * @param startFrame - (inclusive)
	 * @param endFrame   - (inclusive)
	 * @return the unassigned segment (AnimalTrack) that contained the nearest point
	 *         within the given time interval, or *null* if there is NO unassigned
	 *         segment that contains any TimePoints within the given range.
	 */
	public AnimalTrack getNearestUnassignedSegment(double x, double y, int startFrame, int endFrame) {
		TimePoint thisPoint = new TimePoint(x, y, 0);
		AnimalTrack closestTrack = null;
		double distance = Integer.MAX_VALUE;
		for (AnimalTrack track : unassignedSegments) {
			List<TimePoint> pointsInInterval = track.getTimePointsWithinInterval(startFrame, endFrame);
			for (TimePoint intervalPoint : pointsInInterval) { 
				double newDistance = thisPoint.getDistanceTo(intervalPoint); 
				if (newDistance < distance) {
					closestTrack = track;
					distance = newDistance;
				}
			}
		}
		return closestTrack;
	}

	/**
	 * Saves the ProjectData to a File 
	 * @param saveFile - where to save the file
	 * @throws FileNotFoundException if the saveFile cannot be found
	 */
	public void saveToFile(File saveFile) throws FileNotFoundException {
		String json = toJSON();
		PrintWriter out = new PrintWriter(saveFile);
		out.print(json);
		out.close();
	}

	/**
	 * @return a String containing the ProjectData object in a pretty JSON format
	 */
	public String toJSON() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}

	/**
	 * Loads a Project Data from a file
	 * @param loadFile - the file that should hold a Project Data
	 * @return The ProjectData object in the file
	 * @throws FileNotFoundException if the loadFile cannot be found
	 */
	public static ProjectData loadFromFile(File loadFile) throws FileNotFoundException {
		@SuppressWarnings("resource")
		String json = new Scanner(loadFile).useDelimiter("\\Z").next();
		return fromJSON(json);
	}

	/**
	 * Changes the String of JSON to an ProjectData object
	 * @param jsonText - the JSON String version of the ProjectData object
	 * @return the ProjectData Object
	 * @throws FileNotFoundException if the file containing the jsonText is not found
	 */
	public static ProjectData fromJSON(String jsonText) throws FileNotFoundException {
		Gson gson = new Gson();
		ProjectData data = gson.fromJson(jsonText, ProjectData.class);
		data.getVideo().connectVideoCapture();
		return data;
	}
}
