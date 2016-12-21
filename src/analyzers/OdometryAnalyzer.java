package analyzers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class outputs odometry from class OdometricAngles 
 * in Wolfram Mathematica format for analysis.
 * @author Milan Zelenka
 *
 */
public class OdometryAnalyzer {
	private List<double[]> odometry;
	private long timeStepMs;
	private int fps;
	
	private final int MILISECONDS_IN_SECOND = 1000;
	
	public OdometryAnalyzer(String odometryPath, long timeStepMs, int fps) throws ClassNotFoundException, IOException {
		odometry = deserializeResults(odometryPath);
		this.timeStepMs  = timeStepMs;
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
	
	public void printRollForWolfram() {
		printSeriesForWolfram(0);
	}
	
	public void printPitchForWolfram() {
		printSeriesForWolfram(1);
	}
	
	public void printYawForWolfram() {
		printSeriesForWolfram(2);
	}
	
	private void printSeriesForWolfram(int idx) {
		System.out.print("{");
		for (int i = 0; i < odometry.size(); i ++) {
			System.out.print("{" + odometry.get(i)[idx] + ", " + (i*(double)timeStepMs/MILISECONDS_IN_SECOND) + "}" + ",");
		}
		System.out.println("}");
	}
	
	public void printRollComulativeForWolfram() {
		printComulativeForWolfram(0);
	}
	
	public void printPitchComulativeForWolfram() {
		printComulativeForWolfram(1);
	}
	
	public void printYawComulativeForWolfram() {
		printComulativeForWolfram(2);
	}
	
	private void printComulativeForWolfram(int idx) {
		double value = 0;
		System.out.print("{");
		for (int i = 0; i < odometry.size(); i ++) {
			value += odometry.get(i)[idx];
			System.out.print("{" + value%360 + ", " + (i*(double)timeStepMs/MILISECONDS_IN_SECOND) + "}" + ",");
		}
		System.out.println("}");
	}
	
	public void printRollDebug() {
		printDataForDebug(0, "Roll");
	}
	
	public void printPitchDebug() {
		printDataForDebug(1, "Pitch");
	}
	
	public void printYawDebug() {
		printDataForDebug(2, "Yaw");
	}
	
	private void printDataForDebug(int idx, String description) {
		System.out.println("Frame, " + description);
		for (int i = 0; i < odometry.size(); i ++) {
			System.out.println(i*(double)MILISECONDS_IN_SECOND/timeStepMs*fps + 1 + " " + odometry.get(i)[idx]);
		}
	}
}





















