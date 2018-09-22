package edu.augustana.csc285.Egret;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnimalTrack {
	
	private Point centerPoint;
	private Map<Double, Point> locations;
	private String name;
	
	public AnimalTrack(String name, Point centerPoint, Color colorId) {
		this.name = name;
		this.centerPoint = centerPoint;
		locations = new HashMap<Double, Point>();
		locations.put(0.0, this.centerPoint);
	}

	public void setLocations(Map<Double, Point> locations) {
		this.locations = locations;
	}

	/**
	 * @param name - the name that is set for the animal track
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name of the animal
	 */
	public String getName() {
		return name;
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
	public Point getCenterPointAtTime(double time) {
		return locations.get(time);
	}
	
	/**
	 * @return the current list of all center locations of the animal
	 */
	public Map<Double, Point> getLocations() {
		return locations;
	}
	
	/**
	 * @return the String representation of the AnimalTrack object
	 */
	public String toString() {
		String str = "";
		//I'm not sure if we need this or how we would want this to look
		return str;
	}
	
	/**
	 * Takes in a new point and sets the center location of the animal and 
	 * adds the new center location to the list of previous locations. 
	 * @param newCenterPoint - the new center point of the animal
	 * @param time - the time stamp of the center point
	 */
	public void addLocation(Point newCenterPoint, double time) {
		centerPoint = newCenterPoint;
		locations.put(time, newCenterPoint);
	}
	
	
}
