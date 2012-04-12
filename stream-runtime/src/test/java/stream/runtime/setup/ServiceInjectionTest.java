/**
 * 
 */
package stream.runtime.setup;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import stream.learner.Prediction;
import stream.learner.PredictionService;
import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class ServiceInjectionTest {

	/**
	 * Test method for
	 * {@link stream.runtime.setup.ServiceInjection#getServiceSetter(java.lang.Object, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetServiceSetter() {

		Prediction pred = new Prediction();

		Method m = ServiceInjection.getServiceSetter(pred, "learner-ref");
		Assert.assertNotNull(m);

		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link stream.runtime.setup.ServiceInjection#isServiceImplementation(java.lang.Class)}
	 * .
	 */
	@Test
	public void testIsServiceImplementation() {
		Assert.assertTrue(ServiceInjection
				.isServiceImplementation(PredictionService.class));

		Assert.assertFalse(ServiceInjection
				.isServiceImplementation(ParameterUtils.class));
	}

	/**
	 * Test method for service injection
	 * 
	 * @throws Exception
	 */
	@Test
	public void testServiceInjection() throws Exception {

		URL url = ServiceInjectionTest.class.getResource("/service-test.xml");
		ProcessContainer container = new ProcessContainer(url);

		Collection<ServiceReference> refs = container.getServiceRefs();

		ServiceInjection.injectServices(refs, container.getContext());

		container.run();
	}
}
