/**
 * 
 */
package stream.runtime.setup;

import java.util.Map;

/**
 * @author chris
 * 
 */
public interface ParameterAware {

	public void setParameters(Map<String, String> params) throws Exception;

	public Map<String, String> getParameters() throws Exception;
}
