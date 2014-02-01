/**
 * 
 */
package stream.script;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.BodyContent;
import stream.runtime.setup.ObjectCreator;
import stream.util.Variables;

/**
 * @author chris
 * 
 */
public class JavaScriptProcessorFactory implements ObjectCreator {

	static Logger log = LoggerFactory
			.getLogger(JavaScriptProcessorFactory.class);

	/**
	 * @see stream.runtime.setup.ObjectCreator#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return "js:";
	}

	/**
	 * @see stream.runtime.setup.ObjectCreator#create(java.lang.String,
	 *      java.util.Map)
	 */
	@Override
	public Object create(String className, Map<String, String> parameters,
			Variables local) throws Exception {

		log.info("Request for creating {}", className);
		String res = className.substring(3);
		log.info("  expecting resource: {}", res);

		JavaScript processor = new JavaScript();
		processor.setScript(new BodyContent(parameters.get(BodyContent.KEY)));
		return processor;
	}
}