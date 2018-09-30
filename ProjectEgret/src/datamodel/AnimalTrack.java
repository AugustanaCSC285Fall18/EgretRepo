package datamodel;

import java.awt.Color;
import java.util.ArrayList;

import org.opencv.core.Point;

public class AnimalTrack {
	
	private Point centerPoint;
	private ArrayList<TimePoint> positions = new ArrayList<TimePoint>();
	private String animalId;
	
	public AnimalTrack(String name) {
		this.animalId = name;
	}

	public void setLocations(ArrayList<TimePoint> position) {
		this.positions = position;
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
	
	public void add(TimePoint pt) {
		positions.add(pt);
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
	
	public boolean hasTimePointAtTime(int frameNum) {
		//TODO: This method's implementation is inefficient [linear search is O(N)]
		//      Replace this with binary search (O(log n)] or use a Map for fast access
		System.out.println("frame: " + frameNum);
		System.out.println("positions: " + positions);
		
		return getTimePointAtTime(frameNum) != null;
	}
	
	public void setTimePointAtTime(Point curPoint, int frameNum) {
		TimePoint currentTimePoint = getTimePointAtTime(frameNum);
		currentTimePoint.setX(curPoint.x);
		currentTimePoint.setY(curPoint.y);
	}
	
	public TimePoint getFinalTimePoint() {
		return positions.get(positions.size()-1);
	}
	
	public double getX() {
		return centerPoint.x;
	}
	
	public double getY() {
		return centerPoint.y;
	}
	
}