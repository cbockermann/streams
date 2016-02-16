/**
 * 
 */
package stream.runtime.setup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Parameter;
import stream.annotations.ParameterException;

/**
 * @author Christian Bockermann
 *
 */
public class ParameterValidator {

    static Logger log = LoggerFactory.getLogger(ParameterValidator.class);

    public static boolean check(Object o, Map<String, Object> vals) throws ParameterException {

        Map<String, Class<?>> types = checkClassParameters(o.getClass(), vals);
        log.debug("Object {} has {} paramters: {}", o, types.size(), types);

        return true;
    }

    /**
     * This method checks if any of the fields and methods define parameters
     * with the same name, i.e. a parameter "p" being defined by an annotated
     * field <b>and</b> an annotated setter method.
     * 
     * In the case, a parameter is detected to be defined twice, an exception is
     * thrown.
     * 
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Map<String, Class<?>> checkClassParameters(Class<?> clazz, Map<String, ?> vals)
            throws ParameterException {

        Map<String, Class<?>> params = new LinkedHashMap<String, Class<?>>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Parameter info = field.getAnnotation(Parameter.class);
            if (info != null) {

                String name = info.name();
                if (name == null || name.isEmpty()) {
                    name = field.getName();
                }
                log.debug("Found parameter '{}' for field '{}'", name, field.getName());
                params.put(name, field.getType());

                if (info.required() && !vals.containsKey(name)) {
                    throw new ParameterException("Field '" + name + "' is missing required value!");
                }

            }
        }

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {

            Parameter p = method.getAnnotation(Parameter.class);
            if (p == null) {
                continue;
            }

            String name = ParameterMethodInjection.getParameterName(method);
            if (name == null) {
                continue;
            }

            if (params.containsKey(name) && p != null) {
                Class<?> mtype = method.getParameterTypes()[0];
                if (!mtype.equals(params.get(name))) {
                    throw new ParameterException(
                            "Input type '" + mtype.getName() + "' of set-method '" + clazz.getSimpleName() + "."
                                    + method.getName() + "' for parameter '" + name + "' does not match type '"
                                    + params.get(name).getName() + "' defined by field annotation!");
                }
            }

            if (p.required() && !vals.containsKey(name)) {
                throw new ParameterException("Missing value for parameter '" + name + "'!");
            }

            params.put(name, method.getParameterTypes()[0]);
        }

        return params;
    }
}