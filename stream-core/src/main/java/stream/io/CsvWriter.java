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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ConditionedProcessor;
import stream.ProcessContext;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.expressions.ExpressionResolver;
import stream.service.Service;

/**
 * <p>
 * This class implements a listener which will write all its incoming data into
 * a one-by-one line output.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
@Description(group = "Data Stream.Output")
public class CsvWriter extends ConditionedProcessor implements Service {
	static Logger log = LoggerFactory.getLogger(CsvWriter.class);
	protected PrintStream p;
	protected String separator = ",";
	protected String lastHeader;
	protected boolean headerWritten;
	protected String filter;
	protected List<String> headers;
	protected boolean closed;
	protected String[] keys;
	protected String urlString;
	protected URL url;
	protected File file;
	protected String lastUrlString = null;

	public CsvWriter() {
		super();
	}

	public CsvWriter(URL url) throws Exception {
		this(new FileOutputStream(new File(url.toURI())));
		this.setUrl(url.toString());
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

	/**
	 * 
	 * 
	 * @param out
	 */
	public CsvWriter(OutputStream out) {
		this(out, ",");
	}

	public CsvWriter(OutputStream out, String separator) {
		this();
		p = new PrintStream(out);
		this.separator = separator;
	}

	public CsvWriter(File file, String separator) throws IOException {
		this(file);
		this.separator = separator;
	}

	@Parameter(required = true, description = "The url to write to.")
	public void setUrl(String url) {
		this.urlString = url;
	}

	public String getUrl() {
		return this.urlString;
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
	@Parameter(required = false, description = "The separator to separate columns, usually ','", defaultValue = ",")
	public void setSeparator(String separator) {
		this.separator = separator;

	}

	/**
	 * 
	 * @param str
	 */
	@Parameter(required = false, description = "The attributes to write out, leave blank to write out all attributes.")
	public void setKeys(String[] str) {
		if (str == null || str.length == 0)
			this.keys = null;
		this.keys = str;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		headers = new LinkedList<String>();
		closed = false;
		lastHeader = null;
		headerWritten = false;
		filter = ".*";
	}

	/**
	 * @see stream.io.DataStreamListener#dataArrived(java.util.Map)
	 */
	@Override
	public Data processMatchingData(Data datum) {
		String expandedUrlString = (String) ExpressionResolver.expand(
				urlString, context, datum);
		if (expandedUrlString == null) {
			log.error("can't find the file! {}", urlString);
			return datum;
		}

		// check if this is the first time we write something or the URL has
		// changed. in
		// any of these cases, we need to open a new output stream for writing
		//
		if (lastUrlString == null || !expandedUrlString.equals(lastUrlString)) {

			// if 'p' exists, this means there has been a previous file and just
			// the
			// output URL has changed: we close the old file, first.
			//
			// TODO: Maybe we should not immediately close the old file, but
			// keep a
			// open handle, i.e. a HashMap of URL-Strings to open output-streams
			// in case we the URL string is expanded to an existing file...
			//
			if (p != null) {
				p.flush();
				p.close();
			}

			// TODO: decide at which times we want to possibly append to
			// existing
			// files instead of creating new files. This might make sense when
			// splitting a stream into multiple files based on URL expansion...
			//
			// For now, we append only, if the switch to a new file is caused by
			// changes of the URL, i.e. initially, we start with new files...
			//
			boolean append = (p == null);

			try {
				lastUrlString = expandedUrlString;
				this.url = new URL(expandedUrlString);
				file = new File(url.toURI());

				OutputStream out;
				if (file.getAbsolutePath().endsWith(".gz"))
					out = new GZIPOutputStream(new FileOutputStream(file,
							append));
				else
					out = new FileOutputStream(file, append);

				p = new PrintStream(out);
				lastHeader = null;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				log.error("Failed to open file: {}", e.getMessage());
			}
		}

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
			p.print("# ");
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
		if (keys != null && keys.length != 0)
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
		s.append("# ");

		Iterator<String> it = null;

		if (keys != null)
			it = Arrays.asList(keys).iterator();
		else
			it = item.keySet().iterator();

		while (it.hasNext()) {
			s.append(it.next());
			if (it.hasNext())
				s.append(separator);
		}
		return s.toString();
	}

	public void finish() throws Exception {
		if (p != null) {
			p.flush();
			p.close();
		}
		closed = true;
	}

	@Override
	public void reset() throws Exception {
		headers = new LinkedList<String>();
		closed = false;
		separator = ",";
		lastHeader = null;
		headerWritten = false;

	}
}
