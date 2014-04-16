/**
 * 
 */
package stream;

/**
 * @author chris
 * 
 */
public class VIdentity extends ValidatedProcessor {

	/**
	 * @see stream.ValidatedProcessor#processItem(stream.Data)
	 */
	@Override
	public Data processItem(Data input) {
		return input;
	}
}
