package analyzers;

import java.sql.SQLException;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import database.VideoPicturesDao;

/**
 * Class used for development. Tests conversions from GPS to Cartesian coordinates and back.
 * Method names should be self-explanatory.
 * @author Milan
 *
 */
public class VideoAnalyzer {
	
	private GMatrix rotationMatrix = null;
	private GVector translationVector = null;
	
	public VideoAnalyzer() {
		
	}
	
	public void printDataSetInformation(int dataSetId) {
		VideoPicturesDao dao = VideoPicturesDao.getInstance();
		try {
			dao.printDataSetInformation(dataSetId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setRotationMatrixAndTranslationVector(double lon, double lat, double alt) {
		rotationMatrix = hw.utils.GeographyUtils.getRotationMatrix(lon, lat);
		translationVector = hw.utils.GeographyUtils.getTranslationVector(lon, lat, alt);
	}
	
	public void testConversion(Vector3d coordinates) {
		System.out.println("GPS: " + coordinates.toString());
		Vector3d converted = hw.utils.GeographyUtils.
				fromGPStoCart(coordinates.x, coordinates.y, coordinates.z, rotationMatrix, translationVector);
		System.out.println("Converted: " + converted);
		Vector3d convertedBack = hw.utils.GeographyUtils.
				fromCartToGPS(new Point3d(converted.x, converted.y, converted.z), rotationMatrix, translationVector);
		System.out.println("Converted back: " + convertedBack);
	}
	
	public void printGpsOfCamera(int frameId) {
		VideoPicturesDao dao = VideoPicturesDao.getInstance();
		try {
			double [] coord = dao.getCameraCoordinates(frameId);
			double [] angles = dao.getCameraAngles(frameId);
			System.out.println("x: " + coord[0] + ", y: " + coord[1] + ", z: " + coord[2] + ", roll: " + Math.toDegrees(angles[0])
					+ ", pitch: " + Math.toDegrees(angles[1]) + ", yaw: " + Math.toDegrees(angles[2]));
			convertCartToGps(coord[0], coord[1], coord[2]);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void convertCartToGps(double x, double y, double z) {
		if (rotationMatrix == null || translationVector == null) {
			System.out.println("Rot matrix and trans vector undefined");
		} else {
			Vector3d coordinates = hw.utils.GeographyUtils.fromCartToGPS(new Point3d(x, y, z), rotationMatrix, translationVector);
			System.out.println(coordinates.toString());
		}
	}
}
