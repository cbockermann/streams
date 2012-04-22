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
package stream.generator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import stream.data.Data;
import stream.data.DataFactory;
import stream.io.DataStream;

/**
 * @author chris
 * 
 */
public class MixedStream extends GeneratorDataStream {

	Map<String, Class<?>> types = new LinkedHashMap<String, Class<?>>();
	Double totalWeight = 0.0d;
	List<Double> weights = new ArrayList<Double>();
	List<DataStream> streams = new ArrayList<DataStream>();

	Random rnd = new Random();

	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return types;
	}

	public void add(Double weight, DataStream stream) {
		streams.add(stream);
		weights.add(totalWeight + weight);
		types.putAll(stream.getAttributes());
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
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return readNext(DataFactory.create());
	}

	/**
	 * @see stream.io.DataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readNext(Data datum) throws Exception {
		int i = this.choose();
		return streams.get(i).readNext(datum);
	}

	public static void main(String[] args) throws Exception {
		MixedStream ms = new MixedStream();
		ms.readNext();
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
	}
}