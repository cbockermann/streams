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

import java.util.Random;

/**
 * @author chris
 *
 */
/**
 * <p>
 * This class implements a gaussian distributor.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public class Gaussian implements DistributionFunction {
	Random rnd;
	Long seed;
	Double mean = 0.0d;
	Double variance = 1.0d;
	
	public Gaussian(){
	}
	
	
	public Gaussian( Long seed ){
		this.seed = seed;
	}
	
	public Gaussian( Double mean, Double variance, Long seed ){
		this( seed );
		this.mean = mean;
		this.variance = variance;
	}
	
	public Gaussian( Double mean, Double variance ){
		this( mean, variance, null );
	}
	
	
	/**
	 * @return the seed
	 */
	public Long getSeed() {
		return seed;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(Long seed) {
		this.seed = seed;
		rnd = new Random( this.seed );
	}

	/**
	 * @return the mean
	 */
	public Double getMean() {
		return mean;
	}

	/**
	 * @param mean the mean to set
	 */
	public void setMean(Double mean) {
		this.mean = mean;
	}


	/**
	 * @return the variance
	 */
	public Double getVariance() {
		return variance;
	}


	/**
	 * @param variance the variance to set
	 */
	public void setVariance(Double variance) {
		this.variance = variance;
	}


	public Double next(){
		if( rnd == null ){
			if( seed == null )
				seed = System.currentTimeMillis();
			rnd = new Random( seed );
		}
		
		return mean + variance * rnd.nextGaussian();
	}
	
	public Double nextGaussian(){
		if( rnd == null ){
			if( seed == null )
				seed = System.currentTimeMillis();
			rnd = new Random( seed );
		}
		
		return rnd.nextGaussian();
	}
	
	public String toHtml(){
		return "<p>Gaussian, mean: <i>" + mean + "</i>, variance: <i>" + variance + "</i>, random seed: <code>" + seed + "</code></p>";
	}

	public String toString(){
		return( "N(" + mean + ", " + variance + ")" );
	}

	/**
	 * @see stream.generator.DistributionFunction#p(java.lang.Double)
	 */
	@Override
	public Double p(Double x) {
		return phi( x, mean, variance );
		/*
		Double sig2 = variance * variance;
		Double exponent = ( ( (x-mean)*(x-mean) ) / 2*sig2 );
		Double fac = 1 / ( variance * Math.sqrt( 2 * Math.PI ) );
		return fac * Math.exp( - exponent );
		 */
	}
	
	
	 // return phi(x) = standard Gaussian pdf
    public static double phi(double x) {
        return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
    }

    // return phi(x, mu, signma) = Gaussian pdf with mean mu and stddev sigma
    public static double phi(double x, double mu, double sigma) {
        return phi((x - mu) / sigma) / sigma;
    }

    // return Phi(z) = standard Gaussian cdf using Taylor approximation
    public static double Phi(double z) {
        if (z < -8.0) return 0.0;
        if (z >  8.0) return 1.0;
        double sum = 0.0, term = z;
        for (int i = 3; sum + term != sum; i += 2) {
            sum  = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * phi(z);
    }

    // return Phi(z, mu, sigma) = Gaussian cdf with mean mu and stddev sigma
    public static double Phi(double z, double mu, double sigma) {
        return Phi((z - mu) / sigma);
    } 
}