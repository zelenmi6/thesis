package hw.utils;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * This class uses tools from {@link GpsTools} to convert coordinates from Cartesian to GPS
 * coordinate system and vice versa (where the Cartesian system is Geocentric and its z-axis points at the North pole).
 * Then translation and rotations are used to convert from/to Geocentric coordination system to/from Cartesian system
 * with specified origin and x-axis pointing east, y-axis pointing south and z-axis pointing away from the Earth's center.   
 * 
 * @author Selecky
 *
 */
public class GeographyUtils {
	
	///////////////////////////////////////////////////
	///////// GPS -> Cartesian  ///////////////////////
	///////////////////////////////////////////////////
	
	/** constant to invert coordinate system -> y-axis points to the South*/
	private static final int INV = -1;
	
	/**
	 * Transforms GPS coordinates of a point to Cartesian coordinates, given GPS coordinates of new origin.
	 * (x-axis is pointing to the east, y-axis to the south and z-axis up)
	 * @param pointGPS - GPS coordinates of particular point (longitude,latitude,altitude)
	 * @param originGPS - GPS coordinates of new origin (longitude,latitude,altitude)
	 * @return Vector3d (x,y,z)
	 */
	public static Vector3d fromGPStoCart(Vector3d pointGPS , Vector3d originGPS){
		return fromGPStoCart(pointGPS.x, pointGPS.y, pointGPS.z , originGPS.x, originGPS.y, originGPS.z);
	}
	
	/**
	 * Transforms GPS coordinates of a point to Cartesian coordinates, given GPS coordinates of new origin.
	 * (x-axis is pointing to the east, y-axis to the south and z-axis up)
	 * @param lon - GPS longitude of particular point
	 * @param lat - GPS latitude of particular point
	 * @param alt - GPS altitude of particular point
	 * @param orig_lon - GPS longitude of the origin
	 * @param orig_lat - GPS latitude of the origin
	 * @param orig_alt - GPS altitude of the origin
	 * @return Vector3d (x,y,z)
	 */
	public static Vector3d fromGPStoCart(double lon, double lat, double alt , double orig_lon, double orig_lat, double orig_alt){
		Vector3d vec = GpsTools.getCartCoordsFromGpsDeg(lon,lat,alt);
		Vector3d orig_vec = GpsTools.getCartCoordsFromGpsDeg(orig_lon, orig_lat, orig_alt);
		// CONVERT TO RADIANS
		GMatrix m1 = getRotationMatrix(orig_lon, orig_lat);				
		// translation vec
		GVector t = new GVector(new double[]{-orig_vec.x, -orig_vec.y, -orig_vec.z});
		// vector
		GVector gv = new GVector(new double[]{vec.x,vec.y,vec.z});		
		// X = R.(gv-t)
		t.add(gv);		
		t.mul(m1, t);			
		return new Vector3d(new double[]{t.getElement(0), INV*t.getElement(1), t.getElement(2)});
	}
	
	/**
	 * Transforms GPS coordinates of a point to Cartesian coordinates, given rotation matrix and translation 
	 * vector to specify new coordinates basis. (x-axis is pointing to the east, y-axis to the south and z-axis up)
	 * @param lon - longitude [deg] of the given point.
	 * @param lat - latitude [deg] of the given point.
	 * @param alt - altitude [m] of the given point.
	 * @param rototion - rotation matrix constructed as rotation around z-axis(longitude), around y-axis (latitude),and around z-axis (pi/2).
	 * @param translation - translation vector - translate the center of the earth to the new origin.
	 * @return Vector3d (x,y,z)
	 */
	public static Vector3d fromGPStoCart(double lon, double lat, double alt , GMatrix rototion, GVector translation){
		// clone because values are changed
		GMatrix rot = (GMatrix) rototion.clone();
		GVector transl = (GVector) translation.clone();
				
		Vector3d vec = GpsTools.getCartCoordsFromGpsDeg(lon,lat,alt);
		GVector gv = new GVector(new double[]{vec.x,vec.y,vec.z});		
		// X = rot.(gv-t)
		transl.negate();
		transl.add(gv);		
		transl.mul(rot, transl);			
		return new Vector3d(new double[]{transl.getElement(0), INV*transl.getElement(1), transl.getElement(2)});
	}
	
	
	///////////////////////////////////////////////////
	///////// Cartesian -> GPS  ///////////////////////
	///////////////////////////////////////////////////
	
