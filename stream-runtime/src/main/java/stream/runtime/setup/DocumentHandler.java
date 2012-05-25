/**
 * 
 */
package stream.runtime.setup;

import org.w3c.dom.Document;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public interface DocumentHandler {

	public void handle(ProcessContainer container, Document doc)
			throws Exception;
}
