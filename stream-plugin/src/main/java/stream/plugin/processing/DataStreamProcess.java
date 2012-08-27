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
package stream.plugin.processing;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ProcessContext;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.expressions.Condition;
import stream.plugin.GenericStreamOperator;
import stream.plugin.StreamsPlugin;
import stream.plugin.data.DataObject;
import stream.plugin.data.DataSourceObject;
import stream.runtime.ContainerContext;
import stream.runtime.ProcessContextImpl;

import com.rapidminer.beans.utils.OperatorParameters;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;

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
@Description(group = "Data Stream", name = "Data Stream Process", text = "Process a data stream by processing each single item (one-pass)")
public class DataStreamProcess extends
		AbstractDataStreamProcess<DataSourceObject, DataObject> {

	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger(DataStreamProcess.class);

	final ContainerContext containerContext = new ContainerContext();
	final ProcessContext processContext = new ProcessContextImpl(
			containerContext);

	Long limit = -1L;
	Condition condition;

	/**
	 * @param description
	 */
	public DataStreamProcess(OperatorDescription description) {
		super(description, "Process Data Stream",
				StreamsPlugin.DATA_STREAM_PORT_NAME, DataSourceObject.class,
				StreamsPlugin.DATA_ITEM_PORT_NAME);
	}

	/**
	 * @return the limit
	 */
	public Long getLimit() {
		return limit;
	}

	/**
	 * @param limit
	 *            the limit to set
	 */
	@Parameter(description = "Specifies the maximum number of items read from the stream.", defaultValue = "-1", min = -1.0, max = Long.MAX_VALUE, required = false)
	public void setLimit(Long limit) {
		this.limit = limit;
	}

	/**
	 * @return the condition
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * @param condition
	 *            the condition to set
	 */
	@Parameter(description = "Specifies a condition that has to match. Items not matching that condition will be skipped.", required = false)
	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	/**
	 * @see com.rapidminer.operator.OperatorChain#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		resultBuffer.clear();

		OperatorParameters.setParameters(this);

		List<Operator> nested = this.getImmediateChildren();
		log.debug("This StreamProcess has {} nested operators", nested.size());
		for (Operator op : nested) {
			log.debug("  op: {}  (class is {})", op, op.getClass());

			if (op instanceof GenericStreamOperator) {
				log.debug("Resetting stream-operator {}", op);

				GenericStreamOperator dso = (GenericStreamOperator) op;
				dso.setProcessContext(this.processContext);
				dso.reset();
			}
		}

		log.debug("Starting some work in doWork()");
		DataSourceObject dataSource = input.getData(DataSourceObject.class);
		log.debug("input is a data-stream-source...");
		int i = 0;
		if (limit == null)
			limit = -1L;

		Data item = dataSource.readNext();
		while (item != null && (limit < 0 || i < limit)) {

			if (condition == null || condition.matches(processContext, item)) {

				log.debug("Processing example {}", i);
				DataObject datum = dataSource.wrap(item);
				log.debug("Wrapped data-object is: {}", datum);
				dataStream.deliver(datum);
				inApplyLoop();
				getSubprocess(0).execute();
				i++;

				try {

					outputStream.getData(DataObject.class);
				} catch (Exception e) {
					log.error(
							"Failed to retrieve processed data-item from port '{}': {}",
							outputStream, e.getMessage());
					if (log.isDebugEnabled())
						e.printStackTrace();
				}

			} else {
				log.debug("Skipping non-matching data-item: {}", item);
			}

			item = dataSource.readNext();
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

}