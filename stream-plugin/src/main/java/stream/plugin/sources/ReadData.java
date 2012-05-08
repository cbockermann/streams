/**
 * 
 */
package stream.plugin.sources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
import stream.data.Data;
import stream.plugin.data.DataObject;
import stream.plugin.data.DataSourceObject;

import com.rapidminer.beans.OperatorBean;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;

/**
 * @author chris
 * 
 */
@Description(name = "Read Data", group = "Data Stream.Sources", text = "Read a single data item from a stream.")
public class ReadData extends OperatorBean {

	static Logger log = LoggerFactory.getLogger(ReadData.class);
	final InputPort input = getInputPorts().createPort("stream",
			DataSourceObject.class);

	final OutputPort output = getOutputPorts().createPort("data item");

	DataSourceObject stream = null;

	/**
	 * @param description
	 */
	public ReadData(OperatorDescription description) {
		super(description);
	}

	/**
	 * @see com.rapidminer.beans.OperatorBean#onProcessStart()
	 */
	@Override
	public void onProcessStart() throws OperatorException {
		super.onProcessStart();
		stream = null;
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		if (stream == null) {
			log.debug("Obtaining data-stream from input port...");
			stream = input.getData(DataSourceObject.class);
		}

		log.debug("data stream is: {}", stream);
		Data item = stream.readNext();

		log.debug("read item: {}", item);
		if (item != null) {
			DataObject dataItem = stream.wrap(item);
			log.debug("delivering wrapped item to output port...");
			output.deliver(dataItem);
		} else {
			getLogger().warning("End of stream reached!");
		}
	}
}