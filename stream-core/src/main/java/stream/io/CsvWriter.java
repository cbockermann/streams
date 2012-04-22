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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.ProcessContext;
import stream.data.Data;

/**
 * <p>
 * This class implements a listener which will write all its incoming data into
 * a one-by-one line output.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public class CsvWriter extends AbstractProcessor {
	static Logger log = LoggerFactory.getLogger(CsvWriter.class);
	PrintStream p;
	String separator = ",";
	String lastHeader = null;
	boolean headerWritten = false;
	String filter = ".*";
	List<String> headers = new LinkedList<String>();
	boolean closed = false;
	String[] keys;
	String url;

	public CsvWriter() {
	}

	public CsvWriter(URL url) throws Exception {
		this(new FileOutputStream(new File(url.toURI())));
	}

	/**
	 * Create a new DataStreamWriter which writes all data to the given file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public CsvWriter(File file) throws IOException {
		this(new FileOutputStream(file));
	}

	public CsvWriter(File file, String separator) throws IOException {
		this(file);
		this.separator = separator;
	}

	public void setUrl(String url) {
		this.url = url;
		File file = new File(url);
		try {
			p = new PrintStream(new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getUrl() {
		return this.url;
	}

	/**
	 * 
	 * 
	 * @param out
	 */
	public CsvWriter(OutputStream out) {
		this(out, ",");
	}

	public CsvWriter(OutputStream out, String separator) {
		p = new PrintStream(out);
		this.separator = separator;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		if (p == null) {
			throw new FileNotFoundException("File " + url + " not found");
		}
	}

	public void setAttributeFilter(String filter) {
		this.filter = filter;
	}

	public List<String> getHeaderNames() {
		return headers;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator
	 *            the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public void setKeys(String[] str) {
		this.keys = str;
	}

	/**
	 * @see stream.io.DataStreamListener#dataArrived(java.util.Map)
	 */
	@Override
	public Data process(Data datum) {

		if (closed) {
			log.error("DataStreamWriter is closed! Not writing any more data items!");
			return datum;
		}

		writeHeader(datum);

		// write the datum elements (attribute values)
		//
		write(datum);

		return datum;
	}

	public void writeHeader(Data datum) {
		// write the keys of the very first datum ONCE (attribute names)
		// or if the number of keys has changed
		//
		String header = createHeader(datum);
		if (lastHeader != null && lastHeader.equals(header))
			return;

		if (lastHeader == null || !lastHeader.equals(header)) {
			p.println(header);
			lastHeader = header;
			return;
		}

		if (!headerWritten
				|| (keys == null && datum.keySet().size() > headers.size())) {
			p.print("#");
			Iterator<String> it = datum.keySet().iterator();
			if (keys != null)
				it = Arrays.asList(keys).iterator();

			while (it.hasNext()) {
				String name = it.next();
				headers.add(name);
				p.print(name);
				if (it.hasNext())
					p.print(separator);
			}
			p.println();
			headerWritten = true;
		}
	}

	public void write(Data datum) {

		// write the datum elements (attribute values)
		//

		Iterator<String> it = null;
		if (keys != null)
			it = Arrays.asList(keys).iterator();
		else
			it = datum.keySet().iterator();

		while (it.hasNext()) {
			String name = it.next();
			String stringValue = "?";
			Serializable val = datum.get(name);

			if (val != null)
				stringValue = val.toString().replaceAll("\\n", "\\\\n");
			else
				stringValue = "null";

			p.print(stringValue);
			if (it.hasNext())
				p.print(separator);
		}
		p.println();
	}

	protected String createHeader(Data item) {
		StringWriter s = new StringWriter();
		s.append("#");
		Iterator<String> it = item.keySet().iterator();

		if (keys != null)
			it = Arrays.asList(keys).iterator();

		while (it.hasNext()) {
			s.append(it.next());
			if (it.hasNext())
				s.append(separator);
		}
		return s.toString();
	}

	public void finish() throws Exception {
		p.flush();
		p.close();
		closed = true;
	}
}