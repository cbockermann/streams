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
package stream.plugin.streaming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.data.Data;
import stream.plugin.data.AttributeVectorDataAdapter;
import stream.plugin.util.ParameterTypeDiscovery;
import stream.runtime.VariableContext;
import stream.runtime.setup.ParameterInjection;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.streaming.CloningMode;
import com.rapidminer.streaming.InputQueueMode;
import com.rapidminer.streaming.StreamEvent;
import com.rapidminer.streaming.StreamEventType;
import com.rapidminer.streaming.ioobject.AttributeVector;
import com.rapidminer.streaming.operator.AbstractStreamOperator;

/**
 * <p>
 * This operator implements a generic stream operator that executes a nested
 * processor (stream-api), i.e. it wraps a thin layer around the stream api
 * processing units.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public final class GenericStreamingProcessorOperator extends
		AbstractStreamOperator {

	static Logger log = LoggerFactory
			.getLogger(GenericStreamingProcessorOperator.class);

	final Class<? extends Processor> processorClass;
	final List<ParameterType> parameterTypes = new ArrayList<ParameterType>();

	final InputPort input = getInputPorts().createPort("attribute vector");
	final OutputPort output = getOutputPorts().createPort("attribute vector");

	Processor processor;

	/**
	 * @param description
	 */
	public GenericStreamingProcessorOperator(OperatorDescription description,
			Class<? extends Processor> processorClass) {
		super(description);
		this.processorClass = processorClass;

		Map<String, ParameterType> types = ParameterTypeDiscovery
				.discoverParameterTypes(this.processorClass);
		for (String key : types.keySet()) {
			parameterTypes.add(types.get(key));
		}

		// Processors consume single data items and produce single data items
		//
		getTransformer().addPassThroughRule(input, output);
	}

	/**
	 * @see com.rapidminer.streaming.operator.StreamOperator#preRun()
	 */
	@Override
	public void preRun() {
		try {
			log.debug("Creating new instance of processor {}", processorClass);
			processor = processorClass.newInstance();
		} catch (Exception e) {
			log.error("Faild to instantiate processor '{}': {}",
					processorClass, e.getMessage());
			throw new RuntimeException("Failed to instantiate processor: "
					+ e.getMessage());
		}
	}

	protected void setupProcessor() throws Exception {
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
			processor = processorClass.newInstance();
			ParameterInjection.inject(processor, params, new VariableContext());
		} catch (Exception e) {
			log.error(
					"Failed to set parameters for DataStream Operator '{}': {}",
					processor.getClass(), e.getMessage());
			throw new UndefinedParameterError("", this);
		}

	}

	/**
	 * @see com.rapidminer.streaming.operator.StreamOperator#initParameters()
	 */
	@Override
	public void initParameters() throws UndefinedParameterError {
		try {
			setupProcessor();
		} catch (Exception e) {
			throw new UndefinedParameterError("", this);
		}
	}

	/**
	 * 
	 * @see com.rapidminer.streaming.operator.StreamOperator#getInputQueueMode(com
	 *      .rapidminer.operator.ports.InputPort)
	 */
	@Override
	public InputQueueMode getInputQueueMode(InputPort port) {
		return InputQueueMode.QUEUED;
	}

	/**
	 * @see com.rapidminer.streaming.operator.StreamOperator#getPreferredQueueSize
	 *      (com.rapidminer.operator.ports.InputPort)
	 */
	@Override
	public int getPreferredQueueSize(InputPort port) {
		return 10;
	}

	/**
	 * @see com.rapidminer.streaming.operator.StreamOperator#getOutputCloningMode
	 *      (com.rapidminer.operator.ports.OutputPort)
	 */
	@Override
	public CloningMode getOutputCloningMode(OutputPort port) {
		return CloningMode.BLOCKING_CLONE_ON_GET;
	}

	/**
	 * @see com.rapidminer.streaming.operator.StreamOperator#getInputCloningMode(com.rapidminer.operator.ports.InputPort)
	 */
	@Override
	public CloningMode getInputCloningMode(InputPort port) {
		return CloningMode.BLOCKING_CLONE_ON_GET;
	}

	/**
	 * @see com.rapidminer.streaming.StreamEventListener#getReceivableEventTypes()
	 */
	@Override
	public Set<StreamEventType> getReceivableEventTypes() {
		return new HashSet<StreamEventType>();
	}

	/**
	 * @see com.rapidminer.streaming.StreamEventListener#handleStreamEvent(com.rapidminer
	 *      .streaming.StreamEvent)
	 */
	@Override
	public void handleStreamEvent(StreamEvent event) {

	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		if (processor == null) {
			try {
				log.info("No processor available, setting it up...");
				this.setupProcessor();
			} catch (Exception e) {
				e.printStackTrace();
				throw new OperatorException(e.getMessage(), e);
			}
		}

		AttributeVector vector = input.getDataOrNull(AttributeVector.class);
		if (vector == null) {
			log.debug("No attribute-vector at input-port!");
			return;
		}

		Data item = AttributeVectorDataAdapter.createDataItem(vector);

		log.debug("Wrapping attribute-vector {} in DataItem facade...", vector);
		item = processor.process(item);
		if (item == null) {
			log.debug("DataAttributeVector has been consumed by the processor, 'null' was returned.");
			return;
		}

		vector = AttributeVectorDataAdapter.createAttributeVector(item);

		// delivering output to the output port
		//
		// TODO: This needs to be changed to deliver the mangled data item!
		output.deliver(vector);
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public final List<ParameterType> getParameterTypes() {
		return this.parameterTypes;
	}
}