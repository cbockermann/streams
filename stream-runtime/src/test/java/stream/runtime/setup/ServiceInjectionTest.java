/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
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

		Method m = ServiceInjection
				.getServiceSetter(pred, "learner-ref", false);
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
