package hw.utils;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

/**
* <p>Title: AGENTFLY - GpsConstants</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2009</p>
*
* <p>Company: Agent Technology Center</p>
*
* @author David Sislak
* @version $Revision: 1.28 $ $Date: 2011/02/11 08:45:44 $
*
*/
public class GpsTools {
   /**
    * Common Earth radius
    */
   public static final double EARTH_SPHERE_RADIUS_M = 6371009;
   public static final double EARTH_SPHERE_RADIUS2_M = EARTH_SPHERE_RADIUS_M * EARTH_SPHERE_RADIUS_M;

   public static final double DEG_TO_RAD = Math.PI / 180;  // (exactly)
   public static final double RAD_TO_DEG = 180 / Math.PI;  // (exactly)

////////////////////////////////////////////////////////////////////////////
////////////////////////////ALMIGHTY CONSTANTS /////////////////////////////////////
////////////////////////////////////////////////////////////////////////////


   /**
    * Defines the number of valid digits after decimal point in meters
    * change very wisely
    * 1 digits - about 0.1m precision
    */
   private static final int VALID_DECIMAL_DIGITS_IN_M = 0;


   public static final double EQUAL_PRECISION = 2;
   private static final double HEADING_EQUAL_PRECISION = 1000;

////////////////////////////////////////////////////////////////////////////
////////////////////////////COORDINATES /////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

   /**
    */
   private static final double LONGITUDE_RELATIVE_PRECISION = (Math.pow(10, VALID_DECIMAL_DIGITS_IN_M)*EARTH_SPHERE_RADIUS_M*2*Math.PI)/360;

   /**
    * Defines the number of valid digits after decimal point for latitudeDeg
    * 5 digits - conversion from POSITION_DEG_VALID_DECIMAL_DIGITS_IN_M
    */
   private static final int LATITUDE_DEG_VALID_DECIMAL_DIGITS = 5 + VALID_DECIMAL_DIGITS_IN_M;
   private static final double LATITUDE_DEG_EXP = Math.pow(10,LATITUDE_DEG_VALID_DECIMAL_DIGITS);
   private static final double LATITUDE_DEG_EQUAL_PRECISION = EQUAL_PRECISION/LATITUDE_DEG_EXP;

   /**
    * Defines the number of valid digits after decimal point for coordinates in cartesians
    * 1 digits - about 0.1m precision
    */
   private static final int COORDINATES_CART_VALID_DECIMAL_DIGITS = VALID_DECIMAL_DIGITS_IN_M + 2;
   private static final double COORDINATES_CART_EXP = Math.pow(10,COORDINATES_CART_VALID_DECIMAL_DIGITS);
   private static final double COORDINATES_CART_EQUAL_PRECISION = EQUAL_PRECISION/COORDINATES_CART_EXP;

   /**
    * Defines difference under which two altitude in meters are supposed to be equal
    */
   private static final int ALTITUDE_M_VALID_DECIMAL_DIGITS = VALID_DECIMAL_DIGITS_IN_M + 1;
   private static final double ALTITUDE_M_EXP = Math.pow(10,ALTITUDE_M_VALID_DECIMAL_DIGITS);
   private static final double ALTITUDE_M_EQUAL_PRECISION = EQUAL_PRECISION/ALTITUDE_M_EXP;


////////////////////////////////////////////////////////////////////////////
////////////////////////////DIRECTIONS /////////////////////////////////////
////////////////////////////////////////////////////////////////////////////


   /**
    * Defines the number of valid digits after decimal point for headingDeg, pitchDeg or bankDeg
    * 5 digits - about 1m precision at 10 000km (10 000km * 2pi / (360 * 10^ORIENTATION_DEG_VALID_DECIMAL_DIGITS)
    */
   private static final int PITCH_DEG_VALID_DECIMAL_DIGITS = 5 + VALID_DECIMAL_DIGITS_IN_M;
   private static final double PITCH_DEG_EXP = Math.pow(10,PITCH_DEG_VALID_DECIMAL_DIGITS);
   private static final double PITCH_DEG_EQUAL_PRECISION = EQUAL_PRECISION/PITCH_DEG_EXP;

   /**
    */
   private static final double HEADING_RELATIVE_PRECISION = (Math.pow(10, VALID_DECIMAL_DIGITS_IN_M)*10000000*2*Math.PI)/360;

//   private static final double HEADING_ELEMENT_TRANSITION_CORRECTION_BINARY = 7;

   private static final int BANK_DEG_VALID_DECIMAL_DIGITS = 0;
   private static final double BANK_DEG_EXP = Math.pow(10,BANK_DEG_VALID_DECIMAL_DIGITS);
   private static final double BANK_DEG_EQUAL_PRECISION = EQUAL_PRECISION/BANK_DEG_EXP;

