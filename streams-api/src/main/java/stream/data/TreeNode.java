/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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
package stream.data;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * This interface defines a simple tree node.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public interface TreeNode extends Serializable {
	/**
	 * Returns the parent node of this node or <code>null</code> if this node is
	 * the root element.
	 * 
	 * @return
	 */
	public TreeNode getParent();

	/**
	 * Returns the label value of the node. Each node can be labeled with a
	 * string.
	 * 
	 * @return
	 */
	public String getLabel();

	/**
	 * This method sets the label value for the node.
	 * 
	 * @param label
	 */
	public void setLabel(String label);

	/**
	 * Checks whether this node is a leaf node, i.e. if it does not have any
	 * child nodes.
	 * 
	 * @return
	 */
	public boolean isLeaf();

	/**
	 * Returns the collection child nodes of this node. May return an empty
	 * collection for leaf nodes.
	 * 
	 * @return
	 */
	public Collection<TreeNode> children();

	/**
	 * Adds a child to this node.
	 * 
	 * @param node
	 */
	public void addChild(TreeNode node);
}