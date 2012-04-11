/**
 * 
 */
package stream.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.ConditionedDataProcessor;
import stream.data.Data;
import stream.data.stats.Statistics;
import stream.learner.MetaDataService;

/**
 * @author chris
 * 
 */
public class MetaDataPrinter extends ConditionedDataProcessor {

	static Logger log = LoggerFactory.getLogger(MetaDataPrinter.class);
	MetaDataService metaService;

	String[] keys;

	public void setLearner(MetaDataService service) {
		this.metaService = service;
	}

	/**
	 * @see stream.data.Processor#process(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data input) {

		if (metaService == null) {
			log.error("No meta-data service available!");
			return input;
		}

		if (keys == null) {
			for (String key : input.keySet()) {
				print(key);
			}
		} else {
			for (String key : keys) {
				print(key);
			}
		}

		return input;
	}

	protected void print(String key) {

		Statistics st = metaService.getStatistics(key);
		if (st != null) {
			log.info("Statistics['{}']:  {}", key, st);
		} else {
			log.info("No statstics available for {}", key);
		}
	}
}
