/**
 * 
 */
package stream.plugin.test;

import java.util.Map;

import org.junit.Test;

import stream.learner.MetaDataService;
import stream.node.service.renderer.MetaDataServiceRenderer;
import stream.plugin.OperatorNamingService;
import stream.service.Service;
import stream.service.ServiceInfo;

/**
 * @author chris
 * 
 */
public class MetaDataServiceRendererTest {

	@Test
	public void test() throws Exception {

		OperatorNamingService ons = OperatorNamingService.getInstance();

		// RMIClient client = new RMIClient(9105);
		Map<String, ServiceInfo> info = ons.list();
		for (String name : info.keySet()) {
			System.out.println(name + " => " + info.get(name));
		}

		Service mds = ons.lookup("//RapidMiner/MetaDataLearner", Service.class);
		// MetaDataService mds = client.lookup("//RapidMiner/MetaDataLearner",
		// MetaDataService.class);

		MetaDataServiceRenderer renderer = new MetaDataServiceRenderer();
		String html = renderer.renderToHtml("MetaDataService",
				(MetaDataService) mds);
		System.out.println("HTML:\n" + html);

		// fail("Not yet implemented");
	}

}
