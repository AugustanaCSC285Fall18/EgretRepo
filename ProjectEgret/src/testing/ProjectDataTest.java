package testing;
import datamodel.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;

import static datamodel.AnimalTrack.*;

class ProjectDataTest {

	@BeforeAll
	static void initialize() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);		
	}
	
	ProjectData makeFakeData() throws FileNotFoundException {
		ProjectData project = new ProjectData("testVideos/CircleTest1_no_overlap.mp4");
		AnimalTrack track1 = new AnimalTrack("chicken1");
		AnimalTrack track2 = new AnimalTrack("chicken2");
		project.getAnimalTracksList().add(track1);
		project.getAnimalTracksList().add(track2);
		
		track1.add(new TimePoint(100,200,0));
		track1.add(new TimePoint(105,225,30));
		
		track2.add(new TimePoint(300,400,90));
		return project;
	}
	
	@Test
	void testCreationProjectData() throws FileNotFoundException {
		ProjectData fake = makeFakeData();
		assertEquals(2,fake.getAnimalTracksList().size());
		assertEquals("testVideos/CircleTest1_no_overlap.mp4", fake.getVideo().getFilePath());
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	void testJSONSerializationDeserialization() throws FileNotFoundException {
		ProjectData fake = makeFakeData();
		String json = fake.toJSON();
		
		ProjectData reconstructedFake = ProjectData.fromJSON(json);
		
		assertEquals(fake.getVideo().getFilePath(), reconstructedFake.getVideo().getFilePath());		
		
		equals(comparePoint(fake.getAnimalTracksList().get(0).getTimePointAtIndex(0), 
				reconstructedFake.getAnimalTracksList().get(0).getTimePointAtIndex(0)));
	}

	@Test
	void testFileSaving() throws FileNotFoundException {
		ProjectData fake = makeFakeData();
		File fSave = new File("fake_test.project");
		fake.saveToFile(fSave);
		assertTrue(fSave.exists());
	}
		

}
