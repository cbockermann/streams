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
package stream.data;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ConditionedProcessor;
import stream.Data;
import stream.annotations.Description;
import stream.annotations.Parameter;

/**
 * <p>
 * This processor simply renames a single key.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(name = "Rename Key", text = "", group = "Data Stream.Processing.Transformations.Attributes")
public class RenameKey extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(RenameKey.class);
	String oldKey;
	String newKey;

	public RenameKey(String oldKey, String newKey) {
		this.oldKey = oldKey;
		this.newKey = newKey;
	}

	public RenameKey() {
		this.oldKey = "";
		this.newKey = "";
	}

	/**
	 * @return the oldKey
	 */
	public String getFrom() {
		return oldKey;
	}

	/**
	 * @param oldKey
	 *            the oldKey to set
	 */
	@Parameter(required = true, description = "The old name of the key.")
	public void setFrom(String oldKey) {
		this.oldKey = oldKey;
	}

	/**
	 * @return the newKey
	 */
	public String getTo() {
		return newKey;
	}

	/**
	 * @param newKey
	 *            the newKey to set
	 */
	@Parameter(required = true, description = "The new name of the key.")
	public void setTo(String newKey) {
		this.newKey = newKey;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data input) {
		if (oldKey != null && newKey != null && input.containsKey(oldKey)) {
			if (input.containsKey(newKey))
				log.warn("Overwriting existing key '{}'!", newKey);

			Serializable o = input.remove(oldKey);
			input.put(newKey, o);
		}
		return input;
	}
}