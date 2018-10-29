package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;
import org.opencv.core.Point;

import datamodel.AnimalTrack;
import datamodel.TimePoint;

import static datamodel.AnimalTrack.*;

class AnimalTrackTester {

	/**
	 * Makes an AnimalTrack for testing.
	 * 
	 * @return - AnimalTrack
	 */
	AnimalTrack makeFakeTrack() {
		AnimalTrack testTrack = new AnimalTrack("ChickenLittle");
		testTrack.addTimePoint(new TimePoint(100, 100, 0));
		testTrack.addTimePoint(new TimePoint(110, 110, 1));
		testTrack.addTimePoint(new TimePoint(150, 200, 5));
		return testTrack;
	}

	/**
	 * Tests the compareTimePoint method and the getTimePointAtTime method
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	void testAddingAndModyfingAnimalTracks() {
		AnimalTrack fake = makeFakeTrack();

		assertEquals("ChickenLittle", fake.getName());

		assertEquals(3, fake.getNumPoints());

		TimePoint ptAt0 = fake.getTimePointAtTime(0);
		assertEquals(true, comparePoint(new TimePoint(100, 100, 0), ptAt0));
		TimePoint ptAt1 = fake.getTimePointAtTime(1);
		assertEquals(true, comparePoint(new TimePoint(110, 110, 1), ptAt1));
		TimePoint ptAt2 = fake.getTimePointAtTime(5);
		assertEquals(true, comparePoint(new TimePoint(150, 200, 5), ptAt2));

		Point centerPoint = new Point(15, 10);
		fake.addLocation(centerPoint, 10);
		TimePoint ptAt3 = fake.getTimePointAtTime(10);
		assertEquals(true, comparePoint(new TimePoint(15, 10, 10), ptAt3));
	}

	/**
	 * Tests the getters/setters and deleting points.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	void testGettersSettersDelete() {
		AnimalTrack fake = makeFakeTrack();

		assertEquals(true, fake.hasTimePointAtTime(5));

		Point centerPoint = new Point(15, 10);
		fake.setTimePointAtTime(centerPoint, 5);
		TimePoint ptAt2 = fake.getTimePointAtTime(5);
		assertEquals(true, comparePoint(new TimePoint(15, 10, 5), ptAt2));

		assertEquals(true, comparePoint(new TimePoint(15, 10, 5), fake.getFinalTimePoint()));
	}
}
