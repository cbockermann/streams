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
package stream.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import stream.Data;
import stream.io.AbstractStream;
import stream.io.SourceURL;
import stream.io.Stream;

/**
 * @author chris TODO: this should not extend abstract stream and might be
 *         obsolete with the Joins in the future.
 */
public class MixedStream extends AbstractStream {

	/**
	 * @param url
	 */
	public MixedStream(SourceURL url) {
		super((SourceURL) url);
	}

	Double totalWeight = 0.0d;
	List<Double> weights = new ArrayList<Double>();
	List<Stream> streams = new ArrayList<Stream>();

	Random rnd = new Random();

	public void add(Double weight, Stream stream) {
		streams.add(stream);
		weights.add(totalWeight + weight);
		totalWeight += weight;
	}

	protected int choose() {

		double d = rnd.nextDouble();
		Double t = d * totalWeight;

		for (int i = 0; i < weights.size(); i++) {
			if (i + 1 < weights.size() && weights.get(i + 1) > t)
				return i;
		}

		return weights.size() - 1;
	}

	/**
	 * @see stream.io.Stream#read()
	 */
	@Override
	public Data readNext() throws Exception {
		int i = this.choose();
		return streams.get(i).read();
	}
}