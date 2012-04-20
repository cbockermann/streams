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

import java.util.Iterator;
import java.util.List;

import stream.data.Data;
import stream.data.DataFactory;
import stream.plugin.data.DataObject;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeString;

/**
 * @author chris
 * 
 */
public class ExampleSet2Array extends Operator {

	public final static String KEY_PARAMETER = "Key";
	public final static String INCLUDE_SPECIAL = "include special attributes";

	final InputPort input = getInputPorts().createPort("example set");
	final OutputPort output = getOutputPorts().createPort("data item");

	/**
	 * @param description
	 */
	public ExampleSet2Array(OperatorDescription description) {
		super(description);
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		String key = getParameterAsString(KEY_PARAMETER);
		boolean includeSpecial = getParameterAsBoolean(INCLUDE_SPECIAL);

		ExampleSet exampleSet = input.getData(ExampleSet.class);
		Attributes attributes = exampleSet.getAttributes();

		int columns = attributes.size();
		if (includeSpecial)
			columns += attributes.specialSize();

		double[] array = new double[exampleSet.size() * columns];

		int row = 0;
		for (Example example : exampleSet) {

			Iterator<Attribute> cols;
			if (includeSpecial) {
				cols = attributes.allAttributes();
			} else {
				cols = attributes.iterator();
			}

			int c = 0;
			int offset = row * columns;
			while (cols.hasNext()) {
				double value = example.getValue(cols.next());
				array[offset + c] = value;
				c++;
			}

			row++;
		}

		Data item = DataFactory.create();
		item.put(key, array);

		output.deliver(new DataObject(item));
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add(new ParameterTypeString(KEY_PARAMETER, "", false));
		types.add(new ParameterTypeBoolean(INCLUDE_SPECIAL, "", false));
		return types;
	}
}