/**
 * 
 */
package stream.runtime;

/**
 * @author chris
 * 
 */
public class ContainerController implements Controller {

	final ProcessContainer container;

	public ContainerController(ProcessContainer container) {
		this.container = container;
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		throw new Exception("Operation not supported by this service!");
	}

	/**
	 * @see stream.runtime.Controller#shutdown()
	 */
	@Override
	public void shutdown() {
		try {
			container.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}