package video;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.video.Video;

import camera_calibration.MatSerializer;

public class TransformEstimate {
//	static{ System.loadLibrary("opencv_java300"); }
	static{ System.loadLibrary("libopencv_java310"); }
	
	// SIFT a SURF jsou v Jave bugly
	FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.ORB);
	DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB );
	DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2); // BRUTEFORCE_SL2 = 6**
	
	List<double[]> rotations = new ArrayList<double[]>();
	List<double[]> translations = new ArrayList<double[]>();
	
	public TransformEstimate(String img1Path, String img2Path) {
		try {
			Mat firstImage = getImage(img1Path);
			Mat secondImage = getImage(img2Path);
			
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
			
			showMatches(firstImage, keypointsFirstImage, secondImage, keypointsSecondImage, matches, false);
			
			
			LinkedList<DMatch> goodMatches = new LinkedList<DMatch>();
			MatOfDMatch gm = new MatOfDMatch();
			
			for (int i = 0; i < descriptorFirstImage.rows(); i++) {
				// filter out some of the matches if needed
				goodMatches.addLast(matchesList.get(i)); // not filtering anything
			}
			
			gm.fromList(goodMatches);
			
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
			
			Mat essentialMat = org.opencv.calib3d.Calib3d.findEssentialMat(firstImageMop2f, secondImageMop2f, 1.0, new Point(0, 0),
					org.opencv.calib3d.Calib3d.RANSAC, 0.999, 3);
			decomposeEssential(essentialMat);
			
//			Mat homographyMatrix = org.opencv.calib3d.Calib3d.findHomography(obj, scene, 
//					org.opencv.calib3d.Calib3d.RANSAC, 1);
//			decomposeHomography(homographyMatrix, "resources/camera/cameraMatrix_gopro_0.23.json");
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void decomposeEssential(Mat essentialMat) {
		Mat firstRotation = new Mat();
		Mat secondRotation = new Mat();
		Mat translation = new Mat();
		org.opencv.calib3d.Calib3d.decomposeEssentialMat(essentialMat, firstRotation, secondRotation, translation);
		printRotationMatrix(firstRotation, "Rotation matrix");
		printRotationMatrix(secondRotation, "Rotation matrix");
		printGeneralMatrix(translation, "Translation");
	}
	
	private void decomposeHomography(Mat homographyMatrix, String cameraMatrixPath) throws IOException {
		List<Mat> rotations = new ArrayList<Mat>();
		List<Mat> translations = new ArrayList<Mat>();
		List<Mat> normals = new ArrayList<Mat>();
		String fileContent = MatSerializer.loadStringFromFile(cameraMatrixPath);
		Mat cameraMat = MatSerializer.matFromJson(fileContent);
		org.opencv.calib3d.Calib3d.decomposeHomographyMat(homographyMatrix, cameraMat, rotations, translations, normals);
		
		for (Mat rotation : rotations) {
			printRotationMatrix(rotation, "Rotation matrix");
		}
		
		for (Mat translation : translations) {
			printGeneralMatrix(translation, "Translation");
		}
	}
	
	private void showMatches(Mat matFrame1, MatOfKeyPoint keyPoints1, Mat matFrame2,
		MatOfKeyPoint keyPoints2, MatOfDMatch matches, boolean show) {
		if (!show) return;
		Mat imgMatches = new Mat();
		org.opencv.features2d.Features2d.drawMatches(matFrame1, keyPoints1, matFrame2, keyPoints2, matches, imgMatches);
		Imshow ims = new Imshow("Matches");
		ims.showImage(imgMatches);
	}
	
	private Mat getImage(String path) throws IOException {
		BufferedImage image = ImageIO.read(new File(path));
		Mat matFrame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		matFrame.put(0, 0, pixels);
//		Imshow ims = new Imshow("From video source ... ");
//		ims.showImage(matFrame);
		return matFrame;
	}
	
	private void printRotationMatrix(Mat matrix, String description) {
		System.out.println();
		System.out.println("Printing " + description);
		for (int i = 0; i < matrix.height(); i ++) {
			for (int j = 0; j < matrix.width(); j ++) {
				System.out.print(matrix.get(i, j)[0] + " ");
			}
			System.out.println();
		}
		double x, y, z;
		x = Math.atan2(matrix.get(2, 1)[0], matrix.get(2, 2)[0]); //roll
		y = Math.atan2(-matrix.get(2, 0)[0], Math.sqrt(matrix.get(2, 1)[0] * matrix.get(2, 1)[0] + 
				matrix.get(2, 2)[0] * matrix.get(2, 2)[0] )); //pitch
		z = Math.atan2(matrix.get(1, 0)[0], matrix.get(0, 0)[0]); //yaw
		System.out.println("Axes angles: ");
		System.out.println("x (roll): " + Math.toDegrees(x));
		System.out.println("y (pitch): " + Math.toDegrees(y));
		System.out.println("z (yaw): " + Math.toDegrees(z));
		rotations.add(new double[]{Math.toDegrees(x), Math.toDegrees(y), Math.toDegrees(z)});
	}
	
	private void printGeneralMatrix(Mat matrix, String description) {
		System.out.println("\nPrinting " + description);
		translations.add(new double[3]);
		for (int i = 0; i < matrix.height(); i ++) {
			for (int j = 0; j < matrix.width(); j ++) {
				translations.get(translations.size()-1)[i] = matrix.get(i, j)[0];
				System.out.print(matrix.get(i, j)[0] + " ");
			}
			System.out.println();
		}
	}
	
	public List<double[]> getRotations() {
		return rotations;
	}
	
	public List<double[]> getTranslations() {
		return translations;
	}

}









