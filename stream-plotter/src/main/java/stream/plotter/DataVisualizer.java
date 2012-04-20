/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
package stream.plotter;

import stream.ConditionedProcessor;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.util.parser.TimeParser;

/**
 * <p>
 * This class is a base class for a data processor that allows for a
 * visualization of data items received.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public abstract class DataVisualizer extends ConditionedProcessor {

	protected Integer width = 1024;
	protected Integer height = 400;
	Long updateInterval = 1000L;

	/**
	 * @return the width
	 */
	public Integer getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	@Parameter(required = false, min = 0.0d, max = 2048.0, description = "Preferred width of the visualization component")
	public void setWidth(Integer width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public Integer getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	@Parameter(required = false, min = 0.0d, max = 1536.0, description = "Preferred height of the visualization component")
	public void setHeight(Integer height) {
		this.height = height;
	}

	/**
	 * @return the updateInterval
	 */
	public Long getUpdateInterval() {
		return updateInterval;
	}

	/**
	 * @param updateInterval
	 *            the updateInterval to set
	 */
	public void setUpdateInterval(String updateInterval) {
		try {
			this.updateInterval = TimeParser.parseTime(updateInterval);
		} catch (Exception e) {
			this.updateInterval = 1000L;
		}
	}

	/**
	 * @see stream.data.ConditionedDataProcessor#processMatchingData(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {
		return data;
	}
}
