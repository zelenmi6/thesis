package loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;
import javax.vecmath.Vector3d;

import Cameras.AbstractCamera;
import database.VideoPicturesDao;
import geometry.Calculations;
import geometry.CameraCalculator;

public class VideoLoader {
	
	private final long TELEMETRY_INTERVAL_MS = 1000;
	
	private AbstractCamera camera = null;
	private VideoPicturesDao dao = VideoPicturesDao.getInstance();
	private GMatrix rotationMatrix = null;
	private GVector translationVector = null;
	private Timestamp initialTime = null;
	private Timestamp telemetryStartTime = null;
	private Timestamp lastTelemetryTime = null;
	
	
	public VideoLoader(String videoPath, String telemetryPath, String monitoredAreaName, AbstractCamera camera,
			long telemetryStartTime) throws SQLException {
		this.camera = camera;
		
		try {
			FileInputStream fs;
			fs = new FileInputStream(telemetryPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
			readMeta(br, telemetryStartTime);
			
			dao.setAutocommit(false);
			int monitoredAreaId = dao.addMonitoredArea(monitoredAreaName);
			int dataSetId = dao.addDataSet(monitoredAreaId, videoPath, camera, initialTime, true);
			readTelemetry(br, monitoredAreaId, dataSetId);
			dao.commit();
		} catch (FileNotFoundException e) {
			dao.rollback();
			e.printStackTrace();
		} catch (IOException e) {
			dao.rollback();
			e.printStackTrace();
		} catch (SQLException e) {
			dao.rollback();
			e.printStackTrace();
		}
		File videoFile = new File(videoPath);
	}
	
	private void readMeta(BufferedReader br, long telemetryStartTime) throws IOException {
		String line;
		if ((line = br.readLine()) == null) {
			throw new IOException("File in incorrect format. The first line has to contain InitialTime \"Epoch Time\"");
		} else {
			initialTime = new Timestamp(Long.parseLong(line.split(" ")[1]));
			this.telemetryStartTime = new Timestamp(initialTime.getTime() + telemetryStartTime);
		}
		
		if ((line = br.readLine()) == null) {
			throw new IOException("File in incorrect format. The second line has to contain information about columns of the cvs file.");
		} else {
			return;
		}
	}
	
	private void readTelemetry(BufferedReader br, int monitoredAreaId, int dataSetId) throws IOException, SQLException {
		String line;
		if ((line = br.readLine()) != null) {
			double [] lonLatAlt = getLongitudeLatitudeAltitudeFromTelemetry(line);
			setRotationMatrixAndTranslationVector(monitoredAreaId, lonLatAlt);
			Telemetry telemetry = parseTelemetryLine(line);
			
			long time = (telemetry.timestamp.getTime() - initialTime.getTime()) / 1000;
			int frame = Math.round(time * camera.getFps());
			dao.addFrame(dataSetId, telemetry, getBoundingPolygon(telemetry), frame);
			lastTelemetryTime = telemetry.timestamp;
		}
		
		while ((line = br.readLine()) != null) {
			Telemetry telemetry = parseTelemetryLine(line);
			if (telemetry.timestamp.getTime() - lastTelemetryTime.getTime() < TELEMETRY_INTERVAL_MS)
				continue;
			
			long time = (telemetry.timestamp.getTime() - initialTime.getTime()) / 1000;
			int frame = Math.round(time * camera.getFps());
			dao.addFrame(dataSetId, telemetry, getBoundingPolygon(telemetry), frame);
			lastTelemetryTime = telemetry.timestamp;
			System.out.println(telemetry);
		}
	}
	
	private Vector3d [] getBoundingPolygon(Telemetry telemetry) {
		Vector3d [] boundingPolygon = CameraCalculator.getBoundingPolygon(camera.getFovHorizontal(),
				camera.getFovVertical(), telemetry.coordinates.z, telemetry.roll, telemetry.pitch, telemetry.heading);
		for (Vector3d point : boundingPolygon) {
			Calculations.translate3dPointXYonly(point, telemetry.coordinates);
		}
		return boundingPolygon;
	}
	
	private void setRotationMatrixAndTranslationVector(int monitoredAreaId, double [] lonLatAlt) throws SQLException {
		String origin = dao.originSet(monitoredAreaId);
		if (origin == null) {
			// set new rotation matrix and translation vector and save origin to the database
			dao.setMonitoredAreaOrigin(monitoredAreaId, lonLatAlt);
			rotationMatrix = hw.utils.GeographyUtils.getRotationMatrix(lonLatAlt[0], lonLatAlt[1]);
			translationVector = hw.utils.GeographyUtils.getTranslationVector(lonLatAlt[0], lonLatAlt[1], lonLatAlt[2]);
		} else {
			String pattern = "POINT Z \\(|\\s|\\)";
			String tokens [] = origin.split(pattern);
			double longitude = Double.parseDouble(tokens[1]);
			double latitude = Double.parseDouble(tokens[2]);
			double altitude = Double.parseDouble(tokens[3]);
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
		
		return new Telemetry(new Timestamp(Long.parseLong(tokens[0]) + telemetryStartTime.getTime()),
				cartCoords,
				Math.toRadians(Double.parseDouble(tokens[4])),
				Math.toRadians(Double.parseDouble(tokens[5])),
				Math.toRadians(Double.parseDouble(tokens[6])));
	}
}










