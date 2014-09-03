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

import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Parameter;
import stream.data.Statistics;

/**
 * @author chris
 * 
 */
public abstract class StatisticsLearner extends AbstractProcessor implements
		StatisticsService {

	protected String[] keys;
	protected Statistics statistics = new Statistics();
	protected String prefix = "";

	/**
	 * @return the keys
	 */
	public String[] getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	@Parameter(required = false, description = "The keys/attributes this processor should account statistics (sum,average,min/max) for")
	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	@Parameter(required = false, description = "An optional prefix that is prepended to the attribute names, which prevents the original attribute values from being overwritten by the respective sum/average/min/max value")
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @see stream.DataProcessor#process(stream.Data)
	 */
	@Override
	public Data process(Data data) {
		updateStatistics(data);
		return data;
	}

	/**
	 * @see stream.statistics.StatisticsService#getStatistics(java.lang.String)
	 */
	@Override
	public Statistics getStatistics() {
		return new Statistics(statistics);
	}

	public abstract void updateStatistics(Data item);

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		this.resetState();
	}

	/**
	 * @see stream.AbstractProcessor#resetState()
	 */
	@Override
	public void resetState() throws Exception {
		this.statistics = new Statistics();
	}
}