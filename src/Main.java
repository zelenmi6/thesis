import database.PictureTelemetryDao;
import loaders.DirectoryPicturesLoader;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Running program");
		
//		DirectoryPicturesLoader parser = new DirectoryPicturesLoader("C:/Users/Milan/Google Drive/Škola/Magistr/Diplomka2/bkpImages/dvur");
//		parser.processData();
		PictureTelemetryDao dao = PictureTelemetryDao.getInstance();
		dao.addMonitoredArea("Dvur", null, null);
		
		
		
		System.out.println("Program has finished");
	}

}
