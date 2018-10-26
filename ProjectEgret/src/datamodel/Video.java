package datamodel;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Video {

	private String filePath;
	private transient VideoCapture vidCap;
	private int emptyFrameNum;
	private int startFrameNum;
	private int endFrameNum;

	private double xPixelsPerCm;
	private double yPixelsPerCm;
	private Rectangle arenaBounds;
	private Point origin;

	public Video(String filePath) throws FileNotFoundException {
		this.filePath = filePath;
		connectVideoCapture();
		if (!vidCap.isOpened()) {
			throw new FileNotFoundException("Unable to open video file: " + filePath);
		}
		// fill in some reasonable default/starting values for several fields
		this.emptyFrameNum = 0;
		this.startFrameNum = 0;
		this.endFrameNum = this.getTotalNumFrames() - 1;

		int frameWidth = (int) vidCap.get(Videoio.CAP_PROP_FRAME_WIDTH);
		int frameHeight = (int) vidCap.get(Videoio.CAP_PROP_FRAME_HEIGHT);
		this.arenaBounds = new Rectangle(0, 0, frameWidth, frameHeight);
	}

	synchronized void connectVideoCapture() throws FileNotFoundException {
		this.vidCap = new VideoCapture(filePath);
		Mat mat = new Mat();
		this.vidCap.read(mat);
		if (!vidCap.isOpened()) {
			throw new FileNotFoundException("Unable to open video file: " + filePath);
		}
	}

	
	public void setCurrentFrameNum(double seekFrame) {
		vidCap.set((int)Math.floor(Videoio.CV_CAP_PROP_POS_FRAMES), seekFrame);
	}

	public synchronized int getCurrentFrameNum() {
		return (int) Math.floor(vidCap.get(Videoio.CV_CAP_PROP_POS_FRAMES));
	}

	public synchronized Mat readFrame() {
		Mat frame = new Mat();
		vidCap.read(frame);
		return frame;
	}

	public String getFilePath() {
		return this.filePath;
	}
	
	public String getFilePathJustName() {
		return filePath.substring(filePath.lastIndexOf('/') + 1, filePath.lastIndexOf('.'));
	}

	/**
	 * @return frames per second
	 */
	public synchronized double getFrameRate() {
		return vidCap.get(Videoio.CAP_PROP_FPS);
	}
	
	/**
	 * @param frameNum - current frame number
	 * @return in seconds the current time stamp
	 */
	public int getTimeInSeconds(double frameNum) {
		return (int) (frameNum / getFrameRate());
	}
	
	public int getTimeInFrames(int seconds) {
		return (int) (seconds * getFrameRate());
	}
	
	/**
	 * @return total number of frames
	 */
	public synchronized int getTotalNumFrames() {
		return (int) vidCap.get(Videoio.CAP_PROP_FRAME_COUNT);
	}

	/**
	 * @return the frame number with the empty view
	 */
	public int getEmptyFrameNum() {
		return emptyFrameNum;
	}

	/**
	 * Sets the current empty frame to the new one (Empty frame is 
	 * where the empty view is)
	 * @param emptyFrameNum - the new empty frame
	 */
	public void setEmptyFrameNum(int emptyFrameNum) {
		this.emptyFrameNum = emptyFrameNum;
	}

	/**
	 * @return the calibrated start frame number
	 */
	public int getStartFrameNum() {
		return startFrameNum;
	}
	/**
	 * 
	 * @param startFrameNum
	 */
	public void setStartFrameNum(int startFrameNum) {
		this.startFrameNum = startFrameNum;
	}

	/**
	 * @return the calibrated end frame number
	 */
	public int getEndFrameNum() {
		return endFrameNum;
	}

	public void setEndFrameNum(int endFrameNum) {
		this.endFrameNum = endFrameNum;
	}

	/**
	 * @return the number of x pixels per centimeter (cm)
	 */
	public double getXPixelsPerCm() {
		return xPixelsPerCm;
	}

	public void setXPixelsPerCm(double boxWidthCm, Point a, Point b) {
		double distance = Point2D.distance(a.getX(), a.getY(), b.getX(), b.getY());
		yPixelsPerCm = distance/boxWidthCm;
	}

	/**
	 * @return the number of y pixels per centimeter (cm)
	 */
	public double getYPixelsPerCm() {
		return yPixelsPerCm;
	}

	public void setYPixelsPerCm(double boxHeightCm, Point a, Point b) {
		double distance = Point2D.distance(a.getX(), a.getY(), b.getX(), b.getY());
		yPixelsPerCm = distance/boxHeightCm;
	}

	public double getAvgPixelsPerCm() {
		return (xPixelsPerCm + yPixelsPerCm) / 2;
	}

	public Rectangle getArenaBounds() {
		return arenaBounds;
	}

	public void setArenaBounds(Rectangle arenaBounds) {
		this.arenaBounds = arenaBounds;
	}

	//same as get time in seconds
//	public double convertFrameNumsToSeconds(int numFrames) {
//		return numFrames / getFrameRate();
//	}
	
	//same as get time in frames
//	public int convertSecondsToFrameNums(double numSecs) {
//		return (int) Math.round(numSecs * getFrameRate());
//	}

	public VideoCapture getVidCap() {
		return vidCap;
	}
	
	public void setOriginPoint(Point origin) {
		this.origin = origin;
	}
	
	public Point getOriginPoint() {
		return origin;
	}

}
