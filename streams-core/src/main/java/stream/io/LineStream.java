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
package stream.io;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.DataFactory;
import stream.util.parser.Parser;
import stream.util.parser.ParserGenerator;

/**
 * This is a very simple stream that just reads from a URL line-by-line. The
 * content of the line is stored in the attribute determined by the
 * <code>getKey()</code> method of this instance. By default the key
 * <code>LINE</code> is used.
 * 
 * It also supports the specification of a simple format string that can be used
 * to create a generic parser to populate additional fields of the data item
 * read from the stream.
 * 
 * The parser format is:
 * 
 * <pre>
 *       %{IP} [%{DATE}] "%{URL}"
 * </pre>
 * 
 * This will create a parser that is able to read line in the format
 * 
 * <pre>
 *       127.0.0.1 [2012/03/14 12:03:48 +0100] "http://example.com/index.html"
 * </pre>
 * 
 * The outcoming data item will have the attribute <code>IP</code> set to
 * "127.0.0.1" and the <code>DATE</code> attribute set to
 * "2012/03/14 12:03:48 +0100". The <code>URL</code> attribute will be set to
 * <code>http://example.com/index.html</code>.
 * 
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Sources")
public class LineStream extends AbstractLineStream {

	static Logger log = LoggerFactory.getLogger(LineStream.class);

	String key = "LINE";
	String format = null;
	Parser<Map<String, String>> parser = null;

	public LineStream(SourceURL url) throws Exception {
		super(url);
	}

	public LineStream(InputStream in) throws Exception {
		super(in);
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	@Parameter(required = false, defaultValue = "LINE", description = "")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	@Parameter(required = false, description = "The format how to parse each line. Elements like %(KEY) will be detected and automatically populated in the resulting items.")
	public void setFormat(String format) {
		this.format = format;
		try {
			ParserGenerator pg = new ParserGenerator(format);
			parser = pg.newParser();
		} catch (Exception e) {
			throw new RuntimeException("Failed to create parser for format: " + e.getMessage());
		}
	}

	/**
	 * @see stream.io.AbstractStream#readItem(stream.Data)
	 */
	@Override
	public synchronized Data readNext() throws Exception {

		Data instance = DataFactory.create();

		String line = readLine();
		if (line == null)
			return null;

		instance.put(key, line);

		if (parser != null) {
			Map<String, String> map = parser.parse(line);
			for (String key : map.keySet()) {
				instance.put(key, map.get(key));
			}
		}

		return instance;
	}
}