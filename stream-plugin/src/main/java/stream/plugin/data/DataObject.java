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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import com.rapidminer.operator.AbstractIOObject;
import com.rapidminer.operator.Annotations;
import com.rapidminer.operator.ResultObject;


/**
 * 
 * This class implements a wrapper to wrap simple Data objects into
 * IOObjects to be passed along within RapidMiner.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class DataObject 
	extends AbstractIOObject
	implements stream.data.Data, ResultObject {
	
	/** The unique class ID */
	private static final long serialVersionUID = -358985628975633770L;
	stream.data.Data data;
	
	
	public DataObject( stream.data.Data data ){
		this.data = data;
	}

	public void clear() {
		data.clear();
	}

	public boolean containsKey(Object arg0) {
		return data.containsKey(arg0);
	}

	public boolean containsValue(Object arg0) {
		return data.containsValue(arg0);
	}

	public Set<java.util.Map.Entry<String, Serializable>> entrySet() {
		return data.entrySet();
	}

	public boolean equals(Object arg0) {
		return data.equals(arg0);
	}

	public Serializable get(Object arg0) {
		return data.get(arg0);
	}

	public int hashCode() {
		return data.hashCode();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public Set<String> keySet() {
		return data.keySet();
	}

	public Serializable put(String arg0, Serializable arg1) {
		return data.put(arg0, arg1);
	}

	public void putAll(Map<? extends String, ? extends Serializable> arg0) {
		data.putAll(arg0);
	}

	public Serializable remove(Object arg0) {
		return data.remove(arg0);
	}

	public int size() {
		return data.size();
	}

	public Collection<Serializable> values() {
		return data.values();
	}

	@Override
	public Annotations getAnnotations() {
		return new Annotations();
	}
	
	public stream.data.Data getWrappedDataItem(){
		return data;
	}
	
	public void setWrappedDataItem( stream.data.Data item ){
		this.data = item;
	}
	
	public String toString(){
		return "Data Item[  " + data + "  ]";
	}

	
	/**
	 * @see com.rapidminer.operator.ResultObject#getName()
	 */
	@Override
	public String getName() {
		return "Data Item";
	}
	

	/**
	 * @see com.rapidminer.operator.ResultObject#toResultString()
	 */
	@Override
	public String toResultString() {
		StringBuffer s = new StringBuffer( "<html><head><style> body, p, div, table, td { font-style: normal; } </style></head><body>" );
		s.append( "<table>" );
		for( String key : data.keySet() ){
			s.append( "<tr>" );
			s.append( "<td><b>" + key + "</b></td>" );
			s.append( "<td><code>" );
			Serializable val = data.get( key );
			if( val.getClass().isArray() ){
				Class<?> type = val.getClass().getComponentType();
				int len = Array.getLength( val );
				s.append( type.getName() + "[" + len + "]" );
				
				try {
					s.append( "  (values: " );
					for( int i = 0; i < len && i < 4; i++ ){
						Object o = Array.get( val, i );
						if( o == null )
							s.append( "null" );
						else
							s.append( o.toString() );
						
						if( i + 1 < len )
							s.append( ", " );
					}
					s.append( "...)" );
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} else {
				s.append( val.toString() );
			}
			s.append( "</code></td></tr>\n" );
		}
		s.append( "</table>" );
		s.append( "</body></html>" );
		
		return s.toString();
	}

	/**
	 * @see com.rapidminer.operator.ResultObject#getResultIcon()
	 */
	@Override
	public Icon getResultIcon() {
		return null;
	}

	/**
	 * @see com.rapidminer.operator.ResultObject#getActions()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List getActions() {
		return new ArrayList<Object>();
	}
}