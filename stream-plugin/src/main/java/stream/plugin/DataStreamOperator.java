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
package stream.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Measurable;
import stream.ProcessContext;
import stream.Processor;
import stream.StatefulProcessor;
import stream.data.Data;
import stream.plugin.data.DataObject;
import stream.plugin.util.ParameterTypeDiscovery;
import stream.runtime.VariableContext;
import stream.runtime.setup.ParameterInjection;

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
		Measurable, Processor {
	static Logger log = LoggerFactory.getLogger(DataStreamOperator.class);

	final InputPort input = getInputPorts().createPort(
			DataStreamPlugin.DATA_ITEM_PORT_NAME);
	final OutputPort output = getOutputPorts().createPort(
			DataStreamPlugin.DATA_ITEM_PORT_NAME);
	List<ParameterType> parameterTypes = new ArrayList<ParameterType>();

	ProcessContext processContext = null;
	Processor processor;
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
			processor = (Processor) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		getTransformer().addPassThroughRule(input, output);
	}

	public void setProcessContext(ProcessContext ctx) {
		this.processContext = ctx;
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
				ParameterInjection.inject(processor, params,
						new VariableContext());
			} catch (Exception e) {
				log.error(
						"Failed to set parameters for DataStream Operator '{}': {}",
						processor.getClass(), e.getMessage());
				throw new UserError(this, e, -1);
			}

			setup = true;

			try {
				if (processor instanceof StatefulProcessor)
					((StatefulProcessor) processor).init(processContext);
			} catch (Exception e) {
				throw new UserError(this, e, "Failed to initialize processor: "
						+ e.getMessage());
			}

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

		@SuppressWarnings("deprecation")
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
	 * @see com.rapidminer.operator.Operator#processStarts()
	 */
	@Override
	public void processStarts() throws OperatorException {
		super.processStarts();
		try {
			if (processor instanceof StatefulProcessor)
				((StatefulProcessor) processor).init(processContext);
		} catch (Exception e) {
			throw new UserError(this, e, "Failed to initialize operator: "
					+ e.getMessage());
		}
	}

	/**
	 * @see com.rapidminer.operator.Operator#processFinished()
	 */
	@Override
	public void processFinished() throws OperatorException {
		super.processFinished();
		try {
			if (processor instanceof StatefulProcessor)
				((StatefulProcessor) processor).finish();
		} catch (Exception e) {
			throw new UserError(this, e, "Failed to finish operator: "
					+ e.getMessage());
		}
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
}