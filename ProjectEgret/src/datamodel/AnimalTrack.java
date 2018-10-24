package datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opencv.core.Point;


public class AnimalTrack {

	private Point centerPoint;
	private ArrayList<TimePoint> positions;
	private String animalId;

	public AnimalTrack(String name) {
		this.animalId = name;
		positions = new ArrayList<TimePoint>();
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

	public void add(TimePoint pt) {
		positions.add(pt);
	}

	public void removeLocation(int index) {
		positions.remove(index);
	}

	public TimePoint getTimePointAtIndex(int index) {
		if (positions.size()<= index || index < 0) {
			return null;
		} else {
			return positions.get(index);
		}
		
	}

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

	public boolean hasTimePointAtTime(int frameNum) {
		// TODO: This method's implementation is inefficient [linear search is O(N)]
		// Replace this with binary search (O(log n)] or use a Map for fast access
		return getTimePointAtTime(frameNum) != null;
	}
	
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
	
	public List<TimePoint> getTimePointsWithinInterval(double startFrameNum, double endFrameNum) {
		List<TimePoint> pointsInInterval = new ArrayList<>(); 
		for (TimePoint pt : positions) {
			if (pt.getFrameNum() >= startFrameNum && pt.getFrameNum() <= endFrameNum) {
				pointsInInterval.add(pt);
			}
		}
		return pointsInInterval;
	}
	
	

	public void setTimePointAtTime(Point curPoint, int frameNum) {
		TimePoint currentTimePoint = getTimePointAtTime(frameNum);
		currentTimePoint.setX(curPoint.x);
		currentTimePoint.setY(curPoint.y);
	}
	
	public void addTrackSegment(AnimalTrack newList) {
		for (int i = 0; i < newList.getNumPoints(); i++) {
		    positions.add(newList.getTimePointAtIndex(i)); 
		}
		//positions.addAll((Collection<? extends TimePoint>) newList);
	}

	public TimePoint getFinalTimePoint() {
		if(positions.size() <= 0) { 
			return null;
		}else {
			return positions.get(positions.size() - 1);
		}
	}
	
	public TimePoint getFirstTimePoint() {
		if(positions.size() <= 0) {
			return null;
		}else {
		
			return positions.get(0);
	
		}
	}
	public double getX() {
		return centerPoint.x;
	}

	public double getY() {
		return centerPoint.y;
	}
	
	public int getFirstFrame() {
		return positions.get(0).getFrameNum();
	}
	
	public int getLastFrame() {
		return positions.get(positions.size()-1).getFrameNum();
	}


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