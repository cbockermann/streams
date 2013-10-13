/**
 * 
 */
package stream.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ConditionedProcessor;
import stream.Data;

/**
 * @author chris
 * 
 */
public class Reset extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(Reset.class);
	protected Service service;

	/**
	 * @return the service
	 */
	public Service getService() {
		return service;
	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setService(Service service) {
		this.service = service;
	}

	/**
	 * @see stream.ConditionedProcessor#processMatchingData(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {

		if (service != null) {
			try {
				log.debug("Resetting service {}", service);
				service.reset();
			} catch (Exception e) {
				log.error("Failed to reset service: {}", e.getMessage());
				if (log.isDebugEnabled())
					e.printStackTrace();
			}
		} else {
			log.info("No service to reset! ");
		}

		return data;
	}
}
