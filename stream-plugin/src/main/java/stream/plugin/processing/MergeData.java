/**
 * 
 */
package stream.plugin.processing;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
import stream.plugin.data.DataObject;

import com.rapidminer.beans.OperatorBean;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPort;

/**
 * @author chris
 * 
 */
@Description(name = "Merge Data", group = "Data Stream.Processing.Transformations")
public class MergeData extends OperatorBean {

	static Logger log = LoggerFactory.getLogger(MergeData.class);
	InputPort pivot = getInputPorts().createPort("data (pivot)");
	InputPortExtender inExtender = new InputPortExtender("data",
			getInputPorts());
	final OutputPort output = getOutputPorts().createPort("data");

	/**
	 * @param description
	 */
	public MergeData(OperatorDescription description) {
		super(description);
		inExtender.start();
		getTransformer().addRule(
				inExtender.makeFlatteningPassThroughRule(output));
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		DataObject pivotElement = pivot.getDataOrNull(DataObject.class);
		log.debug("Input data item is: {}", pivotElement);
		if (pivotElement == null) {
			throw new OperatorException("No data object found at input port '"
					+ pivot.getName() + "'!");
		}

		List<InputPort> inputs = inExtender.getManagedPorts();
		for (InputPort input : inputs) {
			DataObject merge = input.getDataOrNull(DataObject.class);
			if (merge != null) {
				log.debug("Merging element: {}", merge);
				pivotElement.putAll(merge);
			} else {
				log.error("No element could be retrieved from input port {}",
						input.getName());
			}
		}

		log.debug("Delivering merged element: {}", pivotElement);
		output.deliver(pivotElement);
	}
}