   /**
    * Defines the number of valid digits after decimal point for normalized orientation in cartesians
    * COORDINATES_CART_VALID_DECIMAL_DIGITS digits + 7digits (earth radius) - about 0.001m precision
    */
//   public static final int ORIENTATION_NORMALIZED_CART_VALID_DECIMAL_DIGITS = COORDINATES_CART_VALID_DECIMAL_DIGITS + 7;
//   private static final double ORIENTATION_NORMALIZED_CART_EXP = Math.pow(10,ORIENTATION_NORMALIZED_CART_VALID_DECIMAL_DIGITS);
//   private static final double ORIENTATION_NORMALIZED_EQUAL_PRECISION = EQUAL_PRECISION/ORIENTATION_NORMALIZED_CART_EXP;

////////////////////////////////////////////////////////////////////////////
////////////////////////////OTHER /////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

   /**
    * Defines difference under which two distances are supposed to be equal
    */
   private static final double DISTANCE_EQUAL_PRECISION = EQUAL_PRECISION/Math.pow(10,VALID_DECIMAL_DIGITS_IN_M);
   
   /**
    * Defines difference under which two times are supposed to be equal
    * multiplied by 10, because we can fly 1 meter in 10 ms at minimal speed
    */
   private static final long TIME_EQUAL_PRECISION = (long)(EQUAL_PRECISION * 10 /Math.pow(10,VALID_DECIMAL_DIGITS_IN_M));
   
   /**
    * Defines difference under which two weight variables in gps state are supposed to be equal
    */
   private static final int WEIGHT_VALID_DECIMAL_DIGITS_KG = 1;
   private static final double WEIGHT_EQUAL_PRECISION = EQUAL_PRECISION/Math.pow(10,WEIGHT_VALID_DECIMAL_DIGITS_KG);

   /**
    * Defines difference under which two velocitis in gps state are supposed to be equal
    */
   private static final int VELOCITY_VALID_DECIMAL_DIGITS_MS = 2;
   private static final double VELOCITY_EQUAL_PRECISION = EQUAL_PRECISION/Math.pow(10,VELOCITY_VALID_DECIMAL_DIGITS_MS);

   /**
    * Defines the number of valid digits after decimal point for auxiliary angle in radians
    */
   private static final int ANGLE_RAD_VALID_DECIMAL_DIGITS = 5;
   private static final double ANGLE_RAD_EQUAL_PRECISION = EQUAL_PRECISION/Math.pow(10,ANGLE_RAD_VALID_DECIMAL_DIGITS);


////////////////////////////////////////////////////////////////////////////
////////////////////////////GRAPHICS //////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

   public static final double GRAPHICS_STRAIGHT_ARC_PRECISION_RAD = Math.PI/200;
   public static final double GRAPHICS_STRAIGHT_ARC_MINIMUM_RAD = Math.PI/50000;
   public static final double GRAPHICS_VERTICAL_ARC_PRECISION_RAD = Math.PI/800;
   public static final double GRAPHICS_VERTICAL_ARC_MINIMUM_RAD = Math.PI/20000;
   public static final double GRAPHICS_VERTICAL_ARC_TURN_PRECISION_RAD = Math.PI/800;
   public static final double GRAPHICS_VERTICAL_ARC_TURN_MINIMUM_RAD = Math.PI/20000;
   public static final double GRAPHICS_TURN_ARC_PRECISION_RAD = Math.PI/10;
   public static final double GRAPHICS_TURN_ARC_MINIMUM_RAD = Math.PI/1000;
   public static final float GRAPHICS_FLIGHT_PLAN_ALPHA_3D = 0.1f;
   public static final float GRAPHICS_FLIGHT_PLAN_ALPHA_3D_EDGE = 0.3f;
   public static final float GRAPHICS_FLIGHT_PLAN_EDGE_WIDTH = 2f;



////////////////////////////////////////////////////////////////////////////
////////////////////////////FUNCTIONS /////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

   public static final double getEarthSphereRadiusM() {
       return EARTH_SPHERE_RADIUS_M;
   }
   
   public static final double getUtcHours(final long timeStamp){
   	return (timeStamp % (24*60*60*1000))/(60.0*60*1000);
   }
   
   public static final double getUtcHoursInDoubleMs(final double timeStamp){
   	return (timeStamp % (24*60*60*1000))/(60*60*1000);
   }
   
   public static final long getUtcHoursLongInMs(final long timeStamp){
   	return timeStamp % (24*60*60*1000);
   }

   private static final double roundByExp(final double num, final double exp) {
       return Math.round(num*exp) / exp;
   }
   
