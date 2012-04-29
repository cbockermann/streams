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
package fact.plugin.ui;

import java.awt.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidminer.gui.renderer.AbstractRenderer;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.report.Reportable;

/**
 * @author chris
 *
 */
public class FactEventStreamRenderer 
	extends AbstractRenderer 
{
	static Logger log = LoggerFactory.getLogger( FactEventStreamRenderer.class );

	/* (non-Javadoc)
	 * @see com.rapidminer.gui.renderer.Renderer#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.rapidminer.gui.renderer.Renderer#getVisualizationComponent(java.lang.Object, com.rapidminer.operator.IOContainer)
	 */
	@Override
	public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {

		
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.rapidminer.gui.renderer.Renderer#createReportable(java.lang.Object, com.rapidminer.operator.IOContainer, int, int)
	 */
	@Override
	public Reportable createReportable(Object renderable,
			IOContainer ioContainer, int desiredWidth, int desiredHeight) {
		// TODO Auto-generated method stub
		return null;
	}
	

	/**
	 * @see com.rapidminer.gui.renderer.DefaultTextRenderer#getVisualizationComponent(java.lang.Object, com.rapidminer.operator.IOContainer)
	@Override
	public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {
		
		if( renderable instanceof FactEventObject ){
			
			log.info( "Need to render FactEventObject!" );
			
			FactEventObject factEvent = (FactEventObject) renderable;
			Data data = factEvent.getWrappedDataItem();
		} else {
			log.info( "Don't know how to render object {}", renderable );
		}
		
		return super.getVisualizationComponent(renderable, ioContainer);
	}
	 */
}