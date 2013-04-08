/**
 * 
 */
package stream.io;

/**
 * @author chris
 * 
 */
public interface OrderedStream extends Stream {

	public static final class ID {

		Long[] count;

		public ID getSuccessor() {
			return null;
		}
	}
}
