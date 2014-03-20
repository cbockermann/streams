/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
import java.lang.reflect.Constructor;
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
	public static Set<String> inject(Object o, Map<String, ?> params,
			Variables variableContext) throws Exception {
		log.debug("Injecting parameters {} into object {}", params, o);

		// this set contains a list of parameters that have been successfully
		// set using
		// accessible fields
		//
		Set<String> alreadySet = new HashSet<String>();

		Object embedded = params.get(BodyContent.KEY);

        //check annotations for parameters
        checkForMissingParametersAndSetters(o, params);


        // now, walk over all methods and check if one of these is a setter of a
		// corresponding
		// key value in the parameter map
		//
		for (Method m : o.getClass().getMethods()) {

			Class<?>[] t = m.getParameterTypes();

			if (DependencyInjection.isServiceSetter(m)) {
				log.debug("Skipping ServiceSetter '{}'", m.getName());
				continue;
			}

			if (DependencyInjection.isSourceSetter(m)) {
				log.debug("Skipping SourceSetter '{}'", m.getName());
				continue;
			}

			if (isQueueSetter(m)) {
				log.debug("Skipping QueueSetter '{}'", m.getName());
				continue;
			}

			if (embedded != null && m.getName().startsWith("set")
					&& t.length == 1 && t[0] == BodyContent.class) {
				log.debug("Setting embedded content...");
				m.invoke(o, new BodyContent(embedded.toString()));
				continue;
			}

			for (String k : params.keySet()) {

				if (m.getName().startsWith("set") && alreadySet.contains(k)) {
					log.debug(
							"Skipping setter '{}' for already injected field {}",
							m.getName(), k);
					continue;
				}

				//
				// if the method corresponds to a parameter of the map, try to
				// call it
				// with the appropriate value
				//
				if (m.getName().equalsIgnoreCase("set" + k)
						&& m.getParameterTypes().length == 1) {

					// Class<?> t = m.getParameterTypes()[0];

					if (t.equals(params.get(k).getClass())) {
						log.debug("Using setter '{}' to inject parameter '{}'",
								m.getName(), k);
						//
						// if the setter's argument type matches the value
						// object's class
						// in the parameter-map, we simply inject that object
						//
						m.invoke(o, params.get(k));
						alreadySet.add(k);

					} else {

						//
						// if the setter's argument does NOT match, we try to
						// create a new,
						// appropriate value for that setter using the
						// string-constructor
						// of the setter's argument type class
						//
						Object po = null;

						if (t[0].isPrimitive()) {
							String in = params.get(k).toString();


							if (t[0] == Double.TYPE)
								po = new Double(in);

							if (t[0] == Integer.TYPE)
								po = new Integer(in);

							if (t[0] == Boolean.TYPE)
								po = Boolean.valueOf(in);

							if (t[0] == Float.TYPE)
								po = new Float(in);

							if (t[0] == Long.TYPE)
								po = new Long(in);

							if (t[0] == Character.TYPE)
								po = new Character(in.charAt(0));

						} else {

							if (t[0].isArray()) {

								log.debug("setter is an array, using split(,) and array creation...");
								String[] args = ParameterUtils.split(params
										.get(k).toString());

								Class<?> content = t[0].getComponentType();
								Constructor<?> c = content
										.getConstructor(String.class);
								Object array = Array.newInstance(content,
										args.length);

								for (int i = 0; i < args.length; i++) {
									Object value = c.newInstance(args[i]);
									Array.set(array, i, value);
								}

								po = array;
							} else {

								try {
									Constructor<?> c = t[0]
											.getConstructor(String.class);
									String s = params.get(k).toString();
									po = c.newInstance(s);
									log.debug("Invoking {}({})", m.getName(),
											po);
								} catch (NoSuchMethodException nsm) {
									log.error(
											"No String-constructor found for type {} of method {}",
											t, m.getName());
								}
								catch (Exception e) {
									throw new IllegalArgumentException("Error in Object:" + o.toString() + " method:"+m + " key:" +k, e);
								}
							}
						}

						m.invoke(o, po);
						alreadySet.add(k);
					}
				}
			}
		}

		return alreadySet;
	}

    /**
     * This methods checks for the XMLParameter annotation in the processor and if the corresponindg parameters
     * or setter methods are missing. A new ParameterException will be thrown in both cases
     *
     * @param o the processor instance
     * @param params the params map from the xml file
     *
     * @throws stream.annotations.ParameterException in case parameter or setter is missing
     */
    private static void checkForMissingParametersAndSetters(Object o, Map<String, ?> params) throws ParameterException {
        //iterate through all fields and get their annotations. If annotation is present check for
        // parameters from xml file
        for ( Field field : o.getClass().getDeclaredFields() ){
            if ( field.isAnnotationPresent( Parameter.class ) ){
                log.debug("Has Parameter annotation " + field.toString());
                boolean required = field.getAnnotation(Parameter.class).required();

                String xmlName = field.getName();

                //check if annotation has a value for name. If thats the case the parameter will be named differently
                // in the xml file.
                if (!field.getAnnotation(Parameter.class).name().isEmpty()){
                    xmlName = field.getAnnotation(Parameter.class).name();
                }

                //if field is required it has to have a value defined in the .xml file
                if (required){
                    boolean xmlHasParameter = params.containsKey(xmlName)
                            && (  params.get(xmlName) != null  );
                    if (!xmlHasParameter){
                        throw new ParameterException("XML is missing parameter " + xmlName + " for field "
                                + field.getName()
                                + " in processor " + o.getClass().getSimpleName());
                    }
                }

                boolean setterMissing = true;
                for(Method m : o.getClass().getDeclaredMethods()){
                    if (m.getName().toLowerCase().equalsIgnoreCase("set" + field.getName())){
                        // we found the matching setter for our parameter. Now lets see if there is another annotation
                        // and create a warning if so
                        if(m.isAnnotationPresent(Parameter.class)){
                            log.warn("There are conflicting annotations for the field " + field.getName()
                            + ". Remove annotation from method " + m.getName() + ".");
                        }
                        setterMissing = false;
                        break;
                    }
                }
                if (setterMissing){
                    throw new ParameterException("Processor " + o.getClass().getSimpleName()
                            + " is missing setter method for field " + field.getName());
                }
             }
        }
    }

    public static void injectSystemProperties(Object object, String prefix)
			throws Exception {
		Map<String, String> params = ParameterDiscovery
				.getSystemProperties(prefix);
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
				log.debug("Found getter '{}' for class '{}'", name,
						learner.getClass());
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

		if (clazz.equals(String.class) || clazz.equals(Long.class)
				|| clazz.equals(Integer.class) || clazz.equals(Double.class)
				|| clazz.equals(Boolean.class) || clazz.equals(File.class)
				|| clazz.equals(BodyContent.class)
				|| clazz.equals(boolean.class) || clazz.equals(int.class)
				|| clazz.isPrimitive() || clazz.equals(Condition.class)
				|| clazz.equals(Map.class))
			return true;

		if (clazz.isPrimitive())
			return true;

		return false;
	}

	public static boolean isNativeType(Class<?> clazz) {
		return clazz.equals(String.class) || clazz.equals(Long.class)
				|| clazz.equals(Integer.class) || clazz.equals(Double.class)
				|| clazz.equals(Boolean.class) || clazz.equals(boolean.class);
	}

	public static boolean isQueueSetter(Method m) {

		if (!m.getName().toLowerCase().startsWith("set")) {
			log.debug("Not a setter -> method not starting with 'set'");
			return false;
		}

		Class<?>[] types = m.getParameterTypes();
		if (types.length != 1) {
			log.debug("Not a setter, parameter types: {}", (Object[]) types);
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