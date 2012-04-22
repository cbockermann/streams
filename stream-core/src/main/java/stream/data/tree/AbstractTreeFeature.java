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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import stream.AbstractProcessor;
import stream.data.Data;
import stream.data.TreeNode;

public abstract class AbstractTreeFeature extends AbstractProcessor implements
		TreeFeature {
	String id;
	String missingValue = "null";

	String[] keys = null;

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		List<String> treeKeys = new ArrayList<String>();
		Iterator<String> it;
		if (keys != null) {
			it = Arrays.asList(keys).iterator();
		} else {
			it = data.keySet().iterator();
		}

		while (it.hasNext()) {
			String key = it.next();
			Serializable value = data.get(key);
			if (value != null && (value instanceof TreeNode)) {
				treeKeys.add(key);
			}
		}

		if (treeKeys.isEmpty())
			return data;

		for (String treeKey : treeKeys) {
			processTree(treeKey, data);
		}

		return data;
	}

	public void processTree(String treeKey, Data data) {
		TreeNode tree = (TreeNode) data.get(treeKey);
		String featureName = this.createFeatureKey(treeKey);
		Serializable featureValue = this.compute(tree);
		data.put(featureName, featureValue);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the keys
	 */
	public String[] getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	/**
	 * @return the missingValue
	 */
	public String getMissing() {
		return missingValue;
	}

	/**
	 * @param missingValue
	 *            the missingValue to set
	 */
	public void setMissing(String missingValue) {
		this.missingValue = missingValue;
	}

	@Override
	public String createFeatureKey(String inputKey) {
		return getId() + "(" + inputKey + ")";
	}

	@Override
	public abstract Serializable compute(TreeNode tree);
}
