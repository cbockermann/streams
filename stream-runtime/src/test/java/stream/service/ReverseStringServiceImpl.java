/**
 * 
 */
package stream.service;

/**
 * @author chris
 * 
 */
public class ReverseStringServiceImpl implements ReverseStringService {

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
	}

	/**
	 * @see stream.service.ReverseStringService#reverse(java.lang.String)
	 */
	@Override
	public String reverse(String string) {

		if (string == null)
			return null;

		StringBuffer s = new StringBuffer();
		for (int i = string.length() - 1; i >= 0; i--) {
			s.append(string.charAt(i));
		}

		return s.toString();
	}
}
