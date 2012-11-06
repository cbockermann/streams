/**
 * 
 */
package com.rapidminer.beans.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.plugin.GenericStreamOperator;
import stream.plugin.OperatorNamingService;
import stream.runtime.ContainerContext;
import stream.runtime.setup.ServiceInjection;
import stream.runtime.setup.ServiceReference;
import stream.runtime.shutdown.DependencyGraph;
import stream.service.Service;

import com.rapidminer.parameter.ParameterType;

/**
 * <p>
 * This class looks up any referenced services and injects these into the
 * operator/processor.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class ServiceBinding {

	static Logger log = LoggerFactory.getLogger(ServiceBinding.class);

	public static void findAndBind(GenericStreamOperator op) {

		List<ServiceReference> refs = new ArrayList<ServiceReference>();
		Map<String, Service> serviceMap = new HashMap<String, Service>();

		List<ParameterType> types = op.getParameterTypes();
		for (ParameterType type : types) {
			if (type instanceof ParameterTypeService) {
				ParameterTypeService serviceParam = (ParameterTypeService) type;
				try {
					String serviceName = op.getParameter(serviceParam.getKey());
					log.info("Operator {} references service {}", op,
							serviceName);

					Service service = OperatorNamingService.getInstance()
							.lookup(serviceName, Service.class);
					log.info("Found service for name '{}': {}", serviceName,
							service);

					if (service != null) {
						serviceMap.put(serviceName, service);
					}

					ServiceReference ref = new ServiceReference(serviceName,
							op.getProcessor(), serviceParam.getKey(),
							serviceParam.serviceType);
					refs.add(ref);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		try {
			ContainerContext ctx = new ContainerContext();
			log.info("Found {} service references: {}", refs.size(), refs);
			ServiceInjection.injectServices(refs, ctx, new DependencyGraph());
		} catch (Exception e) {
			log.error("Failed to inject services: {}", e.getMessage());
		}
	}
}
