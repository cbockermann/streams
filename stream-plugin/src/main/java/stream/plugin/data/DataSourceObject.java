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
package stream.plugin.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.DataStream;

import com.rapidminer.operator.AbstractIOObject;
import com.rapidminer.operator.Annotations;

/**
 * @author chris
 *
 */
public class DataSourceObject extends AbstractIOObject {

	/** The unique class ID */
	private static final long serialVersionUID = 2191156531359947979L;

	static Logger log = LoggerFactory.getLogger( DataSourceObject.class );
	
	Annotations annotations = new Annotations();
	transient DataStream stream;
	
	
	public DataSourceObject( DataStream stream ){
		this.stream = stream;
	}
	
	
	/**
	 * @see com.rapidminer.operator.IOObject#getAnnotations()
	 */
	@Override
	public Annotations getAnnotations() {
		return annotations;
	}

	
	public Data readNext(){
		try {
			return stream.readNext();
		} catch (Exception e) {
			log.error( "Failed to read from data-stream: {}", e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();
			return null;
		}
	}
	
	
	public DataObject wrap( Data item ){
		return new DataObject( item );
	}
}