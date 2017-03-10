package test.framework;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;
import start.application.core.XmlPathApplicationContext;

public class StartTest extends TestCase {

	XmlPathApplicationContext container=new XmlPathApplicationContext();

	@BeforeClass
	public void testBeforeClass() {
		container.start();
	}

	@Test
	public void testAssert() {
		System.out.println("测试方法");
	}

	@AfterClass
	public void testAfterClass() throws IOException {
		container.close();
	}

}
