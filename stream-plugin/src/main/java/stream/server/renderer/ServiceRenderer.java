/**
 * 
 */
package stream.server.renderer;

import stream.service.Service;

/**
 * @author chris
 * 
 */
public interface ServiceRenderer<S extends Service> {

	public String renderToHtml(String serviceName, S service);
}