package tests;

import java.util.Random;

import javax.vecmath.Vector3d;

import database.PictureTelemetryDao;

public class PolygonLoader {
	
	private PictureTelemetryDao dao = PictureTelemetryDao.getInstance();
	int areaId;
	int dataSetId;

	public void loadRectangles() {
		try {
			areaId = dao.addMonitoredArea("Test");
			dataSetId = dao.addDataSet(areaId, "noPath", 0, 0);
			dao.addPicture(dataSetId, "noPath", generateRectangle(0, 0, 7, 0, 7, 7, 0, 7));
			dao.addPicture(dataSetId, "noPath2", generateRectangle(0, 0, 3.5, 0, 3.5, 15, 0, 15));
			dao.addPicture(dataSetId, "noPath3", generateRectangle(10, 0, 18.94, 4.47, 8.94, 24.47, 0, 20));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void loadTrapezoids() {
		try {
			areaId = dao.addMonitoredArea("Test");
			dataSetId = dao.addDataSet(areaId, "noPath", 0, 0);
			loadManyRandom();
			dao.addPicture(dataSetId, "noPath", generateRectangle(0, 0, 10, 0, 9, 10, 3, 10));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadManyRandom() {
		System.out.println("Loading started");
		Random r = new Random();
		int low1 = 0;
		int high1 = 100000;
		int low2 = 10;
		int high2 = 100;
		
		for (int i = 0; i < 100000; i ++) {
			int x1 = r.nextInt(high1 - low1) + low1;
			int y1 = r.nextInt(high1 - low1) + low1;
			int x2 = x1 + r.nextInt(high2 - low2) + low2;
			int y2 = y1;
			int x3 = x1 + r.nextInt(high2 - low2) + low2;
			int y3 = y1 + r.nextInt(high2 - low2) + low2;
			int x4 = x3 + r.nextInt(high2 - low2) + low2;
			int y4 = y3;
			try {
				dao.addPicture(dataSetId, "noPath/" + i, generateRectangle(x1, y1, x2, y2, x3, y3, x4, y4));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Loading finished");
	}

	public void deleteData() {
		dao.deleteMonitoredArea(areaId);
		
	}
	
	private Vector3d [] generateRectangle(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		Vector3d a = new Vector3d(x1, y1, 0);
		Vector3d b = new Vector3d(x2, y2, 0);
		Vector3d c = new Vector3d(x3, y3, 0);
		Vector3d d = new Vector3d(x4, y4, 0);
		return new Vector3d[]{a, b, c, d};
	}
	
}
