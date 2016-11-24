package analyzers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class OdometryTelemetryComparator {
	private List<double[]> odometry;
	private int fps;
	
	private boolean telemetryLoaded = false;
	HashMap<Long, Telemetry> telemetryMap = new HashMap<Long, Telemetry>();
	private int[][] imgPairs;
	
	private final int MILISECONDS_IN_SECOND = 1000;
	
	public OdometryTelemetryComparator(String odometryPath, int[][] imgPairs, int fps) throws ClassNotFoundException, IOException {
		this.imgPairs = imgPairs;
		odometry = deserializeResults(odometryPath);
		this.fps = fps;
	}
	
	private List<double[]> deserializeResults(String odometryPath) throws IOException, ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(odometryPath);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		List<double[]> allRotations = (ArrayList) in.readObject();
		in.close();
		fileIn.close();
		return allRotations;
	}
	
	public void compareTelemetryOdometry() throws Exception {
		if (telemetryLoaded == false) {
			throw new Exception("Need to load telemetry first.");
		}
		for (int i = 0; i < imgPairs.length; i ++) {
			int idx1 = imgPairs[i][0];
			int idx2 = imgPairs[i][1];
			
			long telemetry1Time = idx1 * MILISECONDS_IN_SECOND / fps - MILISECONDS_IN_SECOND / fps;
			long telemetry2Time = idx2 * MILISECONDS_IN_SECOND / fps - MILISECONDS_IN_SECOND / fps;
			
			compareRotations(i, telemetry1Time, telemetry2Time);
		}
	}
	
	private void compareRotations(int imgPairIdx, long telemetry1Time, long telemetry2Time) {
		Telemetry firstTelemetry = telemetryMap.get(telemetry1Time);
		Telemetry secondTelemetry = telemetryMap.get(telemetry2Time);
		double telemetryRollDifference = secondTelemetry.roll - firstTelemetry.roll;
		double telemetryPitchDifference = secondTelemetry.pitch - firstTelemetry.pitch;
		double telemetryYawDifference = secondTelemetry.yaw - firstTelemetry.yaw;
		
		System.out.println("Comparing " + imgPairs[imgPairIdx][0] + " and " + imgPairs[imgPairIdx][1]);
		System.out.println(" Angles in axes: x, y, z\n Drone telemetry:");
		System.out.println("  Roll: " + telemetryRollDifference + ", Pitch: " + 
							telemetryPitchDifference + ", Yaw: " + telemetryYawDifference);
		
		System.out.println(" Odometry results:");
		System.out.println("  Roll: " + odometry.get(imgPairIdx)[0] + ", pitch: " +
							odometry.get(imgPairIdx)[1] + ", yaw:" + odometry.get(imgPairIdx)[2]);
		
		
	}
	
	public void loadTelemetry(String filePath) throws IOException {
		FileInputStream fs;
		fs = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		String line;
		for (int i = 0 ; i < 1; i ++) {
			if ((line = br.readLine()) != null) {
				//read meta and ignore
			}
		}
		
		while ((line = br.readLine()) != null) {
			addTelemetryEntry(line);
		}
		
		telemetryLoaded = true;
	}
	
	private void addTelemetryEntry(String telemetryString) {
		Telemetry telemetry = new Telemetry();
		String [] tokens = telemetryString.split(",");
		telemetry.longitude = Double.parseDouble(tokens[2]);
		telemetry.latitude = Double.parseDouble(tokens[1]);
		telemetry.altitude = Double.parseDouble(tokens[3]);
		telemetry.roll = Double.parseDouble(tokens[4]);
		telemetry.pitch = Double.parseDouble(tokens[5]);
		telemetry.yaw = Double.parseDouble(tokens[6]);
		
		telemetryMap.put(Long.parseLong(tokens[0]), telemetry);
	}
	
	
	private class Telemetry {
		public double longitude, latitude, altitude, roll, pitch, yaw;
	}

}
