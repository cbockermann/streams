/**
 * 
 */
package stream.plugin.test;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.learner.MetaDataLearner;
import stream.learner.MetaDataService;
import stream.plugin.OperatorNamingService;
import stream.runtime.rpc.RMINamingService;
import stream.service.Service;
import stream.service.ServiceInfo;

/**
 * @author chris
 * 
 */
public class ServiceInfoTest {

	static Logger log = LoggerFactory.getLogger(ServiceInfoTest.class);

	@Test
	public void test() {

		ServiceInfo info = ServiceInfo.createServiceInfo("MetaDataLearner",
				MetaDataLearner.class);
		log.info("Service-info: {}", info);

		try {
			RMINamingService ns = new RMINamingService("local", "127.0.0.1",
					9011);
			MetaDataLearner mdl = new MetaDataLearner();
			ns.register("mdl", mdl);

			MetaDataService mds = ns.lookup("mdl", MetaDataService.class);
			Assert.assertNotNull(mds);
			info = ServiceInfo.createServiceInfo("proxy", mds);

			Assert.assertEquals(1, info.getServices().length);

			log.info("info of proxy: {}", info);
			log.info("Proxy class: {}", mds.getClass().getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error: " + e.getMessage());
		}
	}

	@Test
	public void testOperatorNamingService() {
		try {

			OperatorNamingService ons = OperatorNamingService.getInstance();
			log.info("Testing OperatorNamingService {}", ons);

			MetaDataLearner mdl = new MetaDataLearner();
			ons.register("mdl", mdl);

			Service mds = ons.lookup("mdl", Service.class);
			Assert.assertNotNull(mds);
			ServiceInfo info = ServiceInfo.createServiceInfo("proxy", mds);
			log.info("info: {}", info);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Error: " + e.getMessage());
		}
	}
}
