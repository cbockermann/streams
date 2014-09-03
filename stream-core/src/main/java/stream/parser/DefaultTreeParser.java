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
package stream.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stream.AbstractProcessor;
import stream.Data;
import stream.data.TreeNode;
import stream.util.parser.ParseException;
import stream.util.parser.Parser;

public class DefaultTreeParser extends AbstractProcessor implements
		Parser<TreeNode> {
	int pos = 0;
	String data = "";
	String sourceKey = "sql";
	Map<String, String> defaults = new HashMap<String, String>();

	public DefaultTreeParser() {
		this("sql");
	}

	public DefaultTreeParser(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	/**
	 * @see stream.io.Parser#parse(java.lang.String)
	 */
	@Override
	public TreeNode parse(String input) throws ParseException {
		data = input;
		pos = 0;
		try {
			return readTreeNode();
		} catch (Exception e) {
			throw new ParseException(e.getMessage());
		}
	}

	protected TreeNode readTreeNode() throws Exception {

		skip();
		read("(");
		String node = readToken(new char[] { '(', ')' });
		skip();
		List<TreeNode> children = new ArrayList<TreeNode>();
		while (startsWith("(")) {
			children.add(readTreeNode());
			skip();
		}

		skip();
		read(")");
		return new DefaultTreeNode(node, null, children);
	}

	@Override
	public Data process(Data data) {

		if (sourceKey != null && data.get(sourceKey) != null) {
			try {
				String source = data.get(sourceKey).toString();
				TreeNode tree = parse(source);
				if (tree != null) {
					data.put("@tree", tree);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return data;
	}

	protected String readToken() throws Exception {

		skip();
		StringBuffer tok = new StringBuffer();
		while (pos < data.length() && !Character.isWhitespace(data.charAt(pos))) {
			tok.append(data.charAt(pos++));
		}

		return tok.toString();
	}

	protected String readToken(char[] delimiters) throws Exception {
		skip();
		StringBuffer tok = new StringBuffer();
		while (pos < data.length() && !contains(delimiters, data.charAt(pos))) {
			tok.append(data.charAt(pos++));
		}

		return tok.toString();
	}

	protected boolean contains(char[] set, char ch) {
		for (char c : set)
			if (c == ch)
				return true;
		return false;
	}

	protected boolean startsWith(String start) {

		String remain = null;
		if (pos < data.length())
			remain = data.substring(pos);

		return remain != null && remain.startsWith(start);
	}

	protected String read(String expected) throws Exception {
		if (data.substring(pos).startsWith(expected)) {
			pos += expected.length();
			return expected;
		} else
			throw new Exception("Could not read '" + expected
					+ "' from string: " + data.substring(pos));
	}

	protected int skip() {
		int skipped = 0;
		while (pos < data.length() && Character.isWhitespace(data.charAt(pos))) {
			pos++;
			skipped++;
		}
		return skipped;
	}

	public class DefaultTreeNode implements TreeNode {

		/** The unique class ID */
		private static final long serialVersionUID = 5603730461142746019L;

		String label;
		TreeNode parent;
		Collection<TreeNode> children;

		public DefaultTreeNode(String label, TreeNode parent) {
			this(label, parent, new ArrayList<TreeNode>());
		}

		public DefaultTreeNode(String label, TreeNode parent,
				Collection<TreeNode> siblings) {
			this.label = label;
			this.parent = parent;
			this.children = siblings;
		}

		@Override
		public TreeNode getParent() {
			return parent;
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public void setLabel(String label) {
			this.label = label;
		}

		@Override
		public boolean isLeaf() {
			return children == null || children.isEmpty();
		}

		@Override
		public Collection<TreeNode> children() {
			return children;
		}

		@Override
		public void addChild(TreeNode node) {
			if (children == null)
				children = new ArrayList<TreeNode>();
			children.add(node);
		}

		public String toString() {
			StringBuffer s = new StringBuffer();
			s.append("( ");
			s.append(label);
			for (TreeNode ch : children()) {
				s.append(" ");
				s.append(ch.toString());
			}

			s.append(" )");
			return s.toString();
		}
	}

	public static void main(String[] args) throws Exception {

		String treeString = "( ROOT ( A1 ) ( A2 ) )";
		DefaultTreeParser parser = new DefaultTreeParser();
		TreeNode tree = parser.parse(treeString);

		System.out.println("the tree is: " + tree);
	}

	/**
	 * @see stream.util.parser.Parser#getDefaults()
	 */
	@Override
	public Map<String, String> getDefaults() {
		return defaults;
	}

	/**
	 * @see stream.util.parser.Parser#setDefaults(java.util.Map)
	 */
	@Override
	public void setDefaults(Map<String, String> defaults) {
		this.defaults = defaults;
	}
}