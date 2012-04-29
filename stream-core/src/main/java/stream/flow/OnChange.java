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
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;

/**
 * <p>
 * This class provides a data process that will identify return true if the
 * given attribute will change from "from" to "to".
 * </p>
 * 
 * @author Hendrik Blom &lt;hendrik.blom@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Flow")
public class OnChange extends If {

	static Logger log = LoggerFactory.getLogger(OnChange.class);

	private String key;
	private String oldValue;

	private String from;
	private String to;

	public String getFrom() {
		return from;
	}

	@Parameter(required = false, defaultValue = "")
	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	@Parameter(required = false, defaultValue = "")
	public void setTo(String to) {
		this.to = to;
	}

	public OnChange() {
		oldValue = "";
	}

	@Parameter(required = true, defaultValue = "")
	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public boolean matches(Data item) {
		String value = String.valueOf(item.get(key));
		boolean result = false;

		if (from.equals(to) && "".equals(from)) {
			if (!oldValue.equals(value))
				result = true;
		} else if (oldValue.equals(from) && value.equals(to)) {
			result = true;
			log.debug(key + " changed from " + from + " to " + to + "!");
		}
		oldValue = value;
		return result;
	}
}