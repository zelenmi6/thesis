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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.video.Video;

import camera_calibration.MatSerializer;

public class TransformEstimate {
//	static{ System.loadLibrary("opencv_java300"); }
	static{ System.loadLibrary("libopencv_java310"); }
	
	// SIFT a SURF jsou v Jave bugly
	FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SURF);
	DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF );
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
			
			// filtering out matches
			Double max_dist = 0.0;
			Double min_dist = 100.0;
			for(int i = 0; i < descriptorFirstImage.rows(); i++){
			    Double dist = (double) matchesList.get(i).distance;
			    if(dist < min_dist) min_dist = dist;
			    if(dist > max_dist) max_dist = dist;
			}
			System.out.println("-- Max dist : " + max_dist);
			System.out.println("-- Min dist : " + min_dist);
			
			showMatches(firstImage, keypointsFirstImage, secondImage, keypointsSecondImage, matches, false);
			
			
			LinkedList<DMatch> goodMatches = new LinkedList<DMatch>();
			MatOfDMatch gm = new MatOfDMatch();
			
			for (int i = 0; i < descriptorFirstImage.rows(); i++) {
				// filter out some of the matches if needed
//				if (matchesList.get(i).distance < 3.5*min_dist)
					goodMatches.addLast(matchesList.get(i)); // not filtering anything
			}
			
			gm.fromList(goodMatches);
			showMatches(firstImage, keypointsFirstImage, secondImage, keypointsSecondImage, gm, false);
			
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
			
			//3.1427346523449033, y: 2.383214663142159
			//focal len 14
			Mat essentialMat = org.opencv.calib3d.Calib3d.findEssentialMat(firstImageMop2f, secondImageMop2f,
					3.5714310113232735, new Point(3.1427346523449033, 2.383214663142159),
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
		boolean testApproach = false;
		if (testApproach) {
			Mat w = new Mat();
		    Mat u = new Mat();
		    Mat vt = new Mat();
		    
			Mat diag = new Mat(3,3,CvType.CV_64FC1);
		    double[] diagVal = {1,0,0,0,1,0,0,0,1};
		    diag.put(0, 0, diagVal);

		    Mat newE = new Mat(3,3,CvType.CV_64FC1);

		    Core.SVDecomp(essentialMat, w, u, vt, Core.DECOMP_SVD); 

		    Core.gemm(u, diag, 1, vt, 1, newE);

		    Core.SVDecomp(newE, w, u, vt, Core.DECOMP_SVD);

		    double[] W_Values = {0,-1,0,1,0,0,0,0,1};
		    Mat W = new Mat(new Size(3,3), CvType.CV_64FC1);
		    W.put(0, 0, W_Values);

		    double[] Wt_values = {0,1,0-1,0,0,0,0,1};
		    Mat Wt = new Mat(new Size(3,3), CvType.CV_64FC1);
		    Wt.put(0,0,Wt_values);


		    Mat R1 = new Mat();
		    Mat R2 = new Mat();

		    // u * W * vt = R 
		    Core.gemm(u, Wt, 1, vt, 1, R2);
		    Core.gemm(u, W, 1, vt, 1, R1);
		    
		    printRotationMatrix(R1, "Rotation matrix 1");
		    printRotationMatrix(R2, "Rotation matrix 2");

		    // +- T (2 possible solutions for T)
		    Mat T1 = new Mat();
		    Mat T2 = new Mat();
		    // T = u.t
		    u.col(2).copyTo(T1);

		    Core.multiply(translation, new Scalar(-1.0, -1.0, -1.0), T2);
		    
		    
		    
		} else {
			org.opencv.calib3d.Calib3d.decomposeEssentialMat(essentialMat, firstRotation, secondRotation, translation);
			printRotationMatrix(firstRotation, "Rotation matrix");
			printRotationMatrix(secondRotation, "Rotation matrix");
			printGeneralMatrix(translation, "Translation");
		}
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
		
		Mat matFrameGray = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC1);
		org.opencv.imgproc.Imgproc.cvtColor(matFrame, matFrameGray, org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY);
//		Imshow ims = new Imshow("From video source ... ");
//		ims.showImage(matFrame);
		return matFrameGray;
	}
	
	private void printRotationMatrix(Mat matrix, String description) {
		System.out.println("Printing " + description);
		for (int i = 0; i < matrix.height(); i ++) {
			for (int j = 0; j < matrix.width(); j ++) {
				System.out.print(matrix.get(i, j)[0] + " ");
			}
			System.out.println();
		}
		double x, y, z;
		double sy = Math.sqrt(matrix.get(0, 0)[0] * matrix.get(0, 0)[0]
								+ matrix.get(1, 0)[0] * matrix.get(1, 0)[0]);
		boolean singular = sy < 1e-6;
		
		if (!singular) {
			System.out.println(" as a non singular matrix");
			x = Math.atan2(matrix.get(2, 1)[0], matrix.get(2, 2)[0]); //roll
			y = Math.atan2(-matrix.get(2, 0)[0], sy); // pitch
			z = Math.atan2(matrix.get(1, 0)[0], matrix.get(0, 0)[0]); // yaw
		} else {
			System.out.println(" as a singular matrix");
			x = Math.atan2(-matrix.get(2, 1)[0], matrix.get(2, 2)[0]);
			y = Math.atan2(-matrix.get(2, 0)[0], sy);
			z = 0;
		}
		
		System.out.println("Axes angles: ");
		System.out.println("x (roll): " + Math.toDegrees(x));
		System.out.println("y (pitch): " + Math.toDegrees(y));
		System.out.println("z (yaw): " + Math.toDegrees(z));
		rotations.add(new double[]{Math.toDegrees(x), Math.toDegrees(y), Math.toDegrees(z)});
		
		
		System.out.println();
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









