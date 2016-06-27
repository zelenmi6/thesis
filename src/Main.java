import javax.vecmath.Vector3d;

import database.PictureTelemetryDao;
import geometry.CameraCalculator;
import loaders.DirectoryPicturesLoader;
import loaders.PictureTelemetry;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Running program");
		
//		DirectoryPicturesLoader parser = new DirectoryPicturesLoader("Dvur, ", "C:/Users/Milan/Google Drive/Škola/Magistr/Diplomka2/bkpImages/dvurSmall");
//		DirectoryPicturesLoader parser = new DirectoryPicturesLoader("Invalidovna, ", "C:/Users/Milan/Google Drive/Škola/Magistr/Diplomka2/bkpImages/invalidovna");
//		PictureTelemetryDao dao = PictureTelemetryDao.getInstance();
		
		final double altitude = 8;
		final double angle = 0;
		final double FOV = 94;
		
		System.out.println(CameraCalculator.droneToBottomDist(altitude, FOV, angle));
		System.out.println(CameraCalculator.droneToTopDist(altitude, FOV, angle));
		
		
		
		System.out.println("Program has finished");
	}

}
