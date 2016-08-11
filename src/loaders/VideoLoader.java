package loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;

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
//	private int monitoredAreaId;
//	private Integer dataSetId = null;
//	private Vector3d origin = null;
	private GMatrix rotationMatrix = null;
	private GVector translationVector = null;
	private Timestamp initialTime;
	
	
	public VideoLoader(String videoPath, String telemetryPath, String monitoredAreaName, AbstractCamera camera) throws Exception {
		this.videoPath = videoPath;
		this.telemetryPath = telemetryPath;
		this.camera = camera;
		
		//!TODO vypnout autocommit, pri nejakem neuspechu hodit rollback. spravne odchytavat vyjimky
		
		FileInputStream fs= new FileInputStream(telemetryPath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		
		readMeta(br);
		int monitoredAreaId = dao.addMonitoredArea(monitoredAreaName);
		int dataSetId = dao.addDataSet(monitoredAreaId, videoPath, camera, initialTime, true);
		readTelemetry(br, monitoredAreaId);
		
		
		File videoFile = new File(videoPath);
	}
	
	private void readMeta(BufferedReader br) throws IOException {
		String line;
		if ((line = br.readLine()) == null) {
			throw new IOException("File in incorrect format. The first line has to contain InitialTime \"Epoch Time\"");
		} else {
			initialTime = new Timestamp(Long.parseLong(line.split(" ")[1]));
		}
		
		if ((line = br.readLine()) == null) {
			throw new IOException("File in incorrect format. The second line has to contain information about columns of the cvs file.");
		} else {
			return;
		}
	}
	
	private void readTelemetry(BufferedReader br, int monitoredAreaId) throws Exception {
		String line;
		if ((line = br.readLine()) != null) {
			double [] lonLatAlt = getLongitudeLatitudeAltitudeFromTelemetry(line);
			setRotationMatrixAndTranslationVector(monitoredAreaId, lonLatAlt);
			Telemetry telemetry = parseTelemetryLine(line);
		}
		
		while ((line = br.readLine()) != null) {
			Telemetry telemetry = parseTelemetryLine(line);
			System.out.println(telemetry);
		}
	}
	
	private void setRotationMatrixAndTranslationVector(int monitoredAreaId, double [] lonLatAlt) throws Exception {
		String origin = dao.originSet(monitoredAreaId);
		if (origin == null) {
			// set new rotation matrix and translation vector and save origin to the database
			dao.setMonitoredAreaOrigin(monitoredAreaId, lonLatAlt);
//			this.origin = new Vector3d(lonLatAlt[0], lonLatAlt[1], lonLatAlt[2]);
			rotationMatrix = hw.utils.GeographyUtils.getRotationMatrix(lonLatAlt[0], lonLatAlt[1]);
			translationVector = hw.utils.GeographyUtils.getTranslationVector(lonLatAlt[0], lonLatAlt[1], lonLatAlt[2]);
		} else {
			String pattern = "POINT Z \\(|\\s|\\)";
			String tokens [] = origin.split(pattern);
			double longitude = Double.parseDouble(tokens[1]);
			double latitude = Double.parseDouble(tokens[2]);
			double altitude = Double.parseDouble(tokens[3]);
//			this.origin = new Vector3d(longitude, latitude, altitude);
			rotationMatrix = hw.utils.GeographyUtils.getRotationMatrix(longitude, latitude);
			translationVector = hw.utils.GeographyUtils.getTranslationVector(longitude, latitude, altitude);
		}
	}
	
	private double [] getLongitudeLatitudeAltitudeFromTelemetry(String line) {
		String [] tokens = line.split(",");
		return new double[]{Double.parseDouble(tokens[2]), Double.parseDouble(tokens[1]), Double.parseDouble(tokens[3])};
	}
	
	private Telemetry parseTelemetryLine(String telemetry) {
		String [] tokens = telemetry.split(",");
		
		Vector3d cartCoords = hw.utils.GeographyUtils.fromGPStoCart(Double.parseDouble(tokens[2]), 
				Double.parseDouble(tokens[1]), 
				Double.parseDouble(tokens[3]), 
				rotationMatrix, translationVector);
		
		return new Telemetry(new Timestamp(Long.parseLong(tokens[0]) + initialTime.getTime()),
				cartCoords,
				Math.toRadians(Double.parseDouble(tokens[4])),
				Math.toRadians(Double.parseDouble(tokens[5])),
				Math.toRadians(Double.parseDouble(tokens[6])));
	}
}










