/**
 * Description: Creates and houses information for a TimePoint object
 */

package datamodel;

import org.opencv.core.Point;

public class TimePoint implements Comparable<TimePoint> {

	// Data Fields
	private double x; // location
	private double y;
	private int frameNum; // time (measured in frames)

	// Constructor 1
	public TimePoint(double x, double y, int frameNum) {
		this.x = x;
		this.y = y;
		this.frameNum = frameNum;
	}

	// Constructor 2
	public TimePoint(Point pt, int frameNum) {
		this(pt.x, pt.y, frameNum);
	}

	/**
	 * @return the X coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x - the x coordinate to set the point's original x coordinate to
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the Y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y - the y coordinate to set the point's original y coordinate to
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return the point as an OpenCV point
	 */
	public org.opencv.core.Point getPointOpenCV() {
		return new org.opencv.core.Point(x, y);
	}

	/**
	 * @return the point as a Java.awt point
	 */
	public java.awt.Point getPointAWT() {
		return new java.awt.Point((int) x, (int) y);
	}

	/**
	 * @return the frame number
	 */
	public int getFrameNum() {
		return frameNum;
	}

	/**
	 * @return a string: (x,y) @ frameNum
	 */
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")" + " @ " + frameNum;
	}

	/**
	 * 
	 * @param other - the other point to get the distance from
	 * @return the distance to the other point
	 */
	public double getDistanceTo(TimePoint other) {
		double dx = other.x - x;
		double dy = other.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * How many frames have passed since another TimePoint
	 * 
	 * @param other - the otherTimePoint to compare with
	 * @return the difference (negative if the other TimePoint is later)
	 */
	public int getTimeDiffAfter(TimePoint other) {
		return this.frameNum - other.frameNum;
	}

	/**
	 * Comparison based on the time (frame number).
	 */
	@Override
	public int compareTo(TimePoint other) {
		return this.getTimeDiffAfter(other);
	}
}
