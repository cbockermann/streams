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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.data.DataFactory;
import stream.plugin.data.DataObject;

import com.rapidminer.beans.OperatorBean;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Processing.Transformation.Data")
public class Array2ExampleSet extends OperatorBean {

	static Logger log = LoggerFactory.getLogger(Array2ExampleSet.class);

	public final static String DATA_KEY_PARAMETER = "Key";
	public final static String ROWS_PARAMETER = "Rows";

	final InputPort input = getInputPorts().createPort("data item");
	final OutputPort output = getOutputPorts().createPort("example set");
	final OutputPort passThroughPort = getOutputPorts().createPort("data item");

	final ExampleSetFactory exampleSetFactory = new ExampleSetFactory();

	String key;
	Integer rows;
	Boolean transpose = false;

	/**
	 * @param description
	 */
	public Array2ExampleSet(OperatorDescription description) {
		super(description);
		acceptsInput(DataObject.class);
		producesOutput(ExampleSet.class);

		getTransformer().addPassThroughRule(input, passThroughPort);
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	@Parameter(required = true, defaultValue = "Data", description = "The attribute key (name) of the attribute containing the array to convert.")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the rows
	 */
	public Integer getRows() {
		return rows;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	@Parameter(required = true, description = "The number of rows that this array contains, used to compute the columns.")
	public void setRows(Integer rows) {
		this.rows = rows;
	}

	/**
	 * @return the transpose
	 */
	public Boolean getTranspose() {
		return transpose;
	}

	/**
	 * @param transpose
	 *            the transpose to set
	 */
	@Parameter(required = false, defaultValue = "false", description = "Whether the table should be created transposed.")
	public void setTranspose(Boolean transpose) {
		this.transpose = transpose;
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		// this init-call will inject all parameters...
		init();

		DataObject event = input.getData(DataObject.class);

		Serializable data = event.get(key);
		if (data == null) {
			throw new UserError(this, "No data found for key '" + key + "'!",
					-1);
		}

		if (!data.getClass().isArray()) {
			throw new UserError(this, new Exception(""), -1);
		}

		int arrayLength = Array.getLength(data);
		if (arrayLength % rows > 0) {
			throw new UserError(this,
					"Number of rows does not properly divide array-length!", -1);
		}

		List<Data> rows = expand(event, this.rows, key, false);

		ExampleSet exampleSet = exampleSetFactory.createExampleSet(rows);
		output.deliver(exampleSet);
		passThroughPort.deliver(event);
	}

	public static List<Data> expand(Data event, int numberOfPixels,
			String dataKey, boolean transpose) {

		List<Data> pixels = new ArrayList<Data>(numberOfPixels);

		Serializable value = event.get(dataKey);
		if (value == null) {
			return pixels;
		}

		if (!value.getClass().isArray()) {
			throw new RuntimeException("Object for key '" + dataKey
					+ "' is not an array!");
		}

		int rowLength = numberOfPixels;
		int colLength = Array.getLength(value) / rowLength;

		if (transpose) {
			int t = colLength;
			colLength = rowLength;
			rowLength = t;
		}

		DecimalFormat df = new DecimalFormat(dataKey + "_000");

		for (int i = 0; i < rowLength; i++) {
			Data pixel = DataFactory.create();
			pixel.put("@row", "" + i);

			for (int j = 0; j < colLength; j++) {
				String key = df.format(j);
				String val;
				if (!transpose)
					val = Array.get(value, i * colLength + j) + "";
				else
					val = Array.get(value, j * rowLength + i) + "";
				try {
					Double d = new Double(val);
					pixel.put(key, d);
				} catch (Exception e) {
					pixel.put(key, val);
				}
			}

			pixels.add(pixel);
		}

		return pixels;
	}
}