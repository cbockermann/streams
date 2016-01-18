/**
 * 
 */
package stream.runtime.setup;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Parameter;
import stream.runtime.DependencyInjection;
import stream.util.Variables;

/**
 * This class implements the parameter injection via setter-methods.
 * 
 * @author Christian Bockermann
 *
 */
public class ParameterMethodInjection extends ParameterValueMapper {

    static Logger log = LoggerFactory.getLogger(ParameterMethodInjection.class);

    /**
     * 
     * 
     * @param o
     * @param params
     * @param context
     * @return
     * @throws Exception
     */
    public Set<String> inject(Object o, Map<String, ?> params, Variables context) throws Exception {

        Set<String> alreadySet = new HashSet<String>();

        // now, walk over all methods and check if one of these is a setter of a
        // corresponding
        // key value in the parameter map
        //
        for (Method m : o.getClass().getMethods()) {

            if (DependencyInjection.isServiceSetter(m)) {
                log.debug("Skipping ServiceSetter '{}'", m.getName());
                continue;
            }

            if (DependencyInjection.isSourceSetter(m)) {
                log.debug("Skipping SourceSetter '{}'", m.getName());
                continue;
            }

            if (ParameterInjection.isQueueSetter(m)) {
                log.debug("Skipping QueueSetter '{}'", m.getName());
                continue;
            }
            //
            // if (embedded != null && m.getName().startsWith("set") && t.length
            // == 1 && t[0] == BodyContent.class) {
            // log.debug("Setting embedded content...");
            // m.invoke(o, new BodyContent(embedded.toString()));
            // continue;
            // }

            if (isSetter(m)) {
                log.debug("Handling set-method '{}'", m.getName());

                boolean required = false;
                String key = getParameterName(m);

                Parameter p = m.getAnnotation(Parameter.class);
                if (p != null) {

                    if (p.name() != null && !p.name().isEmpty()) {
                        key = p.name();
                        log.info("Using parameter name '{}' from annotation", key);
                    }

                    required = p.required();
                    if (required) {
                        log.debug("Parameter '{}' is annotated as required", key);
                    } else {
                        log.debug("Parameter '{}' is annotated as optional", key);
                    }
                }

                log.debug("Parameter key is '{}'", key);

                Object value = params.get(key);
                if (value != null) {
                    Object po = createValue(m.getParameterTypes()[0], params.get(key));
                    m.invoke(o, po);
                    alreadySet.add(key);
                }
            }
        }

        return alreadySet;
    }

    public static boolean isSetter(Method m) {
        if (!m.getName().startsWith("set")) {
            return false;
        }

        Class<?>[] types = m.getParameterTypes();
        return types.length == 1;
    }

    public static String getParameterName(Method m) {
        if (!isSetter(m)) {
            return null;
        }

        String key = null;

        Parameter p = m.getAnnotation(Parameter.class);
        if (p != null && p.name() != null && !p.name().isEmpty()) {
            key = p.name();
        } else {
            key = m.getName().substring(3);
            key = Character.toLowerCase(key.charAt(0)) + key.substring(1);
        }

        return key;
    }

    public static boolean hasSetterFor(Object o, String paramName, Class<?> type) {

        for (Method m : o.getClass().getDeclaredMethods()) {

            Class<?>[] types = m.getParameterTypes();
            if (types.length != 1) {
                continue;
            }

            String mn = m.getName();
            if (mn.toLowerCase().equals("set" + paramName)) {
                if (type.isAssignableFrom(types[0])) {
                    return true;
                }
            }
        }

        return false;
    }
}