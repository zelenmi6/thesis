import database.PictureTelemetryDao;
import loaders.DirectoryPicturesLoader;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Running program");
		
//		DirectoryPicturesLoader parser = new DirectoryPicturesLoader("C:/Users/Milan/Google Drive/�kola/Magistr/Diplomka2/bkpImages/dvur");
//		parser.processData();
		PictureTelemetryDao dao = PictureTelemetryDao.getInstance();
//		dao.addMonitoredArea("Dvur", null, null);
		try {
			System.out.println(dao.addDataSet(1, "Random String"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		System.out.println("Program has finished");
	}

}
