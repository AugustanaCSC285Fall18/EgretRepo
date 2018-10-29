/**
 * Description: Creates and houses information for a Video object
 */

package datamodel;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Video {

	// Data Fields for Video
	private String filePath;
	private transient VideoCapture vidCap;
	private int emptyFrameNum;
	private int startFrameNum;
	private int endFrameNum;
	private int timeStep;
	private int timeStepIndex;

	// Data Fields for Calibration
	private double xPixelsPerCm;
	private double yPixelsPerCm;
	private Rectangle arenaBounds;
	private Point origin;

	// Constructor
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

	/**
	 * Connects the videoCapture object to the video when using the JSON stuff.
	 * 
	 * @throws FileNotFoundException - if filePath (data field) cannot be found
	 */
	synchronized void connectVideoCapture() throws FileNotFoundException {
		this.vidCap = new VideoCapture(filePath);
		Mat mat = new Mat();
		this.vidCap.read(mat);
		if (!vidCap.isOpened()) {
			throw new FileNotFoundException("Unable to open video file: " + filePath);
		}
	}

	/**
	 * @param currentFrame - the new current frame to set the video to
	 */
	public void setCurrentFrameNum(double currentFrame) {
		vidCap.set((int) Math.floor(Videoio.CV_CAP_PROP_POS_FRAMES), currentFrame);
	}

	/**
	 * @return the currentFrameNumber
	 */
	public synchronized int getCurrentFrameNum() {
		return (int) Math.floor(vidCap.get(Videoio.CV_CAP_PROP_POS_FRAMES));
	}

	/**
	 * @return current frame as a matrix
	 */
	public synchronized Mat readFrame() {
		Mat frame = new Mat();
		vidCap.read(frame);
		return frame;
	}

	/**
	 * @return the file path as a String
	 */
	public String getFilePath() {
		return this.filePath;
	}

	/**
	 * @return the name of the video (without the location stuff and extension)
	 */
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
	 * Sets the current empty frame to the new one (Empty frame is where the empty
	 * view is)
	 * 
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

	/**
	 * @param endFrameNum - frame number to set the end frame number to
	 */
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
		xPixelsPerCm = distance / boxWidthCm;
	}

	/**
	 * @return the number of y pixels per centimeter (cm)
	 */
	public double getYPixelsPerCm() {
		return yPixelsPerCm;
	}

	/**
	 * Sets the the number of y pixels per centimeter (cm)
	 * 
	 * @param boxHeightCm - height of box
	 * @param a           - first point in pixels
	 * @param b           - second point in pixels
	 */
	public void setYPixelsPerCm(double boxHeightCm, Point a, Point b) {
		double distance = Point2D.distance(a.getX(), a.getY(), b.getX(), b.getY());
		yPixelsPerCm = distance / boxHeightCm;
	}

	/**
	 * @return average pixels per cm
	 */
	public double getAvgPixelsPerCm() {
		return (xPixelsPerCm + yPixelsPerCm) / 2;
	}

	/**
	 * @return the Rectangle containing the ArenaBounds
	 */
	public Rectangle getArenaBounds() {
		return arenaBounds;
	}

	/**
	 * @param arenaBounds - the rectangle to set ArenaBounds to
	 */
	public void setArenaBounds(Rectangle arenaBounds) {
		this.arenaBounds = arenaBounds;
	}

	/**
	 * @return the time step to move forward in the video
	 */
	public int getTimeStep() {
		return timeStep;
	}

	/**
	 * @param timeStep - the time step to set the video time step to
	 */
	public void setTimeStep(int timeStep) {
		this.timeStep = timeStep;
	}

	/**
	 * @return the index of the time step
	 */
	public int getTimeStepIndex() {
		return timeStepIndex;
	}

	/**
	 * @param index - the index to set timeStepIndex to
	 */
	public void setTimeStepIndex(int index) {
		timeStepIndex = index;
	}

	/**
	 * @return the VideoCapture
	 */
	public VideoCapture getVidCap() {
		return vidCap;
	}

	/**
	 * @param origin - the point to set the origin to
	 */
	public void setOriginPoint(Point origin) {
		this.origin = origin;
	}

	/**
	 * @return the origin point (as a java.awt.Point)
	 */
	public Point getOriginPoint() {
		return origin;
	}

	/**
	 * @return the height of the video in pixels
	 */
	public double getVideoHeightInPixels() {
		return vidCap.get(Videoio.CAP_PROP_FRAME_HEIGHT);
	}

	/**
	 * @return the width of the video in pixels
	 */
	public double getVideoWidthInPixels() {
		return vidCap.get(Videoio.CAP_PROP_FRAME_WIDTH);
	}

}
