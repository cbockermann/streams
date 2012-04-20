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
package fact.plugin.operators;

import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;

import fact.plugin.FactEventObject;

/**
 * @author chris
 *
 */
public class AddPixelVectorOperator extends Operator {

	public final static String COLUMN_NAME = "Column name";
	
	InputPort input = getInputPorts().createPort( "example set" );
	InputPort eventInput = getInputPorts().createPort( "evt fact event" );
	
	OutputPort output = getOutputPorts().createPort( "evt fact event" );
	
	
	/**
	 * @param description
	 */
	public AddPixelVectorOperator(OperatorDescription description) {
		super(description);
	}


	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		
		ExampleSet exampleSet = input.getData( ExampleSet.class );
		FactEventObject event = eventInput.getData( FactEventObject.class );

		String column = getParameterAsString( COLUMN_NAME );
		
		float[] vector = new float[ exampleSet.size() ];
		
		Attribute col = exampleSet.getAttributes().get( column );
		for( int i = 0; i < vector.length; i++ ){
			Example example = exampleSet.getExample( i );
			vector[i] = new Float(example.getValue( col ));
		}
		
		event.put( column, vector );
		output.deliver( event );
	}


	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add( new ParameterTypeString( COLUMN_NAME, "", false ) );
		return types;
	}
}