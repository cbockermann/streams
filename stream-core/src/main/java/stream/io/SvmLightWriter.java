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
package stream.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import stream.data.Data;
import stream.data.DataUtils;

public class SvmLightWriter extends DataStreamWriter {

	/* This map provides a mapping of features to indexes 
	 * features with a numeric (integer) name will be mapped to their value directly */
	Map<String,Integer> indexes = new HashMap<String,Integer>();
	Integer largestIndex = 0;
	boolean includeAnnotations = true;
	
	public SvmLightWriter(){
	}
	
	public SvmLightWriter(OutputStream out) {
		super(out);
	}

	@Override
	public void writeHeader(Data datum) {
	}

	@Override
	public void write(Data datum) {

		Serializable label = datum.get( "@label" );
		if( label == null ){
			log.error( "SvmLightStreamWriter does only support writing labeled data!" );
			log.error( "Skipping datum {}", datum );
			return;
		}

		p.print( label );

		StringBuffer annotation = new StringBuffer();
		
		for( String key : DataUtils.getKeys( datum ) ){

			try {
				Double value = new Double( datum.get( key ).toString() );
				p.print( " " );
				Integer index = -1;
				if( key.matches( "\\d+" ) ){
					index = new Integer( key );
				} else {
					index = this.indexes.get( key );
					if( index == null ){
						index = largestIndex + 1;
						indexes.put( key, index );
					}
				}

				if( largestIndex < index )
					largestIndex = index;

				p.print( index );
				p.print( ":" );
				p.print( value );
			} catch (Exception e) {
				log.debug( "Skipping non-numerical feature '{}'", key );
				annotation.append( " " );
				annotation.append( key );
				annotation.append( ":'" );
				annotation.append( lineEscape( datum.get( key ) ) );
				annotation.append( "'" );
			}
		}
		if( includeAnnotations && annotation.length() > 0 ){
			p.print( " #" );
			p.print( annotation.toString() );
		}
			
		p.println();
	}
	
	
	
	
	
	
	/**
     * @return the includeAnnotations
     */
    public boolean isIncludeAnnotations()
    {
        return includeAnnotations;
    }

    /**
     * @param includeAnnotations the includeAnnotations to set
     */
    public void setIncludeAnnotations(boolean includeAnnotations)
    {
        this.includeAnnotations = includeAnnotations;
    }

    protected String lineEscape( Serializable val ){
		if( val == null )
			return "";
		
		String str = val.toString();
		return str.replaceAll( "'", "," ).replaceAll( "\\n", " " );
	}
	
	
	
	public void printMapping( File file ) throws Exception {
		FileOutputStream fos = new FileOutputStream( file );
		printMapping( fos );
		fos.close();
	}
	
	
	public void printMapping( OutputStream out ){
		PrintStream p = new PrintStream( out );
		p.println( "#feature,index" );
		for( String key : indexes.keySet() ){
			p.println( key + "," + indexes.get( key ) );
		}
		p.flush();
		p.close();
	}
}