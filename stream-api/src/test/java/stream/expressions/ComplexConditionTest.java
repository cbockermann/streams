/**
 * 
 */
package stream.expressions;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Context;
import stream.data.Data;
import stream.data.DataFactory;
import stream.service.NamingService;
import stream.service.Service;
import stream.service.ServiceInfo;

/**
 * @author chris
 * 
 */
public class ComplexConditionTest {

	static Logger log = LoggerFactory.getLogger(ComplexConditionTest.class);

	// An empty dummy-context
	final Context ctx = new Context() {

		@Override
		public <T extends Service> T lookup(String ref, Class<T> serviceClass)
				throws Exception {
			return null;
		}

		@Override
		public void register(String ref, Service p) throws Exception {
		}

		@Override
		public void unregister(String ref) throws Exception {
		}

		@Override
		public Object resolve(String variable) {
			return null;
		}

		@Override
		public Map<String, ServiceInfo> list() throws Exception {
			return new HashMap<String, ServiceInfo>();
		}

		@Override
		public void addContainer(String key, NamingService remoteNamingService)
				throws Exception {
		}
	};

	/**
	 * 
	 */
	@Test
	public void test() {

		try {
			String cond = "( %{data.@flight} != 39 ) OR ( %{data.@flight} == 39 and %{data.time} < 1040 )";

			Data item = DataFactory.create();
			item.put("@flight", "39");

			Expression exp = ExpressionCompiler.parse(cond);
			log.info("condition:\t{}", exp);
			boolean match = exp.matches(ctx, item);
			Assert.assertFalse(match);

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
