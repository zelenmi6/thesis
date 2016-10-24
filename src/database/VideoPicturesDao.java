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
import java.util.List;

import javax.vecmath.Vector3d;

import org.postgis.LinearRing;
import org.postgis.Polygon;

import cameras.AbstractCamera;
import loaders.Telemetry;

public class VideoPicturesDao {

	private static VideoPicturesDao instance = null;
	private Connection connection = null;
	PostGISStringBuilder postgisBuilder = new PostGISStringBuilder();
	PreparedStatement addMonitoredAreaNameOnly, getMonitoredAreaId, originSet, setOrigin,
					  addDataSet, isaVideo, getDataSetInformation, getDataSetCoordinates,
					  addFrame, isaVideoFrame, getCameraCoordinates, getCameraAngles, boundingPolygonsContainingPoint;

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

			originSet = connection.prepareStatement("SELECT ST_AsText(origin) from \"MonitoredArea\" WHERE id = ?");

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
			
			getDataSetCoordinates = connection.prepareStatement(
					"SELECT ST_AsText(bounding_polygon) poly FROM \"Frame\" "
					+ "WHERE data_set_id = ?");

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
			
			getCameraAngles = connection.prepareStatement("SELECT camera_roll roll, camera_pitch pitch, camera_heading heading "
					+ "FROM \"Frame\" WHERE id = ?");
			
			boundingPolygonsContainingPoint = connection.prepareStatement(
					"SELECT \"Frame\".id, frame_number from \"Frame\" "
					+ "INNER JOIN \"VideoFrame\" on \"Frame\".id = \"VideoFrame\".frame_id "
					+ "WHERE ST_Contains(\"bounding_polygon\", ST_GeometryFromText(?)) "
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

	public int addMonitoredArea(String name) throws SQLException {
		addMonitoredAreaNameOnly.setString(1, name);
		addMonitoredAreaNameOnly.setString(2, name);
		addMonitoredAreaNameOnly.executeUpdate();

		getMonitoredAreaId.setString(1, name);
		ResultSet rs = getMonitoredAreaId.executeQuery();
		if (!rs.next()) {
			throw new SQLException(
					"Error occured while adding or retreiving MonitoredArea from the database with name: " + name);
		}
		return rs.getInt(1);
	}

	//!TODO refactor to two separate methods for PictureSet and Video
	public int addDataSet(int monitoredAreaid, String targetPath, AbstractCamera camera, Timestamp timestamp,
			boolean isVideo) throws SQLException {
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
		}
		return dataSetId;
	}

	private void isaVideo(int dataSetId, AbstractCamera camera) throws SQLException {
		isaVideo.setInt(1, dataSetId);
		isaVideo.setInt(2, camera.getFps());
		isaVideo.executeUpdate();
	}
	
	public void getDataSetInformation(int dataSetId) throws SQLException {
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
	
	public List<Vector3d[]> getDataSetCoordinates(int dataSetId) throws SQLException {
		getDataSetCoordinates.setInt(1, dataSetId);
		ResultSet rs = getDataSetCoordinates.executeQuery();
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

	public void addFrame(int dataSetId, Telemetry telemetry, Vector3d[] boundingPolygon, int frameNumber)
			throws SQLException {
		addFrame.setInt(1, dataSetId);
		addFrame.setString(2, postgisBuilder.polygo2dFrom3dVector(boundingPolygon));
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

	private void isaVideoFrame(int frameId, int frameNumber) throws SQLException {
		isaVideoFrame.setInt(1, frameId);
		isaVideoFrame.setInt(2, frameNumber);
		isaVideoFrame.executeUpdate();
	}

	public String originSet(int monitoredAreaid) throws SQLException {
		originSet.setInt(1, monitoredAreaid);
		ResultSet rs = originSet.executeQuery();
		if (!rs.next()) {
			throw new SQLException(
					"Error occured while retrieving origin of MonitoredArea with id: " + monitoredAreaid);
		}
		String origin = rs.getString(1);
		return origin;
	}
	
	public double [] getCameraCoordinates(int frameId) throws SQLException {
		getCameraCoordinates.setInt(1, frameId);
		
		ResultSet rs = getCameraCoordinates.executeQuery();
		if (!rs.next()) {
			throw new SQLException("Error retrieving camera coordinates");
		}
		return new double[]{rs.getDouble(1), rs.getDouble(2), rs.getDouble(3)};
//		System.out.println("x: " + rs.getDouble(1) + ", y: " + rs.getDouble(2) + ", z: " + rs.getDouble(3));
	}
	
	public double [] getCameraAngles(int frameId) throws SQLException {
		getCameraAngles.setInt(1, frameId);
		ResultSet rs = getCameraAngles.executeQuery();
		if (!rs.next()) {
			throw new SQLException("Error retrieving camera coordinates");
		}
		return new double[]{rs.getDouble(1), rs.getDouble(2), rs.getDouble(3)};
	}

	public void setMonitoredAreaOrigin(int monitoredArea, double[] lonLatAlt) throws SQLException {
		setOrigin.setString(1, postgisBuilder.point3d(lonLatAlt[0], lonLatAlt[1], lonLatAlt[2]));
		setOrigin.setInt(2, monitoredArea);
		setOrigin.executeUpdate();
	}
	
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
	
	public void setAutocommit(boolean autocommit) throws SQLException {
		connection.setAutoCommit(autocommit);
	}
	
	public void commit() throws SQLException {
		connection.commit();
	}
	
	public void rollback() throws SQLException {
		connection.rollback();
	}

}





























