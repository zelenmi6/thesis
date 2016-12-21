package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Vector3d;

import org.postgis.LinearRing;
import org.postgis.Polygon;

import cameras.AbstractCamera;
import loaders.Telemetry;

/**
 * Dao to access the spatial database. Not thread safe! Just for development purposes.
 * @author Milan Zelenka
 *
 */
public class VideoPicturesDao {

	private static VideoPicturesDao instance = null;
	private Connection connection = null;
	PostGISStringBuilder postgisBuilder = new PostGISStringBuilder();
	PreparedStatement addMonitoredAreaNameOnly, getMonitoredAreaId, getOrigin, setOrigin, getMonitoredAreaBoundingPolygon,
						saveMonitoredAreaBoundingPolygon,
					  addDataSet, isaVideo, getDataSetInformation, getDataSetBoundingPolygons, getFrameIdsAndFrameNumbersAndAltitudesFromDataSet,
					  addFrame, isaVideoFrame, getCameraCoordinates, getFrameNumbersAndCameraCoordinatesFromDataSet, getCameraAngles, boundingPolygonsContainingPoint,
					    boundingPolygonsContainingPointFromDataSet;

	protected VideoPicturesDao() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/videos_pictures", "test",
					"asd54asd24asd897");

			///////////////////////////// Monitored Area /////////////////////////////

			addMonitoredAreaNameOnly = connection
					.prepareStatement("INSERT INTO \"MonitoredArea\" (\"name\") SELECT (?) "
							+ "WHERE NOT EXISTS ( SELECT \"name\" FROM \"MonitoredArea\" WHERE \"name\" = ? )");

			getMonitoredAreaId = connection.prepareStatement("SELECT id from \"MonitoredArea\" WHERE \"name\" = ?");

			getOrigin = connection.prepareStatement("SELECT ST_AsText(origin) from \"MonitoredArea\" WHERE id = ?");
			
			getMonitoredAreaBoundingPolygon = connection.prepareStatement(
					"SELECT ST_AsText(bounding_polygon) FROM \"MonitoredArea\" "
					+ "WHERE id = ?");
			
			saveMonitoredAreaBoundingPolygon = connection.prepareStatement(
					"UPDATE \"MonitoredArea\" "
					+ "SET bounding_polygon = ST_GeographyFromText(?) " 
					+ "WHERE id = ?");

			setOrigin = connection
					.prepareStatement("UPDATE \"MonitoredArea\" SET origin = ST_GeographyFromText(?) WHERE id = ?");

			///////////////////////////// Data Set /////////////////////////////

			addDataSet = connection
					.prepareStatement("INSERT INTO \"DataSet\" (\"path\", monitored_area_id, \"timestamp\","
							+ " \"FOV_vertical\", \"FOV_horizontal\") VALUES(?, ?, ?, ?, ?) RETURNING id");

			isaVideo = connection.prepareStatement("INSERT INTO \"Video\" (data_set_id, fps) VALUES (?, ?)");
			
			getDataSetInformation = connection.prepareStatement(
					"SELECT (ds.timestamp + interval '1 second' * frames.frame_number / 25) \"Time of Frame\","
							+ " frames.frame_id \"id\", frames.frame_number \"Frame number\","
							+ "(interval '1 second' * frames.frame_number / 25) \"Video time\", "
							+ "ST_AsText(frames.bounding_polygon) poly, " //5
							+ "camera_roll roll, " //6
							+ "camera_pitch pitch," //7
							+ "camera_heading heading FROM " //8
							+ "(SELECT * FROM \"DataSet\" WHERE id = ?) as ds "
							+ "inner join (SELECT * FROM \"Frame\" f "
							+ "inner join \"VideoFrame\" vf on f.id = vf.frame_id) as frames on ds.id = frames.data_set_id "
							+ "ORDER BY \"Video time\" ASC;");
			
			getDataSetBoundingPolygons = connection.prepareStatement(
					"SELECT ST_AsText(bounding_polygon) poly FROM \"Frame\" "
					+ "WHERE data_set_id = ? "
					+ "ORDER BY id");
			
			getFrameIdsAndFrameNumbersAndAltitudesFromDataSet = connection.prepareStatement(
					"SELECT frame_id, frame_number,  ST_Z(frames.camera_coordinates) altitude "
					+ "FROM "
					+ "(SELECT * FROM \"DataSet\" WHERE id = ?) as ds "
					+ "inner join (SELECT f.id frame_id, f.data_set_id data_set_id, vf.frame_number frame_number, f.camera_coordinates camera_coordinates "
					+ "FROM \"Frame\" f "
					+ "inner join \"VideoFrame\" vf on f.id = vf.frame_id) as frames on ds.id = frames.data_set_id;");
			
