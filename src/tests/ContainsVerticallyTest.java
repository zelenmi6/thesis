package tests;

import static org.junit.Assert.*;

import java.util.List;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import geometry.Calculations;
import geometry.CameraCalculator;

public class ContainsVerticallyTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void test() {
//		Vector3d rays[] = rays = new Vector3d[4];
//		rays[0] = new Vector3d(-1, 1, 1);
//		rays[1] = new Vector3d(-1, 1, -1);
//		rays[2] = new Vector3d(-1, -1, -1);
//		rays[3] = new Vector3d(-1, -1, 1);
//		
//		Vector3d origin = new Vector3d(0, 0, 0);
//		Vector3d pointOfInterest = new Vector3d(-1, 0, 0);
//		shouldContain(rays, pointOfInterest, origin);
//		
//	}
	
	@Test
	public void test2() {
		Vector3d rays[] = rays = new Vector3d[4];
		rays[0] = new Vector3d(-1, 1, 1);
		rays[1] = new Vector3d(-1, 1, 0);
		rays[2] = new Vector3d(-1, -1, 0);
		rays[3] = new Vector3d(-1, -1, 1);
		
		Vector3d origin = new Vector3d(0, 0, 0);
		Vector3d pointOfInterest = new Vector3d(-1, 0, 2);
		shouldNotContain(rays, pointOfInterest, origin);
	}
	
	private void shouldContain(Vector3d [] rays, Vector3d pointOfInterest, Vector3d origin) {
		Vector3d [] intersections = CameraCalculator.findRaysVerticalPlaneIntersection(rays, origin, pointOfInterest);
		Vector4d plane = Calculations.getEquationOfAPlane(origin, pointOfInterest);
		boolean contains = Calculations.polygonContainsPoint(pointOfInterest, intersections, plane);
		assertTrue("Point should be contained in the polygon", contains);
	}
	
	private void shouldNotContain(Vector3d [] rays, Vector3d pointOfInterest, Vector3d origin) {
		Vector3d [] intersections = CameraCalculator.findRaysVerticalPlaneIntersection(rays, origin, pointOfInterest);
		// neni asi treba vyrabet celou rovinu. mel by stacit normalovy vektor, protoze nepotrebuji d
		Vector4d plane = Calculations.getEquationOfAPlane(origin, pointOfInterest);
		boolean contains = Calculations.polygonContainsPoint(pointOfInterest, intersections, plane);
		assertTrue("Point should NOT be contained in the polygon", !contains);
	}

}
