package analyzers;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import video.Imshow;

public class OdometricAngles {
	static{ System.loadLibrary("libopencv_java310"); }
	private FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SURF);
	private DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF );
	private DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2); // BRUTEFORCE_SL2 = 6**
	
	private List<double[]> allRotations;
	private String directory;
	private String suffix;
	
	private Mat distortionCoefficients = new Mat(1, 5, CvType.CV_32F);
	Mat cameraMat = new Mat(3, 3, 6);
	
	public OdometricAngles(String directory, String suffix) throws Exception{
		this.directory = directory;
		this.suffix = "." + suffix;
		
		cameraMat.put(0, 0, 582.18394);
		cameraMat.put(0, 2, 663.50655);
		cameraMat.put(1, 1, 582.52915);
		cameraMat.put(1, 2, 378.74541);
		cameraMat.put(2, 2, 1.);
		
		distortionCoefficients.put(0, 0, -0.25722);
		distortionCoefficients.put(0, 1, 0.09022);
		distortionCoefficients.put(0, 2, -0.00060);
		distortionCoefficients.put(0, 3, 0.00009);
		distortionCoefficients.put(0, 4, -0.01662);
	}
	
	/**
	 * Runs the calculation using all consecutive pairs in the array.
	 * @throws Exception 
	 */
	public void runCalculationChain(int[] imgNumbers) throws Exception {
		allRotations = new ArrayList<>();
		
		if (imgNumbers.length < 2) {
			throw new Exception("Not enough images. You must specify at least 2.");
		}
		for (int i = 0; i < imgNumbers.length - 1; i ++) {
			System.out.println("Running calculation " + (i+1) + " out of " + (imgNumbers.length-1));
			double [] rotations = getRotations(imgNumbers[i], imgNumbers[i+1]);
			allRotations.add(rotations);
		}
		
		for (double [] rotation : allRotations) {
			System.out.println("roll: " + rotation[0] + ", pitch: " + rotation[1] + ", yaw: " + rotation[2]);
		}
	}
	
	public void runCalculationPairs(int[][] imgPairs) throws Exception {
		allRotations = new ArrayList<>();
		
		if (imgPairs.length == 0) {
			throw new Exception("Array is empty");
		}
		for (int i = 0; i < imgPairs.length; i ++) {
			if (imgPairs[i].length != 2) {
				throw new Exception("2nd dimension of the array must contain two elements");
			}
			System.out.println("Running calculation " + (i+1) + " out of " + (imgPairs.length));
			double [] rotations = getRotations(imgPairs[i][0], imgPairs[i][1]);
			allRotations.add(rotations);
		}
		
		for (double [] rotation : allRotations) {
			System.out.println("roll: " + rotation[0] + ", pitch: " + rotation[1] + ", yaw: " + rotation[2]);
		}
	}
	
	private double[] getRotations(int img1, int img2) throws IOException, InterruptedException {
		Mat firstImage = loadImage(directory + img1 + suffix);
		Mat secondImage = loadImage(directory + img2 + suffix);
		
		firstImage = undistortHero4Wide720p(firstImage);
		secondImage = undistortHero4Wide720p(secondImage);
		
		// Key point detection
		MatOfKeyPoint keypointsFirstImage = new MatOfKeyPoint();
		MatOfKeyPoint keypointsSecondImage = new MatOfKeyPoint();
		featureDetector.detect(firstImage, keypointsFirstImage);
		featureDetector.detect(secondImage, keypointsSecondImage);
		
		// Descriptor extraction
		Mat descriptorFirstImage = new Mat();
		Mat descriptorSecondImage = new Mat();
		descriptorExtractor.compute(firstImage, keypointsFirstImage, descriptorFirstImage);
		descriptorExtractor.compute(secondImage, keypointsSecondImage, descriptorSecondImage);
		
		// Descriptor matching
		MatOfDMatch matches = new MatOfDMatch();
		descriptorMatcher.match(descriptorFirstImage, descriptorSecondImage, matches);
		List<DMatch> matchesList = matches.toList();
		
		LinkedList<DMatch> goodMatches = new LinkedList<DMatch>();
		MatOfDMatch gm = new MatOfDMatch();
		
		for (int i = 0; i < descriptorFirstImage.rows(); i++) {
			goodMatches.addLast(matchesList.get(i)); // not filtering anything
		}
		
		gm.fromList(goodMatches);
//		showMatches(firstImage, keypointsFirstImage, secondImage, keypointsSecondImage, gm, true);
		
		LinkedList<Point> firstImageList = new LinkedList<Point>();
		LinkedList<Point> secondImageList = new LinkedList<Point>();
		
		List<KeyPoint> keypointsFirstImageList = keypointsFirstImage.toList();
		List<KeyPoint> keypointsSecondImageList = keypointsSecondImage.toList();
		
		for(int i = 0; i < goodMatches.size(); i++){
		    firstImageList.addLast(keypointsFirstImageList.get(goodMatches.get(i).queryIdx).pt);
		    secondImageList.addLast(keypointsSecondImageList.get(goodMatches.get(i).trainIdx).pt);
		}
		
		MatOfPoint2f firstImageMop2f = new MatOfPoint2f();
		firstImageMop2f.fromList(firstImageList);

		MatOfPoint2f secondImageMop2f = new MatOfPoint2f();
		secondImageMop2f.fromList(secondImageList);
		
		Mat mask = new Mat();
		Mat essentialMat = org.opencv.calib3d.Calib3d.findEssentialMat(firstImageMop2f, secondImageMop2f,
				cameraMat, org.opencv.calib3d.Calib3d.RANSAC, 0.999, 3, mask);
		
		Mat R = new Mat();
		Mat t = new Mat();
		
		org.opencv.calib3d.Calib3d.recoverPose(essentialMat, firstImageMop2f, secondImageMop2f,
				cameraMat, R, t, mask);
		
		return calculateAnglesFromMatrix(R);
		
	}
	
	private Mat loadImage(String path) throws IOException {
		BufferedImage image = ImageIO.read(new File(path));
		Mat matFrame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		matFrame.put(0, 0, pixels);
		
		Mat matFrameGray = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		org.opencv.imgproc.Imgproc.cvtColor(matFrame, matFrameGray, org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY);
		return matFrameGray;
	}
	
	private Mat undistortHero4Wide720p(Mat distorted) {
		Mat undistorted = new Mat(distorted.height(), distorted.width(), CvType.CV_8UC3);
		Imgproc.undistort(distorted, undistorted, cameraMat, distortionCoefficients);
		return undistorted;
	}
	
	private double[] calculateAnglesFromMatrix(Mat matrix) {
		double x, y, z;
		x = y = z = 0;
		x = Math.atan2(matrix.get(1, 0)[0], matrix.get(0, 0)[0]);
		y = Math.atan2(matrix.get(1, 2)[0], matrix.get(2, 2)[0]);
		z = Math.atan2(-matrix.get(2, 0)[0], Math.sqrt(matrix.get(2, 1)[0] * matrix.get(2, 1)[0]
				+ matrix.get(2, 2)[0] * matrix.get(2, 2)[0]));
		
		return new double[]{Math.toDegrees(x), Math.toDegrees(y), Math.toDegrees(z)};
	}
	
	private void showMatches(Mat matFrame1, MatOfKeyPoint keyPoints1, Mat matFrame2,
		MatOfKeyPoint keyPoints2, MatOfDMatch matches, boolean show) {
		if (!show) return;
		Mat imgMatches = new Mat();
		org.opencv.features2d.Features2d.drawMatches(matFrame1, keyPoints1, matFrame2, keyPoints2, matches, imgMatches);
		Imshow ims = new Imshow("Matches");
		ims.showImage(imgMatches);
	}
	
	public void serializeResults(String filename) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(directory + filename);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(allRotations);
		out.close();
		fileOut.close();
		System.out.println("Serialization successful");
	}
}





























