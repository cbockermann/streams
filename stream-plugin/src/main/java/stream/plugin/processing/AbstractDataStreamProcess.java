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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.plugin.data.DataObject;
import stream.plugin.data.DataSourceObject;

import com.rapidminer.beans.utils.ParameterTypeFinder;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorChain;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;

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
public abstract class AbstractDataStreamProcess<D extends DataSourceObject, E extends DataObject>
		extends OperatorChain {

	/* The global logger for this class */
	static Logger log = LoggerFactory
			.getLogger(AbstractDataStreamProcess.class);

	/* The input port should provide a example set */
	final InputPort input;

	final OutputPort dataStream;
	final InputPort outputStream;

	List<DataObject> resultBuffer = new ArrayList<DataObject>();

	/**
	 * @param description
	 */
	public AbstractDataStreamProcess(OperatorDescription description,
			String title, String portName, Class<D> clazz, String innerPortNames) {
		super(description, title);
		input = getInputPorts().createPort(portName);

		dataStream = getSubprocess(0).getInnerSources().createPort(
				innerPortNames);
		outputStream = getSubprocess(0).getInnerSinks().createPort(
				innerPortNames);
	}

	/**
	 * @see com.rapidminer.operator.OperatorChain#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		resultBuffer.clear();

		List<Operator> nested = this.getImmediateChildren();
		log.debug("This StreamProcess has {} nested operators", nested.size());
		for (Operator op : nested) {
			log.debug("  op: {}", op);

			/*
			 * if (op instanceof DataStreamOperator) {
			 * log.debug("Resetting stream-operator {}", op);
			 * ((DataStreamOperator) op).reset(); }
			 */
		}

		log.debug("Starting some work in doWork()");
		DataSourceObject dataSource = input.getData(DataSourceObject.class);
		log.debug("input is a data-stream-source...");
		int i = 0;

		Data item = dataSource.readNext();
		while (item != null) {
			log.trace("Processing example {}", i);
			// DataObject datum = new DataObject( item );
			E datum = wrap(item);
			log.trace("Wrapped data-object is: {}", datum);
			dataStream.deliver(datum);
			getSubprocess(0).execute();
			inApplyLoop();
			i++;

			try {
				DataObject processed = outputStream.getData(DataObject.class);
				if (processed != null) {
					log.debug("Adding processed data item: {}",
							processed.getWrappedDataItem());
					resultBuffer.add(processed);
				}
			} catch (Exception e) {
				log.error("Failed to retrieve processed data-item: {}",
						e.getMessage());
			}

			item = dataSource.readNext();
		}

		log.debug("Collected {} data items as result.");
		log.debug("doWork() is finished.");
	}

	public abstract E wrap(Data item);

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		return ParameterTypeFinder.getParameterTypes(getClass());
	}
}