package loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;
import javax.vecmath.Vector3d;

import database.PictureTelemetryDao;
import geometry.Calculations;
import geometry.CameraCalculator;

/**
 * @author Milan
 * @version 1.0
 * @created 16-Jun-2016 12:47:00 PM
 */
public class DirectoryPicturesLoader {
	
	private static final List<String> IMAGE_EXTENSIONS = Collections.unmodifiableList(
		    new ArrayList<String>() {{
		        add("jpg");
		    }});
	private final String TELEMETRY_FILE_EXTENSION = "log";
	
	private String directoryPath = null;
	private Double FOVv = null;
	private Double FOVh = null;
	private Integer dataSetId = null;
	private Vector3d origin = null;
	private GMatrix rotationMatrix = null;
	private GVector translationVector = null;
	
	private PictureTelemetryDao dao = PictureTelemetryDao.getInstance();
	
	/**
	 * 
	 * @param directoryPath
	 */
	public DirectoryPicturesLoader(String monitoredAreaName, String directoryPath, double FOVv, double FOVh){
		this.directoryPath = directoryPath;
		this.FOVv = FOVv;
		this.FOVh = FOVh;
		PictureTelemetryDao.getInstance();//???
		int monitoredAreaId;
		try {
			monitoredAreaId = dao.addMonitoredArea(monitoredAreaName);
			dataSetId = dao.addDataSet(monitoredAreaId, directoryPath, FOVv, FOVh);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File directory = new File(directoryPath);
		File [] listOfFiles = directory.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && isImage(listOfFiles[i])) {
				processImageFile(listOfFiles[i], listOfFiles);
			}
		}
	}
	
	private void processImageFile(File imageFile, File [] listOfFiles) {
		try {
			File telemetryFile = findTelemetryFile(imageFile, listOfFiles);
			try {
				Telemetry telemetry = parseTelemetryFile(telemetryFile);
				Vector3d [] boundingBox = getBoundingBox(telemetry);
				int pictureId = dao.addPicture(dataSetId, imageFile.getPath(), boundingBox);
				dao.addTelemetry(pictureId, telemetry);
			} catch (Exception e) {
				e.printStackTrace();
			}
//			System.out.println("Found: " + imageFile.getName() + " : " + telemetryFile.getName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private Vector3d [] getBoundingBox(Telemetry telemetry) {
		Vector3d [] polygon = CameraCalculator.getBoundingPolygon(FOVh, FOVv, 
				telemetry.coordinates.z, telemetry.heading, telemetry.roll, telemetry.pitch);
//		System.out.println("UAV: " + telemetry.coordinates.x+ ", " + telemetry.coordinates.y + "," + telemetry.coordinates.z);
		for (Vector3d point : polygon) {
			Calculations.translate3dPointXYonly(point, telemetry.coordinates);
//			System.out.println(point.toString());
		}
//		System.out.println("-----------------");
		
		return polygon;
	}
	
	private File findTelemetryFile(File imageFile, File [] listOfFiles) throws FileNotFoundException {
		for (File telemetryFile : listOfFiles) {
			if (isTelemetryFileOfImage(imageFile, telemetryFile)) {
				return telemetryFile;
			}
		}
		throw new FileNotFoundException("Telemetry file not found, name of picture: " + imageFile.getName());
	}
	
	private boolean isImage(File file) {
		String [] tokens = file.getName().split("\\.");
		if (tokens.length != 2) {
			// incorrect file name
			return false;
		}
		if (IMAGE_EXTENSIONS.contains(tokens[1])) {
			return true;
		}
		
		return false;
	}
	
	private boolean isTelemetryFileOfImage(File imageFile, File telemetryFile) {
		String [] telemetryTokens = telemetryFile.getName().split("\\.");
		// we do not have to check if the imageFile is correct since we did that before
		String [] imageTokens = imageFile.getName().split("\\.");
		if (telemetryTokens.length != 2) {
			// incorrect file name
			return false;
		}
		if (telemetryTokens[1].equals(TELEMETRY_FILE_EXTENSION)
				&& telemetryTokens[0].equals(imageTokens[0])) {
			// if it is a telemetry file and its name equals the image name
			return true;
		}
		return false;
	}
	
	private Telemetry parseTelemetryFile(File telemetryFile) throws IOException {
		FileInputStream fs= new FileInputStream(telemetryFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		String telemetry = br.readLine();
		
		String [] tokens = telemetry.split(" ");
		double lon = Double.parseDouble(tokens[0].split(":")[1]);
		double lat = Double.parseDouble(tokens[1].split(":")[1]);
		double alt = Double.parseDouble(tokens[2].split(":")[1]);
		double heading = Double.parseDouble(tokens[3].split(":")[1]);
		double roll = Double.parseDouble(tokens[4].split(":")[1]);
		double pitch = Double.parseDouble(tokens[5].split(":")[1]);
		
		if (origin == null) {
//			!TODO Z coordinate of the origin is 0 and the AUV's altitude!
			calculateRotationMatrixAndVector(lon, lat, 0);
//			origin = hw.utils.GeographyUtils.fromGPStoCart(lon, lat, alt, rotationMatrix, translationVector);
		}
		
		Vector3d cartCoords = hw.utils.GeographyUtils.fromGPStoCart(lon, lat, alt, rotationMatrix, translationVector);
		
//		System.out.println(lon);
//		System.out.println(lat);
//		System.out.println(alt);
//		System.out.println(cartCoords.x);
//		System.out.println(cartCoords.y);
//		System.out.println(cartCoords.z);
//		System.out.println(heading);
//		System.out.println(roll);
//		System.out.println(pitch);
//		System.out.println("--------------------------");
		
		return new Telemetry(new Timestamp(-1), cartCoords, heading, roll, pitch);
	}
	
	private void calculateRotationMatrixAndVector(double longitude, double latitude, double altitude) {
		origin = new Vector3d(longitude, latitude, altitude);
		rotationMatrix = hw.utils.GeographyUtils.getRotationMatrix(longitude, latitude);
		translationVector = hw.utils.GeographyUtils.getTranslationVector(longitude, latitude, altitude);
	}

}

















