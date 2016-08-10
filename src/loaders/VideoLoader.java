package loaders;

import java.io.BufferedReader;
import java.io.File;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;
import javax.vecmath.Vector3d;

import Cameras.AbstractCamera;
import database.VideoPicturesDao;

public class VideoLoader {
	
	private AbstractCamera camera;
	private VideoPicturesDao dao = VideoPicturesDao.getInstance();
	private String videoPath;
	private String telemetryPath;
	private long telemetryStartTime;
	private Integer dataSetId = null;
	private Vector3d origin = null;
	private GMatrix rotationMatrix = null;
	private GVector translationVector = null;
	
	
	public VideoLoader(String videoPath, String telemetryPath, String monitoredAreaName, AbstractCamera camera) throws Exception {
		this.videoPath = videoPath;
		this.telemetryPath = telemetryPath;
		this.camera = camera;
		
		int monitoredAreaId = dao.addMonitoredArea(monitoredAreaName);
		
		File videoFile = new File(videoPath);
	}
	
	
	
	private Telemetry parseTelemetryLine(String telemetry) {
		String [] tokens = telemetry.split(",");
		
		Vector3d cartCoords = hw.utils.GeographyUtils.fromGPStoCart(Double.parseDouble(tokens[2]), 
				Double.parseDouble(tokens[1]), 
				Double.parseDouble(tokens[3]), 
				rotationMatrix, translationVector);
		
		return new Telemetry(Long.parseLong(tokens[0]),
				cartCoords,
				Math.toRadians(Double.parseDouble(tokens[4])),
				Math.toRadians(Double.parseDouble(tokens[5])),
				Math.toRadians(Double.parseDouble(tokens[6])));
	}
}
