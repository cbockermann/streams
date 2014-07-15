/**
 * 
 */
package stream.runtime.setup.handler;

import org.w3c.dom.Document;

import stream.container.IContainer;
import stream.runtime.DependencyInjection;
import stream.util.Variables;

/**
 * @author chris
 * 
 */
public interface DocumentHandler {

	public void handle(IContainer container, Document doc, Variables variables,
			DependencyInjection dependencies) throws Exception;
}
