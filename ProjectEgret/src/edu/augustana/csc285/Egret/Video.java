package edu.augustana.csc285.Egret;

import java.util.ArrayList;

public class Video {
	private int videoLength;
	private int currentTimeStamp;
	private String formatType;
	//Integer because the time steps are in seconds most likely else we could maybe do them in milliseconds or something
	private ArrayList<Integer> flagMarkers;
	private int timeInterval;
	private int numTracks;
	
	public Video() { //what parameters might we need?
		//insert constructor stuff here
	}
}
