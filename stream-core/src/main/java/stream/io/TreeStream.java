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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.data.Data;
import stream.data.DataFactory;
import stream.data.TreeNode;
import stream.parser.DefaultTreeParser;

/**
 * A tree stream is a simple entity that reads and parses trees, one tree per
 * line. The trees are expected to be in the default NLP format:
 * 
 * <pre>
 *    ( ROOT ( A1 ( A1.1 ) ( A1.2 ) ) ( A2 ) )
 * </pre>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * @deprecated
 */
public class TreeStream implements DataStream {

	static Logger log = LoggerFactory.getLogger(TreeStream.class);
	String treeAttribute = "tree";
	BufferedReader reader;
	DefaultTreeParser treeParser;
	Map<String, Class<?>> attributes = new LinkedHashMap<String, Class<?>>();
	final List<Processor> processors = new ArrayList<Processor>();

	public TreeStream(URL url) throws Exception {
		reader = new BufferedReader(new InputStreamReader(url.openStream()));
		treeParser = new DefaultTreeParser();
		attributes.put("tree", TreeNode.class);
	}

	public String getTreeAttribute() {
		return treeAttribute;
	}

	public void setTreeAttribute(String treeAttribute) {
		this.treeAttribute = treeAttribute;
	}

	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return attributes;
	}

	/**
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return readNext(DataFactory.create());
	}

	/**
	 * @see stream.io.DataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readNext(Data datum) throws Exception {
		String line = reader.readLine();

		// skip comment lines
		//
		while (line != null && line.startsWith("#"))
			line = reader.readLine();

		if (line == null)
			return null;

		TreeNode tree = treeParser.parse(line);

		datum.put(treeAttribute, tree);
		return datum;
	}

	@Override
	public List<Processor> getPreprocessors() {
		return processors;
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
		try {
			reader.close();
		} catch (Exception e) {
			log.error("Failed to properly close reader: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}

	/**
	 * @see stream.io.DataStream#init()
	 */
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub

	}
}