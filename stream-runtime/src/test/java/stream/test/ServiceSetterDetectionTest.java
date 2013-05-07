package stream.test;

import junit.framework.Assert;

import org.junit.Test;

import stream.runtime.DependencyInjection;

public class ServiceSetterDetectionTest {

	@Test
	public void testIsServiceImpl() {
		Assert.assertTrue(DependencyInjection
				.isServiceImplementation(TestService.class));
	}

	@Test
	public void testIsServiceImpl_neg() {
		Assert.assertFalse(DependencyInjection
				.isServiceImplementation(Object.class));
	}

	@Test
	public void testIsServiceImpl_Level2Service() {
		Assert.assertTrue(DependencyInjection
				.isServiceImplementation(Level2Service.class));
	}
}
