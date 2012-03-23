/**
 * 
 */
package stream.plotter;

import stream.data.ConditionedDataProcessor;
import stream.data.Data;
import stream.util.Parameter;
import stream.util.SimpleTimeParser;

/**
 * <p>
 * This class is a base class for a data processor that allows for a
 * visualization of data items received.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public abstract class DataVisualizer extends ConditionedDataProcessor {

	Integer width = 1024;
	Integer height = 400;
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
			this.updateInterval = SimpleTimeParser.parseTime(updateInterval);
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
