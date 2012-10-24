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
package stream.data.tree;

import java.util.LinkedHashMap;
import java.util.Map;

import stream.Data;
import stream.data.TreeNode;

public class TreeEdges extends TreeFeatures {

	/**
	 * @see stream.data.tree.TreeFeatures#processTree(java.lang.String, stream.Data)
	 */
	@Override
	public void processTree(String treeKey, Data data) {
		
		TreeNode tree = (TreeNode) data.get( treeKey );
		
		Map<String,Integer> edges = getEdges( tree );
		for( String key : edges.keySet() ){
			
			String fk = treeKey; // feature key => if treeKey is an annotation, we need to remove the leading '@'
			if( fk.startsWith( "@" ) )
				fk = fk.substring( 1 );
			
			data.put( fk + ":edge[" + key + "]", edges.get( key ) );
		}
	}
	
	
	public Map<String,Integer> getEdges( TreeNode node ){
		
		Map<String,Integer> edges = new LinkedHashMap<String,Integer>();
		
		for( TreeNode ch : node.children() ){
			
			String edge = node.getLabel().trim() + "->" + ch.getLabel().trim();
			Integer count = edges.get( edge );
			if( count == null ){
				count = 1;
			} else
				count += 1;
			
			edges.put( edge, count );
			
			edges = add( edges, getEdges( ch ) );
		}
		
		return edges;
	}
	
	
	public Map<String,Integer> add( Map<String,Integer> m1, Map<String,Integer> m2 ){
		
		for( String key : m2.keySet() ){
			if( m1.containsKey( key ) )
				m1.put( key, m1.get( key ) + m2.get( key ) );
			else
				m1.put( key, m2.get( key ) );
		}
		
		return m1;
	}
}