	/**
	 * Transforms Cartesian coordinates of a point to GPS coordinates, given rotation matrix and translation 
	 * vector to specify new coordinates basis. (x axis is pointing roughly to the positive values of longitude)
	 * @param pointCart - Cartesian coordinates of given point.
	 * @param rot - rotation matrix constructed as rotation around z-axis(longitude), around y-axis (latitude),and around z-axis (pi/2).
	 * @param transl - translation vector - translate the center of the earth to the new origin.
	 * @return Vector3d of (longitude, latitude, altitude)
	 */
	public static Vector3d fromCartToGPS(Point3d pointCart ,  GMatrix rot, GVector transl){
		return fromCartToGPS(pointCart.x, pointCart.y, pointCart.z , rot, transl);
	}
	
	
	/**
	 * Transforms Cartesian coordinates of a point to GPS coordinates, given rotation matrix and translation 
	 * vector to specify new coordinates basis. (x-axis is pointing to the east, y-axis to the south and z-axis up)
	 * @param x - [m]
	 * @param y - [m]
	 * @param z - [m]
	 * @param rototion - rotation matrix constructed as rotation around z-axis(longitude), around y-axis (latitude),and around z-axis (pi/2).
	 * @param translation - translation vector - translate the center of the earth to the new origin.
	 * @return Vector3d (longitude, latitude, altitude)
	 */
	public static Vector3d fromCartToGPS(double x, double y, double z , GMatrix rototion, GVector translation){
		// clone because values are changed
		GMatrix rot = (GMatrix) rototion.clone();
		GVector transl = (GVector) translation.clone();
		
		// inv(R) = transpose(R)	
		rot.transpose();		
		// vector
		GVector gv = new GVector(new double[]{x,INV*y,z});		
		// gv = inv(R).X + t
		gv.mul(rot, gv);
		transl.add(gv);			
		// to GPS
		return getGpsDegCoordsFromCartesian(transl.getElement(0), transl.getElement(1), transl.getElement(2));
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	///////// Computing of the rotation matrix and translation vector ////////
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * Computes rotation matrix specifying new coordinate basis.
	 * @param orig_lon - longitude of the origin (degrees)
	 * @param orig_lat - latitude of the origin (degrees)
	 * @return GMatrix rotation matrix
	 */
	public static GMatrix getRotationMatrix(double orig_lon, double orig_lat){
		// CONVERT TO RADIANS
		double i_long = orig_lon*GpsTools.DEG_TO_RAD;
		double i_lat = orig_lat*GpsTools.DEG_TO_RAD;
		
		double cosLong = Math.cos(i_long);
		double sinLong = Math.sin(i_long);
		double cosLat = Math.cos(Math.PI/2-i_lat);
		double sinLat = Math.sin(Math.PI/2-i_lat);
		
		// rotation around y-axis
		GMatrix m1 = new GMatrix(3,3, new double[]{cosLat, 0, -sinLat, 0, 1, 0, sinLat, 0, cosLat});
		// rotation around z-axis
		GMatrix m2 = new GMatrix(3,3, new double[]{cosLong, sinLong, 0, -sinLong, cosLong, 0, 0, 0, 1});
		// rotation around z-axis (for PI/2)
		GMatrix m3 = new GMatrix(3,3, new double[]{0, 1, 0, -1, 0, 0, 0, 0, 1});
		// count the rotation matrix
		m1.mul(m2); 	
		m3.mul(m1); // R = M3*M1*M2
		return m3;
	}
	
	/**
	 * Computes translation vector from the Earth center to the new coordinate system origin.
	 * @param orig_lon - longitude of the new origin
	 * @param orig_lat - latitude of the new origin 
	 * @param orig_alt - altitude of the new origin
	 * @return
	 */
	public static GVector getTranslationVector(double orig_lon, double orig_lat, double orig_alt){
		Vector3d orig_vec = GpsTools.getCartCoordsFromGpsDeg(orig_lon, orig_lat, orig_alt);
		// translation vec
		return new GVector(new double[]{orig_vec.x, orig_vec.y, orig_vec.z});
	}

	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////				Other Methods				///////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Methods from {@link GpsTools} altered to enable negative altitude - to be used to position an origin of
	 * a new Cartesian system under the Earth's surface, thus rising the actual altitude.
	 * Used, for example, to rise the flight level of a real UAV in simulation when getting lower altitude real data
	 * from it. 
	 */
	public static Vector3d getGpsDegCoordsFromCartesian(Vector3d position) {
		if(position.z < 0){
			return getGpsDegCoordsFromCartesian(position.x,position.y,position.z);
		} else {
			return GpsTools.getGpsDegCoordsFromCartesian(position.x,position.y,position.z);
		}
	}

	public static Vector3d getGpsDegCoordsFromCartesian(final double x, final double y, final double z) {
		final double r = Math.sqrt(x*x + y*y + z*z);
		final double altitudeM = GpsTools.adjustAltitudeM(r - GpsTools.getEarthSphereRadiusM());
		final double latitudeDeg = GpsTools.adjustLatitudeDeg(Math.asin(z/r)*GpsTools.RAD_TO_DEG);
		double longitudeDeg = GpsTools.adjustLongitudeDeg(Math.atan2(y, x)*GpsTools.RAD_TO_DEG,latitudeDeg);
		if (longitudeDeg == -180) {
			longitudeDeg = 180;
		}
		return new Vector3d(longitudeDeg,latitudeDeg,altitudeM);
	}
	
	/** 
	  *  Calculates distance between given and base point.
	  * 
	  *  @param latitude2     current latitude in degrees
	  *  @param longitude2     current longitude in degrees
	  *  @return         distance in meters
	  */
	public static double calculateDistance (double longitude1, double latitude1, double longitude2, double latitude2) {
	    double dLongitude, dLatitude;
	    /** Earth´s (mean) radius in meters */
	    final double RADIUS = 6371000;
	    longitude1 = Math.toRadians(longitude1);
	    latitude1 = Math.toRadians(latitude1);
	   
	    dLongitude = Math.toRadians(longitude2) - longitude1;
	    dLatitude = Math.toRadians(latitude2) - latitude1;
	    latitude2 = Math.toRadians(latitude2);
	 
	    double tmp = Math.pow(Math.sin(dLatitude / 2), 2) + Math.pow(Math.sin(dLongitude / 2), 2) * Math.cos(latitude1) * Math.cos(latitude2);
	    return RADIUS * 2 * Math.asin(Math.sqrt(tmp));
	}
	
	public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == "K") {
			dist = dist * 1.609344;
		} else if (unit == "N") {
			dist = dist * 0.8684;
		}

		return (dist);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
}
