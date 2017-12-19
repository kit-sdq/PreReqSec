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
import security.securityPatterns.SecurityPattern;

public class ModelLoaderSecurityPatternTests {

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
	public void testSecurityPatternName() throws LoadingException, InitializationException {
		ModelLoaderEngine engine = new ModelLoaderEngine("res\\SecurityPatternTestCatalogs\\testCatalogForSecurityPatternName.security");
				
		Collection<SecurityPattern> c = new ModelLoader(engine)
				.filterBySecurityPatternName("namedsecurityPattern")
				.loadSecurityPatterns();
			
		if (c.size() == 1) {
			SecurityPattern securityPattern = null;
			for (SecurityPattern s : c) {
				securityPattern = s;
			}
					
			securityPattern.getName().toLowerCase().equals("namedsecurityPattern");
		} else {
			fail();
		}
	}
	
	@org.junit.Test
	public void testSecurityPatternDescription() throws LoadingException, InitializationException {
		ModelLoaderEngine engine = new ModelLoaderEngine("res\\SecurityPatternTestCatalogs\\testCatalogForSecurityPatternDescription.security");
		
		Collection<SecurityPattern> c = new ModelLoader(engine)
				.filterBySecurityPatternDescription("lorem ipsum")
				.loadSecurityPatterns();
		
		if (c.size() == 1) {
			SecurityPattern securityPattern = null;
			for (SecurityPattern p : c) {
				securityPattern = p;
			}
					
			securityPattern.getName().toLowerCase().equals("describedsecurityPattern");
		} else {
			fail();
		}
	}
	
}
