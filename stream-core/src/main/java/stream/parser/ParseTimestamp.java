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
package stream.parser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.ProcessContext;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;

/**
 * <p>
 * This simple processor adds a timestamp (current time in milliseconds) to all
 * processed data items.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
@Description(group = "Streams.Processing.Transformations.Data")
public class ParseTimestamp extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(ParseTimestamp.class);
	SimpleDateFormat dateFormat = null;
	String key = "@timestamp";
	String format = null;
	String from = null;

	public ParseTimestamp() {
	}

	public ParseTimestamp(String key, String format, String from) {
		setKey(key);
		setFormat(format);
		setFrom(from);
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
	@Parameter
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
	@Parameter(required = true, defaultValue = "yyyy-MM-dd HH:mm:ss", description = "The date format string used for parsing.")
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	@Parameter(required = true, description = "The key/attribute from which the timestamp should be parsed.")
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (data != null && key != null) {

			Serializable from = data.get(getFrom());
			if (dateFormat != null && from != null) {
				try {
					Date date = dateFormat.parse(from.toString());
					data.put(key, date.getTime());
				} catch (Exception e) {
					log.error(
							"Failed to parse timestamp from '{}', expected format is '{}'",
							from, format);
				}
			} else {
				data.put(key, new Long(System.currentTimeMillis()));
			}
		}

		return data;
	}

	/**
	 * @see stream.AbstractProcessor#init()
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);

		if (getFormat() != null && getFrom() != null) {
			dateFormat = new SimpleDateFormat(getFormat(), Locale.ENGLISH);
		}
	}
}
