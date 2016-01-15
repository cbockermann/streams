/**
 * 
 */
package stream.runtime.setup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.annotations.Service;
import stream.app.ComputeGraph.ServiceRef;
import stream.runtime.DependencyInjection;

/**
 *
 * @author Christian Bockermann
 * 
 */
public class ServiceFieldInjection {

    static Logger log = LoggerFactory.getLogger(ServiceFieldInjection.class);

    public List<ServiceRef> getServiceRefsForFields(Processor p, Map<String, String> params) {
        List<ServiceRef> refs = new ArrayList<ServiceRef>();

        for (Field field : p.getClass().getDeclaredFields()) {

            if (DependencyInjection.isServiceImplementation(field.getType())) {
                String property = field.getName();

                @SuppressWarnings("unchecked")
                Class<? extends stream.service.Service> type = (Class<? extends stream.service.Service>) field
                        .getType();

                Service info = field.getAnnotation(Service.class);
                if (info != null && !info.name().isEmpty()) {
                    property = info.name();
                    log.info("Using property '{}' from @Service annotation", info.name());
                } else {
                    log.info("Using property '{}' derived from field name", field.getName());
                }

                if (DependencyInjection.hasServiceSetter(property, p) != null) {
                    log.info("A service-setter method for '" + property
                            + "' exists, skipping field-injection for this service field.");
                } else {
                    String refStr = params.get(property);
                    if (refStr == null) {
                        throw new RuntimeException("Found service field for '" + field.getName()
                                + "', but related XML attribute '" + property + "' is not provided!");
                    } else {
                        refs.add(new ServiceRef(p, property, refStr.split(","), type));
                    }
                }
            }

        }

        return refs;
    }
}
