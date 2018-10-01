package datamodel;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Point;

import edu.augustana.csc285.Egret.Video;

public class ProjectData {
	private List<AnimalTrack> animalTracksList;
	private Video video;
	private List<AnimalTrack> untrackedTacks;
	
	public void exportCSVFile(File outFile) {
		for (int i = 0; i < animalTracksList.size(); i++) {
			System.out.println(animalTracksList.get(i));
		}
	}
	
	public void saveProject(File projectFile) {
		
	}
	
	public ProjectData() {
		AnimalTrack animal1 = new AnimalTrack("Chick1", new Point(0.0,0.0));
		AnimalTrack animal2 = new AnimalTrack("Chick2", new Point(0.0,0.0));
		AnimalTrack animal3 = new AnimalTrack("Chick3", new Point(0.0,0.0));
		animalTracksList = new ArrayList<AnimalTrack>();
		animalTracksList.add(animal1);
		animalTracksList.add(animal2);
		animalTracksList.add(animal3);
		
		
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

	public List<AnimalTrack> getUntrackedTacks() {
		return untrackedTacks;
	}

	public void setUntrackedTacks(List<AnimalTrack> untrackedTacks) {
		this.untrackedTacks = untrackedTacks;
	}
}
