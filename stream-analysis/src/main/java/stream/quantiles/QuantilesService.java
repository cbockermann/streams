/**
 * 
 */
package stream.quantiles;

import stream.service.Service;

/**
 * <p>
 * This interface defines all methods provided by quantile estimators.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface QuantilesService extends Service {

	/**
	 * This method returns the keys of all attributes for which the service
	 * provides quantile estimates.
	 * 
	 * @return
	 */
	public String[] getQuantileKeys();

	/**
	 * This method returns the estimated <code>phi</code>-quantile for the
	 * attribute specified by <code>key</code>.
	 * 
	 * @param key
	 * @param phi
	 * @return
	 */
	public Double getQuantile(String key, Double phi);
}