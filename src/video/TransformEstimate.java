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
	
	public TransformEstimate(String img1Path, String img2Path) {
		try {
			Mat imObject = getImage(img1Path);
			Mat imgScene = getImage(img2Path);
			
			// Key point detection
			MatOfKeyPoint keypointsObject = new MatOfKeyPoint();
			MatOfKeyPoint keypointsScene = new MatOfKeyPoint();
			featureDetector.detect(imObject, keypointsObject);
			featureDetector.detect(imgScene, keypointsScene);
			
			// Descriptor extraction
			Mat descriptorObject = new Mat();
			Mat descriptorScene = new Mat();
			descriptorExtractor.compute(imObject, keypointsObject, descriptorObject);
			descriptorExtractor.compute(imgScene, keypointsScene, descriptorScene);
			
			// Descriptor matching
			MatOfDMatch matches = new MatOfDMatch();
			descriptorMatcher.match(descriptorObject, descriptorScene, matches);
			List<DMatch> matchesList = matches.toList();
			
			// orezani matchu
//			matches = new MatOfDMatch(matches.submat(100, 120, 0, 1));
			
//			Mat[] points = getPointMatricesFromDescriptors(descriptor1, descriptor2, matches);
//			Mat[] pointsWithZ = getPointMatricesFromKeyPointsWithZ(keyPoints1, keyPoints2, matches);
//			printKeyPointMatrices(keyPoints1, keyPoints2);
//			printMatrices(descriptor1, descriptor2);
//			printMatrices(pointsWithZ[0], pointsWithZ[1]);
			
			showMatches(imObject, keypointsObject, imgScene, keypointsScene, matches, false);
			
//			System.out.println(keyPoints1.toList().size());
//			System.out.println(keyPoints2.toList().size());
			
//			Mat result = Video.estimateRigidTransform(matFrame1, matFrame2, true);
//			Mat out = new Mat(3, 4, CvType.CV_8SC3);
//			Mat inliers = new Mat();
//			Mat affineRotationMatrix = new Mat();
//			int result = org.opencv.calib3d.Calib3d.estimateAffine3D(pointsWithZ[0], pointsWithZ[1], affineRotationMatrix, inliers, 3, 0.99);
			
//			Mat[] points = getPointMatricesFromKeyPoints(keyPoints1, keyPoints2, matches);
//			MatOfPoint2f matOfPoint1 = convertMatToMatOfPoint2f(points[0]);
//			MatOfPoint2f matOfPoint2 = convertMatToMatOfPoint2f(points[1]);
			
			LinkedList<DMatch> goodMatches = new LinkedList<DMatch>();
			MatOfDMatch gm = new MatOfDMatch();
			
			for (int i = 0; i < descriptorObject.rows(); i++) {
				goodMatches.addLast(matchesList.get(i));
			}
			
			gm.fromList(goodMatches);
			
			LinkedList<Point> objList = new LinkedList<Point>();
			LinkedList<Point> sceneList = new LinkedList<Point>();
			
			List<KeyPoint> keypointsObjectList = keypointsObject.toList();
			List<KeyPoint> keypointsSceneList = keypointsScene.toList();
			
			for(int i = 0; i < goodMatches.size(); i++){
			    objList.addLast(keypointsObjectList.get(goodMatches.get(i).queryIdx).pt);
			    sceneList.addLast(keypointsSceneList.get(goodMatches.get(i).trainIdx).pt);
			}
			
			MatOfPoint2f obj = new MatOfPoint2f();
			obj.fromList(objList);

			MatOfPoint2f scene = new MatOfPoint2f();
			scene.fromList(sceneList);
			
			Mat homographyMatrix = org.opencv.calib3d.Calib3d.findHomography(obj, scene, 
					org.opencv.calib3d.Calib3d.RANSAC, 3);
			decomposeHomography(homographyMatrix, "resources/camera/cameraMatrix_gopro_0.23.json");
			
//			printMatrix(affineRotationMatrix, "affine rotation matrix");
			printRotationMatrix(homographyMatrix, "homography matrix");
		} catch (IOException e) {
			e.printStackTrace();
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
	
	private MatOfPoint2f convertMatToMatOfPoint2f(Mat mat) {
		MatOfPoint mpoints = new MatOfPoint(mat);
		MatOfPoint2f points2f = new MatOfPoint2f(mpoints.toArray());
		return points2f;
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
	
	private Mat[] getPointMatricesFromDescriptors(Mat desc1, Mat desc2, MatOfDMatch matches) {
		List<DMatch> listOfMatches = matches.toList();
		Mat pts1 = new Mat(listOfMatches.size(), 3, CvType.CV_8UC3);
		Mat pts2 = new Mat(listOfMatches.size(), 3, CvType.CV_8UC3);
		for (int i = 0; i < listOfMatches.size(); i ++) {
			int queryIdx = listOfMatches.get(i).queryIdx;
			int trainIdx = listOfMatches.get(i).trainIdx;
			
//			System.out.println(desc1.get(queryIdx, 0)[0] + " " + desc1.get(queryIdx, 1)[0] + " " + desc1.get(queryIdx, 2)[0]);
//			System.out.println(desc2.get(trainIdx, 0)[0] + " " + desc2.get(trainIdx, 1)[0] + " " + desc2.get(trainIdx, 2)[0]);
			pts1.get(i, 0)[0] = desc1.get(queryIdx, 0)[0];
			pts1.get(i, 1)[0] = desc1.get(queryIdx, 1)[0];
			pts1.get(i, 2)[0] = desc1.get(queryIdx, 2)[0];
			
			pts2.get(i, 0)[0] = desc2.get(trainIdx, 0)[0];
			pts2.get(i, 1)[0] = desc2.get(trainIdx, 1)[0];
			pts2.get(i, 2)[0] = desc2.get(trainIdx, 2)[0];
		}
		
		return new Mat[]{pts1, pts2};
	}
	
	private Mat[] getPointMatricesFromKeyPoints(MatOfKeyPoint kp1, MatOfKeyPoint kp2, MatOfDMatch matches) {
		List<DMatch> listOfMatches = matches.toList();
		List<KeyPoint> keyPoints1 = kp1.toList();
		List<KeyPoint> keyPoints2 = kp2.toList();
		Mat pts1 = new Mat(listOfMatches.size(), 2, CvType.CV_32S);
		Mat pts2 = new Mat(listOfMatches.size(), 2, CvType.CV_32S);
		
		for (int i = 0; i < listOfMatches.size(); i ++) {
			int queryIdx = listOfMatches.get(i).queryIdx;
			int trainIdx = listOfMatches.get(i).trainIdx;
			
//			pts1.get(i, 0)[0] = keyPoints1.get(queryIdx).pt.x;
			pts1.put(i, 0, new double[]{keyPoints1.get(queryIdx).pt.x});
//			System.out.println(keyPoints1.get(queryIdx).pt.x + "|||" + pts1.get(i, 0)[0]);
			pts1.put(i, 1, new double[]{keyPoints1.get(queryIdx).pt.y});
			
			pts2.put(i, 0, new double[]{keyPoints2.get(trainIdx).pt.x});
			pts2.put(i, 1, new double[]{keyPoints2.get(trainIdx).pt.y});
		}
		
		return new Mat[]{pts1, pts2};
	}
	
	private Mat[] getPointMatricesFromKeyPointsWithZ(MatOfKeyPoint kp1, MatOfKeyPoint kp2, MatOfDMatch matches) {
		List<DMatch> listOfMatches = matches.toList();
		List<KeyPoint> keyPoints1 = kp1.toList();
		List<KeyPoint> keyPoints2 = kp2.toList();
		Mat pts1 = new Mat(listOfMatches.size(), 3, CvType.CV_32S);
		Mat pts2 = new Mat(listOfMatches.size(), 3, CvType.CV_32S);
		int Z = 30;
		
		for (int i = 0; i < listOfMatches.size(); i ++) {
			int queryIdx = listOfMatches.get(i).queryIdx;
			int trainIdx = listOfMatches.get(i).trainIdx;
			
//			pts1.get(i, 0)[0] = keyPoints1.get(queryIdx).pt.x;
			pts1.put(i, 0, new double[]{keyPoints1.get(queryIdx).pt.x});
//			System.out.println(keyPoints1.get(queryIdx).pt.x + "|||" + pts1.get(i, 0)[0]);
			pts1.put(i, 1, new double[]{keyPoints1.get(queryIdx).pt.y});
			pts1.put(i, 2, new double[]{Z});
			
			pts2.put(i, 0, new double[]{keyPoints2.get(trainIdx).pt.x});
			pts2.put(i, 1, new double[]{keyPoints2.get(trainIdx).pt.y});
			pts2.put(i, 2, new double[]{Z});
		}
		
		return new Mat[]{pts1, pts2};
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
	}
	
	private void printGeneralMatrix(Mat matrix, String description) {
		System.out.println("\nPrinting " + description);
		for (int i = 0; i < matrix.height(); i ++) {
			for (int j = 0; j < matrix.width(); j ++) {
				System.out.print(matrix.get(i, j)[0] + " ");
			}
			System.out.println();
		}
	}
	
	private void printKeyPointMatrices(MatOfKeyPoint matrix1, MatOfKeyPoint matrix2) {
		System.out.println(matrix1.rows() + " " + matrix1.cols() + " \t|\t " + matrix2.rows() + " " + matrix2.cols());
		List<KeyPoint> keyPoints1 = matrix1.toList();
		List<KeyPoint> keyPoints2 = matrix1.toList();
		keyPoints1.forEach(value -> System.out.println("x: " + value.pt.x + " y:" + value.pt.y));
		System.out.println("---------------------------------------------------------------------------------------------------------------");
		keyPoints2.forEach(value -> System.out.println("x: " + value.pt.x + " y:" + value.pt.y));
	}
	
	private void printMatrices(Mat matrix1, Mat matrix2) {
		System.out.println(matrix1.rows() + " " + matrix1.cols() + " \t|\t " + matrix2.rows() + " " + matrix2.cols());
		if (matrix1.rows() != matrix2.rows()) 
			return;
		
		for (int i = 0; i < matrix1.rows(); i ++) {
			for (int j = 0; j < matrix1.cols(); j ++) {
				System.out.print(matrix1.get(i, j)[0] + " ");
			}
			System.out.print(" | ");
			for (int j = 0; j < matrix2.cols(); j ++) {
				System.out.print(matrix2.get(i, j)[0] + " ");
			}
			System.out.println();
		}
	}

}









