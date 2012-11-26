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
import java.nio.charset.Charset;
import java.util.LinkedList;

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

	String[] keys;
	String splitExpression = "(;|,)";
	LinkedList<String> buffer;

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

	@Parameter(name = "separator", required = true, defaultValue = "(;|,)")
	public void setSeparator(String separator) {
		splitExpression = separator;
	}

	public String getSeparator() {
		return splitExpression;
	}

	public String removeQuotes(String str) {
		String s = str;
		if (s.startsWith("\""))
			s = s.substring(1);

		if (s.endsWith("\""))
			s = s.substring(0, s.length() - 1);

		return s;
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

	public void readHeader() throws Exception {
		log.debug("Reading header, splitExpression is '{}'", splitExpression);
		if (buffer == null)
			buffer = new LinkedList<String>();

		String[] tok;

		if (keys == null || keys.length == 0) {

			String line = reader.readLine();
			log.debug("line is: {}", line);
			while (line.startsWith("#"))
				line = line.substring(1);

			tok = line.split(splitExpression);
			for (int i = 0; i < tok.length; i++) {
				tok[i] = removeQuotes(tok[i]);
			}
		} else
			tok = keys;

		String data = reader.readLine();
		while (data.startsWith("#")) {
			data = reader.readLine();
		}

		buffer.add(data);
		String dt[] = data.split(splitExpression);
		for (int i = 0; i < tok.length && i < dt.length; i++) {
			if (i < dt.length) {
				if (dt[i].matches("-{0,1}\\d*\\.\\d*E{0,1}-{0,1}\\d*"))
					attributes.put(tok[i], Double.class);
				else
					attributes.put(tok[i], String.class);
			}
		}
	}

	/**
	 * @see stream.io.Stream#read()
	 */
	public Data readNext() throws Exception {
		Data datum = DataFactory.create();

		String line = readLine();
		while (line != null && (line.trim().isEmpty() || line.startsWith("#"))) {
			if (line.startsWith("#")) {
				String dt[] = line.substring(1).split(splitExpression);
				for (int i = 0; i < dt.length; i++) {
					if (i < dt.length) {
						if (dt[i].matches("(-|\\+)?\\d*\\.\\d*"))
							attributes.put(dt[i], Double.class);
						else
							attributes.put(dt[i], String.class);
					}
				}
			}

			line = readLine();
		}

		if (line != null && !line.trim().equals("")) {
			String[] tok = line.split(splitExpression); // QuotedStringTokenizer.splitRespectQuotes(
			// line, ';');
			// //line.split( "(;|,)"
			// );
			int i = 0;
			for (String name : attributes.keySet()) {
				if (i < tok.length) {
					if (Double.class.equals(attributes.get(name))) {
						try {
							datum.put(name, new Double(removeQuotes(tok[i])));
						} catch (Exception e) {
							datum.put(name, removeQuotes(tok[i]));
						}
					} else
						datum.put(name, removeQuotes(tok[i]));
					i++;
				} else
					break;
			}
		} else
			return null;

		return datum;
	}

	public String readLine() throws Exception {
		if (buffer != null && !buffer.isEmpty())
			return buffer.removeFirst();
		return reader.readLine();
	}
}