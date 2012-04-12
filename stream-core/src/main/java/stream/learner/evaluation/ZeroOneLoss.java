/**
 * 
 */
package stream.learner.evaluation;

import java.io.Serializable;

/**
 * @author chris
 * 
 */
public class ZeroOneLoss<T extends Serializable> implements LossFunction<T> {
	/* The loss function used to assess the prediction error */
	@Override
	public double loss(T x1, T x2) {

		if (x1 == x2 || x1.toString().equals(x2.toString())) {
			return 0.0d;
		} else
			return 1.0d;
		/*
		 * 
		 * if( x1 instanceof Double && x2 instanceof Double ){ Double d1 =
		 * (Double) x1; Double d2 = (Double) x2; return Math.abs(d1 - d2); }
		 * else {
		 * 
		 * if( !x1.toString().equals( x2 + "" ) ){ return 1.0d; }
		 * 
		 * }
		 * 
		 * if( x1.equals( x2 ) ) return 0.0d;
		 * 
		 * return 1.0d;
		 */
	}
}
