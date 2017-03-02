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
package stream.runtime;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.PartitionedStream;
import stream.io.Queue;
import stream.io.Sink;
import stream.io.Source;
import stream.io.Stream;
import stream.runtime.setup.ParameterInjection;
import stream.service.NamingService;
import stream.service.Service;
import streams.application.ComputeGraph;
import streams.application.ComputeGraph.ServiceRef;
import streams.application.ComputeGraph.SinkRef;
import streams.application.ComputeGraph.SourceRef;
import streams.application.Reference;

/**
 * 
 * 
 * @author Christian Bockermann
 * 
 */
public class DependencyInjection {

    static Logger log = LoggerFactory.getLogger(DependencyInjection.class);

    final static boolean PARTITIONED_STREAMS = "true"
            .equalsIgnoreCase(System.getProperty("partitioned-streams", "false"));

    final List<Reference> refs = new ArrayList<Reference>();
    final Map<String, List<String>> remapping = new LinkedHashMap<String, List<String>>();

    public void add(Reference ref) {
        log.debug("Adding reference  {} -> {}", ref.object(), ref.ids());
        refs.add(ref);
    }

    public void addAll(Collection<Reference> refs) {
        this.refs.addAll(refs);
    }

    protected boolean hasPartitions(String streamId) {
        return remapping.containsKey(streamId);
    }

    protected String getNextPartition(String streamId) {
        List<String> partitions = remapping.get(streamId);
        if (partitions == null) {
            return streamId;
        }

        if (partitions.isEmpty()) {
            return "_no_more_partitions_left_";
        }

        String part = partitions.remove(0);
        log.info("  {} ~> {}", streamId, part);
        return part;
    }

    public void injectDependencies(ComputeGraph graph, NamingService namingService) throws Exception {

        log.debug("Found {} references to be resolved...", refs);

        Map<String, Stream> streamPartitions = new LinkedHashMap<String, Stream>();
        for (Object s : graph.getAll(Stream.class)) {
            Stream stream = (Stream) s;
            String sid = stream.getId();
            if (PARTITIONED_STREAMS && s instanceof PartitionedStream) {
                log.info("<EXPERIMENTAL>");
                log.info("Assigning partitioned streams to multiple copies (sub-stream - consumer mapping)");
                PartitionedStream ms = (PartitionedStream) s;
                ms.init();
                log.info("Found partitioned-stream {}", ms);
                Map<String, Stream> sub = ms.partitions();

                List<String> parts = new ArrayList<String>();

                for (String id : sub.keySet()) {
                    streamPartitions.put(sid + ":" + id, sub.get(id));
                    parts.add(sid + ":" + id);
                }

                this.remapping.put(sid, parts);
                log.info("</EXPERIMENTAL>");

            } else {
                log.debug("Object {} is not a partitioned-stream", s);
                streamPartitions.put(sid, stream);
            }
        }

        log.debug("Stream partitions are:");
        for (String id : streamPartitions.keySet()) {
            log.debug("  {}  ->   {}", id, streamPartitions.get(id));
        }

        log.debug("graph has {} streams, {} processes", graph.getAll(Stream.class).size(),
                graph.getAll(stream.Process.class).size());

        Iterator<Reference> it = refs.iterator();
        while (it.hasNext()) {
            Reference ref = it.next();
            log.debug("next unresolved reference is {}", ref);
            boolean success = inject(ref, graph, namingService);
            if (success) {
                log.debug("Successfully injected dependency {}", ref);
                it.remove();
            } else {
                log.error("Failed to resolve dependency {}", ref);
            }
        }

        if (!refs.isEmpty()) {
            throw new Exception(refs.size() + " unresolved dependencies!");
        }
    }

    private boolean inject(Reference ref, ComputeGraph graph, NamingService namingService) throws Exception {

        if (ref instanceof SinkRef)
            return inject((SinkRef) ref, graph);

        if (ref instanceof SourceRef)
            return inject((SourceRef) ref, graph);

        if (ref instanceof ServiceRef)
            return inject((ServiceRef) ref, graph, namingService);

        return false;
    }

