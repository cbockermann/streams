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
	protected Service[] services;

	public Service[] getServices() {
		return services;
	}

	public void setServices(Service[] services) {
		this.services = services;
	}

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
			if (services != null) {
				for (Service s : services) {
					if (s != null) {
						log.debug("Resetting service {}", s);
						try {
							s.reset();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			} else
				log.info("No service to reset! ");
		}

		return data;
	}
}
