/**
 * 
 */
package com.rapidminer.beans.utils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;

import com.rapidminer.annotations.OperatorInfo;
import com.rapidminer.beans.OperatorBeanDescription;

/**
 * @author chris
 * 
 */
public class BeanFinder {

	static Logger log = LoggerFactory.getLogger(BeanFinder.class);

	public static List<Class<?>> findProcessors(String[] packageNames,
			ClassLoader classLoader) {

		List<Class<?>> result = new ArrayList<Class<?>>();

		for (String pkgName : packageNames) {
			result.addAll(findProcessors(pkgName, classLoader));
		}

		return result;
	}

	public static List<Class<?>> findProcessors(String pkgName,
			ClassLoader classLoader) {
		List<Class<?>> list = new ArrayList<Class<?>>();

		try {
			Class<?>[] classes = ClassFinder.getClasses(pkgName, classLoader);
			for (Class<?> clazz : classes) {

				if (clazz.isInterface()
						|| Modifier.isAbstract(clazz.getModifiers())) {
					continue;
				}

				if (clazz.isAnonymousClass()
						|| clazz.toString().indexOf("$") > 0) {
					continue;
				}

				if (!OperatorBeanDescription.canCreate(clazz))
					continue;

				Description desc = clazz.getAnnotation(Description.class);
				OperatorInfo operatorInfo = clazz
						.getAnnotation(OperatorInfo.class);
				if (desc == null && operatorInfo == null) {
					log.debug(
							"Skipping processor class '{}' due to missing Description annotation...",
							clazz);
					continue;
				}

				list.add(clazz);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("Found {} operator-beans.", list.size());
		return list;
	}
}
