package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import Cameras.AbstractCamera;

public class VideoPicturesDao {
	
	private static VideoPicturesDao instance = null;
	private Connection connection = null;
	PostGISStringBuilder postgisBuilder = new PostGISStringBuilder();
	PreparedStatement addMonitoredAreaNameOnly, getMonitoredAreaId,
						addDataSet;
	
	protected VideoPicturesDao() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/videos_pictures","test", "asd54asd24asd897");
			
			addMonitoredAreaNameOnly = connection.prepareStatement("INSERT INTO \"MonitoredArea\" (\"name\") SELECT (?) "
					+ "WHERE NOT EXISTS ( SELECT \"name\" FROM \"MonitoredArea\" WHERE \"name\" = ? )");
			
			getMonitoredAreaId = connection.prepareStatement("SELECT id from \"MonitoredArea\" WHERE \"name\" = ?");
			
			addDataSet = connection.prepareStatement("INSERT INTO \"DataSet\" (\"dir_path\", monitored_area_id, \"date\","
					+ " \"FOV_vertical\", \"FOV_horizontal\") VALUES(?, ?, ?, ?, ?) RETURNING id");
			
			
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
	
	public int addDataSet(int monitoredAreaid, String directoryPath, AbstractCamera camera, Date date) throws Exception {
		try {
			addDataSet.setString(1, directoryPath);
			addDataSet.setInt(2, monitoredAreaid);
//!TODO		addDataSet.setDate(3, x);
			addDataSet.setDouble(4, camera.getFovVertical());
			addDataSet.setDouble(5, camera.getFovHorizontal());
			ResultSet rs = addDataSet.executeQuery();
			if (!rs.next()) {
				throw new Exception("Error occured while adding DataSet in the database: ");
			}
			return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new Exception("Adding DataSet failed.");
	}
	
}





















