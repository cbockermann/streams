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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.DataStream;
import stream.plugin.data.AttributeVectorDataAdapter;
import stream.plugin.util.OperatorUtils;
import stream.plugin.util.ParameterTypeDiscovery;
import stream.runtime.setup.DataStreamFactory;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.streaming.CloningMode;
import com.rapidminer.streaming.InputQueueMode;
import com.rapidminer.streaming.StreamEvent;
import com.rapidminer.streaming.StreamEventType;
import com.rapidminer.streaming.ioobject.AttributeVector;
import com.rapidminer.streaming.ioobject.StreamingAttributeHeader;
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
public final class GenericStreamingSourceOperator extends
		AbstractStreamOperator {

	static Logger log = LoggerFactory
			.getLogger(GenericStreamingSourceOperator.class);

	final Class<? extends DataStream> dataStreamClass;
	final List<ParameterType> parameterTypes = new ArrayList<ParameterType>();
	final Map<String, StreamingAttributeHeader> headers = new LinkedHashMap<String, StreamingAttributeHeader>();
	final OutputPort output = getOutputPorts().createPort("attribute vector");

	DataStream dataStream;

	/**
	 * @param description
	 */
	public GenericStreamingSourceOperator(OperatorDescription description,
			Class<? extends DataStream> processorClass) {
		super(description);
		this.dataStreamClass = processorClass;

		parameterTypes.add(new ParameterTypeFile("url",
				"The file to read from", "", false));
		parameterTypes.add(new ParameterTypeInt("limit",
				"The maximum number of items read from the stream", -1,
				Integer.MAX_VALUE, true));

		Map<String, ParameterType> discovered = ParameterTypeDiscovery
				.discoverParameterTypes(dataStreamClass);
		for (String key : discovered.keySet()) {
			if (!"url".equalsIgnoreCase(key) && !"limit".equalsIgnoreCase(key)) {
				parameterTypes.add(discovered.get(key));
			}
		}

		// Processors consume single data items and produce single data items
		//
		getTransformer().addGenerationRule(getOutputPorts().getPortByIndex(0),
				AttributeVector.class);
	}

	protected void setupDataStream() {
		try {
			log.info("Setting up DataStream...");
			Map<String, String> params = OperatorUtils.getParameters(this);
			log.info("Creating DataStream from class {} with parameters {}",
					dataStreamClass, params);
			dataStream = createDataStream(dataStreamClass, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see com.rapidminer.streaming.operator.StreamOperator#preRun()
	 */
	@Override
	public void preRun() {
	}

	/**
	 * @see com.rapidminer.streaming.operator.StreamOperator#initParameters()
	 */
	@Override
	public void initParameters() throws UndefinedParameterError {
		setupDataStream();
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
		if (event.getType() == StreamEventType.TYPE_RESET) {
			this.setupDataStream();
		}
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		try {

			if (dataStream == null) {
				setupDataStream();
			}

			log.info("Reading from dataStream {}", dataStream);
			Data item = dataStream.readNext();
			log.debug("Read item from data-stream: {}", item);
			if (item == null)
				return;

			AttributeVector v = AttributeVectorDataAdapter
					.createAttributeVector(item);
			log.debug("Delivering wrapped DataAttributeVector to output-port...");
			output.deliver(v);

		} catch (Exception e) {
			log.error("Failed to read from stream: {}", e.getMessage());
			e.printStackTrace();
			throw new OperatorException(e.getMessage(), e);
		}
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		return parameterTypes;
	}

	@SuppressWarnings("deprecation")
	public DataStream createDataStream(
			Class<? extends DataStream> dataStreamClass,
			Map<String, String> parameters) throws Exception {
		parameters.put("class", dataStreamClass.getName());
		try {
			DataStream stream = (DataStream) DataStreamFactory
					.createStream(parameters);
			return stream;
		} catch (Exception e) {
			String url = parameters.get("url");
			parameters.put("url", "file:" + url);
			DataStream stream = (DataStream) DataStreamFactory
					.createStream(parameters);
			return stream;
		}
	}
}