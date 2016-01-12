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

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractStream;
import stream.io.SourceURL;
import cern.jet.random.engine.MersenneTwister64;

/**
 * @author Hendrik Blom, chris
 * 
 */
public class ColtGaussianStream extends AbstractStream {

	static Logger log = LoggerFactory.getLogger(ColtGaussianStream.class);

	protected Map<String, ColtGaussian> generators;

	/* The data types provided by this stream */
	protected Map<String, Class<?>> types;

	protected Long seed;

	protected MersenneTwister64 seedGenerator;

	protected Double[] attributes;

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
	public ColtGaussianStream() {
		super((SourceURL) null);
		seed = System.currentTimeMillis();
		generators = new LinkedHashMap<String, ColtGaussian>();
		types = new LinkedHashMap<String, Class<?>>();
		seedGenerator = new MersenneTwister64();
	}

	@Override
	public void init() throws Exception {
		super.init();

		if (attributes == null && generators.isEmpty()) {
			throw new Exception(
					"Parameter 'attributes' missing! This should be a list of mean,deviation pairs!");
		}

		if (attributes != null) {
			int cnt = 1;
			for (int i = 0; i + 1 < attributes.length; i += 2) {
				ColtGaussian cg = new ColtGaussian(attributes[i],
						attributes[i + 1], seed);
				setGenerator("x" + cnt, cg);
				cnt++;
			}
		}
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
		this.seedGenerator = new MersenneTwister64(seed.intValue());
	}

	public Data generate() {
		Data item = DataFactory.create();

		for (String attribute : generators.keySet()) {
			ColtGaussian g = generators.get(attribute);
			item.put(attribute, g.next());
		}

		return item;
	}

	public void setGenerator(String attribute, ColtGaussian dist) {
		generators.put(attribute, dist);
		if (!this.types.containsKey(attribute))
			types.put(attribute, Double.class);

		if (dist.getSeed() == null) {
			Long seed = getNextSeed();
			log.info("Setting seed for new generator to {}", seed);
			dist.setSeed(seed);
		}
	}

	public void setAttributes(Double[] attributes) {
		this.attributes = attributes;
	}

	/**
	 * @see stream.io.Stream#read()
	 */
	@Override
	public Data readNext() throws Exception {
		return this.generate();
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
			ColtGaussian g = generators.get(att);
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