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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import stream.AbstractProcessor;
import stream.Data;
import stream.data.TreeNode;

public class TreeFeatures extends AbstractProcessor {

	List<TreeFeature> features = new ArrayList<TreeFeature>();

	/**
	 * @see stream.DataProcessor#process(stream.Data)
	 */
	@Override
	public Data process(Data data) {

		List<String> treeKeys = new ArrayList<String>();

		for (String key : data.keySet()) {
			if (data.get(key) instanceof TreeNode)
				treeKeys.add(key);
		}

		if (treeKeys.isEmpty())
			return data;

		for (String treeKey : treeKeys)
			processTree(treeKey, data);

		return data;
	}

	public void add(TreeFeature feature) {
		features.add(feature);
	}

	public void processTree(String treeKey, Data data) {
		for (TreeFeature feat : features) {
			TreeNode tree = (TreeNode) data.get(treeKey);
			String featureName = feat.createFeatureKey(treeKey);
			Serializable featureValue = feat.compute(tree);
			data.put(featureName, featureValue);
		}
	}
}