package stream.annotations;

/**
 * Exception that should occur when a nonoptional field is not set in the xml file
 * Created by bruegge on 3/5/14.
 */
public class XMLParameterException extends RuntimeException {
    String parameterName;
}