//			= connection.prepareStatement("SELECT f.id id "
//							+ "FROM \"DataSet\" ds "
//							+ "inner join \"Frame\" f on ds.id = f.data_set_id "
//							+ "WHERE ds.id = ? "
//							+ "AND "
//							+ "f.frame_number = ?;");

			/////////////////////////// Frames /////////////////////////////

			addFrame = connection
					.prepareStatement("INSERT INTO \"Frame\" (data_set_id, bounding_polygon, camera_coordinates,"
							+ "camera_heading, camera_roll, camera_pitch) VALUES(?, ST_GeometryFromText(?), ST_GeometryFromText(?), ?, ?, ?) "
							+ "RETURNING id");

			isaVideoFrame = connection
					.prepareStatement("INSERT INTO \"VideoFrame\" (frame_id, frame_number) " + "VALUES (?, ?)");
			
			getCameraCoordinates = connection.prepareStatement("SELECT ST_X(camera_coordinates) \"x\", "
					+ "ST_Y(camera_coordinates) \"y\", ST_Z(camera_coordinates) \"z\" "
					+ "FROM \"Frame\" WHERE id = ?");
			
			getFrameNumbersAndCameraCoordinatesFromDataSet = connection.prepareStatement("SELECT vf.frame_number frame_number, "
					+ "ST_X(fr.camera_coordinates) \"x\", ST_Y(fr.camera_coordinates) \"y\", "
					+ "ST_Z(fr.camera_coordinates) \"z\", fr.camera_heading heading FROM \"Frame\" fr "
					+ "INNER JOIN \"DataSet\" ds on fr.data_set_id = ds.id "
					+ "INNER JOIN \"VideoFrame\" vf on fr.id = vf.frame_id "
					+ "WHERE ds.id = ?");
			
			getCameraAngles = connection.prepareStatement("SELECT camera_roll roll, camera_pitch pitch, camera_heading heading "
					+ "FROM \"Frame\" WHERE id = ?");
			
			boundingPolygonsContainingPoint = connection.prepareStatement(
					"SELECT \"Frame\".id, frame_number from \"Frame\" "
					+ "INNER JOIN \"VideoFrame\" on \"Frame\".id = \"VideoFrame\".frame_id "
					+ "WHERE ST_Contains(\"bounding_polygon\", ST_GeometryFromText(?)) "
					+ "ORDER BY frame_number;");
			
			boundingPolygonsContainingPointFromDataSet  = connection.prepareStatement(
					"SELECT \"Frame\".id, frame_number from \"Frame\" "
					+ "INNER JOIN \"VideoFrame\" on \"Frame\".id = \"VideoFrame\".frame_id "
					+ "WHERE data_set_id = ? "
					+ "AND "
					+ "ST_Contains(\"bounding_polygon\", ST_GeometryFromText(?)) "
					+ "ORDER BY frame_number;");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static VideoPicturesDao getInstance() {
		if (instance == null) {
			instance = new VideoPicturesDao();
		}
		return instance;
	}

	/**
	 * Adds a new monitored area to the database and returns its id. If it already exists, returns its id.
	 * @param name Name of the monitored area.
	 * @return Id of the monitored area.
	 * @throws SQLException
	 */
	public int addMonitoredArea(String name) throws SQLException {
		addMonitoredAreaNameOnly.setString(1, name);
		addMonitoredAreaNameOnly.setString(2, name);
		addMonitoredAreaNameOnly.executeUpdate();

		getMonitoredAreaId.setString(1, name);
		ResultSet rs = getMonitoredAreaId.executeQuery();
		if (!rs.next()) {
			throw new SQLException(
					"Error occured while adding or retrieving MonitoredArea from the database with name: " + name);
		}
		return rs.getInt(1);
	}
	
	/**
	 * Get the bounding polygon of a monitored area. 
	 * @param areaId Id of the monitored area.
	 * @return List of vectors each representing a GPS coordinate. Its format is <longitude, latitude, 0>.
	 * @throws SQLException
	 */
	public List<Vector3d> getMonitoredAreaBoundingPolygon(int areaId) throws SQLException {
		getMonitoredAreaBoundingPolygon.setInt(1, areaId);
		ResultSet rs = getMonitoredAreaBoundingPolygon.executeQuery();
		List<Vector3d> boundingPolygon = new ArrayList<>();
		if (!rs.next()) {
			throw new SQLException("Error whiel retrieving a monitored area bounding polygon.");
		} else {
			String polygonString = rs.getString(1);
			if (polygonString == null) {
				return boundingPolygon;
			}
			Polygon pl = new Polygon(polygonString);
			LinearRing lr = (LinearRing)pl.getSubGeometry(0);
			for (int i = 0; i < lr.getPoints().length - 1; i ++) {
				boundingPolygon.add(new Vector3d(lr.getPoint(i).x, lr.getPoint(i).y, 0));
			}
		}
		
		return boundingPolygon;
	}
	
	/**
	 * Updates the bounding polygon of a monitored area.
	 * @param areaId Id of the corresponding monitored area.
	 * @param boundingPolygon Bounding polygon, its vertices represented by a 3d vector. The z coordinate is omitted.
	 * @throws SQLException
	 */
	public void saveMonitoredAreaBoundingPolygon(int areaId, Vector3d[] boundingPolygon) throws SQLException {
		String polygon = postgisBuilder.polygo2dFrom3dVector(boundingPolygon);
		saveMonitoredAreaBoundingPolygon.setString(1, polygon);
		saveMonitoredAreaBoundingPolygon.setInt(2, areaId);
		saveMonitoredAreaBoundingPolygon.executeUpdate();
	}
	
	/**
	 * Returns the origin of a monitored area
	 * @param monitoredAreaid Id of the monitored area.
	 * @return Origin of the monitored area. Can be null.
	 * @throws SQLException
	 */
	public String getOrigin(int monitoredAreaid) throws SQLException {
		getOrigin.setInt(1, monitoredAreaid);
		ResultSet rs = getOrigin.executeQuery();
		if (!rs.next()) {
			throw new SQLException(
					"Error occured while retrieving origin of MonitoredArea with id: " + monitoredAreaid);
		}
		String origin = rs.getString(1);
		return origin;
	}

	/**
	 * Sets the origin of a monitored area.
	 * @param monitoredArea Id of the monitored area.
	 * @param lonLatAlt Coordinates of the origin in format {longitude, latitude, altitude}
	 * @throws SQLException
	 */
	public void setMonitoredAreaOrigin(int monitoredArea, double[] lonLatAlt) throws SQLException {
		setOrigin.setString(1, postgisBuilder.point3d(lonLatAlt[0], lonLatAlt[1], lonLatAlt[2]));
		setOrigin.setInt(2, monitoredArea);
		setOrigin.executeUpdate();
	}

	/**
	 * Adds a new data set. Should be refactored into separate methods for video sets and picture sets.
	 * @param monitoredAreaid Id of the set's monitored area
	 * @param targetPath Path to the video or the picture directory
	 * @param camera Camera model used
	 * @param timestamp Time of when data was taken
	 * @param isVideo True if it is a video set.
	 * @return
	 * @throws Exception 
	 */
	public int addDataSet(int monitoredAreaid, String targetPath, AbstractCamera camera, Timestamp timestamp,
			boolean isVideo) throws Exception {
		addDataSet.setString(1, targetPath);
		addDataSet.setInt(2, monitoredAreaid);
		addDataSet.setTimestamp(3, timestamp);
		addDataSet.setDouble(4, camera.getFovVertical());
		addDataSet.setDouble(5, camera.getFovHorizontal());
		ResultSet rs = addDataSet.executeQuery();
		if (!rs.next()) {
			throw new SQLException("Error occured while adding DataSet to the database.");
		}
		int dataSetId = rs.getInt(1);
		if (isVideo) {
			isaVideo(dataSetId, camera);
		} else {
			throw new Exception("Picture sets not implemented yet");
		}
		return dataSetId;
	}
	
	/**
	 * Specifies a data set is a video set
	 * @param dataSetId Id of the corresponding data set
	 * @param camera Camera model used
	 * @throws SQLException
	 */
	private void isaVideo(int dataSetId, AbstractCamera camera) throws SQLException {
		isaVideo.setInt(1, dataSetId);
		isaVideo.setInt(2, camera.getFps());
		isaVideo.executeUpdate();
	}
	
	/**
	 * Prints some data set's information. Used for development.
	 * @param dataSetId Id of the data set to be printed.
	 * @throws SQLException
	 */
	public void printDataSetInformation(int dataSetId) throws SQLException {
		getDataSetInformation.setInt(1, dataSetId);
		ResultSet rs = getDataSetInformation.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (rs.next()) {
		    for (int i = 1; i <= columnsNumber; i++) {
		        if (i > 1) System.out.print(",  ");
		        String columnValue = rs.getString(i);
		        System.out.print(columnValue + " " + rsmd.getColumnName(i));
		    }
		    System.out.println("");
		}
	}
	
	/**
	 * Gets all frames' ids, frame indices in the video and the camera's altitude of each frame from a DataSet
	 * @param dataSetId Id of a dataset
	 * @return List containit elements in format {frame id, frame index, camera altitude}
	 * @throws Exception
	 */
	public List<double[]> getFrameIdsAndFrameNumbersAndAltitudesFromDataSet(int dataSetId) throws Exception {
		getFrameIdsAndFrameNumbersAndAltitudesFromDataSet.setInt(1, dataSetId);
		ResultSet rs = getFrameIdsAndFrameNumbersAndAltitudesFromDataSet.executeQuery();
		List<double[]> info = new ArrayList<>();
		while (rs.next()) {
			info.add(new double[]{rs.getDouble(1), rs.getDouble(2), rs.getDouble(3)});
		}
		return info;
	}
	
	/**
	 * Gets coordinates of all frames'/pictures' bounding polygons.
	 * @param dataSetId Id of the data set.
	 * @return List of 3d vectors representing the coordinates of the bounding polygons' vertices.
	 * Its format is {x, y, 0}
	 * @throws SQLException
	 */
	public List<Vector3d[]> getDataSetBoundingPolygons(int dataSetId) throws SQLException {
		getDataSetBoundingPolygons.setInt(1, dataSetId);
		ResultSet rs = getDataSetBoundingPolygons.executeQuery();
		List<Vector3d[]> coordinates = new ArrayList<Vector3d[]>();
		while (rs.next()) {
			Vector3d [] corners = new Vector3d[4]; //!TODO *** Magic number 4 ***
			Polygon pl = new Polygon(rs.getString(1));
			LinearRing lr = (LinearRing)pl.getSubGeometry(0);
			for (int i = 0; i < lr.getPoints().length - 1; i ++) {
				corners[i] = new Vector3d(lr.getPoint(i).x, lr.getPoint(i).y, 0);
			}
			coordinates.add(corners);
		}
		return coordinates;
	}
	
	/**
	 * Adds a frame to a data set. There should be another method for batch inserts
	 * instead of inserting each frame individually. Another one should be made for
	 * picture sets.
	 * @param dataSetId Id of the frame's corresponding data set.
	 * @param telemetry Telemetry of the corresponding frame.
	 * @param boundingPolygon Bounding polygon represented by an array of 3d vectors.
	 * The z coordinate is set to 0 as all polygons are co-planar.
	 * @param frameNumber
	 * @throws SQLException
	 */
	public void addFrame(int dataSetId, Telemetry telemetry, Vector3d[] boundingPolygon, int frameNumber)
			throws SQLException {
		addFrame.setInt(1, dataSetId);
		addFrame.setString(2, postgisBuilder.polygon2d4cornersFrom3dVector(boundingPolygon));
		addFrame.setString(3,
				postgisBuilder.point3d(telemetry.coordinates.x, telemetry.coordinates.y, telemetry.coordinates.z));
		addFrame.setDouble(4, telemetry.heading);
		addFrame.setDouble(5, telemetry.roll);
		addFrame.setDouble(6, telemetry.pitch);
		ResultSet rs = addFrame.executeQuery();
		if (!rs.next()) {
			throw new SQLException("Error occured while adding Frame to the database.");
		}
		int frameId = rs.getInt(1);
		isaVideoFrame(frameId, frameNumber);
	}

	/**
	 * Makes a frame a video frame
	 * @param frameId Id of the frame to be set
	 * @param frameNumber Frame number in the corresponding video.
	 * @throws SQLException
	 */
	private void isaVideoFrame(int frameId, int frameNumber) throws SQLException {
		isaVideoFrame.setInt(1, frameId);
		isaVideoFrame.setInt(2, frameNumber);
		isaVideoFrame.executeUpdate();
	}
	
	/**
	 * Gets camera's Cartesian coordinates of a corresponding frame 
	 * @param frameId Id of the queried frame.
	 * @return Cartesian coordinates in format {x, y, z}
	 * @throws SQLException
	 */
	public double [] getCameraCoordinates(int frameId) throws SQLException {
		getCameraCoordinates.setInt(1, frameId);
		
		ResultSet rs = getCameraCoordinates.executeQuery();
		if (!rs.next()) {
			throw new SQLException("Error retrieving camera coordinates");
		}
		return new double[]{rs.getDouble(1), rs.getDouble(2), rs.getDouble(3)};
//		System.out.println("x: " + rs.getDouble(1) + ", y: " + rs.getDouble(2) + ", z: " + rs.getDouble(3));
	}
	
	/**
	 * Returns a map where the key is a frame number and the value are the frame's corresponding camera coordinates and heading
	 * @param dataSetId Id of the queried data set
	 * @return Hash map <frame number, {x, y, z, heading}>
	 * @throws SQLException
	 */
	public HashMap<Integer,double[]> getFrameNumbersAndCameraCoordinatesFromDataSet(int dataSetId) throws SQLException {
		getFrameNumbersAndCameraCoordinatesFromDataSet.setInt(1, dataSetId);
		HashMap<Integer, double[]> coordinateMap = new HashMap<>();
		ResultSet rs = getFrameNumbersAndCameraCoordinatesFromDataSet.executeQuery();
		while (rs.next()) {
			coordinateMap.put(rs.getInt(1), new double[]{rs.getDouble(2), rs.getDouble(3), rs.getDouble(4), rs.getDouble(5)});
		}
		return coordinateMap;
	}
	
	/**
	 * Gets camera's rotations in all axes of a corresponding frame.
	 * @param frameId Id of the frame.
	 * @return Angles in format {roll, pitch, heading}
	 * @throws SQLException
	 */
	public double [] getCameraAngles(int frameId) throws SQLException {
		getCameraAngles.setInt(1, frameId);
		ResultSet rs = getCameraAngles.executeQuery();
		if (!rs.next()) {
			throw new SQLException("Error retrieving camera coordinates");
		}
		return new double[]{rs.getDouble(1), rs.getDouble(2), rs.getDouble(3)};
	}
	
	/**
	 * Returns a list of frames containing a point. 
	 * It is obsolete and should not be used since it queries all data sets regardless the monitored area.
	 * @param x Cartesian x coordinate of the object of interest
	 * @param y Cartesian y coordinate of the object of interest
	 * @return List of 2-element arrays. The first element contains the frame's id,
	 *  the second one is the frame number in the video.
	 * @throws Exception
	 */
	public List<int[]> getFramesContainingPoint2d(double x, double y) throws Exception {
		String point = postgisBuilder.point2d(x, y);
		try {
			boundingPolygonsContainingPoint.setString(1, point);
			ResultSet rs = boundingPolygonsContainingPoint.executeQuery();
			List<int[]> frames = new ArrayList<>();
			while (rs.next()) {
//				System.out.println("col 1: " + rs.getInt(1) + "col 2: " + rs.getInt(2));
				frames.add(new int[]{rs.getInt(1), rs.getInt(2)});
			}
			return frames;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new SQLException("Unexpected error while getting frames containing a point from the database.");
	}
	
	/**
	 * Finds frames containing a point of interest in a given data set. 
	 * In order to search across all data sets a new query needs to be made.
	 * @param x Cartesian x coordinate of the object of interest
	 * @param y Cartesian y coordinate of the object of interest
	 * @param dataSetId Id of the data set being searched.
	 * @return List of 2-element arrays. The first element contains the frame's id,
	 *  the second one is the frame number in the video.
	 * @throws Exception
	 */
	public List<int[]> getFramesContainingPoint2dFromDataSet(double x, double y, int dataSetId) throws Exception {
		String point = postgisBuilder.point2d(x, y);
		try {
			boundingPolygonsContainingPointFromDataSet.setInt(1, dataSetId);
			boundingPolygonsContainingPointFromDataSet.setString(2, point);
			ResultSet rs = boundingPolygonsContainingPointFromDataSet.executeQuery();
			List<int[]> frames = new ArrayList<>();
			while (rs.next()) {
//				System.out.println("col 1: " + rs.getInt(1) + "col 2: " + rs.getInt(2));
				frames.add(new int[]{rs.getInt(1), rs.getInt(2)});
			}
			return frames;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new SQLException("Unexpected error while getting frames containing a point from the database.");
	}
	
	/**
	 * Sets autocommit to true or false.
	 * @param autocommit Defines whether autocommit is set to true or false.
	 * @throws SQLException
	 */
	public void setAutocommit(boolean autocommit) throws SQLException {
		connection.setAutoCommit(autocommit);
	}
	
	/**
	 * Commits all database operations.
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		connection.commit();
	}
	
	/**
	 * Rollbacks all database operations.
	 * @throws SQLException
	 */
	public void rollback() throws SQLException {
		connection.rollback();
	}

}





























