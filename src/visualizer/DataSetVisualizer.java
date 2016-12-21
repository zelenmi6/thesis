package visualizer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingConstants;
import javax.vecmath.Vector3d;

import constants.CameraTesting;
import database.VideoPicturesDao;
import visualizer.CameraPolygon.CameraParameter;

public class DataSetVisualizer extends Visualizer {
	
	public DataSetVisualizer() {
		cameraPolygon = new ParametricCameraPolygon();
		cameraPolygon.addMouseWheelListener(this);
		setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		this.setLayout(new BorderLayout());
		this.add(cameraPolygon, BorderLayout.CENTER);
		zoomSlider = new SliderSet(CameraParameter.ZOOM, CameraTesting.ZOOM, -100, 100, SwingConstants.VERTICAL, cameraPolygon);
		this.add(zoomSlider, BorderLayout.WEST);
		this.add(addRollAltitudePanel(cameraPolygon), BorderLayout.EAST);
		this.add(addHeadingPitchPanel(cameraPolygon), BorderLayout.SOUTH);
		this.add(addOptionsBar(cameraPolygon), BorderLayout.NORTH);
	}
	
	public void visualizeDataSet(int dataSetId, int timeStep, int telemetryStartTime) {
		VideoPicturesDao dao = VideoPicturesDao.getInstance();
		try {
			List<Vector3d[]> coordinates = dao.getDataSetBoundingPolygons(dataSetId);
			HashMap<Integer, double[]> coordinateMap = dao.getFrameNumbersAndCameraCoordinatesFromDataSet(dataSetId);
			Date previousTime = null;
			Date currentTime = null;
			long correction = 0;
			int step = 0;
			for (Vector3d [] corners : coordinates) {
				ParametricCameraPolygon cameraPolygon = (ParametricCameraPolygon)this.cameraPolygon;
				cameraPolygon.setCorners(corners);
				double[] cameraCoordinates = coordinateMap.get(step+1);
				cameraPolygon.setDronePosition(cameraCoordinates[0], cameraCoordinates[1], cameraCoordinates[3]);
				currentTime = new Date();
				if (previousTime != null)
					correction = currentTime.getTime() - previousTime.getTime();
				System.out.println("Time: " + (int)((telemetryStartTime + step * timeStep / 1000) / 60) + 
						":" + (telemetryStartTime + step * timeStep / 1000) % 60
						+ " correction ms: " + correction + ", frame num: " + (step+1));
				TimeUnit.MILLISECONDS.sleep(timeStep - correction);
				step++;
				previousTime = new Date();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
