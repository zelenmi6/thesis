package video;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.video.Video;

public class TransformEstimate {
	static{ System.loadLibrary("opencv_java300"); }
	
	// SIFT a SURF jsou v Jave bugly
	FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.ORB);
	DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
	DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(6); // BRUTEFORCE_SL2 = 6**
	
	public TransformEstimate(String img1Path, String img2Path) {
		try {
			Mat matFrame1 = getImage(img1Path);
			Mat matFrame2 = getImage(img2Path);
			
			// Key point detection
			MatOfKeyPoint keyPoints1 = new MatOfKeyPoint();
			MatOfKeyPoint keyPoints2 = new MatOfKeyPoint();
			featureDetector.detect(matFrame1, keyPoints1);
			featureDetector.detect(matFrame2, keyPoints2);
			
			// Descriptor extraction
			Mat descriptor1 = new Mat();
			Mat descriptor2 = new Mat();
			descriptorExtractor.compute(matFrame1, keyPoints1, descriptor1);
			descriptorExtractor.compute(matFrame2, keyPoints2, descriptor2);
			
			// Descriptor matching
			MatOfDMatch matches = new MatOfDMatch();
			descriptorMatcher.match(descriptor1, descriptor2, matches);
			
			// orezani matchu
//			matches = new MatOfDMatch(matches.submat(100, 120, 0, 1));
			
//			Mat[] points = getPointMatricesFromDescriptors(descriptor1, descriptor2, matches);
			Mat[] points = getPointMatricesFromKeyPoints(keyPoints1, keyPoints2, matches);
//			printKeyPointMatrices(keyPoints1, keyPoints2);
//			printMatrices(descriptor1, descriptor2);
			printMatrices(points[0], points[1]);
			
			showMatches(matFrame1, keyPoints1, matFrame2, keyPoints2, matches, false);
			
//			System.out.println(keyPoints1.toList().size());
//			System.out.println(keyPoints2.toList().size());
			
//			Mat result = Video.estimateRigidTransform(matFrame1, matFrame2, true);
//			Mat out = new Mat(3, 4, CvType.CV_8SC3);
			Mat inliers = new Mat();
			Mat out = new Mat();
			int result = org.opencv.calib3d.Calib3d.estimateAffine3D(points[0], points[1], out, inliers);
			printMatrix(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void showMatches(Mat matFrame1, MatOfKeyPoint keyPoints1, Mat matFrame2,
			MatOfKeyPoint keyPoints2, MatOfDMatch matches, boolean show) {
		if (!show) return;
		Mat imgMatches = new Mat();
		org.opencv.features2d.Features2d.drawMatches(matFrame1, keyPoints1, matFrame2, keyPoints2, matches, imgMatches);
		Imshow ims = new Imshow("From video source ... ");
		ims.showImage(imgMatches);
	}
	
	private Mat getImage(String path) throws IOException {
		BufferedImage image = ImageIO.read(new File(path));
		Mat matFrame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC1);
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		matFrame.put(0, 0, pixels);
//		Imshow ims = new Imshow("From video source ... ");
//		ims.showImage(matFrame);
		return matFrame;
	}
	
	private Mat[] getPointMatricesFromDescriptors(Mat desc1, Mat desc2, MatOfDMatch matches) {
		List<DMatch> listOfMatches = matches.toList();
		Mat pts1 = new Mat(listOfMatches.size(), 3, CvType.CV_8UC1);
		Mat pts2 = new Mat(listOfMatches.size(), 3, CvType.CV_8UC1);
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
		Mat pts1 = new Mat(listOfMatches.size(), 3, CvType.CV_32S);
		Mat pts2 = new Mat(listOfMatches.size(), 3, CvType.CV_32S);
		int Z = 1;
		
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
	
	private void printMatrix(Mat matrix) {
		for (int i = 0; i < matrix.height(); i ++) {
			System.out.println(matrix.get(i, 0)[0] + " " + matrix.get(i, 1)[0] + " " + matrix.get(i, 2)[0] + " " + matrix.get(i, 3)[0]);
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









