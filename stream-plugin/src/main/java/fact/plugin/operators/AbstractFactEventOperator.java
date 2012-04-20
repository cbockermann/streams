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

import java.util.ArrayList;
import java.util.List;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;

import fact.plugin.FactEventObject;

/**
 * @author chris
 *
 */
public abstract class AbstractFactEventOperator extends Operator {

	final InputPort input = getInputPorts().createPort( "evt fact event" );
	final OutputPort output = getOutputPorts().createPort( "evt fact event" );
	final List<ParameterType> types = new ArrayList<ParameterType>();
	
	/**
	 * @param description
	 */
	public AbstractFactEventOperator(OperatorDescription description) {
		super(description);
		this.acceptsInput( FactEventObject.class );
		this.producesOutput( FactEventObject.class );
	}

	
	public void doWork() throws OperatorException {
		try {
			FactEventObject event = input.getData( FactEventObject.class );
			FactEventObject processed = process( event );
			output.deliver( processed );
		} catch (Exception e) {
			throw new UserError( this, e, -1 );
		}
	}

	public void addParameterType( ParameterType type ){
		types.add( type );
	}
	
	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> pt = super.getParameterTypes();
		pt.addAll( types );
		return pt;
	}


	public abstract FactEventObject process( FactEventObject event ) throws Exception;
}
