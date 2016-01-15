package streams.runtime;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import org.junit.Test;

import stream.learner.PredictionService;
import stream.runtime.DependencyInjection;
import stream.runtime.setup.ParameterUtils;
import stream.service.Service;

/**
 * Test some of the static helper methods in the DependencyInjection Created by
 * Kai on 14.01.16.
 */
public class ServiceInjectionTest {

    private class TestService implements Service {
        @Override
        public void reset() throws Exception {
        }
    }

    private class ExtendedTestService implements ExtendedServiceInterface {

        @Override
        public void reset() throws Exception {
        }
    }

    private interface ExtendedServiceInterface extends Service {
    }

    /**
     * Test method for
     * {@link stream.runtime.DependencyInjection#isServiceImplementation(java.lang.Class)}
     *
     */
    @Test
    public void testIsServiceImplementation() {
        TestService testService = new TestService();
        assertTrue(DependencyInjection.isServiceImplementation(testService.getClass()));

        Service extendedService = new ExtendedTestService();
        assertTrue(DependencyInjection.isServiceImplementation(extendedService.getClass()));

        ExtendedTestService service = new ExtendedTestService();
        assertTrue(DependencyInjection.isServiceImplementation(service.getClass()));

        assertTrue(DependencyInjection.isServiceImplementation(PredictionService.class));

        assertFalse(DependencyInjection.isServiceImplementation(ParameterUtils.class));

    }
}
