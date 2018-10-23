package datamodel;

import java.awt.Rectangle;
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

	public Video(String filePath) throws FileNotFoundException {
		this.filePath = filePath;
		connectVideoCapture();
		if (!vidCap.isOpened()) {
			throw new FileNotFoundException("Unable to open video file: " + filePath);
		}
		System.out.println("Made video object");
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
		if (!vidCap.isOpened()) {
			throw new FileNotFoundException("Unable to open video file: " + filePath);
		}
	}

	public void setCurrentFrameNum(int seekFrame) {
		vidCap.set(Videoio.CV_CAP_PROP_POS_FRAMES, (double) seekFrame);
	}

	public synchronized int getCurrentFrameNum() {
		return (int) Math.ceil(vidCap.get(Videoio.CV_CAP_PROP_POS_FRAMES));
	}

	public synchronized Mat readFrame() {
		Mat frame = new Mat();
		vidCap.read(frame);
		return frame;
	}

	public String getFilePath() {
		return this.filePath;
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

	public void setXPixelsPerCm(double xPixelsPerCm) {
		this.xPixelsPerCm = xPixelsPerCm;
	}

	/**
	 * @return the number of y pixels per centimeter (cm)
	 */
	public double getYPixelsPerCm() {
		return yPixelsPerCm;
	}

	public void setYPixelsPerCm(double yPixelsPerCm) {
		this.yPixelsPerCm = yPixelsPerCm;
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

	public void setVidCap(VideoCapture vidCap) {
		this.vidCap = vidCap;
	}
	public void setVidCap() {
		this.vidCap = new VideoCapture();
	}

}
