package com.test;

import junit.framework.TestCase;
import start.application.context.Container;

public class StartTest extends TestCase {

	private Container container;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		container=new Container();
	}

	public void toStart(){
		container.init();
	}
	
}
