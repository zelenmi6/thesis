package tests;

import static org.junit.Assert.*;

import javax.vecmath.Vector3d;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cameras.AbstractCamera;
import cameras.Hero4Black;
import cameras.Hero4Black.Hero4BlackFieldOfView;
import cameras.Hero4BlackUndistorted;
import cameras.Hero4BlackUndistorted.Hero4BlackUndistortedFieldOfView;
import geometry.CameraCalculator;

public class PyramidContainsTest {
	AbstractCamera distortedCam, undistortedCam;

	@Before
	public void setUp() throws Exception {
		distortedCam = new Hero4Black(Hero4BlackFieldOfView.WIDE_16X9, 25);
		undistortedCam = new Hero4BlackUndistorted(Hero4BlackUndistortedFieldOfView.WIDE_16X9, 25);
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void testNoTranslationDistorted() {
//		Vector3d[] rays = prepareRays(distortedCam, Math.toRadians(0), Math.toRadians(0), Math.toRadians(0));
//		boolean inside = CameraCalculator.pointIsInsidePyramid(rays, new Vector3d(0, 0, 10), new Vector3d(0, 0, 0));
//		assertTrue("Point should be visible", inside);
//	}
	
	@Test
	public void testTranslationDistorted() {
		Vector3d[] rays = prepareRays(undistortedCam, -0.0398884989334715, -0.79246039576323, 1.76555279218956);
		boolean inside = CameraCalculator.pointIsInsidePyramid(rays,
				new Vector3d(-10.7733065514086, -4.20488681979867, 9.99058020286654),
				new Vector3d(-13.294068549350916, 3.6692342889354252, 4));
		assertTrue("Point should be visible", inside);
	}
	
//	@Test
//	public void testTranslationDistorted() {
//		Vector3d[] rays = prepareRays(distortedCam, Math.toRadians(30), Math.toRadians(30), Math.toRadians(10));
//		boolean inside = CameraCalculator.pointIsInsidePyramid(rays, new Vector3d(0,0,8), new Vector3d(0, 0, 0));
//		assertTrue("Point should be visible", inside);
//	}
	
	private Vector3d[] prepareRays(AbstractCamera camera, double roll, double pitch, double yaw) {
		Vector3d ray1 = CameraCalculator.ray1(camera.getFovHorizontal(), camera.getFovVertical());
		Vector3d ray2 = CameraCalculator.ray2(camera.getFovHorizontal(), camera.getFovVertical());
		Vector3d ray3 = CameraCalculator.ray3(camera.getFovHorizontal(), camera.getFovVertical());
		Vector3d ray4 = CameraCalculator.ray4(camera.getFovHorizontal(), camera.getFovVertical());
		
		Vector3d [] rotatedRays = CameraCalculator.rotateRays(
				ray1, ray2, ray3, ray4, roll, pitch, yaw);
		
		return rotatedRays;
	}

}
