    private boolean inject(SinkRef ref, ComputeGraph graph) throws Exception {
        log.debug("Injecting sink reference {}", ref);
        String[] refs = ref.ids();
        Sink[] sinks = new Sink[refs.length];
        for (int i = 0; i < sinks.length; i++) {
            sinks[i] = graph.sinks().get(refs[i]);
            if (sinks[i] == null) {
                Queue queue = new stream.io.DefaultBlockingQueue();
                queue.setId(refs[i]);
                graph.addQueue(refs[i], queue);

                if (queue instanceof Service) {
                    graph.addService(refs[i], (Service) queue);
                }
                log.debug("Creating implicitly defined queue: {}", queue);
                sinks[i] = queue;
            }
            log.debug("EDGE:  Adding {} -> {}", ref.object(), sinks[i]);
            graph.add(ref.object(), sinks[i]);
        }
        return injectResolvedReferences(ref.object(), ref.property(), sinks);
    }

    private boolean inject(SourceRef ref, ComputeGraph graph) throws Exception {
        log.debug("Injecting source reference {}", ref);
        String[] refs = ref.ids();
        Source[] sources = new Source[refs.length];
        for (int i = 0; i < sources.length; i++) {
            log.debug("resolving source[{}] ~>  refs[{}] = {}", i, i, refs[i]);
            sources[i] = graph.sources().get(refs[i]);

            if (sources[i] == null) {
                Queue queue = new stream.io.DefaultBlockingQueue();
                queue.setId(refs[i]);
                graph.addQueue(refs[i], queue);

                if (queue instanceof Service) {
                    graph.addService(refs[i], (Service) queue);
                }
                log.debug("Created new Queue:{} {}", queue.getId(), queue);
                sources[i] = queue;
            }

            if (sources[i] instanceof PartitionedStream) {
                log.info("resoling multi-stream reference   {}  ->  {} ", sources[i], ref.object());
                String part = getNextPartition(refs[i]);
                log.info("   re-mapping:   {}  =>  {}", refs[i], part);
                PartitionedStream ms = (PartitionedStream) sources[i];
                log.info("parts:  {}", ms.partitions().keySet());

                String id = part.substring(refs[i].length() + 1);
                Stream stream = ms.partitions().get(id);
                log.info("   re-assigning  {}.input  ~> {} ", ref.object(), stream);
                sources[i] = stream;
            }

            graph.add(sources[i], ref.object());
        }

        return injectResolvedReferences(ref.object(), ref.property(), sources);
    }

    private boolean inject(ServiceRef ref, ComputeGraph graph, NamingService namingService) throws Exception {
        log.debug("Injecting service reference {}", ref);

        String[] refs = ref.ids();
        Service[] services = new Service[refs.length];
        for (int i = 0; i < services.length; i++) {
            services[i] = namingService.lookup(refs[i], ref.type());
            if (services[i] == null) {
                log.error("Referenced service '{}' not found!", refs[i]);
                String obj = ref.object() + "";
                if (ref.object() != null) {
                    obj = ref.object().getClass().getName();
                }

                throw new Exception("Service '" + refs[i] + "' referenced by " + obj + " can not be found!");
            }
        }

        return injectResolvedReferences(ref.object(), ref.property(), services);
    }

