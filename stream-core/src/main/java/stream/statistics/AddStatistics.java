/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
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
