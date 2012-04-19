/**
 * 
 */
package stream.plugin.processing.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.data.DataFactory;
import stream.plugin.OperatorBean;
import stream.plugin.data.DataObject;
import stream.plugin.util.ParameterTypeDiscovery;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;

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
			ExampleSet exampleSet = Array2ExampleSet.createExampleSet(buffer);
			buffer.clear();
			output.deliver(exampleSet);
		} else
			output.deliver(null);

		passThroughPort.deliver(event);
	}

	public static void main(String[] args) {
		Map<String, ParameterType> types = ParameterTypeDiscovery
				.discoverParameterTypes(Data2ExampleSet.class);
		for (String key : types.keySet()) {
			log.info("Found '{}'  =>  {}", key, types.get(key));
		}
	}
}