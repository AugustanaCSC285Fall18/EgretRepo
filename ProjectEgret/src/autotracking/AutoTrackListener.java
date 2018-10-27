package autotracking;

import java.io.IOException;
import java.util.List;

import org.opencv.core.Mat;

import datamodel.AnimalTrack;

public interface AutoTrackListener {

	public void handleTrackedFrame(Mat frame, int frameNumber, double percentTrackingComplete);
	public void trackingComplete(List<AnimalTrack> trackedSegments);
}