    public boolean injectResolvedReferences(Object o, String property, Object[] resolvedRefs) throws Exception {

        for (Field field : o.getClass().getDeclaredFields()) {

            if (DependencyInjection.isServiceImplementation(field.getType())) {
                log.debug("Checking service-field {}", field.getName());

                String prop = field.getName();
                stream.annotations.Service sa = field.getAnnotation(stream.annotations.Service.class);
                if (sa != null && !sa.name().isEmpty()) {
                    prop = sa.name();
                }

                log.debug("Service field '{}' relates to property '{}'", field.getName(), prop);
                if (prop.equals(property)) {
                    Class<?> valueType;

                    if (field.getType().isArray()) {
                        valueType = field.getType().getComponentType();
                        if (valueType.isAssignableFrom(resolvedRefs.getClass().getComponentType())) {
                            boolean orig = field.isAccessible();
                            field.setAccessible(true);
                            field.set(o, resolvedRefs);
                            field.setAccessible(orig);
                            return true;
                        } else {
                            throw new Exception("Array type mis-match! Field '" + field.getName() + "' of type "
                                    + field.getType().getComponentType() + "[] is not assignable from "
                                    + resolvedRefs.getClass().getComponentType() + "[]!");
                        }

                    } else {
                        valueType = field.getType();
                        if (valueType.isAssignableFrom(resolvedRefs[0].getClass())) {
                            boolean orig = field.isAccessible();
                            field.setAccessible(true);
                            field.set(o, resolvedRefs[0]);
                            field.setAccessible(orig);
                            return true;
                        } else {
                            throw new Exception("Field '" + field.getName() + "' is not assignable with object of type "
                                    + resolvedRefs[0].getClass());
                        }
                    }

                }

            }
        }

        String name = "set" + property.toLowerCase();

        for (Method m : o.getClass().getMethods()) {
            if (m.getName().toLowerCase().equalsIgnoreCase(name) && m.getParameterTypes().length == 1) {

                Class<?> type = m.getParameterTypes()[0];
                if (type.isArray()) {

                    Object values = Array.newInstance(type.getComponentType(), resolvedRefs.length);
                    for (int i = 0; i < Array.getLength(values); i++) {
                        Array.set(values, i, (resolvedRefs[i]));
                    }
                    log.debug("Injecting   '{}'.{}   <-- " + values, o, property);
                    log.debug("Calling method  '{}'", m);
                    m.invoke(o, values);

                } else {
                    log.debug("Injecting   '{}'.{}   <-- " + resolvedRefs[0], o, property);
                    log.debug("Calling method  '{}' with arg '{}'", m, resolvedRefs[0]);
                    m.invoke(o, new Object[] { resolvedRefs[0] });
                }
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends Sink> hasSinkSetter(String name, Object o) {

        for (Method m : o.getClass().getMethods()) {

            if (!m.getName().toLowerCase().equals("set" + name))
                continue;

            if (ParameterInjection.isQueueSetter(m)) {
                return (Class<? extends Sink>) m.getParameterTypes()[0];
            }

        }

        return null;
    }

    // Lying method name. This needs to be renamed.
    @SuppressWarnings("unchecked")
    public static Class<? extends Service> hasServiceSetter(String name, Object o) {
        try {

            for (Method m : o.getClass().getMethods()) {
                if (m.getName().equalsIgnoreCase("set" + name) && isServiceSetter(m)) {
                    Class<?>[] types = m.getParameterTypes();
                    if (types.length > 0) {
                        return (Class<? extends Service>) m.getParameterTypes()[0];
                    }
                }
            }

            return null;
        } catch (Exception e) {
            log.error("Failed to determine service-setter: {}", e.getMessage());
            return null;
        }
    }

    /**
     * This method checks whether the provided method is a service setter, i.e.
     * it is a setter method to inject service references into the object.
     * 
     * This requires the method to provide the following characteristics:
     * <ol>
     * <li>Its names starts with <code>set</code> and provides additional
     * characters, i.e. <code>set</code> alone is not enough.</li>
     * <li>It takes a single parameter, which is a service implementation</li>
     * </ol>
     * 
     * @param m
     * @return
     */
    public static boolean isServiceSetter(Method m) {

        if (!m.getName().startsWith("set"))
            return false;

        Class<?>[] paramTypes = m.getParameterTypes();
        if (paramTypes.length != 1)
            return false;

        return isServiceImplementation(paramTypes[0]);
    }

    public static boolean isSourceSetter(Method m) {
        if (!m.getName().startsWith("set"))
            return false;

        Class<?>[] paramTypes = m.getParameterTypes();
        if (paramTypes.length != 1)
            return false;

        return Source.class.isAssignableFrom(paramTypes[0]);
    }

    public static boolean isSinkSetter(Method m) {
        return isSetter(m, Sink.class);
    }

    public static boolean isSinkArraySetter(Method m) {
        return isArraySetter(m, Sink.class);
    }

    public static boolean isSetter(Method m, Class<?> type) {
        if (!m.getName().startsWith("set")) {
            return false;
        }

        Class<?>[] paramTypes = m.getParameterTypes();
        if (paramTypes.length != 1) {
            return false;
        }

        if (paramTypes[0].isArray()) {
            return type.isAssignableFrom(paramTypes[0].getComponentType());
        } else {
            return type.isAssignableFrom(paramTypes[0]);
        }
    }

    public static boolean isArraySetter(Method m, Class<?> type) {
        if (isSetter(m, type)) {
            return m.getParameterTypes()[0].isArray();
        }
        return false;
    }

    /**
     * This method checks whether the given class implements the Service
     * interface.
     * 
     * @param clazz
     * @return
     */
    public static boolean isServiceImplementation(Class<?> clazz) {
        if (Service.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }
}