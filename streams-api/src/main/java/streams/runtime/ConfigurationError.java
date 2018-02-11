/**
 * 
 */
package streams.runtime;

/**
 * @author chris
 *
 */
public class ConfigurationError extends RuntimeException {

    /** The unique class ID */
    private static final long serialVersionUID = -7991148169724968044L;

    public ConfigurationError() {
        super();
    }

    public ConfigurationError(String s) {
        super(s);
    }
}