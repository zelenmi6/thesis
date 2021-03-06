package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import loaders.Telemetry;

/**
 * Obsolete and no longer used.
 * @author Milan Zelenka
 *
 */
public class PictureTelemetryDao {
	
	private static PictureTelemetryDao instance = null;

	private Connection connection = null;
	PreparedStatement addMonitoredArea, addMonitoredAreaNameOnly, addDataSet, addPicture, addTelemetry,
	getMonitoredAreaId, getDataSetId, getPictureId, deleteMonitoredArea, getIdContainingPoint, liesWithin;
	PostGISStringBuilder postgisBuilder = new PostGISStringBuilder();
	
	protected PictureTelemetryDao() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			//!TODO dodelat zabezpeceni
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb","test", "asd54asd24asd897");
			
			addMonitoredArea = connection.prepareStatement("INSERT INTO \"MonitoredArea\" (\"name\", bounding_box, origin) "
					+ "VALUES(?, ST_GeometryFromText(?), ST_GeographyFromText(?))");
			
//			addMonitoredAreaNameOnly = connection.prepareStatement("INSERT INTO \"MonitoredArea\" (\"Name\") VALUES(?)");
			addMonitoredAreaNameOnly = connection.prepareStatement("INSERT INTO \"MonitoredArea\" (\"name\") SELECT (?) "
					+ "WHERE NOT EXISTS ( SELECT \"name\" FROM \"MonitoredArea\" WHERE \"name\" = ? )"); //!TODO vyzkouset
			
			getMonitoredAreaId = connection.prepareStatement("SELECT id from \"MonitoredArea\" WHERE \"name\" = ?");
			
			addDataSet = connection.prepareStatement("INSERT INTO \"DataSet\" (\"dir_path\", monitored_area_id, \"FOVv\", \"FOVh\") VALUES(?, ?, ?, ?) RETURNING id");
			
			addPicture = connection.prepareStatement("INSERT INTO \"Picture\" (\"file_path\", data_set_id, bounding_box) VALUES(?, ?, ST_GeometryFromText(?)) RETURNING id");
			
			//!TODO ST_Makepoint je rychlejsi a udajne presnejsi
			addTelemetry = connection.prepareStatement("INSERT INTO \"Telemetry\" (coordinates, heading, roll, pitch, picture_id) VALUES(ST_GeometryFromText(?), ?, ?, ?, ?)");
		
			deleteMonitoredArea = connection.prepareStatement("DELETE FROM \"MonitoredArea\" WHERE id = ?");
			
			getIdContainingPoint = connection.prepareStatement("SELECT id, ST_AsText(bounding_box) from \"Picture\" WHERE ST_CONTAINS(\"bounding_box\", ST_GeometryFromText(?))");
			
			liesWithin = connection.prepareStatement("");
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
	
	public int addDataSet(int monitoredAreaid, String directoryPath, double FOVv, double FOVh) throws Exception {
		try {
			addDataSet.setString(1, directoryPath);
			addDataSet.setInt(2, monitoredAreaid);
			addDataSet.setDouble(3, FOVv);
			addDataSet.setDouble(4, FOVh);
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
	
	public int addPicture(Integer dataSetId, String filePath, Vector3d [] boundingBox) throws Exception {
		try {
			StringBuilder sb = new StringBuilder(100);
			sb.append("POLYGON((").append(boundingBox[0].x).append(" ").append(boundingBox[0].y).append(", ")
			.append(boundingBox[1].x).append(" ").append(boundingBox[1].y).append(", ")
			.append(boundingBox[2].x).append(" ").append(boundingBox[2].y).append(", ")
			.append(boundingBox[3].x).append(" ").append(boundingBox[3].y).append(", ")
			.append(boundingBox[0].x).append(" ").append(boundingBox[0].y)
			.append("))");
			addPicture.setString(1, filePath);
			addPicture.setInt(2, dataSetId);
			addPicture.setString(3, sb.toString());
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
	
	public void addTelemetry(int pictureId, Telemetry telemetry) {
		try {
			addTelemetry.setString(1, postgisBuilder.pointGeometry3D(telemetry));
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
	
	public void deleteMonitoredArea(int id) {
		try {
			deleteMonitoredArea.setInt(1, id);
			deleteMonitoredArea.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Integer> getIdContainingPoint(double x, double y) {
		StringBuilder sb = new StringBuilder();
		sb.append("POINT(").append(x).append(" ").append(y).append(")");
		
		try {
			getIdContainingPoint.setString(1, sb.toString());
			ResultSet rs = getIdContainingPoint.executeQuery();
			if (!rs.next()) {
				return null;
			}
			List<Integer> pictureIdList = new ArrayList<Integer>();
			do {
				pictureIdList.add(rs.getInt(1));
			} while(rs.next());
			
			return pictureIdList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean liesWithin(Vector2d [] triangle, Vector2d trapezoid) {
		return false;
	}
	
}



























