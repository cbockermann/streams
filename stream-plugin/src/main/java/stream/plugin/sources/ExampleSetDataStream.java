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
package stream.plugin.sources;

import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.plugin.data.DataSourceObject;

import com.rapidminer.beans.OperatorBean;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(name = "ExampleSet DataStream", group = "Data Stream.Sources", text = "Creates a data stream from a single example set.")
public class ExampleSetDataStream extends OperatorBean {

	protected final InputPort input = getInputPorts().createPort("example set",
			ExampleSet.class);
	protected final OutputPort output = getOutputPorts().createPort("stream");

	boolean shuffle = false;
	Integer repeat = 1;

	/**
	 * @param description
	 */
	public ExampleSetDataStream(OperatorDescription description) {
		super(description);
	}

	/**
	 * @return the shuffle
	 */
	public boolean isShuffle() {
		return shuffle;
	}

	/**
	 * @param shuffle
	 *            the shuffle to set
	 */
	@Parameter(required = false, description = "Whether the example set should be processed in order or shuffled")
	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	/**
	 * @return the repeat
	 */
	public Integer getRepeat() {
		if (repeat == null)
			repeat = 1;
		return repeat;
	}

	/**
	 * @param repeat
	 *            the repeat to set
	 */
	@Parameter(required = true, defaultValue = "1", description = "Specify how many times the example set should be processed as a stream")
	public void setRepeat(Integer repeat) {
		this.repeat = repeat;
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		ExampleSet exampleSet = input.getData(ExampleSet.class);
		if (exampleSet != null) {
			ExampleSetDataStreamWrapper wrapper = new ExampleSetDataStreamWrapper(
					exampleSet, isShuffle(), getRepeat());
			output.deliver(new DataSourceObject(wrapper));
		}
	}
}