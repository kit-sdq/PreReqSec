package tests;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import modelLoader.InitializationException;
import modelLoader.ModelLoader;
import modelLoader.ModelLoaderEngine;
import security.securityPatterns.SecurityPattern;
import security.securityPatterns.SecurityPatternsFactory;
import security.securityPatterns.impl.SecurityPatternImpl;
import security.securityPatterns.impl.SecurityPatternsFactoryImpl;

public class ModelLoaderTests {
	
	//private static ModelLoaderEngine engine = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/*try {
			engine = new ModelLoaderEngine("res\\test.security");
		} catch (InitializationException e) {
			System.out.println("FUCK");
			e.printStackTrace();
		}*/
		
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
/*
	@Test
	public void t() {
		ModelLoaderEngine engine = null;
		try {
			engine = new ModelLoaderEngine("res\\test.security");
		} catch (InitializationException e) {
			fail();
			//e.printStackTrace();
		}
	}*/
	
	@Test
	public void test() {
		System.out.println("START");
		
		if (1 + 1 != 2) {
			fail("stupid java");
		}
		
		/*ModelLoader loader = new ModelLoader(engine);
		
		SecurityPattern securityPattern = createDefaultSecurityPattern(); //createDefaultSecurityPattern();
		securityPattern.setName("Test");
		
		System.out.println(securityPattern.getName());
		
		Field name = setField(loader, "securityPatternName", "Test");
		
		Method m = getMethod(loader, "securityPatternPartiallyEqual");
		try {
			m.invoke(loader, securityPattern);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		System.out.println("END");*/
		
		//fail("Not yet implemented");
	}

	/*
	private Field setField(ModelLoader loader, String fieldName, String fieldValue) {
		Field f = null;
		try {
			f = ModelLoader.class.getDeclaredField(fieldName);
			f.set(loader, fieldValue);
			f.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f;
	}
	
	private Method getMethod(ModelLoader loader, String methodName) {
		Method method = null;
		try {
			method = ModelLoader.class.getDeclaredMethod(methodName, SecurityPattern.class);
			method.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return method;
	}
	
	private SecurityPattern createDefaultSecurityPattern() {
		Constructor<SecurityPatternImpl> spConstructor;
		try {
			spConstructor = SecurityPatternImpl.class.getDeclaredConstructor();
			SecurityPattern s = spConstructor.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SecurityPatternsFactory f = new SecurityPatternsFactoryImpl();
		return f.createSecurityPattern();
	}*/
	
	
}
