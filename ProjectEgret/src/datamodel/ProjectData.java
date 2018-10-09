package datamodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.awt.Color;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

	public void saveToFile(File saveFile) throws FileNotFoundException {
		String json = toJSON();
		PrintWriter out = new PrintWriter(saveFile);
		out.print(json);
		out.close();
	}

	public String toJSON() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}

	public static ProjectData loadFromFile(File loadFile) throws FileNotFoundException {
		String json = new Scanner(loadFile).useDelimiter("\\Z").next();
		return fromJSON(json);
	}

	public static ProjectData fromJSON(String jsonText) throws FileNotFoundException {
		Gson gson = new Gson();
		ProjectData data = gson.fromJson(jsonText, ProjectData.class);
		data.getVideo().connectVideoCapture();
		return data;
	}
}
