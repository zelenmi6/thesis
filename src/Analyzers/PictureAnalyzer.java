package analyzers;

import java.util.List;

/**
 * @author Milan
 * @version 1.0
 * @created 16-Jun-2016 12:46:44 PM
 */
public class PictureAnalyzer implements DataAnalyzerInterface {

	public PictureAnalyzer(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * Returns list of picture ids in the database that contain the queried point.
	 * 
	 * @param longitude
	 * @param latitude
	 * @param altitude
	 */
	public List<Integer> FindPointCart(double x, double y, double z){
		return null;
	}

	/**
	 * Returns list of picture ids in the database that contain the queried point.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public List<Integer> FindPointGPS(double longitude, double latitude, double altitude){
		return null;
	}

}












