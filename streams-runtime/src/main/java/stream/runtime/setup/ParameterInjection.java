/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.runtime.setup;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.BodyContent;
import stream.annotations.Parameter;
import stream.annotations.ParameterException;
import stream.expressions.Condition;
import stream.io.Sink;
import stream.runtime.DependencyInjection;
import stream.util.Variables;

/**
 * <p>
 * This class provides some utility methods for injecting parameters into an
 * object by the use of Java's reflection API.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class ParameterInjection {

    /* A global logger for this class */
    static Logger log = LoggerFactory.getLogger(ParameterInjection.class);

    /**
     * This method injects a set of parameters to the given object.
     * 
     * @param o
     *            The object to inject parameters into.
     * @param params
     *            The parameters to set on the object.
     * @throws Exception
     * @return Returns the parameter key that have been set using this method.
     */
    public static Set<String> inject(Object o, Map<String, ?> params, Variables variableContext) throws Exception {
        log.debug("Injecting parameters {} into object {}", params, o);

        // check for double parameter definitions
        //
        ParameterValidator.checkClassParameters(o.getClass());

        // the set contains parameters that have been successfully been set
        Set<String> alreadySet = new HashSet<String>();

        // apply parameter injection for all annotated fields
        alreadySet.addAll(new ParameterFieldInjection().inject(o, params, variableContext));

        // apply parameter injection for all annotated methods
        alreadySet.addAll(new ParameterMethodInjection().inject(o, params, variableContext));

        // Object embedded = params.get(BodyContent.KEY);

        Set<String> keys = params.keySet();
        keys.removeAll(alreadySet);
        if (!keys.isEmpty()) {
            log.debug("Missing parameters to be injected: {}", keys);
        }

        // check annotations for parameters
        checkForMissingParametersAndSetters(o, params, alreadySet);
        return alreadySet;
    }

    /**
     * This methods checks for the XMLParameter annotation in the processor and
     * if the corresponindg parameters or setter methods are missing. A new
     * ParameterException will be thrown in both cases
     * 
     * @param o
     *            the processor instance
     * @param params
     *            the params map from the xml file
     * 
     * @throws stream.annotations.ParameterException
     *             in case parameter or setter is missing
     */
    private static void checkForMissingParametersAndSetters(Object o, Map<String, ?> params, Set<String> skip)
            throws ParameterException {

        if (!"false".equalsIgnoreCase(System.getProperty("parameter.validate.setter"))) {
            for (Method m : o.getClass().getMethods()) {

                if (ParameterDiscovery.isSetter(m)) {

                    String name = ParameterDiscovery.getParameterName(m);
                    if (skip.contains(name)) {
                        continue;
                    }

                    Parameter pa = m.getAnnotation(Parameter.class);
                    if (pa != null && pa.required() && !params.containsKey(name)) {
                        throw new ParameterException("Required parameter '" + name + "' for class '" + o.getClass()
                                + "' not provided by configuration!");
                    }
                }
            }
        } else {
            log.debug("Validation of method annotations disabled.");
        }

        // iterate through all fields and get their annotations. If annotation
        // is present check for
        // parameters from xml file

        if (!"false".equalsIgnoreCase(System.getProperty("parameter.validate.fields"))) {

            for (Field field : o.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Parameter.class)) {

                    final Parameter p = field.getAnnotation(Parameter.class);

                    log.debug("Has Parameter annotation " + field.toString());
                    boolean required = p.required();
                    String xmlName = p.name();

                    if (xmlName == null || xmlName.isEmpty()) {
                        xmlName = field.getName();
                    }

                    if (skip.contains(xmlName)) {
                        continue;
                    }

                    // if field is required it has to have a value defined in
                    // the
                    // .xml file
                    if (required) {
                        boolean xmlHasParameter = params.containsKey(xmlName) && (params.get(xmlName) != null);
                        if (!xmlHasParameter) {
                            throw new ParameterException("XML is missing parameter '" + xmlName + "' for field '"
                                    + field.getName() + "' in processor " + o.getClass().getSimpleName());
                        }
                    }

                    // boolean setterMissing = true;
                    // for (Method m : o.getClass().getDeclaredMethods()) {
                    // if (m.getName().toLowerCase().equalsIgnoreCase("set" +
                    // field.getName())) {
                    // // we found the matching setter for our parameter.
                    // // Now
                    // // lets see if there is another annotation
                    // // and create a warning if so
                    // if (m.isAnnotationPresent(Parameter.class)) {
                    // log.warn("There are conflicting annotations for the field
                    // " + field.getName()
                    // + ". Remove annotation from method " + m.getName() +
                    // ".");
                    // }
                    // setterMissing = false;
                    // break;
                    // }
                    // }
                    // if (setterMissing) {
                    // throw new ParameterException("Processor " +
                    // o.getClass().getSimpleName()
                    // + " is missing setter method for field " +
                    // field.getName());
                    // }
                }
            }
        } else {
            log.debug("Field validation disabled!");
        }
    }

    public static void injectSystemProperties(Object object, String prefix) throws Exception {
        Map<String, String> params = ParameterDiscovery.getSystemProperties(prefix);
        inject(object, params, new Variables());
    }

    public static Map<String, String> extract(Object learner) throws Exception {
        Map<String, String> params = new TreeMap<String, String>();

        // iterate over all getters and extract the parameter values (i.e. the
        // return value)
        //
        for (Method m : learner.getClass().getMethods()) {

            String name = m.getName();

            if (name.startsWith("get") && m.getParameterTypes().length == 0) {
                log.debug("Found getter '{}' for class '{}'", name, learner.getClass());
                Class<?> rt = m.getReturnType();
                if (isTypeSupported(rt)) {
                    Object val = m.invoke(learner, new Object[0]);
                    String key = ParameterDiscovery.getParameterName(m);
                    if (key != null && val != null) {
                        if (val.getClass().isArray()) {
                            int len = Array.getLength(val);
                            StringBuffer s = new StringBuffer();
                            for (int i = 0; i < len; i++) {
                                s.append(Array.get(val, i) + "");
                                if (i + 1 < len)
                                    s.append(",");
                            }
                            params.put(key, s.toString());
                        } else {
                            params.put(key, "" + val);
                        }
                    }
                }
            }
        }
        return params;
    }

    public static boolean isGetter(Method m) {
        return ParameterDiscovery.isGetter(m);
    }

    public static boolean hasGetter(Class<?> clazz, String name) {
        try {
            for (Method m : clazz.getMethods()) {
                if (isGetter(m) && m.getName().equalsIgnoreCase("get" + name))
                    return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isTypeSupported(Class<?> clazz) {

        if (DependencyInjection.isServiceImplementation(clazz))
            return false;

        if (clazz.isArray() && isNativeType(clazz.getComponentType())) {
            return true;
        }

        if (clazz.equals(String.class) || clazz.equals(Long.class) || clazz.equals(Integer.class)
                || clazz.equals(Double.class) || clazz.equals(Boolean.class) || clazz.equals(File.class)
                || clazz.equals(BodyContent.class) || clazz.equals(boolean.class) || clazz.equals(int.class)
                || clazz.isPrimitive() || clazz.equals(Condition.class) || clazz.equals(Map.class))
            return true;

        if (clazz.isPrimitive())
            return true;

        return false;
    }

    public static boolean isNativeType(Class<?> clazz) {
        return clazz.equals(String.class) || clazz.equals(Long.class) || clazz.equals(Integer.class)
                || clazz.equals(Double.class) || clazz.equals(Boolean.class) || clazz.equals(boolean.class);
    }

    public static boolean isQueueSetter(Method m) {

        if (!m.getName().toLowerCase().startsWith("set")) {
            log.trace("Not a setter -> method not starting with 'set'");
            return false;
        }

        Class<?>[] types = m.getParameterTypes();
        if (types.length != 1) {
            log.trace("Not a setter, parameter types: {}", (Object[]) types);
            return false;
        }

        Class<?> type = types[0];
        if (!type.isArray()) {
            if (Sink.class.isAssignableFrom(type)) {
                log.debug("Found setter for type '{}': {}", Sink.class, m);
                return true;
            }

        } else {

            Class<?> ct = type.getComponentType();
            if (ct != null && Sink.class.isAssignableFrom(ct)) {
                log.debug("Found setter for array-type '{}': {}", Sink.class, m);
                return true;
            }
        }

        return false;
    }

    public static boolean isQueueArraySetter(Method m) {
        Class<?> type = m.getParameterTypes()[0];
        return type.isArray();
    }
}