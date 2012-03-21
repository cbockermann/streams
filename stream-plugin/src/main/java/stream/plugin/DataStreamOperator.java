package stream.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.Measurable;
import stream.plugin.util.ParameterTypeDiscovery;
import stream.util.ParameterInjection;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;

/**
 * A stream operator is some code that handles a single data item.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public abstract class DataStreamOperator extends Operator implements
		Measurable, DataProcessor {
	static Logger log = LoggerFactory.getLogger(DataStreamOperator.class);

	final InputPort input = getInputPorts().createPort(
			DataStreamPlugin.DATA_ITEM_PORT_NAME);
	final OutputPort output = getOutputPorts().createPort(
			DataStreamPlugin.DATA_ITEM_PORT_NAME);
	List<ParameterType> parameterTypes = new ArrayList<ParameterType>();

	DataProcessor processor;
	boolean setup = false;

	/**
	 * Create a new StreamOperator and ensure that this operator accepts
	 * DataObjects as input.
	 * 
	 * @param description
	 */
	public DataStreamOperator(OperatorDescription description, Class<?> clazz) {
		super(description);

		log.debug("Ensuring that we accept DataObjects as input...");
		acceptsInput(DataObject.class);
		producesOutput(DataObject.class);

		parameterTypes.addAll(super.getParameterTypes());
		parameterTypes.addAll(ParameterTypeDiscovery.discoverParameterTypes(
				clazz).values());

		try {
			processor = (DataProcessor) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		//
		// check if setup is required (first time only)
		//
		if (!setup) {

			log.debug("Setting up stream-processor for the first time (init)");
			Map<String, String> params = new HashMap<String, String>();

			List<ParameterType> types = this.getParameterTypes();
			for (ParameterType type : types) {
				String key = type.getKey();
				String value = getParameter(key);
				log.info("Preparing parameter {} = {}", key, value);
				if (key != null && value != null) {
					params.put(key, value);
				}
			}

			try {
				ParameterInjection.inject(processor, params);
			} catch (Exception e) {
				log.error(
						"Failed to set parameters for DataStream Operator '{}': {}",
						processor.getClass(), e.getMessage());
				throw new UserError(this, e, -1);
			}

			setup = true;

		} else {
			log.debug("Operator already set up...");
		}

		//
		// work is done by fetching a single item from the input port,
		// processing
		// it (as implemented by any implementing class) and delivering the
		// processed
		// data back to the output
		//
		log.debug("Executing stream-operator's doWork()");

		DataObject datum = input.getDataOrNull();
		log.debug("input datum is: {}", datum);
		if (datum == null) {
			log.debug("No input received, returning from work.");
			return;
		}

		DataObject processed = handle(datum);
		log.debug("processed datum is: {}", processed);

		// the processed data may be NULL, i.e. in case the implementing class
		// is a filter
		//
		if (processed != null)
			output.deliver(processed);
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	public List<ParameterType> getParameterTypes() {
		return parameterTypes;
	}

	public void reset() {
	}

	/**
	 * This final method is called for all elements of a data stream that is
	 * being processed by this operator.
	 * 
	 * @param data
	 * @return
	 */
	public final DataObject handle(DataObject data) {

		Data handled = process(data.getWrappedDataItem());
		if (handled instanceof DataObject)
			return (DataObject) handled;

		data.setWrappedDataItem(handled);
		return data;
	}

	/**
	 * This is the main method a stream operator needs to implement.
	 * 
	 * @param data
	 * @return
	 */
	public Data process(Data data) {
		return processor.process(data);
	}

	/**
	 * @see stream.data.Measurable#getByteSize()
	 */
	@Override
	public double getByteSize() {
		return 0;
	}

	/**
	 * @see stream.data.Processor#init()
	 */
	@Override
	public void init() throws Exception {
		processor.init();
	}

	/**
	 * @see stream.data.Processor#finish()
	 */
	@Override
	public void finish() throws Exception {
		processor.finish();
	}
}