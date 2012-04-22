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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import stream.Processor;
import stream.data.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class RandomStream implements DataStream {

	final List<Processor> processors = new ArrayList<Processor>();
	final Map<String, Class<?>> attributes = new LinkedHashMap<String, Class<?>>();

	Random rnd = new Random();

	public RandomStream() {
		this(System.nanoTime());
	}

	public RandomStream(Long seed) {
		rnd = new Random(seed);
	}

	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return attributes;
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
	public Data readNext(Data item) throws Exception {
		item.clear();

		for (String key : attributes.keySet()) {
			item.put(key, rnd.nextDouble());
		}

		for (Processor proc : processors) {
			item = proc.process(item);
		}

		return item;
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * @see stream.io.DataStream#getPreprocessors()
	 */
	@Override
	public List<Processor> getPreprocessors() {
		return processors;
	}

	/**
	 * @see stream.io.DataStream#init()
	 */
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) throws Exception {
		RandomStream stream = new RandomStream();
		stream.getAttributes().put("x1", Double.class);
		stream.getAttributes().put("x2", Double.class);

		JSONWriter writer = new JSONWriter(new File("/Users/chris/test.json"));

		int id = 1;
		int i = 100;
		while (i-- > 0) {
			Data item = stream.readNext();
			item.put("@id", id++);
			System.out.println("Writing out item " + item);
			writer.process(item);
		}

	}
}