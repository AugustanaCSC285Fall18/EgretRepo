package edu.augustana.csc285.Egret;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import datamodel.TimePoint;

public class AnimalTrack {
	
	private Point centerPoint;
	private ArrayList<TimePoint> locations;
	private String animalId;
	
	public AnimalTrack(String name, Point centerPoint, Color colorId) {
		this.animalId = name;
		this.centerPoint = centerPoint;
		locations = new ArrayList<TimePoint>();
		locations.add(new TimePoint(centerPoint, 0));
	}

	public void setLocations(ArrayList<TimePoint> locations) {
		this.locations = locations;
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
		return locations.get(0);
	}
	
	/**
	 * @return the current list of all center locations of the animal
	 */
	public ArrayList<TimePoint> getLocations() {
		return locations;
	}
	
	/**
	 * @return the String representation of the AnimalTrack object
	 */
	public String toString() {
		return "Chick ID: " + animalId + ": Locations: " + locations;
	}
	
	/**
	 * Takes in a new point and sets the center location of the animal and 
	 * adds the new center location to the list of previous locations. 
	 * @param newCenterPoint - the new center point of the animal
	 * @param time - the time stamp of the center point
	 */
	public void addLocation(Point newCenterPoint, Integer time) {
		centerPoint = newCenterPoint;
		locations.add(new TimePoint(newCenterPoint, time));
	}
	
	
}
