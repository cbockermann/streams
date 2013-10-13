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

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister64;

/**
 * <p>
 * This class implements a gaussian distributor.
 * </p>
 * 
 * @author Hendrik Blom
 */
public class ColtGaussian implements DistributionFunction {
	protected Long seed;
	protected Double mean = 0.0d;
	protected Double variance = 1.0d;
	protected Normal rnd;
	protected MersenneTwister64 rndEngine;

	public ColtGaussian(Double mean, Double variance, Long seed) {
		rndEngine = new MersenneTwister64(seed.intValue());
		this.rnd = new Normal(mean, variance, rndEngine);
		this.mean = mean;
		this.variance = variance;
	}

	public ColtGaussian(Double mean, Double variance) {
		this(mean, variance, null);
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
		this.rndEngine = new MersenneTwister64(seed.intValue());
	}

	/**
	 * @return the mean
	 */
	public Double getMean() {
		return mean;
	}

	/**
	 * @param mean
	 *            the mean to set
	 */
	public void setMean(Double mean) {
		this.mean = mean;
		this.rnd.setState(mean, variance);
	}

	/**
	 * @return the variance
	 */
	public Double getVariance() {
		return variance;
	}

	/**
	 * @param variance
	 *            the variance to set
	 */
	public void setVariance(Double variance) {
		this.variance = variance;
		this.rnd.setState(mean, variance);
	}

	public Double next() {
		return rnd.nextDouble();
	}

	public String toHtml() {
		return "<p>Gaussian, mean: <i>" + mean + "</i>, variance: <i>"
				+ variance + "</i>, random seed: <code>" + seed + "</code></p>";
	}

	public String toString() {
		return ("N(" + mean + ", " + variance + ")");
	}

	/**
	 * @see stream.generator.DistributionFunction#p(java.lang.Double)
	 */
	@Override
	public Double p(Double x) {
		return this.rnd.pdf(x);
	}

}