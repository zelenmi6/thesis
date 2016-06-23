package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.vecmath.Vector3d;

import loaders.PictureTelemetry;

public class PictureTelemetryDao {
	
	private static PictureTelemetryDao instance = null;

	private Connection connection = null;;
	PreparedStatement addMonitoredArea, addMonitoredAreaNameOnly, addDataSet, addPicture, addTelemetry,
	getMonitoredAreaId, getDataSetId, getPictureId;
	PostGISStringBuilder sb = new PostGISStringBuilder();
	
	protected PictureTelemetryDao() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb","test", "12345");
			
			addMonitoredArea = connection.prepareStatement("INSERT INTO \"MonitoredArea\" (\"name\", bounding_box, origin) "
					+ "VALUES(?, ST_GeometryFromText(?), ST_GeographyFromText(?))");
			
//			addMonitoredAreaNameOnly = connection.prepareStatement("INSERT INTO \"MonitoredArea\" (\"Name\") VALUES(?)");
			addMonitoredAreaNameOnly = connection.prepareStatement("IF NOT EXISTS (SELECT * FROM \"MonitoredArea\" WHERE \"name\" = ?)"
					+ "BEGIN INSERT INTO \"MonitoredArea\" (\"name\") VALUES(?) END"); //!TODO vyzkouset
			
			getMonitoredAreaId = connection.prepareStatement("SELECT id from \"MonitoredArea\" WHERE \"name\" = ?");
			
			addDataSet = connection.prepareStatement("INSERT INTO \"DataSet\" (\"dir_path\", monitored_area_id) VALUES(?, ?) RETURNING id");
			
			addPicture = connection.prepareStatement("INSERT INTO \"Picture\" (\"file_path\", data_set_id) VALUES(?, ?) RETURNING id");
			
			//!TODO ST_Makepoint je rychlejsi a udajne presnejsi
			addTelemetry = connection.prepareStatement("INSERT INTO \"Telemetry\" (coordinates, heading, roll, pitch, picture_id) VALUES(ST_GeometryFromText(?), ?, ?, ?, ?)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static PictureTelemetryDao getInstance() {
		if (instance == null) {
			instance = new PictureTelemetryDao();
		}
		return instance;
	}
	
	
	//!TODO only for testing
	public void addMonitoredArea(String name, String boundingBox, Vector3d origin) {
		try {
			addMonitoredArea.setString(1, name);
			addMonitoredArea.setString(2, "POLYGON((0 0, 1 1, 2 2, 0 0))");
			addMonitoredArea.setString(3, "POINT(-77.0092 38.889588)");
			addMonitoredArea.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public int addDataSet(int monitoredAreaid, String directoryPath) throws Exception {
		try {
			addDataSet.setString(1, directoryPath);
			addDataSet.setInt(2, monitoredAreaid);
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
	
	public int addPicture(Integer dataSetId, String filePath) throws Exception {
		try {
			addPicture.setString(1, filePath);
			addPicture.setInt(2, dataSetId);
			ResultSet rs = addPicture.executeQuery();
			if (!rs.next()) {
				throw new Exception("Error occured while adding Picture in the database: ");
			}
			return rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return 0; // returns picture id
	}
	
	public void addTelemetry(int pictureId, PictureTelemetry telemetry) {
		try {
			addTelemetry.setString(1, sb.pointGeometry3D(telemetry));
			addTelemetry.setDouble(2, telemetry.heading);
			addTelemetry.setDouble(3, telemetry.roll);
			addTelemetry.setDouble(4, telemetry.pitch);
			addTelemetry.setInt(5, pictureId);
			addTelemetry.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getMonitoredArea() {
		
	}
	
	public void getDataSet() {
		
	}
	
	public void getPicture() {
		
	}
	
	public void getTelemetry() {
		
	}
}
