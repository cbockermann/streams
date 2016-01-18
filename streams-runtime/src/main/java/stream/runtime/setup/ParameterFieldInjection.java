/**
 * 
 */
package stream.runtime.setup;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Parameter;
import stream.annotations.ParameterException;
import stream.service.Service;
import stream.util.Variables;

/**
 * This class handles injection of parameters into annotated fields of an
 * object.
 * 
 * @author Christian Bockermann
 *
 */
public class ParameterFieldInjection extends ParameterValueMapper {

    static Logger log = LoggerFactory.getLogger(ParameterFieldInjection.class);

    public Set<String> inject(final Object o, Map<String, ?> params, Variables context) throws ParameterException {
        Set<String> injected = new HashSet<>();

        Field[] fields = o.getClass().getDeclaredFields();
        for (final Field field : fields) {

            // skip fields that are not annotated as parameters
            if (!field.isAnnotationPresent(Parameter.class)) {
                log.debug("Skipping field '{}' without parameter annotation...", field.getName());
                continue;
            }

            if (Service.class.isAssignableFrom(field.getType())) {
                throw new ParameterException("Field '" + field.getName() + "' of class '" + o.getClass().getSimpleName()
                        + "' represents a service, but is annotated with '@Parameter'!");
            }

            // allow for restoring the original access level to the field
            boolean accessLevel = field.isAccessible();
            field.setAccessible(true);

            Parameter p = field.getAnnotation(Parameter.class);
            String name = p.name();
            log.info("Found parameter annotation with name '{}' for field '{}'", name, field.getName());
            if (name == null || name.isEmpty()) {
                log.info("Parameter annotation for field '{}' has no name, using field name.", field.getName());
                name = field.getName();
            } else {
                log.info("Using property '{}' as defined by annotation, instead of field name '{}'", p.name(),
                        field.getName());
            }

            if (p.required() && !params.containsKey(name)) {
                log.error("Parameter '{}' is required, but no value is provided for it!", name);

                throw new ParameterException("Missing value for required parameter '" + name + "' in class '"
                        + o.getClass().getSimpleName() + "'!");
            }

            Object value = params.get(name);
            Object set = null;
            if (value != null) {
                log.debug("Object for key '{}' is: {}", name, value);
                final Object po = createValue(field.getType(), value);
                set = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        try {
                            field.set(o, po);
                            return po;
                        } catch (Exception e) {
                            return null;
                        }
                    }
                });
            } else {
                log.info("Leaving option field '{}' with no parameter value given untouched.", field.getName());
            }

            if (set != null) {
                log.debug("Successfully injected value for parameter '{}'", name);
                injected.add(name);
            } else {
                log.warn("Failed to set value for parameter '{}' using field-injection", name);
            }

            field.setAccessible(accessLevel);
        }

        // return the set of all successfully injected fields
        return injected;
    }
}