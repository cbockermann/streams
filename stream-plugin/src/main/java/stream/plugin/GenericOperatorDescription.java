/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
package stream.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.annotations.Description;
import stream.io.DataStream;
import stream.plugin.util.OperatorHelpFinder;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.tools.plugin.Plugin;

/**
 * @author chris
 * 
 */
public class GenericOperatorDescription extends OperatorDescription {

	static Logger log = LoggerFactory
			.getLogger(GenericOperatorDescription.class);

	final Class<?> libClass;

	/**
	 * @param fullyQualifiedGroupKey
	 * @param key
	 * @param clazz
	 * @param classLoader
	 * @param iconName
	 * @param provider
	 */
	public GenericOperatorDescription(String fullyQualifiedGroupKey,
			String key, Class<?> clazz, ClassLoader classLoader,
			String iconName, Plugin provider) {
		super(fullyQualifiedGroupKey, key, GenericStreamOperator.class,
				classLoader, iconName, provider, null);

		Description desc = clazz.getAnnotation(Description.class);
		if (desc != null) {
			setFullyQualifiedGroupKey(desc.group());
		}

		libClass = clazz;

		getOperatorDocumentation().setSynopsis("");
		getOperatorDocumentation().setDocumentation("");

		try {
			String html = OperatorHelpFinder.findOperatorHelp(libClass);
			if (html != null) {
				log.debug("Adding operator-documentation:\n{}", html);
				getOperatorDocumentation().setDocumentation(html);
			}
		} catch (Exception e) {
			log.error("Failed to lookup documentation for class '{}': {}",
					libClass, e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}

	}

	/**
	 * @see com.rapidminer.operator.OperatorDescription#createOperatorInstanceByDescription(com.rapidminer.operator.OperatorDescription)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Operator createOperatorInstanceByDescription(
			OperatorDescription description) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException {

		Operator op = null;

		if (description instanceof GenericOperatorDescription) {
			GenericOperatorDescription sod = (GenericOperatorDescription) description;

			if (Operator.class.isAssignableFrom(libClass)) {
				log.debug("Provided class already is a fully blown operator!");

				Constructor<?> constructor = libClass
						.getConstructor(OperatorDescription.class);

				op = (Operator) constructor.newInstance(sod);
				return op;
			}

			if (DataStream.class.isAssignableFrom(libClass)) {
				log.debug("Class {} is a data-stream class!", libClass);

				log.debug(
						"Creating GenericStreamingSourceOperator for class {}",
						libClass.getName());

				if (DataStreamPlugin.inStreamingMode()) {
					// op = new GenericStreamingSourceOperator(description,
					// (Class<? extends DataStream>) sod.libClass);
				} else {
					op = new GenericStreamReader(description,
							(Class<? extends DataStream>) sod.libClass);
				}
				log.debug("Operator is of class {}", op.getClass());
				return op;
			}

			log.debug("Creating GenericStreamOperator for processor-class {}",
					libClass);

			if (DataStreamPlugin.inStreamingMode()) {
				// op = new GenericStreamingProcessorOperator(description,
				// (Class<? extends Processor>) sod.libClass);
			} else {
				op = new GenericStreamOperator(description,
						(Class<? extends Processor>) sod.libClass);
			}

			log.debug("Operator of class {} is {}", libClass, op.getClass());
			return op;
		}

		log.warn("No support for generic operator instantiation of class {}",
				libClass);
		return super.createOperatorInstanceByDescription(description);
	}

	public static boolean canCreate(Class<?> clazz) {

		if (Operator.class.isAssignableFrom(clazz)) {
			log.debug(
					"Yes, we support direct creation of Operators...(class {})",
					clazz);
			return true;
		}

		if (Processor.class.isAssignableFrom(clazz)) {
			log.debug("Yes, we can create an Operator for Processor-class {}",
					clazz);
			return true;
		}

		if (DataStream.class.isAssignableFrom(clazz)) {
			log.debug("Yes, we can create an Operator for DataStream-class {}",
					clazz);
			return true;
		}

		log.debug("No generic operator-support for class '{}'", clazz);
		return false;
	}
}
