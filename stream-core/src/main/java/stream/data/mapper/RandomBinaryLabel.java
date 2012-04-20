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
package stream.data.mapper;

import java.util.Random;

import stream.Processor;
import stream.annotations.Description;
import stream.data.Data;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Data")
public class RandomBinaryLabel implements Processor {

	Long seed = System.currentTimeMillis();
	Random random = new Random();
	String labelAttribute = "@label";

	/**
	 * @return the seed
	 */
	public Long getSeed() {
		return seed;
	}

	/**
	 * @param seed
	 *            the seed to set
	 */
	public void setSeed(Long seed) {
		this.seed = seed;
		if (seed != null)
			random = new Random(seed);
	}

	/**
	 * @return the labelAttribute
	 */
	public String getKey() {
		return labelAttribute;
	}

	/**
	 * @param labelAttribute
	 *            the labelAttribute to set
	 */
	public void setKey(String labelAttribute) {
		this.labelAttribute = labelAttribute;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		Double val = random.nextDouble();
		if (val < 0.5) {
			data.put(labelAttribute, -1.0d);
		} else
			data.put(labelAttribute, 1.0d);

		return data;
	}
}