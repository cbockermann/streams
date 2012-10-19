package stream.test;

import junit.framework.Assert;

import org.junit.Test;

import stream.runtime.setup.ServiceInjection;

public class ServiceSetterDetectionTest {

	@Test
	public void testIsServiceImpl(){
		Assert.assertTrue( ServiceInjection.isServiceImplementation( TestService.class ) );
	}
	
	@Test
	public void testIsServiceImpl_neg(){
		Assert.assertFalse( ServiceInjection.isServiceImplementation( Object.class ) );
	}
	
	@Test
	public void testIsServiceImpl_Level2Service() {
		Assert.assertTrue( ServiceInjection.isServiceImplementation( Level2Service.class ) );
	}
}
