package tests;

import static org.junit.Assert.*;

import java.util.List;

import javax.vecmath.Vector3d;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import geometry.Calculations;
import geometry.CameraCalculator;

public class ContainsVerticallyTest {
	Vector3d rays[];
	Vector3d origin;

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		rays = new Vector3d[4];
		rays[0] = new Vector3d(-1, 1, 1);
		rays[1] = new Vector3d(-1, 1, -1);
		rays[2] = new Vector3d(-1, -1, -1);
		rays[3] = new Vector3d(-1, -1, 1);
		
		origin = new Vector3d(0, 0, 0);
		Vector3d pointOfInterest1 = new Vector3d(-1, 0, 0);
		Vector3d pointOfInterest2 = new Vector3d(1, 0, 0);
		shouldContain(pointOfInterest1);
		shouldNotContain(pointOfInterest2);
	}
	
	private void shouldContain(Vector3d pointOfInterest) {
		Vector3d [] intersections = CameraCalculator.findRaysVerticalPlaneIntersection(rays, origin, pointOfInterest);
//		boolean contains = Calculations.polygonContainsPoint(pointOfInterest, intersections);
//		assertTrue("Point should be contained in the polygon", contains);
	}
	
	private void shouldNotContain(Vector3d pointOfInterest) {
		Vector3d [] intersections = CameraCalculator.findRaysVerticalPlaneIntersection(rays, origin, pointOfInterest);
//		boolean contains = Calculations.polygonContainsPoint(pointOfInterest, intersections);
//		assertTrue("Point should NOT be contained in the polygon", !contains);
	}

}
