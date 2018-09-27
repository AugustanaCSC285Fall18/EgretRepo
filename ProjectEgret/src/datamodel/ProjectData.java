package datamodel;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Point;

import edu.augustana.csc285.Egret.Video;

public class ProjectData {
	private List<AnimalTrack> animalTracks;
	private Video video;
	private List<AnimalTrack> untrackedTacks;
	
	public void exportCSVFile(File outFile) {
		
	}
	
	public void saveProject(File projectFile) {
		
	}
	
	public ProjectData() {
		AnimalTrack animal1 = new AnimalTrack("Chick1", new Point(0.0,0.0));
		AnimalTrack animal2 = new AnimalTrack("Chick2", new Point(0.0,0.0));
		AnimalTrack animal3 = new AnimalTrack("Chick3", new Point(0.0,0.0));
		animalTracks = new ArrayList<AnimalTrack>();
		animalTracks.add(animal1);
		animalTracks.add(animal2);
		animalTracks.add(animal3);
		
		
	}
}
