/**
 * 
 */
package stream.learner;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import stream.data.Statistics;
import stream.service.Service;
import stream.statistics.StatisticsHistory;

/**
 * <p>
 * This simple interface defines a service that will provide meta data
 * statistics about an observed data stream. The statistics basically provide a
 * schema definition and some basic statistics such as minimum, maximum, etc.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface MetaDataService extends Service {

	/**
	 * Returns a map of all attributes and their types, so far observed by this
	 * service.
	 * 
	 * @return
	 */
	public Map<String, Class<?>> getMetaData();

	/**
	 * 
	 * @return
	 */
	public Map<String, Statistics> getMetaDataStatistics();

	/**
	 * This method provides a set of general statistics about the observed
	 * stream, such as the date-time of the first item observed, date-time of
	 * last item observed, the number of items processed, etc.
	 * 
	 */
	public Map<Serializable, Long> getTopElements(String key);

	public StreamInfo getStreamInformation();

	public StatisticsHistory getStreamHistory();

	class StreamInfo implements Serializable {

		/** The unique class ID */
		private static final long serialVersionUID = 4859911418940249850L;

		protected Date firstItem = null;
		protected Date lastItem = null;
		protected Long numberOfItems = 0L;

		protected void reset() {
			firstItem = null;
			lastItem = null;
			numberOfItems = 0L;
		}

		public final Date firstItem() {
			return firstItem;
		}

		public final Date lastItem() {
			return lastItem;
		}

		public final Long numberOfItems() {
			return numberOfItems;
		}

		public final Double rate() {
			if (numberOfItems > 0 && firstItem != null && firstItem != lastItem) {
				Double start = new Double(firstItem.getTime());
				Double end = new Double(lastItem.getTime());
				return numberOfItems.doubleValue() / ((end - start) / 1000.0d);
			}
			return 0.0d;
		}
	}
}
