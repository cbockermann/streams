package stream.annotations;

import java.lang.reflect.Field;

/**
 * Exception that should occur when a nonoptional field is not set in the xml file.
 * Or a field annotated as xmlparameter doesnt have a setter method
 * Created by bruegge on 3/5/14.
 */
public class ParameterException extends RuntimeException {

    private Field missingField;

    public ParameterException(String message){
        super(message);
    }

    public ParameterException(String message, Field missingField){
        super(message);
    }



    public Field getMissingField() {
        return missingField;
    }

    public void setMissingField(Field missingField) {
        this.missingField = missingField;
    }
}
