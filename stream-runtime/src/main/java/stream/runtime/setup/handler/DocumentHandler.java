/**
 * 
 */
package stream.runtime.setup.handler;

import org.w3c.dom.Document;

import stream.runtime.ProcessContainer;
import stream.runtime.Variables;

/**
 * @author chris
 * 
 */
public interface DocumentHandler {

	public void handle(ProcessContainer container, Document doc,
			Variables variables) throws Exception;
}
