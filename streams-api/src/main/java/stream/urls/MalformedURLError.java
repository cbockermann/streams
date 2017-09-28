/**
 * 
 */
package stream.urls;

import streams.runtime.ConfigurationError;

/**
 * @author chris
 *
 */
public class MalformedURLError extends ConfigurationError {

    /**
     * 
     */
    private static final long serialVersionUID = 2041940842231275580L;

    public MalformedURLError(String url, String error) {
        super("URL string '" + url + "' is not a proper SourceURL: " + error);
    }
}