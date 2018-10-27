package datamodel;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Point;


public class AnimalTrack {

	//Data Fields
	private Point centerPoint;
	private ArrayList<TimePoint> positions;
	private String animalId;

	//Constructor
	public AnimalTrack(String name) {
		this.animalId = name;
		positions = new ArrayList<TimePoint>();
	}

	/**
	 * @param positions - the list of TimePoints to set the position to. 
	 */
	public void setLocations(ArrayList<TimePoint> positions) {
		this.positions = positions;
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
	 * @return the current Center Point of the animal
	 */
	public int getNumPoints() {
		return positions.size();
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
		return animalId + ": Locations: " + positions;
	}

	/**
	 * Takes in a new point and sets the center location of the animal and adds the
	 * new center location to the list of previous locations.
	 * 
	 * @param newCenterPoint - the new center point of the animal
	 * @param frameNum       - the time stamp of the center point
	 */
	public void addLocation(Point newCenterPoint, int frameNum) {
		centerPoint = newCenterPoint;
		positions.add(new TimePoint(newCenterPoint, frameNum));
	}

	/**
	 * Allows the user to add a TimePoint to the list of locations. 
	 * @param pt - the point to add
	 */
	public void addTimePoint(TimePoint pt) {
		positions.add(pt);
	}

	/**
	 * Allows the user to remove a location at a given index. 
	 * @param index
	 */
	public void removeLocation(int index) {
		positions.remove(index);
	}

	/**
	 * @param index - index of TimePoint to get
	 * @return a TimePoint at a specified index
	 */
	public TimePoint getTimePointAtIndex(int index) {
		if (positions.size()<= index || index < 0) {
			return null;
		} else {
			return positions.get(index);
		}
		
	}

	/**
	 * @param frameNum - the frame number to search for
	 * @return the TimePoint at a given frame number
	 */
	public TimePoint getTimePointAtTime(int frameNum) {
		// TODO: This method's implementation is inefficient [linear search is O(N)]
		// Replace this with binary search (O(log n)] or use a Map for fast access
		for (TimePoint pt : positions) {
			if (pt.getFrameNum() == frameNum) {
				return pt;
			}
		}
		return null;
	}

	/**
	 * @param frameNum - the frame number of the given time
	 * @return true if there is a TimePoint at a given time
	 */
	public boolean hasTimePointAtTime(int frameNum) {
		// TODO: This method's implementation is inefficient [linear search is O(N)]
		// Replace this with binary search (O(log n)] or use a Map for fast access
		return getTimePointAtTime(frameNum) != null;
	}
	
	/**
	 * @param index - the index of the TimePoint to look for
	 * @return True if there is a TimePoint at that index
	 */
	public boolean hasTimePointAtIndex(int index) {
		// TODO: This method's implementation is inefficient [linear search is O(N)]
		// Replace this with binary search (O(log n)] or use a Map for fast access

		return getTimePointAtIndex(index) != null;
	}
	
	/**
	 * @param frameNum - the given frame number
	 * @return the Point at the frameNum (NOT the TimePoint)
	 */
	public Point getPointAtTime(int frameNum) {
		//TODO: This method's implementation is inefficient [linear search is O(N)]
		//      Replace this with binary search (O(log n)] or use a Map for fast access
		for (TimePoint pt : positions) {
			if (pt.getFrameNum() == frameNum) {
				return pt.getPointOpenCV();
			}
		}
		return null;
	}
	
	/**
	 * @param startFrameNum - the start of the interval
	 * @param endFrameNum - the end of the interval
	 * @return A list of time points within a given interval
	 */
	public List<TimePoint> getTimePointsWithinInterval(double startFrameNum, double endFrameNum) {
		List<TimePoint> pointsInInterval = new ArrayList<>(); 
		for (TimePoint pt : positions) {
			if (pt.getFrameNum() >= startFrameNum && pt.getFrameNum() <= endFrameNum) {
				pointsInInterval.add(pt);
			}
		}
		return pointsInInterval;
	}
	
	/**
	 * Sets a time point at a given time. 
	 * @param newPoint - The new point to set the current time point to
	 * @param frameNum - the frame number that will get a new point
	 */
	public void setTimePointAtTime(Point newPoint, int frameNum) {
		TimePoint currentTimePoint = getTimePointAtTime(frameNum);
		currentTimePoint.setX(newPoint.x);
		currentTimePoint.setY(newPoint.y);
	}
	
	/**
	 * Gets a list of TimePoints to be added to the current AnimalTrack.
	 * @param newList - the other AnimalTrack to get the list from
	 */
	public void addTrackSegment(AnimalTrack newList) {
		for (int i = 0; i < newList.getNumPoints(); i++) {
		    positions.add(newList.getTimePointAtIndex(i)); 
		}
		//positions.addAll((Collection<? extends TimePoint>) newList);
	}

	/**
	 * @return the most recently added time point in the list of locations
	 */
	public TimePoint getFinalTimePoint() {
		if (positions.size() <= 0) { 
			return null;
		} else {
			return positions.get(positions.size() - 1);
		}
	}
	
	/**
	 * @return the first TimePoint in the list of locations
	 */
	public TimePoint getFirstTimePoint() {
		if (positions.size() <= 0) {
			return null;
		} else {
			return positions.get(0);
		}
	}
	
	/**
	 * @return X coordinate of the current center point
	 */
	public double getX() {
		return centerPoint.x;
	}

	/**
	 * @return Y coordinate of the current center point
	 */
	public double getY() {
		return centerPoint.y;
	}
	
	/**
	 * @return the frame number of the first TimePoint in positions. 
	 */
	public int getFirstFrame() {
		return positions.get(0).getFrameNum();
	}
	
	/**
	 * @return the frame number of the last TimePoint in positions. 
	 */
	public int getLastFrame() {
		return positions.get(positions.size()-1).getFrameNum();
	}

	/**
	 * Compares two points to see if they are equal.
	 * @param tp1 - the first TimePoint to compare
	 * @param tp2 - the first TimePoint to compare
	 * @return True if the two points are equal 
	 */
	public static boolean comparePoint(TimePoint tp1, TimePoint tp2) {
		if (!(tp1.getX() == tp2.getX())) {
			return false;
		} else if (!(tp1.getY() == tp2.getY())) {
			return false;
		} else if (!(tp1.getFrameNum() == tp2.getFrameNum())) {
			return false;
		} else {
			return true;
		}
	}
}