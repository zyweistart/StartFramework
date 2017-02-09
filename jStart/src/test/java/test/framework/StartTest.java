package test.framework;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;
import start.application.context.Container;

public class StartTest extends TestCase {

	Container container=new Container();

	@BeforeClass
	public void testBeforeClass() {
		container.init();
	}

	@Test
	public void testAssert() {
		System.out.println("测试方法");
	}

	@AfterClass
	public void testAfterClass() {
		container.close();
	}

}
