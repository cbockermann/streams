/**
 * 
 */
package stream.plugin.processing;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ProcessContext;
import stream.data.Data;
import stream.expressions.Expression;
import stream.expressions.ExpressionCompiler;
import stream.io.ListDataStream;
import stream.plugin.DataStreamOperator;
import stream.plugin.DataStreamPlugin;
import stream.plugin.data.DataObject;
import stream.plugin.data.DataSourceObject;
import stream.runtime.ContainerContext;
import stream.runtime.ProcessContextImpl;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;

/**
 * <p>
 * This operator is a simple operator chain that will execute a <b>one-pass</b>
 * iteration over the input example set. Any operators that will be added to
 * this chain need to be able to deal with that.
 * </p>
 * <p>
 * Thus, it does not make sense to put in any learners that may require multiple
 * passes over the training data.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public class DataStreamProcess extends
		AbstractDataStreamProcess<DataSourceObject, DataObject> {

	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger(DataStreamProcess.class);

	final ContainerContext containerContext = new ContainerContext();
	final ProcessContext processContext = new ProcessContextImpl(
			containerContext);

	public final static String BUFFER_SIZE_PARAMETER = "bufferSize";
	public final static String FILTER_PARAMETER = "condition";
	int bufferSize = 0;
	List<DataObject> resultBuffer = new ArrayList<DataObject>();
	Expression condition = null;

	/**
	 * @param description
	 */
	public DataStreamProcess(OperatorDescription description) {
		super(description, "Process Data Stream",
				DataStreamPlugin.DATA_STREAM_PORT_NAME, DataSourceObject.class,
				DataStreamPlugin.DATA_ITEM_PORT_NAME);
	}

	/**
	 * @see com.rapidminer.operator.OperatorChain#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		resultBuffer.clear();

		bufferSize = getParameterAsInt(BUFFER_SIZE_PARAMETER);

		try {
			if (this.isParameterSet(FILTER_PARAMETER)) {
				String filter = getParameterAsString(FILTER_PARAMETER);
				condition = ExpressionCompiler.parse(filter);
			}
			log.debug("Applying filter {} to data-stream", condition);
		} catch (Exception e) {
			throw new UserError(this, e, "filter.syntax.error", e.getMessage());
		}

		List<Operator> nested = this.getImmediateChildren();
		log.debug("This StreamProcess has {} nested operators", nested.size());
		for (Operator op : nested) {
			log.debug("  op: {}", op);

			if (op instanceof DataStreamOperator) {
				log.debug("Resetting stream-operator {}", op);

				DataStreamOperator dso = (DataStreamOperator) op;
				dso.setProcessContext(this.processContext);
				dso.reset();
			}
		}

		log.debug("Starting some work in doWork()");
		DataSourceObject dataSource = input.getData(DataSourceObject.class);
		log.debug("input is a data-stream-source...");
		int i = 0;

		Data item = dataSource.readNext();
		while (item != null) {

			if (condition == null || condition.matches(processContext, item)) {

				log.debug("Processing example {}", i);
				DataObject datum = dataSource.wrap(item);
				log.debug("Wrapped data-object is: {}", datum);
				dataStream.deliver(datum);
				getSubprocess(0).execute();
				inApplyLoop();
				i++;

				try {
					DataObject processed = outputStream
							.getData(DataObject.class);
					if (bufferSize > 0 && processed != null
							&& output.isConnected()) {
						log.debug("Adding processed data item: {}",
								processed.getWrappedDataItem());
						resultBuffer.add(processed);
					}
				} catch (Exception e) {
					log.error("Failed to retrieve processed data-item: {}",
							e.getMessage());
				}

				if (bufferSize > 0 && resultBuffer.size() >= bufferSize) {
					log.debug("Maximum buffer-size reached");
					break;
				}

				log.debug("resultBuffer.size is {}", resultBuffer.size());

			} else {
				log.debug("Skipping non-matching data-item: {}", item);
			}

			item = dataSource.readNext();
		}

		if (output.isConnected()) {
			log.debug("Collected {} data items as result.");
			output.deliver(new DataSourceObject(
					new ListDataStream(resultBuffer)));
		}

		log.debug("doWork() is finished.");
	}

	/**
	 * @see stream.plugin.processing.AbstractDataStreamProcess#wrap(stream.data.Data)
	 */
	@Override
	public DataObject wrap(Data item) {
		return new DataObject(item);
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = new ArrayList<ParameterType>(); // super.getParameterTypes();

		types.add(new ParameterTypeString(FILTER_PARAMETER,
				"A filter condition for the processing", true));

		types.add(new ParameterTypeInt(BUFFER_SIZE_PARAMETER,
				"The number of data items to collect", 0, Integer.MAX_VALUE, 0));

		for (ParameterType type : types) {
			log.debug("Found parameter '{}' with description '{}'",
					type.getKey(), type.getDescription());
		}
		return types;
	}
}