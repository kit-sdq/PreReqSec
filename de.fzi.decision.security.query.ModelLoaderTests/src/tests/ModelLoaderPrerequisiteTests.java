package tests;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import modelLoader.InitializationException;
import modelLoader.LoadingException;
import modelLoader.ModelLoader;
import modelLoader.ModelLoaderEngine;
import security.securityPrerequisites.Prerequisite;

public class ModelLoaderPrerequisiteTests {

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

	@org.junit.Test
	public void testPrerequisiteName() throws LoadingException, InitializationException {
		ModelLoaderEngine engine = new ModelLoaderEngine("res\\PrerequisiteTestCatalogs\\testCatalogForPrerequisiteName.security");
				
		Collection<Prerequisite> c = new ModelLoader(engine)
				.filterByPrerequisiteName("namedprerequisite")
				.loadPrerequisites();
			
		if (c.size() == 1) {
			Prerequisite prerequisite = null;
			for (Prerequisite p : c) {
				prerequisite = p;
			}
					
			prerequisite.getName().toLowerCase().equals("namedprerequisite");
		} else {
			fail();
		}
	}
	
	@org.junit.Test
	public void testPrerequisiteDescription() throws LoadingException, InitializationException {
		ModelLoaderEngine engine = new ModelLoaderEngine("res\\PrerequisiteTestCatalogs\\testCatalogForPrerequisiteDescription.security");
		
		Collection<Prerequisite> c = new ModelLoader(engine)
				.filterByPrerequisiteDescription("lorem ipsum")
				.loadPrerequisites();
		
		if (c.size() == 1) {
			Prerequisite prerequisite = null;
			for (Prerequisite p : c) {
				prerequisite = p;
			}
					
			prerequisite.getName().toLowerCase().equals("describedprerequisite");
		} else {
			fail();
		}
	}
}