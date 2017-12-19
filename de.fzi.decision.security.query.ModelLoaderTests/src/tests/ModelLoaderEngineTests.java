package tests;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import modelLoader.InitializationException;
import modelLoader.ModelLoaderEngine;


public class ModelLoaderEngineTests {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void initializationTest() {
		ModelLoaderEngine engine = null;
		try {
			engine = new ModelLoaderEngine("res\\test.security");
		} catch (InitializationException e) {
			fail();
			//e.printStackTrace();
		}
		if (engine == null) {
			fail();
		}
	}
	
}
