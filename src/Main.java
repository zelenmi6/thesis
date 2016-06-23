import javax.vecmath.Vector3d;

import database.PictureTelemetryDao;
import loaders.DirectoryPicturesLoader;
import loaders.PictureTelemetry;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Running program");
		
//		DirectoryPicturesLoader parser = new DirectoryPicturesLoader("C:/Users/Milan/Google Drive/Škola/Magistr/Diplomka2/bkpImages/dvur");
//		parser.processData();
		PictureTelemetryDao dao = PictureTelemetryDao.getInstance();
//		dao.addMonitoredArea("Dvur", null, null);
		try {
//			System.out.println(dao.addDataSet(1, "Random String"));
//			dao.addPicture(1, "random string");
			dao.addTelemetry(1, new PictureTelemetry(new Vector3d(1, 2, 3), 180, 0, 0));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		System.out.println("Program has finished");
	}

}
