/**
 * 
 */
package com.rapidminer.beans.utils;

import java.util.ArrayList;
import java.util.Iterator;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.plugin.OperatorNamingService;
import stream.service.Service;

import com.rapidminer.io.process.XMLTools;
import com.rapidminer.parameter.ParameterTypeCategory;

/**
 * @author chris
 * 
 */
public class ParameterTypeService extends ParameterTypeCategory {

	static Logger log = LoggerFactory.getLogger(ParameterTypeService.class);

	/** The unique class ID */
	private static final long serialVersionUID = -7179102916387012285L;

	private static final String ATTRIBUTE_DEFAULT = "default";

	private static final String ELEMENT_VALUES = "Values";

	private static final String ELEMENT_VALUE = "Value";

	int defaultValue;
	Class<? extends Service> serviceType;
	final OperatorNamingService namingService = OperatorNamingService
			.getInstance();

	/**
	 * @param key
	 * @param description
	 */
	public ParameterTypeService(String key, String description,
			Class<? extends Service> serviceType) {
		super(key, description, new String[0], 0, false);
		this.defaultValue = 0;
		this.serviceType = serviceType;
	}

	/**
	 * @return
	 */
	public String[] getCategories() {

		ArrayList<String> names = new ArrayList<String>(
				namingService.getServiceNames());

		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String name = it.next();
			try {
				Service srv = namingService.lookup(name, Service.class);
				if (srv != null && serviceType.isAssignableFrom(srv.getClass())) {
					log.info(
							"Found possible service match for type {} with name {}",
							srv.getClass().getCanonicalName(), name);
				} else {
					it.remove();
				}
			} catch (Exception e) {
				log.error("Failed to lookup service: {}", e.getMessage());
				if (Log.isDebugEnabled())
					e.printStackTrace();
			}
		}

		log.debug("Returning the following services: {}", names);
		return names.toArray(new String[names.size()]);
	}

	/**
	 * @see com.rapidminer.parameter.ParameterTypeCategory#getDefault()
	 */
	public int getDefault() {
		return defaultValue;
	}

	/**
	 * @see com.rapidminer.parameter.ParameterTypeCategory#getDefaultValue()
	 */
	@Override
	public Object getDefaultValue() {
		if (defaultValue == -1) {
			return null;
		} else {
			String[] vals = getCategories();
			if (defaultValue < vals.length)
				return vals[defaultValue];
			return null;
		}
	}

	/**
	 * @see com.rapidminer.parameter.ParameterTypeCategory#setDefaultValue(java.lang.Object)
	 */
	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = (Integer) defaultValue;
	}

	/**
	 * @see com.rapidminer.parameter.ParameterTypeCategory#getCategory(int)
	 */
	public String getCategory(int index) {
		return getCategories()[index];
	}

	/**
	 * @see com.rapidminer.parameter.ParameterTypeCategory#getIndex(java.lang.String)
	 */
	public int getIndex(String string) {
		for (int i = 0; i < getCategories().length; i++) {
			if (getCategories()[i].equals(string)) {
				return Integer.valueOf(i);
			}
		}
		// try to interpret string as number
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * @see com.rapidminer.parameter.ParameterTypeCategory#toString(java.lang.Object)
	 */
	@Override
	public String toString(Object value) {
		try {
			if (value == null)
				return null;
			int index = Integer.parseInt(value.toString());
			if (index >= getCategories().length)
				return "";
			return super.toString(getCategories()[index]);
		} catch (NumberFormatException e) {
			return super.toString(value);
		}
	}

	/**
	 * @see com.rapidminer.parameter.ParameterTypeCategory#getValues()
	 */
	public String[] getValues() {
		return getCategories();
	}

	/**
	 * @see com.rapidminer.parameter.ParameterTypeCategory#getRange()
	 */
	@Override
	public String getRange() {
		StringBuffer values = new StringBuffer();
		for (int i = 0; i < getCategories().length; i++) {
			if (i > 0)
				values.append(", ");
			values.append(getCategories()[i]);
		}
		return values.toString() + "; default: "
				+ getCategories()[defaultValue];
	}

	/**
	 * @see com.rapidminer.parameter.ParameterTypeCategory#getNumberOfCategories()
	 */
	public int getNumberOfCategories() {
		return getCategories().length;
	}

	/**
	 * @see com.rapidminer.parameter.ParameterTypeCategory#writeDefinitionToXML(org.w3c.dom.Element)
	 */
	@Override
	protected void writeDefinitionToXML(Element typeElement) {
		typeElement.setAttribute(ATTRIBUTE_DEFAULT, defaultValue + "");

		Element valuesElement = XMLTools.addTag(typeElement, ELEMENT_VALUES);
		for (String category : getCategories()) {
			XMLTools.addTag(valuesElement, ELEMENT_VALUE, category);
		}
	}
}