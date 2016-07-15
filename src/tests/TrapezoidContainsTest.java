package tests;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.PictureTelemetryDao;

public class TrapezoidContainsTest {
	private PolygonLoader loader = new PolygonLoader();
	private PictureTelemetryDao dao = PictureTelemetryDao.getInstance();

	@Before
	public void setUp() throws Exception {
//		loader.loadTrapezoids();
	}

	@After
	public void tearDown() throws Exception {
//		loader.deleteData();
	}

	/**
	 * Lines above the trapezoid's bottom and top
	 */
	@Test
	public void test1() {
		for (int i = 0; i < 2; i ++)
			for (double j = 0; j <= 10; j += 0.1) {
				shouldNotContain(j, i * 10.2 - 0.1);
			}
		for (double i = 0.1; i <= 9.9; i += 0.1) {
			shouldContain(i, 0.1);
		}
	}
	/**
	 * Next to corners from the inside and middle
	 */
	@Test
	public void test2() {
		shouldContain(5, 5);
		shouldContain(0.1, 0.1);
		shouldContain(9.9, 0.1);
		shouldContain(8.9, 9.9);
		shouldContain(3.1, 9.9);
	}
	
	@Test
	public void test3() {
		shouldContain(-0.1, -0.1);
		shouldNotContain(10.1, -0.1);
		shouldNotContain(9.1, 10.1);
		shouldNotContain(2.9, 10.1);
	}
	
	// Left side
	@Test
	public void test4() {
		shouldNotContain(1.9, 6.6);
		shouldContain(2.1, 6.6);
	}
	
	@Test
	public void testTime() {
		//!TODO zkusit pridat index na bounding_box
		Random r = new Random();
		int low1 = 0;
		int high1 = 10000000;
		
		for (int i = 0; i < 10; i ++) {
			long start = System.currentTimeMillis();
			List<Integer> pictureId = dao.getIdContainingPoint(r.nextInt(high1 - low1) + low1, r.nextInt(high1 - low1) + low1);
			long end = System.currentTimeMillis();
			String feedback = "";
			if (pictureId != null) {
				feedback = ", contains: " + pictureId.toString();
			}
			System.out.println("Test finished in: " + (end - start) + " MilliSeconds" + feedback);
		}
	}
	
	
	private void shouldContain(double x, double y) {
		List<Integer> pictureId = dao.getIdContainingPoint(x, y);
		assertTrue("Point should be contained in the database", pictureId != null);
	}
	
	private void shouldNotContain(double x, double y) {
		List<Integer> pictureId = dao.getIdContainingPoint(x, y);
		assertTrue("Point should NOT be contained in a picture", pictureId == null);
	}

}











