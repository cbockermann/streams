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

import java.awt.BorderLayout;

import javax.swing.JFrame;

import stream.data.Data;

import com.rapidminer.BreakpointListener;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterTypeBoolean;

import fact.FactViewerPanel;
import fact.plugin.FactEventObject;

/**
 * @author chris
 *
 */
public class FactViewerOperator extends AbstractFactEventOperator {

	public final static String PAUSE_AFTER_SHOW = "Pause for each event";
	
	boolean pause = true;
	JFrame frame;
	FactViewerPanel eventPanel;
	
	/**
	 * @param description
	 */
	public FactViewerOperator(OperatorDescription description) {
		super(description);
		this.addParameterType( new ParameterTypeBoolean( PAUSE_AFTER_SHOW, "Add a breakpoint for each event.", true ) );
	}

	
	public void doWork() throws OperatorException {
		super.doWork();

		pause = getParameterAsBoolean( PAUSE_AFTER_SHOW );
		this.setBreakpoint( BreakpointListener.BREAKPOINT_AFTER, pause );
	}
	
	
	/**
	 * @see fact.plugin.operators.AbstractFactEventOperator#process(fact.plugin.FactEventObject)
	 */
	@Override
	public FactEventObject process(FactEventObject event) throws Exception {
		
		if( frame == null || eventPanel == null ){
			frame = new JFrame( "View Event" );
			eventPanel = new FactViewerPanel();
			frame.getContentPane().setLayout( new BorderLayout() );
			frame.getContentPane().add( eventPanel, BorderLayout.CENTER );
		}
		
		Data data = event.getWrappedDataItem();
		eventPanel.setEvent( data );
		
		frame.setAlwaysOnTop( true );
		frame.setVisible( true );
		return event;
	}
}