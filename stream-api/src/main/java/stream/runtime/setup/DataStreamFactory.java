/**
 * 
 */
package stream.runtime.setup;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.DataStream;
import stream.runtime.ProcessContainer;
import stream.runtime.VariableContext;

/**
 * @author chris
 * 
 */
public class DataStreamFactory {

	static Logger log = LoggerFactory.getLogger(DataStreamFactory.class);

	public static DataStream createStream(Map<String, String> params)
			throws Exception {
		Class<?> clazz = Class.forName(params.get("class"));
		Constructor<?> constr = clazz.getConstructor(URL.class);
		String urlParam = params.get("url");
		URL url = null;

		if (params.get("url").startsWith("classpath:")) {
			String resource = urlParam.substring("classpath:".length());
			log.debug("Looking up resource '{}'", resource);
			url = ProcessContainer.class.getResource(resource);
			if (url == null) {
				throw new Exception("Classpath url does not exist! Resource '"
						+ resource + "' not found!");
			}
		} else {
			url = new URL(urlParam);
		}

		DataStream stream = (DataStream) constr.newInstance(url);

		ParameterInjection.inject(stream, params, new VariableContext());
		return stream;
	}
}
