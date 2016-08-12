package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.vecmath.Vector3d;

import Cameras.AbstractCamera;
import loaders.Telemetry;

public class VideoPicturesDao {

	private static VideoPicturesDao instance = null;
	private Connection connection = null;
	PostGISStringBuilder postgisBuilder = new PostGISStringBuilder();
	PreparedStatement addMonitoredAreaNameOnly, getMonitoredAreaId, originSet, setOrigin, addDataSet, isaVideo,
			addFrame, isaVideoFrame;

	protected VideoPicturesDao() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/videos_pictures", "test",
					"asd54asd24asd897");

			///////////////////////////// Monitored Area
			///////////////////////////// /////////////////////////////

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

			/////////////////////////// Frames /////////////////////////////

			addFrame = connection
					.prepareStatement("INSERT INTO \"Frame\" (data_set_id, bounding_polygon, camera_coordinates,"
							+ "camera_heading, camera_roll, camera_pitch) VALUES(?, ST_GeometryFromText(?), ST_GeometryFromText(?), ?, ?, ?) "
							+ "RETURNING id");

			isaVideoFrame = connection
					.prepareStatement("INSERT INTO \"VideoFrame\" (frame_id, frame_number) " + "VALUES (?, ?)");

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

	public void setMonitoredAreaOrigin(int monitoredArea, double[] lonLatAlt) throws SQLException {
		setOrigin.setString(1, postgisBuilder.point3d(lonLatAlt[0], lonLatAlt[1], lonLatAlt[2]));
		setOrigin.setInt(2, monitoredArea);
		setOrigin.executeUpdate();
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





























