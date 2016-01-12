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

import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractStream;
import stream.io.SourceURL;

/**
 * @author Hendrik Blom
 * 
 */
public class NoisySinusWave extends AbstractStream {

	protected Double index;
	protected Double amplitude;
	protected Double frequency;
	protected String key;
	protected ColtGaussian noise;
	protected Long seed;
	protected Double mean;
	protected Double variance;

	public NoisySinusWave() {
		super((SourceURL) null);
		seed = System.currentTimeMillis();
		mean = 0d;
		variance = 1d;
		index = 0d;
		amplitude = 1d;
		frequency = 1d;
		key = "sin(t)";
	}

	@Override
	public void init() throws Exception {
		super.init();
		noise = new ColtGaussian(mean, variance, seed);
	}

	/**
	 * @see stream.io.AbstractStream#read()
	 */
	@Override
	public Data readNext() throws Exception {
		Data instance = DataFactory.create();
		Double value = amplitude * Math.sin(frequency * index) + noise.next();
		instance.put("t", index);
		instance.put(key, value);
		index += 0.01;
		return instance;
	}

	/**
	 * @return the amplitude
	 */
	public Double getAmplitude() {
		return amplitude;
	}

	/**
	 * @param amplitude
	 *            the amplitude to set
	 */
	public void setAmplitude(Double amplitude) {
		this.amplitude = amplitude;
	}

	/**
	 * @return the frequency
	 */
	public Double getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency
	 *            the frequency to set
	 */
	public void setFrequency(Double frequency) {
		this.frequency = frequency;
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
	public void setKey(String key) {
		this.key = key;
	}

	public Long getSeed() {
		return seed;
	}

	public void setSeed(Long seed) {
		this.seed = seed;
	}

	public Double getMean() {
		return mean;
	}

	public void setMean(Double mean) {
		this.mean = mean;
	}

	public Double getVariance() {
		return variance;
	}

	public void setVariance(Double variance) {
		this.variance = variance;
	}

}
