/**
 * 
 */
package stream.plugin.monitoring;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.plugin.DataItemProcessorOperator;
import stream.plugin.DataObject;
import stream.plugin.DataStreamPlugin;
import stream.runtime.setup.ParameterUtils;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterTypeString;

/**
 * @author chris
 * 
 */
public class StatisticsLoggerOperator extends DataItemProcessorOperator {

	static Logger log = LoggerFactory.getLogger(StatisticsLoggerOperator.class);

	Set<String> keys = null;

	/**
	 * @param description
	 */
	public StatisticsLoggerOperator(OperatorDescription description) {
		super(description);
		this.addParameterType(new ParameterTypeString(
				"keys",
				"The keys/attributes to plot (non-numerical attributes will be ignored)",
				""));
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		if (keys == null) {
			keys = new HashSet<String>();
			for (String key : ParameterUtils
					.split(getParameterAsString("keys"))) {

				if (!key.trim().isEmpty()) {
					keys.add(key.trim());
				}
			}
		}

		//
		// work is done by fetching a single item from the input port,
		// processing
		// it (as implemented by any implementing class) and delivering the
		// processed
		// data back to the output
		//
		log.info("Executing stream-operator's doWork()");

		DataObject datum = input.getData(DataObject.class); // .getDataOrNull();
		log.debug("input datum is: {}", datum);
		if (datum == null) {
			log.debug("No input received, returning from work.");
			return;
		}

		DataObject processed = process(datum);
		log.debug("processed datum is: {}", processed);

		// the processed data may be NULL, i.e. in case the implementing class
		// is a filter
		//
		if (processed != null && output.isConnected())
			output.deliver(processed);
	}

	/**
	 * @see stream.plugin.DataItemProcessorOperator#process(stream.plugin.DataObject)
	 */
	@Override
	public DataObject process(DataObject object) {

		log.debug("Processing item {}", object);
		if (object != null && object.getWrappedDataItem() != null) {

			Data data;

			if (keys != null && !keys.isEmpty()) {

				log.debug("Plotting keys {}", keys);
				data = new DataImpl();
				for (String key : keys) {
					if (object.getWrappedDataItem().containsKey(key)) {
						log.debug("Adding key {}", key);
						data.put(key, object.getWrappedDataItem().get(key));
					}
				}
			} else {
				data = new DataImpl(object.getWrappedDataItem());
				log.debug("Plotting all keys: {}", data.keySet());
			}

			DataStreamPlugin.getStreamPlotView().dataArrived(data);
		}
		return object;
	}

	/**
	 * @see com.rapidminer.operator.Operator#processStarts()
	 */
	@Override
	public void processStarts() throws OperatorException {
		super.processStarts();
		DataStreamPlugin.getStreamPlotView().reset();
	}
}