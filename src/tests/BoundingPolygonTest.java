package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cameras.AbstractCamera;
import cameras.Hero4Black;
import cameras.Hero4Black.Hero4BlackFieldOfView;
import loaders.VideoLoader;

public class BoundingPolygonTest {

//	@Before
//	public void setUp() throws Exception {
//		
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		
//	}

	@Test
	public void test() throws Exception {
		System.out.println("Running test");
		AbstractCamera camera = new Hero4Black(Hero4BlackFieldOfView.WIDE_16X9, 25);
		VideoLoader vl = new VideoLoader("nothing yet", "C:/Users/Milan/Desktop/26.8.16 data/parsed_logs/2016_08_26_11_57_56_test.csv",
				"Test", camera, 0, false);
	}

}
