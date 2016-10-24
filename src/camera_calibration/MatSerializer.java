package camera_calibration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.codec.binary.Base64;

//import org.apache.commons.codec.binary.Base64;

//import java.util.Base64;

//import org.apache.commons.lang3.SerializationUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MatSerializer {
	static{ System.loadLibrary("libopencv_java310"); }
	public static String matToJson(Mat mat){
	    JsonObject obj = new JsonObject();
	    
	    if(mat.isContinuous()){
	        int cols = mat.cols();
	        int rows = mat.rows();
	        int elemSize = (int) mat.elemSize();
	        int type = mat.type();

	        obj.addProperty("rows", rows);
	        obj.addProperty("cols", cols);
	        obj.addProperty("type", type);

	        // We cannot set binary data to a json object, so:
	        // Encoding data byte array to Base64.
	        String dataString;

	        if( type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3 || type == CvType.CV_16S) {
	            int[] data = new int[cols * rows * elemSize];
	            mat.get(0, 0, data);
	            dataString = new String(Base64.encodeBase64(SerializationUtils.toByteArray(data)));
	        }
	        else if( type == CvType.CV_32F || type == CvType.CV_32FC2) {
	            float[] data = new float[cols * rows * elemSize];
	            mat.get(0, 0, data);
	            dataString = new String(Base64.encodeBase64(SerializationUtils.toByteArray(data)));
	        }
	        else if( type == CvType.CV_64F || type == CvType.CV_64FC2) {
	            double[] data = new double[cols * rows * elemSize];
	            mat.get(0, 0, data);
	            dataString = new String(Base64.encodeBase64(SerializationUtils.toByteArray(data)));
	        }
	        else if( type == CvType.CV_8U ) {
	            byte[] data = new byte[cols * rows * elemSize];
	            mat.get(0, 0, data);
	            dataString = new String(Base64.encodeBase64(data));
	        }
	        else {

	            throw new UnsupportedOperationException("unknown type");
	        }
	        obj.addProperty("data", dataString);

	        Gson gson = new Gson();
	        String json = gson.toJson(obj);

	        return json;
	    } else {
	        System.out.println("Mat not continuous.");
	    }
	    return "{}";
	}

	public static Mat matFromJson(String json){


	    JsonParser parser = new JsonParser();
	    JsonObject JsonObject = parser.parse(json).getAsJsonObject();

	    int rows = JsonObject.get("rows").getAsInt();
	    int cols = JsonObject.get("cols").getAsInt();
	    int type = JsonObject.get("type").getAsInt();

	    Mat mat = new Mat(rows, cols, type);

	    String dataString = JsonObject.get("data").getAsString();
	    if( type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3 || type == CvType.CV_16S) {
	        int[] data = SerializationUtils.toIntArray(Base64.decodeBase64(dataString.getBytes()));
	        mat.put(0, 0, data);
	    }
	    else if( type == CvType.CV_32F || type == CvType.CV_32FC2) {
	        float[] data = SerializationUtils.toFloatArray(Base64.decodeBase64(dataString.getBytes()));
	        mat.put(0, 0, data);
	    }
	    else if( type == CvType.CV_64F || type == CvType.CV_64FC2) {
	        double[] data = SerializationUtils.toDoubleArray(Base64.decodeBase64(dataString.getBytes()));
	        mat.put(0, 0, data);
	    }
	    else if( type == CvType.CV_8U ) {
	        byte[] data = Base64.decodeBase64(dataString.getBytes());
	        mat.put(0, 0, data);
	    }
	    else {

	        throw new UnsupportedOperationException("unknown type");
	    }
	    return mat;
	}
	
	public static void saveStringToFile(String str, String filePath) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(filePath);
		out.println(str);
		out.close();
	}
	
	public static String loadStringFromFile(String filePath) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(filePath));
		 return new String(encoded, StandardCharsets.UTF_8);
	}
	
	public static void printCalibrationMatrixValues(String filePath) {
		Mat cameraMatrix;
//		try {
//			String fileContent = loadStringFromFile(filePath);
//			Mat cameraMatrix = matFromJson(fileContent);
//			System.out.println("Printing intrinsic camera matrix");
//			for (int i = 0; i < cameraMatrix.height(); i ++) {
//				for (int j = 0; j < cameraMatrix.width(); j ++) {
//					System.out.print(cameraMatrix.get(i, j)[0] + " ");
//				}
//				System.out.println();
//			}
			
//			boolean testMatrix = true;
//			if (testMatrix) {
			cameraMatrix = new Mat(3, 3, 6);
			for (int i = 0; i < cameraMatrix.height(); i ++)
				for (int j = 0; j < cameraMatrix.width(); j ++) {
				cameraMatrix.put(i, j, 0);
			}
			cameraMatrix.put(0, 0, 582.18394);
			cameraMatrix.put(0, 2, 663.50655);
			cameraMatrix.put(1, 1, 582.52915);
			cameraMatrix.put(1, 2, 378.74541);
			cameraMatrix.put(2, 2, 1.);
			
			System.out.println("Printing intrinsic camera matrix");
			for (int i = 0; i < cameraMatrix.height(); i ++) {
				for (int j = 0; j < cameraMatrix.width(); j ++) {
					System.out.print(cameraMatrix.get(i, j)[0] + " ");
				}
				System.out.println();
			}
//			}
			
			System.out.println();
			org.opencv.core.Size size = new org.opencv.core.Size(1280, 720);
			double [] fovx = new double[1];
			double [] fovy = new double[1];
			double [] focLen = new double[1];
			double [] aspectRatio = new double[1];
			Point ppov = new Point(0, 0);
			org.opencv.calib3d.Calib3d.calibrationMatrixValues(cameraMatrix, size, 
					1.97, 1.12, fovx, fovy, focLen, ppov, aspectRatio);
			System.out.println("FoVx: " + fovx[0]);
			System.out.println("FoVy: " + fovy[0]);
			System.out.println("Focal length: " + focLen[0]);
			System.out.println("Principal point of view; x: " + ppov.x + ", y: " + ppov.y);
			System.out.println("Aspect ratio: " + aspectRatio[0]);
			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}



































