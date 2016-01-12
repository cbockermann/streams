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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.annotations.Description;
import stream.data.DataFactory;

/**
 * This class implements a simple reader to read data in the SVM light data
 * format. The data is read from a URL and parsed into a Data instance.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
@Description(group = "Data Stream.Sources")
public class SvmLightStream extends AbstractLineStream {
	static Logger log = LoggerFactory.getLogger(SvmLightStream.class);
	long lineNumber = 0;
	boolean addSparseVector = true;
	String sparseKey = null;

	public SvmLightStream(SourceURL url) throws Exception {
		super(url);
	}

	public SvmLightStream(SourceURL url, String sparseVectorKey) throws Exception {
		this(url);
		this.setSparseKey(sparseVectorKey);
	}

	public SvmLightStream(InputStream in) throws Exception {
		super(in);
	}

	/**
	 * @return the sparseKey
	 */
	public String getSparseKey() {
		return sparseKey;
	}

	/**
	 * @param sparseKey
	 *            the sparseKey to set
	 */
	public void setSparseKey(String sparseKey) {
		if (sparseKey == null)
			this.sparseKey = null;
		else
			this.sparseKey = sparseKey;
	}

	/**
	 * @see stream.io.AbstractStream#readNext(stream.Data)
	 */
	@Override
	public Data readNext() throws Exception {

		Data item = DataFactory.create();

		if (limit > 0 && lineNumber > limit) {
			return null;
		}

		String line = readLine();
		if (line == null)
			return null;

		log.debug("line[{}]: {}", lineNumber, line);
		while (line != null && !line.matches("^(-|\\+)?\\d(\\.\\d+)?\\s.*")) {
			line = readLine();
		}

		if (line == null)
			return null;

		lineNumber++;

		Data datum = parseLine(item, line);
		return datum;
	}

	/**
	 * This method parses a single line into a data item. The line is expected
	 * to match the format of the SVMlight data format.
	 * 
	 * @param item
	 * @param line
	 * @return
	 * @throws Exception
	 */
	public static Data parseLine(Data item, String line, String sparseKey) throws Exception {

		int info = line.indexOf("#");
		if (info > 0)
			line = line.substring(0, info);

		String[] token = line.split("\\s+");
		item.put("@label", new Double(token[0]));

		for (int i = 1; i < token.length; i++) {

			String[] iv = token[i].split(":");
			if (iv.length != 2) {
				log.error("Failed to split token '{}' in line: ", token[i], line);
				return null;
			} else {
				item.put(iv[0], new Double(iv[1]));
			}
		}

		return item;
	}

	public static Data parseLine(Data item, String line) throws Exception {
		return parseLine(item, line, null);
	}

	/**
	 * @see stream.io.Stream#close()
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
}
