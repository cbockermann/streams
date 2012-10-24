/**
 * 
 */
package stream.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;
import stream.annotations.Parameter;
import stream.data.Statistics;

/**
 * @author chris
 * 
 */
// @Description(group = "Data Stream.Process.Statistics", text =
// "Adds statistics from statistic services to the current data item")
public class AddStatistics implements Processor {

	static Logger log = LoggerFactory.getLogger(AddStatistics.class);
	StatisticsService service;
	StatisticsService[] services = new StatisticsService[0];

	/**
	 * @return the service
	 */
	public StatisticsService getService() {
		return service;
	}

	/**
	 * @param service
	 *            the service to set
	 */
	@Parameter(required = false, description = "A statistic service to query")
	public void setService(StatisticsService service) {
		this.service = service;
	}

	/**
	 * @return the services
	 */
	public StatisticsService[] getServices() {
		return services;
	}

	/**
	 * @param services
	 *            the services to set
	 */
	@Parameter(required = false, description = "A list of statistic services to query")
	public void setServices(StatisticsService[] services) {
		this.services = services;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (service != null) {
			Statistics stats = service.getStatistics();
			for (String key : stats.keySet()) {
				input.put(key, stats.get(key));
			}
		} else {
			log.error("No statistics service connected!");
		}

		if (services != null) {
			for (int i = 0; i < services.length; i++) {
				if (services[i] != null) {
					Statistics stats = services[i].getStatistics();
					for (String key : stats.keySet()) {
						input.put(key, stats.get(key));
					}
				}
			}
		}

		return input;
	}
}