   /**
    * Converts input headingDeg into allowed interval for headingDeg.
    * 
    * headingDeg - value from <0; 360), specifies movement direction angle from
    *        direction towards north pole at the same position, 0 - direction towards north pole, 90 - direction towards east,
    *        180 - direction towards south and 270 - direction towards west
    * @param headingDeg
    * @return
    */
   public static final double legalizeHeadingDeg(double headingDeg) {
   	while (headingDeg >= 360) {
   		headingDeg -= 360;
   	}
   	while (headingDeg < 0) {
   		headingDeg += 360;
   	}
   	return headingDeg;
   }
  
   /**
    * Converts input hour into allowed <0;24) interval
    * @param hour
    * @return
    */
   public static final double legalizeHour(double hour) {
   	while (hour >= 24) {
   		hour -= 24;
   	}
   	while (hour < 0) {
   		hour += 24;
   	}
   	return hour;
   }

   private static double getLongitudeExp(final double latitudeDeg) {
       final double p = LONGITUDE_RELATIVE_PRECISION * Math.cos(latitudeDeg*DEG_TO_RAD);
       final int e = Math.getExponent(p);
       if (e>0) {
           return (1<<e);
       } else if (e<0) {
           return (1/(1<<-e));
       } else {
           return 1;
       }
   }

   private static int getHeadingExp(final double pitchDeg) {
       final double p = HEADING_RELATIVE_PRECISION * Math.cos(pitchDeg*DEG_TO_RAD);
       final int e = Math.getExponent(p);
       if (e>0) {
           return (1<<e);
       } else if (e<0) {
           return (1/(1<<-e));
       } else {
           return 1;
       }
   }

   public static final double adjustLatitudeDeg(final double deg) {
       return roundByExp(deg, LATITUDE_DEG_EXP);
   }

   public static final double adjustLongitudeDeg(final double longitudeDeg, final double latitudeDeg) {
       return roundByExp(longitudeDeg, getLongitudeExp(latitudeDeg));
   }

   public static final double adjustCoordinateCart(final double cart) {
       return roundByExp(cart, COORDINATES_CART_EXP);
   }

   public static final double adjustPitchDeg(final double deg) {
       return roundByExp(deg, PITCH_DEG_EXP);
   }

   public static final double adjustHeadingDeg(final double headingDeg, final double pitchDeg) {
       return roundByExp(headingDeg, getHeadingExp(pitchDeg));
   }

   public static final double adjustBankDeg(final double bankDeg) {
       return roundByExp(bankDeg, BANK_DEG_EXP);
   }

   public static final double adjustAltitudeM(final double altitudeM) {
       return roundByExp(altitudeM, ALTITUDE_M_EXP);
   }


//////////////////////////////////////////////EQUALS ///////////////////////////////////////////

   public static final boolean equalLatitudeDeg(final double deg1, final double deg2) {
       return Math.abs(deg1 - deg2) <= LATITUDE_DEG_EQUAL_PRECISION;
   }

   public static final boolean equalLongitudeDeg(final double deg1, final double deg2, final double latitudeDeg) {
       final double precision = EQUAL_PRECISION/(getLongitudeExp(latitudeDeg));
       return (Math.abs(deg1 - deg2) <= precision) || (Math.abs(deg1 - deg2) >= (360-precision));
   }

   public static final boolean equalAltitudeM(final double altM1, final double altM2) {
       return Math.abs(altM1 - altM2) <= ALTITUDE_M_EQUAL_PRECISION;
   }

   public static final boolean equalPitchDeg(final double deg1, final double deg2) {
       return Math.abs(deg1 - deg2) <= PITCH_DEG_EQUAL_PRECISION;
   }

   public static final boolean equalHeadingDeg(final double deg1, final double deg2, final double pitchDeg) {
   	final double precision = HEADING_EQUAL_PRECISION/(getHeadingExp(pitchDeg));
       return (Math.abs(deg1 - deg2) <= precision) || (Math.abs(deg1 - deg2) >= (360-precision));
   }

   public static final boolean equalBankDeg(final double deg1, final double deg2) {
       return Math.abs(deg1 - deg2) <= BANK_DEG_EQUAL_PRECISION;
   }

   public static final boolean equalCoordinatesCart(final Vector3d cart1, final Vector3d cart2) {
       return (Math.abs(cart1.x - cart2.x) <= COORDINATES_CART_EQUAL_PRECISION) &&
       	   (Math.abs(cart1.y - cart2.y) <= COORDINATES_CART_EQUAL_PRECISION) &&
       	   (Math.abs(cart1.z - cart2.z) <= COORDINATES_CART_EQUAL_PRECISION);
   }

   public static final boolean equalDistanceM(final double dist1, final double dist2) {
       return Math.abs(dist1 - dist2) <= DISTANCE_EQUAL_PRECISION;
   }

