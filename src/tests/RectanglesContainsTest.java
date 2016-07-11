package tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.PictureTelemetryDao;

public class RectanglesContainsTest {
	private PolygonLoader loader = new PolygonLoader();
	private PictureTelemetryDao dao = PictureTelemetryDao.getInstance();
	
	@Before
	public void setUp() throws Exception {
		loader.loadRectangles();
	}

	@After
	public void tearDown() throws Exception {
		loader.deleteData();
	}

	@Test
	public void test1() {
		shouldContain(6.9, 6.9);
		shouldContain(6.5, 6.5);
		shouldNotContain(5, 8);
		shouldNotContain(3.6, 10);
	}
	
	@Test
	public void test2() {
		double EPSILON = 0.05;
		shouldContain(3.4, 10);
		for (int i = 36; i < 51; i ++) {
			shouldNotContain((double)i/10, 10);
		}
		shouldContain(5.01, 10);
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
