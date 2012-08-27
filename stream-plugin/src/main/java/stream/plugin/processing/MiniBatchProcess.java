/**
 * 
 */
package stream.plugin.processing;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.plugin.data.DataSourceObject;
import stream.plugin.processing.convert.ExampleSetFactory;

import com.rapidminer.beans.utils.OperatorParameters;
import com.rapidminer.beans.utils.ParameterTypeFinder;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorChain;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream", name = "Mini Batch Processing", text = "Operator chain that reads batches from a data stream and converts them to example sets for batch processing.")
public class MiniBatchProcess extends OperatorChain {

	static Logger log = LoggerFactory.getLogger(MiniBatchProcess.class);
	InputPort input = getInputPorts().createPort("stream");
	OutputPort innerExampleSet;

	ExampleSetFactory exampleSetFactory = ExampleSetFactory.newInstance();

	Integer batchSize = 10;
	Boolean batchAttribute = false;

	/**
	 * @param description
	 */
	public MiniBatchProcess(OperatorDescription description) {
		super(description, "Stream Processing", "Process Batch");

		innerExampleSet = this.getSubprocess(0).getInnerSources()
				.createPort("example set");
	}

	/**
	 * @return the batchSize
	 */
	public Integer getBatchSize() {
		return batchSize;
	}

	/**
	 * @param batchSize
	 *            the batchSize to set
	 */
	@Parameter(required = true, defaultValue = "100", min = 1.0, max = Double.MAX_VALUE, description = "The batch size")
	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	/**
	 * @return the batchAttribute
	 */
	public Boolean getBatchAttribute() {
		return batchAttribute;
	}

	/**
	 * @param batchAttribute
	 *            the batchAttribute to set
	 */
	@Parameter(required = false, defaultValue = "false", description = "Whether to add the current batch number as additional attribute")
	public void setBatchAttribute(Boolean batchAttribute) {
		this.batchAttribute = batchAttribute;
	}

	/**
	 * @see com.rapidminer.operator.OperatorChain#processStarts()
	 */
	@Override
	public void processStarts() throws OperatorException {
		super.processStarts();
		OperatorParameters.setParameters(this);
		exampleSetFactory = ExampleSetFactory.newInstance();
	}

	/**
	 * @see com.rapidminer.operator.OperatorChain#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		DataSourceObject stream = input.getDataOrNull(DataSourceObject.class);
		log.debug("Checking input (data stream handle): {}", stream);
		if (stream == null) {
			throw new OperatorException(
					"No stream handle received at input port!");
		}

		boolean endOfStream = false;
		int iteration = 1;

		while (!endOfStream) {
			List<Data> items = new ArrayList<Data>(batchSize);

			getProcess().getMacroHandler().addMacro("batch", iteration + "");

			log.debug("Reading {} elements into batch...", batchSize);

			Data item = stream.readNext();
			while (item != null && items.size() < batchSize) {
				log.debug("   adding {}-th item: {}", items.size() + 1, item);
				log.debug("    batch now contains {} items", items.size());

				if (batchAttribute) {
					item.put("@batch:id", iteration + "");
				}

				items.add(item);
				item = stream.readNext();
				log.debug("Next item is: {}", item);
				endOfStream = item == null;
			}

			log.debug("Creating example set...");
			ExampleSet exampleSet = exampleSetFactory.createExampleSet(items);
			innerExampleSet.deliver(exampleSet);
			log.debug("Executing inner operators...");
			getSubprocess(0).execute();

			log.debug("end-of-stream reached? {}", endOfStream);
			iteration++;
		}
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		return ParameterTypeFinder.getParameterTypes(this.getClass());
	}
}