package stream.annotations;

import java.lang.annotation.Target;
/**
 * Created by bruegge on 3/5/14.
 */

@Target( java.lang.annotation.ElementType.FIELD )
public @interface XMLParameter {
    boolean optional() default false;
}
