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
package stream.data.tree;

import java.io.Serializable;
import java.util.Collection;

import stream.data.TreeNode;


public class CountLeaves 
	extends AbstractTreeFeature 
{
	/**
	 * @see stream.data.tree.TreeFeature#createFeatureKey(java.lang.String)
	 */
	@Override
	public String createFeatureKey(String inputKey) {
		return "leafCount(" + inputKey + ")";
	}

	@Override
	public Serializable compute(TreeNode tree) {
		return getNumberOfLeaves( tree );
	}
	
	public Integer getNumberOfLeaves( TreeNode tree ){
		if( tree.isLeaf() )
			return 1;
		
		Integer sum = 0;
		for( TreeNode ch : tree.children() )
			sum += getNumberOfLeaves( ch );
				
		return sum;
	}
	
	
	protected Integer max( Collection<Integer> input ){
		Integer max = null;
		for( Integer i : input ){
			if( max == null || i > max )
				max = i;
		}
		return max;
	}
}