   public static final boolean equalTimeMs(final long time1, final long time2) {
       return Math.abs(time1 - time2) <= TIME_EQUAL_PRECISION;
   }

   public static final boolean equalWeightKg(final double weight1, final double weight2) {
       return Math.abs(weight1 - weight2) <= WEIGHT_EQUAL_PRECISION;
   }

   public static final boolean equalVelocityMS(final double velocity1, final double velocity2) {
       return Math.abs(velocity1 - velocity2) <= VELOCITY_EQUAL_PRECISION;
   }

   public static final boolean equalAngleRad(final double angle1, final double angle2) {
       return Math.abs(angle1 - angle2) <= ANGLE_RAD_EQUAL_PRECISION;
   }


////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////CONVERSION FUNCTIONS //////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
   
   public static double Vector3dDistance(final Vector3d v1, final Vector3d v2) {
   	final double dx = v1.x-v2.x;
   	final double dy = v1.y-v2.y;
   	final double dz = v1.z-v2.z;
   	return Math.sqrt(dx*dx + dy*dy + dz*dz);
   }

   
   /**
    * Computes Cartesian coordinates of desired GPS point.
    * @param longitudeDeg
    * @param latitudeDeg
    * @param altitudeM
    * @return
    */
   public static Vector3d getCartCoordsFromGpsDeg(final double longitudeDeg, final double latitudeDeg, final double altitudeM) {
       final double cosLat = Math.cos(latitudeDeg*DEG_TO_RAD);
       final double sinLat = Math.sin(latitudeDeg*DEG_TO_RAD);
       final double cosLon = Math.cos(longitudeDeg*DEG_TO_RAD);
       final double sinLon = Math.sin(longitudeDeg*DEG_TO_RAD);
       return new Vector3d(
               GpsTools.adjustCoordinateCart((GpsTools.getEarthSphereRadiusM() + altitudeM) * cosLat * cosLon),
               GpsTools.adjustCoordinateCart((GpsTools.getEarthSphereRadiusM() + altitudeM) * cosLat * sinLon),
               GpsTools.adjustCoordinateCart((GpsTools.getEarthSphereRadiusM() + altitudeM) * sinLat));
   }
   
   public static Point3f getCartCoordsFromGpsDegs3f(final double longitudeDeg, final double latitudeDeg, final double altitudeM) {
       final double cosLat = Math.cos(latitudeDeg*DEG_TO_RAD);
       final double sinLat = Math.sin(latitudeDeg*DEG_TO_RAD);
       final double cosLon = Math.cos(longitudeDeg*DEG_TO_RAD);
       final double sinLon = Math.sin(longitudeDeg*DEG_TO_RAD);
       return new Point3f(
       		(float)(GpsTools.adjustCoordinateCart((GpsTools.getEarthSphereRadiusM() + altitudeM) * cosLat * cosLon)),
       		(float)(GpsTools.adjustCoordinateCart((GpsTools.getEarthSphereRadiusM() + altitudeM) * cosLat * sinLon)),
       		(float)(GpsTools.adjustCoordinateCart((GpsTools.getEarthSphereRadiusM() + altitudeM) * sinLat)));
   }

   
   public static Vector3d getGpsDegCoordsFromCartesian(Vector3d position) {
   	return getGpsDegCoordsFromCartesian(position.x,position.y,position.z);
   }

   public static Vector3d getGpsDegCoordsFromCartesian(final double x, final double y, final double z) {
       final double r = Math.sqrt(x*x + y*y + z*z);
       final double altitudeM = GpsTools.adjustAltitudeM(r - GpsTools.getEarthSphereRadiusM());
       if (altitudeM < 0) {
           throw new IllegalArgumentException("Wrong cartesianPosition as target altitudeM is below 0: "+altitudeM+", x: "+x+", y: "+y+", z: "+z);
       }
       final double latitudeDeg = GpsTools.adjustLatitudeDeg(Math.asin(z/r)*RAD_TO_DEG);
       double longitudeDeg = GpsTools.adjustLongitudeDeg(Math.atan2(y, x)*RAD_TO_DEG,latitudeDeg);
       if (longitudeDeg == -180) {
           longitudeDeg = 180;
       }
   	return new Vector3d(longitudeDeg,latitudeDeg,altitudeM);
   }
   
   public static double countHorizontalDistanceTo(final double firstLongDeg, double firstLatDeg, 
   											  final double secondLongDeg, double secondLatDeg, double altM) {
   	final Vector3d first = getCartCoordsFromGpsDeg(firstLongDeg,firstLatDeg,altM);
   	final Vector3d second = getCartCoordsFromGpsDeg(secondLongDeg,secondLatDeg,altM);
       final double angle = first.angle(second);
       return angle * (GpsTools.getEarthSphereRadiusM()+altM);
   }
}

