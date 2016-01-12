/**
 * 
 */
package stream.runtime.setup;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.ParameterException;

/**
 * This class provides the mapping of an input string to the desired output
 * object. The class can handle parsing of native types as well as all types,
 * that can be instantiated with a string-arg constructor.
 * 
 * In addition, arrays of all the supported types can be created from a comma-
 * separated list of string values.
 * 
 * @author Christian Bockermann
 *
 */
public class ParameterValueMapper {

    static Logger log = LoggerFactory.getLogger(ParameterValueMapper.class);

    public Object createValue(Class<? extends Object> type, Object str) throws ParameterException {
        Object po = null;

        if (type.equals(str.getClass())) {
            // direct use of the object possible!
            return str;
        }

        if (type.isPrimitive()) {
            String in = str.toString();

            if (type == Double.TYPE)
                po = new Double(in);

            if (type == Integer.TYPE)
                po = new Integer(in);

            if (type == Boolean.TYPE)
                po = Boolean.valueOf(in);

            if (type == Float.TYPE)
                po = new Float(in);

            if (type == Long.TYPE)
                po = new Long(in);

            if (type == Character.TYPE)
                po = new Character(in.charAt(0));

        } else {

            if (type.isArray()) {

                log.debug("setter is an array, using split(,) and array creation...");
                String[] args = ParameterUtils.split(str.toString());

                try {
                    Class<?> content = type.getComponentType();
                    Constructor<?> c = content.getConstructor(String.class);
                    Object array = Array.newInstance(content, args.length);

                    for (int i = 0; i < args.length; i++) {
                        Object value = c.newInstance(args[i]);
                        Array.set(array, i, value);
                    }
                    po = array;
                } catch (NoSuchMethodException nsm) {
                    throw new ParameterException(
                            "Class '" + type.getComponentType() + "' does not provide String-arg constructor!");
                } catch (InvocationTargetException ite) {
                    throw new ParameterException("InvocationTargetException while creating object of class '"
                            + type.getComponentType() + "' from string '" + str + "'!");
                } catch (IllegalAccessException iae) {
                    throw new ParameterException(
                            "No access to call String-arg constructor for class '" + type.getComponentType() + "'!");
                } catch (InstantiationException ie) {
                    throw new ParameterException(
                            "Failed to instantiate object from class '" + type.getComponentType() + "'!");
                }
            } else {

                try {
                    Constructor<?> c = type.getConstructor(String.class);
                    String s = str.toString();
                    po = c.newInstance(s);
                    log.debug("Invoking {}({})", po);
                } catch (NoSuchMethodException nsm) {
                    throw new ParameterException(
                            "Class '" + type.getComponentType() + "' does not provide String-arg constructor!");
                } catch (InvocationTargetException ite) {
                    throw new ParameterException("InvocationTargetException while creating object of class '"
                            + type.getComponentType() + "' from string '" + str + "'!");
                } catch (IllegalAccessException iae) {
                    throw new ParameterException(
                            "No access to call String-arg constructor for class '" + type.getComponentType() + "'!");
                } catch (InstantiationException ie) {
                    throw new ParameterException(
                            "Failed to instantiate object from class '" + type.getComponentType() + "'!");
                }
            }
        }

        return po;
    }
}
