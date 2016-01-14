package streams.runtime;

import org.junit.Test;
import stream.app.ComputeGraph;
import stream.learner.PredictionService;
import stream.runtime.DependencyInjection;
import stream.runtime.ProcessContainer;
import stream.runtime.ServiceReference;
import stream.runtime.setup.ParameterUtils;
import stream.runtime.setup.ServiceInjection;
import stream.service.Service;

import java.net.URL;
import java.util.Collection;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Test some of the static helper methods in the DependencyInjection
 * Created by Kai on 14.01.16.
 */
public class ServiceInjectionTest {

	private class TestService implements  Service{
		@Override
		public void reset() throws Exception {
		}
	}

	private class ExtendedTestService implements ExtendedServiceInterface{

		@Override
		public void reset() throws Exception {
		}
	}

	private interface ExtendedServiceInterface extends Service {}


	/**
	 * Test method for
	 * {@link stream.runtime.DependencyInjection#isServiceImplementation(java.lang.Class)}
	 *
	 */
	@Test
	public void testIsServiceImplementation(){
		TestService testService = new TestService();
		assertTrue(DependencyInjection.isServiceImplementation(testService.getClass()));

		Service extendedService = new ExtendedTestService();
		assertTrue(DependencyInjection.isServiceImplementation(extendedService.getClass()));

		ExtendedTestService service = new ExtendedTestService();
		assertTrue(DependencyInjection.isServiceImplementation(service.getClass()));

		assertTrue(DependencyInjection.isServiceImplementation(PredictionService.class));

		assertFalse(DependencyInjection.isServiceImplementation(ParameterUtils.class));

	}



	/**
	 * Old method to test injection for ServiceInjection
	 * Test method for service injection
	 *
	 * @throws Exception
	 */
	@Test
	public void testServiceInjection() throws Exception {

		URL url = ServiceInjectionTest.class.getResource("/service-test.xml");
		ProcessContainer container = new ProcessContainer(url);

		ComputeGraph graph = new ComputeGraph();
		Collection<ServiceReference> refs = container.getServiceRefs();

		ServiceInjection.injectServices(refs, container.getContext(), graph,
				container.getVariables());

		container.run();
	}

}
