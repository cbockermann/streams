/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
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
package stream.io;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import stream.data.Data;

public class SparseDataWriter extends DataStreamWriter {

	/* This map provides a mapping of features to indexes 
	 * features with a numeric (integer) name will be mapped to their value directly */
	Map<String,Integer> indexes = new HashMap<String,Integer>();
	Integer largestIndex = 0;

	public SparseDataWriter(OutputStream out) {
		super(out);
	}

	@Override
	public void writeHeader(Data datum) {
	}

	@Override
	public void write(Data datum) {

		StringBuffer annotation = new StringBuffer();
		
		for( String key : datum.keySet() ){

			try {
				String value = datum.get( key ).toString();
				if( value.indexOf( " " ) > 0 ) {
					value = "'" + value.replaceAll( "'", "\\\'" ) + "'";
				}
				
				p.print( " " );
				p.print( key );
				p.print( ":" );
				p.print( value );
			} catch (Exception e) {
				e.printStackTrace();
				log.warn( "Skipping non-numerical feature '{}'", key );
				annotation.append( " " );
				annotation.append( key );
				annotation.append( ":'" );
				annotation.append( lineEscape( datum.get( key ) ) );
				annotation.append( "'" );
			}
		}
		if( annotation.length() > 0 ){
			p.print( " #" );
			p.print( annotation.toString() );
		}
			
		p.println();
	}
	
	protected String lineEscape( Serializable val ){
		if( val == null )
			return "";
		
		String str = val.toString();
		return str.replaceAll( "'", "," ).replaceAll( "\\n", " " );
	}
}