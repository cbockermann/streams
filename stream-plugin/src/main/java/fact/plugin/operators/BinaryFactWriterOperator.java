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

import java.io.File;
import java.util.List;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeFile;

import fact.io.BinaryFactWriter;
import fact.plugin.FactEventObject;

/**
 * @author chris
 *
 */
public class BinaryFactWriterOperator extends Operator {

	public final static String OUTPUT_FILE = "Output file";
	final InputPort input = getInputPorts().createPort( "evt fact event" );

	/**
	 * @param description
	 */
	public BinaryFactWriterOperator(OperatorDescription description) {
		super(description);
		this.acceptsInput( FactEventObject.class );
	}

	/* (non-Javadoc)
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
	
		File file = getParameterAsFile( OUTPUT_FILE );
		
		BinaryFactWriter writer = new BinaryFactWriter();
		writer.setFile( file.getAbsolutePath() );

		FactEventObject event = input.getData( FactEventObject.class );
		writer.process( event );
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add( new ParameterTypeFile( OUTPUT_FILE, "", "dat", false ) );
		return types;
	}
}