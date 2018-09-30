package datamodel;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Point;

public class ProjectData {
	private Video video;
	private List<AnimalTrack> tracks;
	private List<AnimalTrack> unassignedSegments;
	
	public void exportCSVFile(File outFile) {
		
	}
	
	public void saveProject(File projectFile) {
		
	}
	
	public ProjectData(String videoFilePath) throws FileNotFoundException {
		video = new Video(videoFilePath);
		tracks = new ArrayList<>();
		unassignedSegments = new ArrayList<>();
	}
	
	public ProjectData() {
		AnimalTrack animal1 = new AnimalTrack("Chick1");
		AnimalTrack animal2 = new AnimalTrack("Chick2");
		AnimalTrack animal3 = new AnimalTrack("Chick3");
		tracks = new ArrayList<AnimalTrack>();
		tracks.add(animal1);
		tracks.add(animal2);
		tracks.add(animal3);
		
		
	}

	public List<AnimalTrack> getAnimalTracksList() {
		return tracks;
	}

	public void setAnimalTracksList(List<AnimalTrack> animalTracks) {
		this.tracks = animalTracks;
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
}
