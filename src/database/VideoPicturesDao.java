package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import Cameras.AbstractCamera;
import loaders.Telemetry;

public class VideoPicturesDao {
	
	private static VideoPicturesDao instance = null;
	private Connection connection = null;
	PostGISStringBuilder postgisBuilder = new PostGISStringBuilder();
	PreparedStatement addMonitoredAreaNameOnly, getMonitoredAreaId, originSet, setOrigin,
						addDataSet, isaVideo;
	
	protected VideoPicturesDao() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/videos_pictures","test", "asd54asd24asd897");
			
			/////////////////////////////  Monitored Area /////////////////////////////
			
			addMonitoredAreaNameOnly = connection.prepareStatement("INSERT INTO \"MonitoredArea\" (\"name\") SELECT (?) "
					+ "WHERE NOT EXISTS ( SELECT \"name\" FROM \"MonitoredArea\" WHERE \"name\" = ? )");
			
			getMonitoredAreaId = connection.prepareStatement("SELECT id from \"MonitoredArea\" WHERE \"name\" = ?");
			
			originSet = connection.prepareStatement("SELECT ST_AsText(origin) from \"MonitoredArea\" WHERE id = ?");
			
			setOrigin = connection.prepareStatement("UPDATE \"MonitoredArea\" SET origin = ST_GeographyFromText(?) WHERE id = ?");
			
			/////////////////////////////  Data Set /////////////////////////////
			
			addDataSet = connection.prepareStatement("INSERT INTO \"DataSet\" (\"path\", monitored_area_id, \"timestamp\","
					+ " \"FOV_vertical\", \"FOV_horizontal\") VALUES(?, ?, ?, ?, ?) RETURNING id");
			
			isaVideo = connection.prepareStatement("INSERT INTO \"Video\" (data_set_id, fps) VALUES (?, ?)");
			
			
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
	
	public int addMonitoredArea(String name) throws Exception {
		try {
			addMonitoredAreaNameOnly.setString(1, name);
			addMonitoredAreaNameOnly.setString(2, name);
			addMonitoredAreaNameOnly.executeUpdate();
			
			getMonitoredAreaId.setString(1, name);
			ResultSet rs = getMonitoredAreaId.executeQuery();
			if (!rs.next()) {
				throw new Exception("Error occured while adding or retreiving MonitoredArea from the database with name: " + name);
			}
			return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new Exception("Adding MonitoredAread failed.");
	}
	
	public int addDataSet(int monitoredAreaid, String targetPath, AbstractCamera camera, Timestamp timestamp, boolean isVideo) 
			throws Exception {
		try {
			addDataSet.setString(1, targetPath);
			addDataSet.setInt(2, monitoredAreaid);
			addDataSet.setTimestamp(3, timestamp);
			addDataSet.setDouble(4, camera.getFovVertical());
			addDataSet.setDouble(5, camera.getFovHorizontal());
			ResultSet rs = addDataSet.executeQuery();
			if (!rs.next()) {
				throw new Exception("Error occured while adding DataSet in the database.");
			}
			int dataSetId = rs.getInt(1);
			if (isVideo) {
				isaVideo(dataSetId, camera);
			}
			return dataSetId;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new Exception("Adding DataSet failed.");
	}
	
	private void isaVideo(int dataSetId, AbstractCamera camera) {
		try {
			isaVideo.setInt(1, dataSetId);
			isaVideo.setInt(2, camera.getFps());
			isaVideo.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String originSet(int monitoredAreaid) throws Exception {
		try {
			originSet.setInt(1, monitoredAreaid);
			ResultSet rs = originSet.executeQuery();
			if (!rs.next()) {
				throw new Exception("Error occured while retrieving origin of MonitoredArea with id: " + monitoredAreaid);
			}
			String origin = rs.getString(1);
			return origin;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new Exception("Unexpected error. Code should be unreachable.");
	}
	
	public void setMonitoredAreaOrigin(int monitoredArea, double [] lonLatAlt) {
		try {
			setOrigin.setString(1, postgisBuilder.point3d(lonLatAlt[0], lonLatAlt[1], lonLatAlt[2]));
			setOrigin.setInt(2, monitoredArea);
			setOrigin.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}





















