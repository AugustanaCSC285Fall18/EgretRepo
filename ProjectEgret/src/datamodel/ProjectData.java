package datamodel;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

import org.opencv.core.Point;

public class ProjectData {
	private Video video;
	private List<AnimalTrack> animalTracksList;
	private List<AnimalTrack> unassignedSegments;
	private List<Color> colorArrayForAnimalTracks = new ArrayList<Color>();
	
	public void exportCSVFile(File outFile) {
		for (int i = 0; i < animalTracksList.size(); i++) {
			System.out.println(animalTracksList.get(i));
		}
	}
	
	public void saveProject(File projectFile) {
		
	}
	
	public ProjectData(String videoFilePath) throws FileNotFoundException {
		video = new Video(videoFilePath);
		animalTracksList = new ArrayList<>();
		unassignedSegments = new ArrayList<>();
	}
	
	public ProjectData() {
		AnimalTrack animal1 = new AnimalTrack("Chick1");
		AnimalTrack animal2 = new AnimalTrack("Chick2");
		AnimalTrack animal3 = new AnimalTrack("Chick3");
		animalTracksList = new ArrayList<AnimalTrack>();
		animalTracksList.add(animal1);
		animalTracksList.add(animal2);
		animalTracksList.add(animal3);
		
		colorArrayForAnimalTracks.add(Color.BLACK);
		colorArrayForAnimalTracks.add(Color.RED);
		colorArrayForAnimalTracks.add(Color.BLUE);
		colorArrayForAnimalTracks.add(Color.ORANGE);
		colorArrayForAnimalTracks.add(Color.GREEN);
	}

	public List<AnimalTrack> getAnimalTracksList() {
		return animalTracksList;
	}

	public void setAnimalTracksList(List<AnimalTrack> animalTracks) {
		this.animalTracksList = animalTracks;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public List<AnimalTrack> getUnassignedSegments() {
		return unassignedSegments;
	}

	public void setUnassignedSegments(List<AnimalTrack> untrackedTacks) {
		this.unassignedSegments = untrackedTacks;
	}

	public List<Color> getColorArrayForAnimalTracks() {
		return colorArrayForAnimalTracks;
	}
}
