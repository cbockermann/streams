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
package stream.plugin.processing.convert;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.data.DataFactory;
import stream.plugin.OperatorBean;
import stream.plugin.data.DataObject;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Processing.Convert")
public class Data2ExampleSet extends OperatorBean {

	static Logger log = LoggerFactory.getLogger(Data2ExampleSet.class);

	final InputPort input = getInputPorts().createPort("data item");
	final OutputPort output = getOutputPorts().createPort("example set");
	final OutputPort passThroughPort = getOutputPorts().createPort("data item");

	final List<Data> buffer = new ArrayList<Data>();
	protected Integer bufferSize = 1;
	final ExampleSetFactory exampleSetFactory = new ExampleSetFactory();

	/**
	 * @param description
	 */
	public Data2ExampleSet(OperatorDescription description) {
		super(description);
		getTransformer().addPassThroughRule(input, passThroughPort);
	}

	/**
	 * @return the bufferSize
	 */
	public Integer getBufferSize() {
		return bufferSize;
	}

	/**
	 * @param bufferSize
	 *            the bufferSize to set
	 */
	@Parameter(required = true, defaultValue = "1")
	public void setBufferSize(Integer bufferSize) {
		this.bufferSize = bufferSize;
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		DataObject event = input.getData(DataObject.class);
		buffer.add(DataFactory.create(event.getWrappedDataItem()));

		if (buffer.size() >= bufferSize) {
			ExampleSet exampleSet = exampleSetFactory.createExampleSet(buffer);
			buffer.clear();
			output.deliver(exampleSet);
		}
		passThroughPort.deliver(event);
	}
}