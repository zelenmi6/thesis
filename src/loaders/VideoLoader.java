package loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import cameras.AbstractCamera;
import constants.CameraTesting;
import database.PictureTelemetryDao;
import database.VideoPicturesDao;
import geometry.Calculations;
import geometry.CameraCalculator;
import geometry.ConvexHull;
import geometry.GeoLocation;

public class VideoLoader {
	
	private final long TELEMETRY_INTERVAL_MS = 0;
	private final double PITCH_OFFSET_DEGREES = -50;
	private final double HEADING_OFFSET_DEGREES = -90;
	private final double ALTITUDE_OFFSET_METERS = 2;
//	private final double PITCH_OFFSET_DEGREES = 0;
	
	private AbstractCamera camera = null;
	private VideoPicturesDao dao = VideoPicturesDao.getInstance();
	private GMatrix rotationMatrix = null;
	private GVector translationVector = null;
	private Timestamp initialTime = null;
	private Timestamp telemetryStartTime = null;
	private Timestamp lastTelemetryTime = null;
	
	private boolean cameraFacingBack = false;
	
	private AreaBoundingPolygon areaBoundingPolygon;
	
	
	public VideoLoader(String videoPath, String telemetryPath, String monitoredAreaName, AbstractCamera camera,
			long telemetryStartTime, boolean cameraFacingBack) throws SQLException {
		this.camera = camera;
		this.cameraFacingBack = cameraFacingBack;
		
		try {
			FileInputStream fs;
			fs = new FileInputStream(telemetryPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
			readMeta(br, telemetryStartTime);
			
			dao.setAutocommit(false);
			int monitoredAreaId = dao.addMonitoredArea(monitoredAreaName);
			int dataSetId = dao.addDataSet(monitoredAreaId, videoPath, camera, initialTime, true);
			readTelemetry(br, monitoredAreaId, dataSetId);
			areaBoundingPolygon.computeNewBoundingPolygon();
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
//		File videoFile = new File(videoPath);
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
			Vector3d [] boundingPolygon = getBoundingPolygon(telemetry);
			dao.addFrame(dataSetId, telemetry, boundingPolygon, frame);
			lastTelemetryTime = telemetry.timestamp;
			System.out.println(telemetry);
		}
		
		while ((line = br.readLine()) != null) {
			Telemetry telemetry = parseTelemetryLine(line);
			if (telemetry.timestamp.getTime() - lastTelemetryTime.getTime() < TELEMETRY_INTERVAL_MS)
				continue;
			
			long time = (telemetry.timestamp.getTime() - initialTime.getTime());
			int frame = Math.round(time * camera.getFps()) / 1000;
			Vector3d [] boundingPolygon = getBoundingPolygon(telemetry);
			dao.addFrame(dataSetId, telemetry, boundingPolygon, frame);
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
		areaBoundingPolygon = new AreaBoundingPolygon(monitoredAreaId);
	}
	
	private double [] getLongitudeLatitudeAltitudeFromTelemetry(String line) {
		String [] tokens = line.split(",");
		return new double[]{Double.parseDouble(tokens[2]), Double.parseDouble(tokens[1]), Double.parseDouble(tokens[3])};
	}
	
	//!TODO pokud telemetrii neberu, tak ji ani nevyrabet
	private Telemetry parseTelemetryLine(String telemetry) {
		String [] tokens = telemetry.split(",");
		
		double longitude = Double.parseDouble(tokens[2]);
		double latitude = Double.parseDouble(tokens[1]);
		// !TODO tohle mozna odecitat od az z konvertovanych souradnic, jinak nam vychazi zaporna altitude
		double altitude = Double.parseDouble(tokens[3]) + ALTITUDE_OFFSET_METERS;
		if (altitude < 0) {
			altitude = 0;
		}
		Vector3d cartCoords = hw.utils.GeographyUtils.fromGPStoCart(longitude, latitude, 
				altitude , rotationMatrix, translationVector);
		
		areaBoundingPolygon.addPointsToBoundingPolygon(longitude, latitude);
		
		if (cameraFacingBack == false) {
			return new Telemetry(new Timestamp(Long.parseLong(tokens[0]) + telemetryStartTime.getTime()),
					cartCoords,
					Math.toRadians(Double.parseDouble(tokens[4])),
					Math.toRadians(Double.parseDouble(tokens[5]) + PITCH_OFFSET_DEGREES),
					Math.toRadians(Double.parseDouble(tokens[6]) + HEADING_OFFSET_DEGREES));
		} else {
			return new Telemetry(new Timestamp(Long.parseLong(tokens[0]) + telemetryStartTime.getTime()),
					cartCoords,
					Math.toRadians(Double.parseDouble(tokens[4]) * -1),
					Math.toRadians((Double.parseDouble(tokens[5]) * -1 + PITCH_OFFSET_DEGREES)%360),
					Math.toRadians((Double.parseDouble(tokens[6]) + HEADING_OFFSET_DEGREES))%360);
		}
		
	}
	
private class AreaBoundingPolygon {
		List<Vector3d> boundingPolygonGPS = new ArrayList<>();
		int monitoredAreaId;
		
		public AreaBoundingPolygon(int monitoredAreaId) throws SQLException {
			VideoPicturesDao dao = VideoPicturesDao.getInstance();
				// get bounding polygon in GPS
			this.monitoredAreaId = monitoredAreaId;
				boundingPolygonGPS = dao.getMonitoredAreaBoundingPolygon(monitoredAreaId);
//				printBoundingPolygonCart();
//				printBoundingPolygonGPS();
			// najit v db bounding_polygon
				// pokud neni, tak prazdny
				// pokud ma 1, nebo 2 body, tak co?
			// prevest na kart
			// pri nacitani pridavat dalsi body
			// provest convex hull
			// mozna prescalovat, aby se kompenzovaly nepresnosti?
			// ulozit do db
				System.out.println("BP ready");
		}
		
		public void addPointsToBoundingPolygon(double longitude, double latitude) {
			GeoLocation cameraPosition = GeoLocation.fromDegrees(latitude, longitude);
			GeoLocation[] geoLocation = cameraPosition.boundingCoordinates(CameraTesting.MAX_DISTANCE / 1000, 6371.01);
			boundingPolygonGPS.add(new Vector3d(geoLocation[0].getLatitudeInDegrees(), geoLocation[0].getLongitudeInDegrees(), 0));
			boundingPolygonGPS.add(new Vector3d(geoLocation[1].getLatitudeInDegrees(), geoLocation[0].getLongitudeInDegrees(), 0));
			boundingPolygonGPS.add(new Vector3d(geoLocation[0].getLatitudeInDegrees(), geoLocation[1].getLongitudeInDegrees(), 0));
			boundingPolygonGPS.add(new Vector3d(geoLocation[1].getLatitudeInDegrees(), geoLocation[1].getLongitudeInDegrees(), 0));
			
		}
		
		public void computeNewBoundingPolygon() {
			Vector3d[] newBoundingPolygon = ConvexHull.convex_hull(
					boundingPolygonGPS.toArray(new Vector3d[boundingPolygonGPS.size()]));
			System.out.println("Printing new polygon");
			try {
				dao.saveMonitoredAreaBoundingPolygon(monitoredAreaId, newBoundingPolygon);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			printBoundingPolygonGPS(newBoundingPolygon);
//			for (Vector3d corner : newBoundingPolygon) {
//				System.out.println("x: " + corner.x + " y: " + corner.y);
//			}
		}
		
		
		private void printBoundingPolygonGPS() {
			System.out.println("Printing new polygon");
			for (Vector3d corner : boundingPolygonGPS) {
				System.out.println("lat, long, alt: " + corner.y + ", " + corner.x + ", " + corner.z);
			}
		}
		
		private void printBoundingPolygonGPS(Vector3d[] boundingPolygon) {
			System.out.println("Printing new polygon");
			for (Vector3d corner : boundingPolygon) {
//				Vector3d gpsCoord = hw.utils.GeographyUtils.fromCartToGPS(corner.x, corner.y, corner.z, 
//						rotationMatrix, translationVector);
				System.out.println(corner.y + ", " + corner.x + " z: " + corner.z);
			}
		}
	}
}










