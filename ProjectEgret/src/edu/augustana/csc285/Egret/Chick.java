package edu.augustana.csc285.Egret;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class Chick {
	
	private Point centerPoint;
	private ArrayList<Point> locations;
	private String name;
	private Color colorId;
	
	public Chick(String name, Point centerPoint, Color colorId) {
		this.name = name;
		this.centerPoint = centerPoint;
		this.colorId = colorId;
		locations = new ArrayList<Point>();
		locations.add(this.centerPoint);
	}
	
	/**
	 * @return the name of the chick
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the current color ID of the chick
	 */
	public Color getColorId() {
		return colorId;
	}
	
	/**
	 * @return the current Center Point of the chick
	 */
	public Point getCurrentCenterPoint() {
		return centerPoint;
	}
	
	/**
	 * @param time - the time to get the center of the chick
	 * @return the center point of the chick at a given time
	 */
	public Point getCenterPointAtTime(int time) {
		return locations.get(time);
	}
	
	/**
	 * @return the current list of all center locations of the chick
	 */
	public ArrayList<Point> getLocations() {
		return locations;
	}
	
	/**
	 * @return the String representation of the Chick object
	 */
	public String toString() {
		String str = "";
		//I'm not sure if we need this or how we would want this to look
		return str;
	}
	
	/**
	 * Takes in a new point and sets the center location of the chick and 
	 * adds the new center location to the list of previous locations. 
	 * @param newCenterPoint - the new center point of the chick
	 * @param time - the time stamp of the center point
	 */
	public void addLocation(Point newCenterPoint, int time) {
		centerPoint = newCenterPoint;
		locations.add(newCenterPoint);
	}
	
	
}
