package analyzers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Vector3d;

import cameras.AbstractCamera;
import hw.utils.GeographyUtils;
import loaders.Telemetry;
import video.FrameGrabber;
import video.TransformEstimate;

public class TelemetryHomographyComparator {
	HashMap<Long, Telemetry> telemetryMap = new HashMap<Long, Telemetry>();
	FrameGrabber fg;
	AbstractCamera camera;
	private final String OUTPUT_DIRECTORY = "C:\\JavaPrograms\\thesis\\resources\\output\\temp\\";
	
	public TelemetryHomographyComparator(String videoPath, String telemetryPath, AbstractCamera camera) throws IOException {
		fg = new FrameGrabber(videoPath);
		this.camera = camera;
		FileInputStream fs;
		fs = new FileInputStream(telemetryPath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		String line;
		for (int i = 0 ; i < 2; i ++) {
			if ((line = br.readLine()) != null) {
				//read meta and ignore
			}
		}
		
		while ((line = br.readLine()) != null) {
			saveTelemetry(line);
		}
	}
	
	public void compareTelemetryAndHomography(long startMs, long endMs, boolean deleteWhenFinished) {
		long firstFrame = startMs / 1000 * camera.getFps(); 
		long lastFrame = (long)((double)endMs / 1000 * camera.getFps());
		fg.saveNthFrame ((int)firstFrame, OUTPUT_DIRECTORY);
		fg.saveNthFrame ((int)lastFrame, OUTPUT_DIRECTORY);
		String firstImagePath = OUTPUT_DIRECTORY + firstFrame + ".png";
		String secondImagePath = OUTPUT_DIRECTORY + lastFrame + ".png";
		
		TransformEstimate te = new TransformEstimate(firstImagePath, secondImagePath);
		List<double[]> rotations = te.getRotations(); // roll, pitch, yaw
		List<double[]> translations = te.getTranslations();
		compareRotations(rotations, startMs, endMs);
		compareTranslations(translations, startMs, endMs);
		
		
		if (deleteWhenFinished) {
			File firstPicture = new File(firstImagePath);
			File lastPicture = new File(secondImagePath);
			firstPicture.delete();
			lastPicture.delete();
		}
	}
	
	private void compareRotations(List<double[]> rotations, long startMs, long endMs) {
		Telemetry firstTelemetry = telemetryMap.get(startMs);
		Telemetry lastTelemetry = telemetryMap.get(endMs);
		double telemetryRoll = Math.abs(firstTelemetry.roll - lastTelemetry.roll);
		double telemetryPitch = Math.abs(firstTelemetry.pitch - lastTelemetry.pitch);
		double telemetryYaw = Math.abs(firstTelemetry.yaw - lastTelemetry.yaw);
		
		System.out.println("Angles in axes: x, y, z\nDrone telemetry:");
		System.out.println("Roll: " + telemetryRoll + ", Pitch: " + telemetryPitch + ", Yaw: " + telemetryYaw);
		System.out.println("OpenCV homography");
		for (double [] rotation : rotations) {
			System.out.println("Roll: " + rotation[0] + ", Pitch: " + rotation[1] + ", Yaw: " + rotation[2]);
		}
	}
	
	private void compareTranslations(List<double[]> translations, long startMs, long endMs) {
		Telemetry firstTelemetry = telemetryMap.get(startMs);
		Telemetry lastTelemetry = telemetryMap.get(endMs);
		double gpsDistance = GeographyUtils.calculateDistance(firstTelemetry.longitude, firstTelemetry.latitude,
				lastTelemetry.longitude, lastTelemetry.latitude);
//		double gpsDistance = GeographyUtils.distance(firstTelemetry.longitude, firstTelemetry.latitude,
//				lastTelemetry.longitude, lastTelemetry.latitude, "K");
		System.out.println("Translations in axes x, y.\nDrone telemetry (gps distance)");
		System.out.println(gpsDistance);
		System.out.println("OpenCV homography (pythagorean theorem)");
		for (double [] translation : translations) {
//			System.out.println("x: " + translation[0] + ", y: " + translation[1] + ", z: " + translation[2]);
			double dist = Math.sqrt(translation[0] * translation[0] + translation[1] * translation[1]);
			System.out.print(dist + ", ");
		}
		System.out.println();
	}
	
	private void saveTelemetry(String telemetryString) {
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
