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
import stream.runtime.VariableContext;
import stream.runtime.setup.ParameterInjection;
import stream.service.Service;

import com.rapidminer.Process;
import com.rapidminer.beans.OperatorBean;
import com.rapidminer.beans.utils.OperatorParameters;
import com.rapidminer.beans.utils.ParameterTypeFinder;
import com.rapidminer.beans.utils.ServiceBinding;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;

/**
 * A stream operator is some piece of code that handles a single data item. This
 * operator class obtains a stream processor from the Streams library that will
 * perform the processing. Thus, this operator simply wraps any processors
 * provided by the streams library.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class GenericStreamOperator extends OperatorBean implements Measurable,
		Processor {

	static Logger log = LoggerFactory.getLogger(GenericStreamOperator.class);

	protected final InputPort input = getInputPorts().createPort(
			DataStreamPlugin.DATA_ITEM_PORT_NAME);
	protected final OutputPort output = getOutputPorts().createPort(
			DataStreamPlugin.DATA_ITEM_PORT_NAME);

	ProcessContext processContext = null;
	Processor processor;
	boolean setup = false;

	/**
	 * Create a new StreamOperator and ensure that this operator accepts
	 * DataObjects as input.
	 * 
	 * @param description
	 */
	public GenericStreamOperator(OperatorDescription description, Class<?> clazz) {
		super(description);

		try {
			processor = (Processor) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Processor getProcessor() {
		return processor;
	}

	/**
	 * @see com.rapidminer.operator.Operator#registerOperator(com.rapidminer.Process)
	 */
	@Override
	protected void registerOperator(Process process) {
		super.registerOperator(process);

		try {
			// log.info("Registering processor {} => {}", getName(), processor);
			// OperatorNamingService.getInstance().registerProcessor(getName(),
			// processor);

			log.info("Checking if {} provides a service... ", processor);
			if (processor instanceof Service) {
				log.info("Registering processor {} as service '{}'", processor,
						getName());
				OperatorNamingService.getInstance().register(getName(),
						(Service) processor);
			} else {
				log.info("Processor {} does not provide any services",
						processor);
			}
		} catch (Exception e) {
			log.error("Failed to register operator: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
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
		// work is done by fetching a single item from the input port,
		// processing
		// it (as implemented by any implementing class) and delivering the
		// processed
		// data back to the output
		//
		log.debug("Executing stream-operator's doWork(), processor is {}",
				this.processor);

		DataObject datum = input.getDataOrNull(DataObject.class);
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
		if (processed != null) {
			log.debug("Delivering item to output port {}", output);
			output.deliver(processed);
		} else {
			log.debug("Not delivering item 'null' to output port.");
		}
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	public List<ParameterType> getParameterTypes() {
		return ParameterTypeFinder.getParameterTypes(processor.getClass());
	}

	public void reset() {
	}

	/**
	 * @see com.rapidminer.operator.Operator#processStarts()
	 */
	@Override
	public void onProcessStart() throws OperatorException {
		try {

			Map<String, String> params = OperatorParameters.getParameters(this);
			ParameterInjection.inject(processor, params, new VariableContext());

			log.info("Trying to resolve referenced services...");
			ServiceBinding.findAndBind(this);

			if (processor instanceof StatefulProcessor) {
				log.debug("initializing processor {}", processor);
				((StatefulProcessor) processor).init(processContext);
			}

			setup = true;

			if (processor instanceof Service) {
				log.info("Resetting service {}", processor);
				((Service) processor).reset();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new UserError(this, e, "Failed to initialize operator: "
					+ e.getMessage());
		}
	}

	/**
	 * @see com.rapidminer.operator.Operator#processFinished()
	 */
	@Override
	public void onProcessEnd() throws OperatorException {
		try {
			if (processor instanceof StatefulProcessor)
				((StatefulProcessor) processor).finish();

			/*
			 * log.debug(
			 * "Removing processor {} from the OperatorNamingService...",
			 * getName()); OperatorNamingService.getInstance().getProcessors()
			 * .remove(getName());
			 * 
			 * if (processor instanceof Service) {
			 * log.debug("Un-registering service {}", getName());
			 * OperatorNamingService.getInstance().unregister(getName()); }
			 */

			setup = false;
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
		if (handled instanceof DataObject) {
			log.debug("Processed item is already wrapped in a DataObject...");
			return (DataObject) handled;
		}

		log.debug("Wrapping item into DataObject...");
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
		log.debug("Calling {}'s process method on: {}", processor, data);
		Data item = processor.process(data);
		log.info("Processor {} returned: {}", item);
		return item;
	}

	/**
	 * @see stream.data.Measurable#getByteSize()
	 */
	@Override
	public double getByteSize() {
		return 0;
	}
}