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

import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Sources")
public class CsvStream extends AbstractLineStream {
	static Logger log = LoggerFactory.getLogger(CsvStream.class);

	final ArrayList<String> columns = new ArrayList<String>();
	String[] keys;
	String splitExpression = "(;|,)";
	long lineNo = 0L;
	boolean header = true;

	/**
	 * @param url
	 * @throws Exception
	 */
	public CsvStream(SourceURL url) throws Exception {
		super(url);
	}

	public CsvStream(InputStream in) throws Exception {
		super(in);
		this.splitExpression = "(;|,)";
		log.debug("Split expression is: {}", splitExpression);
	}

	public CsvStream(InputStream in, String splitter) throws Exception {
		this(in, Charset.defaultCharset(), splitter);
	}

	public CsvStream(InputStream in, Charset charset, String splitter)
			throws Exception {
		super(in);
		this.splitExpression = splitter;
		log.debug("Split expression is: {}", splitExpression);
	}

	public CsvStream(SourceURL url, String splitExp) throws Exception {
		super(url);
		this.url = url;
		this.splitExpression = splitExp;
	}

	/**
	 * @return the header
	 */
	public boolean isHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	@Parameter(description = "Determines whether the first line should be used as header (column names), defaults to 'true'.", required = false)
	public void setHeader(boolean header) {
		this.header = header;
	}

	@Parameter(name = "separator", required = true, defaultValue = "(;|,)")
	public void setSeparator(String separator) {
		splitExpression = separator;
	}

	public String getSeparator() {
		return splitExpression;
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
		if (this.keys != null && this.keys.length > 0)
			this.header = false;
	}

	/**
	 * @see stream.io.Stream#read()
	 */
	public Data readNext() throws Exception {

		String line = readLine();
		if (line == null)
			return null;

		// if we are reading the first "real" (non-comment) line, and the
		// 'header' parameter is set, we regard the line as header
		//
		if (lineNo == 0L) {
			if (header) {
				String[] token = line.split(splitExpression);
				for (int i = 0; i < token.length; i++) {

					String col = token[i];
					if (col.startsWith("\"") && col.endsWith("\"")) {
						col = col.substring(col.length() - 1).substring(1);
					}

					columns.add(col);
				}

				// we advance to the next line for real data if asked for
				// reading
				// the header from the first line
				//
				line = readLine();
			}

			// the 'keys' parameter can be used to override some of the keys
			//
			if (keys != null) {
				for (int i = 0; i < keys.length; i++) {
					if (i < columns.size())
						columns.set(i, keys[i]);
					else
						columns.add(keys[i]);
				}
			}
		}
		lineNo++;

		if (line == null)
			return null;

		final Data item = DataFactory.create();
		final String[] tok = line.split(splitExpression);

		for (int i = 0; i < tok.length; i++) {
			String key;

			if (i >= columns.size()) {
				key = "column:" + i;
				columns.add(key);
			} else {
				key = columns.get(i);
			}

			Serializable value;

			try {
				// a quoted string is always treated as a plain
				// string type
				//
				if (tok[i].startsWith("\"")) {
					//
					// remove surrounding quotes from the value
					//
					value = removeQuotes(tok[i]);

				} else {
					//
					// If no quotes are provided around the value, we
					// parse it into an integer or a double, depending
					// on the presence of a decimal point
					//
					if (tok[i].indexOf(".") > 0)
						value = new Double(tok[i]);
					else
						value = new Integer(tok[i]);
				}

			} catch (Exception e) {
				//
				// if parsing fails, we simply treat the value as a
				// plain string value
				//
				value = removeQuotes(tok[i]);
			}

			item.put(key, value);
		}

		return item;
	}

	/**
	 * This implementation of the readLine() method simply skips all comments,
	 * i.e. lines starting with the '#' character.
	 * 
	 * @see stream.io.AbstractLineStream#readLine()
	 */
	public String readLine() throws Exception {
		String line = reader.readLine();
		while (line != null && line.startsWith("#")) {
			line = reader.readLine();
		}

		return line;
	}

	protected String removeQuotes(String str) {
		if (str == null)
			return str;

		String s = str;
		if (s.startsWith("\""))
			s = s.substring(1);

		if (s.endsWith("\""))
			s = s.substring(0, s.length() - 1);

		return s;
	}
}