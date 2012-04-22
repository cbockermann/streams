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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class GaussianStream extends GeneratorDataStream {

	static Logger log = LoggerFactory.getLogger(GaussianStream.class);

	Map<String, Gaussian> generators = new LinkedHashMap<String, Gaussian>();

	/* The data types provided by this stream */
	Map<String, Class<?>> types = new LinkedHashMap<String, Class<?>>();

	/* The random generator to choose which class to create next */
	Random random = new Random();

	Long seed = System.currentTimeMillis();

	Random seedGenerator = new Random();

	/**
	 * Create a new Gaussian stream with the given number of classes. The map
	 * contains a mapping of attribute-names to double-pairs each of which
	 * describes the mean/variance of a Gaussian distribution.
	 * 
	 * @param numberOfClasses
	 *            The number of classes.
	 * @param attributeDistributions
	 *            The parameterization of the attribute distributions.
	 */
	public GaussianStream() {
	}

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
		random = new Random(this.seed);
		this.seedGenerator = new Random(this.seed);
	}

	public Data generate() {
		Data item = DataFactory.create();

		for (String attribute : generators.keySet()) {
			Gaussian g = generators.get(attribute);
			item.put(attribute, g.next());
		}

		return item;
	}

	public void setGenerator(String attribute, Gaussian dist) {
		generators.put(attribute, dist);
		if (!this.types.containsKey(attribute))
			types.put(attribute, Double.class);

		if (dist.getSeed() == null) {
			Long seed = getNextSeed();
			log.info("Setting seed for new generator to {}", seed);
			dist.setSeed(seed);
		}
	}

	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return types;
	}

	/**
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return readNext(DataFactory.create());
	}

	/**
	 * @see stream.io.AbstractDataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readNext(Data item) throws Exception {
		//
		// TODO: This is cheated: we still create a new item-object, which
		// should not
		// happen... :-)
		//
		Data gen = this.generate();
		item.clear();
		item.putAll(gen);
		return item;
	}

	public String getDescription() {
		StringBuffer s = new StringBuffer();
		s.append("<p>");
		s.append("This is a pseudo-randomized generated data stream, consisting of "
				+ types.size() + " attributes. <br/>");
		s.append("The attributes are { " + this.generators.keySet()
				+ " } and are independently generated using the");
		s.append("following generators:");
		s.append("<table>");
		for (String att : types.keySet()) {
			s.append("<tr>");
			s.append("<td>");
			s.append(att);
			s.append("</td>");
			s.append("<td>");
			Gaussian g = generators.get(att);
			s.append("mean: " + g.getMean() + ", variance: " + g.getVariance());
			s.append("</td>");
			s.append("</tr>");
		}

		s.append("</p>");

		return s.toString();
	}

	public Long getNextSeed() {
		return seedGenerator.nextLong();
	}

	public void close() {
	}
}