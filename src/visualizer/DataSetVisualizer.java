package visualizer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.sql.SQLException;
import java.util.List;

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
	
	public void visualizeDataSet(int dataSetId, int timeStep) {
		VideoPicturesDao dao = VideoPicturesDao.getInstance();
		try {
			List<Vector3d[]> coordinates = dao.getDataSetCoordinates(dataSetId);
			for (Vector3d [] corners : coordinates) {
				ParametricCameraPolygon cameraPolygon = (ParametricCameraPolygon)this.cameraPolygon;
				cameraPolygon.setCorners(corners);
				Thread.sleep(timeStep);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
