package datamodel;

import java.awt.Color;
import java.util.ArrayList;

import org.opencv.core.Point;

public class AnimalTrack {
	
	private Point centerPoint;
	private ArrayList<TimePoint> positions;
	private String animalId;
	
	public AnimalTrack(String name, Point point) {
		this.animalId = name;
		this.centerPoint = point;
		positions = new ArrayList<TimePoint>();
		positions.add(new TimePoint(point, 0));
	}

	public void setLocations(ArrayList<TimePoint> locations) {
		this.positions = locations;
	}

	/**
	 * @param name - the name that is set for the animal track
	 */
	public void setName(String name) {
		this.animalId = name;
	}

	/**
	 * @return the name of the animal
	 */
	public String getName() {
		return animalId;
	}
	
	/**
	 * @return the current Center Point of the animal
	 */
	public Point getCurrentCenterPoint() {
		return centerPoint;
	}
	
	/**
	 * @param time - the time to get the center of the animal
	 * @return the center point of the animal at a given time
	 */
	public TimePoint getCenterPointAtTime(double time) {
		return positions.get(0);
	}
	
	/**
	 * @return the current list of all center locations of the animal
	 */
	public ArrayList<TimePoint> getLocations() {
		return positions;
	}
	
	/**
	 * @return the String representation of the AnimalTrack object
	 */
	public String toString() {
		return "Chick ID: " + animalId + ": Locations: " + positions;
	}
	
	/**
	 * Takes in a new point and sets the center location of the animal and 
	 * adds the new center location to the list of previous locations. 
	 * @param newCenterPoint - the new center point of the animal
	 * @param time - the time stamp of the center point
	 */
	public void addLocation(Point newCenterPoint, Integer time) {
		centerPoint = newCenterPoint;
		positions.add(new TimePoint(newCenterPoint, time));
	}
	
	public void removeLocation() {
		if(positions.size()>0) {
			positions.remove(positions.size()-1);
		}
	}
	
	public TimePoint getTimePointAtTime(int frameNum) {
		//TODO: This method's implementation is inefficient [linear search is O(N)]
		//      Replace this with binary search (O(log n)] or use a Map for fast access
		for (TimePoint pt : positions) {
			if (pt.getFrameNum() == frameNum) {
				return pt;
			}
		}
		return null;
	}
	
	
}
