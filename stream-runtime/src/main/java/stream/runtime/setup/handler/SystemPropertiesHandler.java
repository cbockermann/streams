/**
 * 
 */
package stream.runtime.setup.handler;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.runtime.ProcessContainer;
import stream.runtime.Variables;

/**
 * <p>
 * This handler extracts all properties defined in a document and adds these to
 * the process container.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class SystemPropertiesHandler implements DocumentHandler {

	static Logger log = LoggerFactory.getLogger(SystemPropertiesHandler.class);

	/**
	 * @see stream.runtime.setup.handler.DocumentHandler#handle(stream.runtime.ProcessContainer,
	 *      org.w3c.dom.Document)
	 */
	@Override
	public void handle(ProcessContainer container, Document doc,
			Variables variables) throws Exception {
		// add system properties, e.g defined at command line using the -D flag:
		// java -Dproperty-name=property-value
		//
		addSystemProperties(container, variables);
	}


	/**
	 * This method adds all the system properties to the container properties,
	 * possibly overwriting pre-defined properties.
	 * 
	 * @param container
	 */
	private void addSystemProperties(ProcessContainer container,
			Variables variables) {
		for (Object key : System.getProperties().keySet()) {
			// log.debug("Adding system property '{}' = {}", key,
			// System.getProperty(key.toString()));
			variables.set(key.toString(), System.getProperty(key.toString()));
		}
	}
}
