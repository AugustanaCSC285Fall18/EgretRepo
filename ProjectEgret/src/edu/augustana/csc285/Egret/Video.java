package edu.augustana.csc285.Egret;

import java.io.FileNotFoundException;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import java.util.ArrayList;

public class Video {
	
	private double xPixelsPerCm;
	private double yPixelsPerCm;	
	private int startFrameNum;
	//private Integer curFrameNum;
	private int endFrameNum;
	private int totalFrames;
	private int curFrameNum;
	private int numFrame;
	private double videoLength;
	private int currentTimeStamp; //CHANGE: to frame
	private ArrayList<Integer> flagMarkers;
	private double timeInterval;
	private VideoCapture vidCap = new VideoCapture();
	private String fileName;


	public Video(String fileName) throws FileNotFoundException { 
		this.fileName = fileName;
		this.vidCap = new VideoCapture(fileName);
		if(!vidCap.isOpened()) {
			throw new FileNotFoundException("Unable to open video file: " + fileName);
		}
		
	}
	
	public int getFrameRate() {
		return (int) this.vidCap.get(Videoio.CV_CAP_PROP_FPS);
	}

	public double getwidthPixels() {
		return this.vidCap.get(Videoio.CV_CAP_PROP_FRAME_WIDTH);
	}

	public double getheightPixels() {
		return this.vidCap.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT);	
	}

	public void setStartFrameNum(double startFrame) {
		startFrameNum = (int) startFrame;
	}
	
	public int getStartFrameNum() {
		return startFrameNum;
	}

	public void setEndFrameNum(double endFrame) {
		endFrameNum = (int) endFrame;
	}
	
	public int getEndFrameNum() {
		return endFrameNum;
	}

	public void setCurFrameNum(double curFrameNum) {
		this.curFrameNum = (int) curFrameNum;
	}
	
	public int getCurFrameNum() {
		return curFrameNum;
	}

	public double getVideoLength() {
		return videoLength;
	}

	public int getCurrentTimeStamp() {
		return currentTimeStamp;
	}
	
	public int getTotalNumberofFrames() {
		return endFrameNum - startFrameNum;
	}

	public VideoCapture getVidCap() {
		return vidCap;
	}

	public int getNumFrame() {
		return numFrame;
	}

	public void setNumFrame(int numFrame) {
		this.numFrame = numFrame;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}