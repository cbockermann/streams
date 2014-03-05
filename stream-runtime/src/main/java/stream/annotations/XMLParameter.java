package stream.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
/**
 * Created by bruegge on 3/5/14.
 */

@Target( java.lang.annotation.ElementType.FIELD ) @Retention(value= RetentionPolicy.RUNTIME)
public @interface XMLParameter {
    boolean optional() default false;
}
