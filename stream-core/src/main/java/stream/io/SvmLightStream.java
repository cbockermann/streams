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
package stream.io;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
import stream.data.Data;
import stream.data.DataUtils;
import stream.data.vector.InputVector;

/**
 * This class implements a simple reader to read data in the SVM light data
 * format. The data is read from a URL and parsed into a Data instance.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
@Description(group = "Data Stream.Sources")
public class SvmLightStream extends AbstractDataStream {
	static Logger log = LoggerFactory.getLogger(SvmLightStream.class);
	long lineNumber = 0;
	boolean addSparseVector = true;
	String sparseKey = null;

	public SvmLightStream(String url) throws Exception {
		this(new URL(url), "@sparse-vector");
	}

	public SvmLightStream(URL url) throws Exception {
		super(url);
		initReader();
	}

	public SvmLightStream(URL url, String sparseVectorKey) throws Exception {
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
	 * @see stream.io.AbstractDataStream#readHeader()
	 */
	@Override
	public void readHeader() throws Exception {
	}

	/**
	 * @see stream.io.AbstractDataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readItem(Data item) throws Exception {

		if (limit > 0 && lineNumber > limit) {
			return null;
		}

		if (reader == null)
			initReader();

		String line = reader.readLine();
		if (line == null)
			return null;

		log.debug("line[{}]: {}", lineNumber, line);
		while (line != null && !line.matches("^(-|\\+)?\\d(\\.\\d+)?\\s.*")) {
			line = reader.readLine();
		}

		if (line == null)
			return null;

		lineNumber++;

		Data datum = parseLine(item, line);
		if (sparseKey != null) {
			log.debug("Adding sparse vector as key '{}'", sparseKey);
			datum.put(sparseKey, readSparseVector(line));
		} else
			log.debug("No sparse key defined, not creating sparse vector!");
		return datum;
	}

	public Data readSparseVector(Data item) throws Exception {
		if (reader == null)
			initReader();

		String line = reader.readLine();
		if (line == null)
			return null;

		log.debug("line[{}]: {}", lineNumber, line);
		lineNumber++;

		InputVector sp = readSparseVector(line);
		item.put(sparseKey, sp);
		return item;
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
	public static Data parseLine(Data item, String line, String sparseKey)
			throws Exception {

		int info = line.indexOf("#");
		if (info > 0)
			line = line.substring(0, info);

		String[] token = line.split("\\s+");
		item.put("@label", new Double(token[0]));

		for (int i = 1; i < token.length; i++) {

			String[] iv = token[i].split(":");
			if (iv.length != 2) {
				log.error("Failed to split token '{}' in line: ", token[i],
						line);
				return null;
			} else {
				item.put(iv[0], new Double(iv[1]));
			}
		}

		if (sparseKey != null) {
			HashMap<Integer, Double> pairs = new HashMap<Integer, Double>();

			for (int i = 1; i < token.length; i++) {

				String[] iv = token[i].split(":");
				if (iv.length != 2) {
					log.error("Failed to split token '{}' in line: ", token[i],
							line);
					return null;
				} else {
					pairs.put(Integer.parseInt(iv[0]),
							Double.parseDouble(iv[1]));
				}
			}

			item.put(sparseKey,
					new InputVector(pairs, false, Double.parseDouble(token[0])));
		}

		return item;
	}

	public static Data parseLine(Data item, String line) throws Exception {
		return parseLine(item, line, null);
	}

	public static InputVector readSparseVector(String line) throws Exception {
		int info = line.indexOf("#");
		if (info > 0)
			line = line.substring(0, info);

		String[] token = line.split("\\s+");

		HashMap<Integer, Double> pairs = new HashMap<Integer, Double>();

		for (int i = 1; i < token.length; i++) {

			String[] iv = token[i].split(":");
			if (iv.length != 2) {
				log.error("Failed to split token '{}' in line: ", token[i],
						line);
				return null;
			} else {
				pairs.put(Integer.parseInt(iv[0]), Double.parseDouble(iv[1]));
			}
		}

		return new InputVector(pairs, false, Double.parseDouble(token[0]));
	}

	public static InputVector createSparseVector(Data datum) {
		if (datum.containsKey(".sparse-vector")) {
			log.trace("Found existing sparse-vector object!");
			return (InputVector) datum.get(".sparse-vector");
		}

		for (Serializable val : datum.values()) {
			if (val instanceof InputVector) {
				log.trace("Found existing sparse-vector object!");
				return (InputVector) val;
			}
		}

		TreeSet<String> indexes = new TreeSet<String>();
		for (String key : datum.keySet()) {
			Serializable val = datum.get(key);
			if (!DataUtils.isAnnotation(key) && key.matches("\\d+")
					&& val instanceof Double) {
				log.debug("Found numeric feature {}", key);
				indexes.add(key);
			} else {
				log.debug("Skipping non-numeric feature {} of type {}", key,
						val.getClass());
			}
		}

		double y = Double.NaN;
		if (datum.containsKey("@label")) {
			try {
				y = (Double) datum.get("@label");
			} catch (Exception e) {
				y = Double.NaN;
			}
		}

		// int[] idx = new int[ indexes.size() ];
		// double[] vals = new double[ indexes.size() ];
		HashMap<Integer, Double> pairs = new HashMap<Integer, Double>();

		// int i = 0;
		for (String key : indexes) {
			// idx[i] = Integer.parseInt( key );
			// vals[i] = (Double) datum.get( key );
			// i++;
			pairs.put((Integer) Integer.parseInt(key), (Double) datum.get(key));
		}

		// SparseVector vec = new SparseVector( idx, vals, y, false );
		InputVector vec = new InputVector(pairs, false, y);
		log.trace("SparseVector: {}", vec);
		return vec;
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
}
