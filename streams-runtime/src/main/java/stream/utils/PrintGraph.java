/**
 * 
 */
package stream.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.Source;
import stream.service.Service;
import streams.application.ComputeGraph;

/**
 * @author chris
 *
 */
public class PrintGraph {

    static Logger log = LoggerFactory.getLogger(PrintGraph.class);

    protected static String toString(ComputeGraph g) {

        log.info("######## Sources ########");
        for (Object o : g.getSources()) {
            if (o instanceof Source) {
                log.info("########" + o.toString() + "########");
                for (Object t : g.getTargets(o)) {
                    log.info("\t==> " + t.toString());
                }
            }
        }

        log.info("######## RootSources ########");
        for (Object o : g.getRootSources()) {
            log.info(o.toString());
        }

        // log.info("######## Targets ########");
        // for (Object o : g.getTargets()) {
        // log.info(o.toString());
        // }

        log.info("######## NonRefSinks ########");
        for (Object o : g.getNonRefQueues()) {
            log.info("########" + o.toString() + "########");
            for (Object t : g.getSourcesFor(o)) {
                log.info("\t==> " + t.toString());
            }

        }

        return "";
    }

    public static String print(ComputeGraph graph) {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);

        final String prefix = "  ";

        out.println(prefix + "{");

        Iterator<String> it = graph.sources().keySet().iterator();
        if (it.hasNext()) {
            out.println(prefix + " 'sources': [");
            while (it.hasNext()) {
                String src = it.next();
                Source source = (Source) graph.sources().get(src);
                out.print(prefix + "\t{ 'id': '" + source.getId() + "', 'class': '"
                        + source.getClass().getCanonicalName() + "' }");
                if (it.hasNext()) {
                    out.println(",");
                } else {
                    out.println("");
                }
            }
            out.println(prefix + "   ],");
        }

        it = graph.services().keySet().iterator();
        if (it.hasNext()) {
            out.println(prefix + " 'services': [");
            while (it.hasNext()) {
                String id = it.next();
                Service p = graph.services().get(id);
                out.print(prefix + "\t{ 'id': '" + id + "',  'class': '" + p.getClass().getCanonicalName() + "' }");
                if (it.hasNext()) {
                    out.println(",");
                } else {
                    out.println("");
                }
            }
            out.println("  ]");
        }

        out.println(prefix + " 'processes': [");
        it = graph.processes().keySet().iterator();
        while (it.hasNext()) {
            String id = it.next();
            stream.Process p = graph.processes().get(id);
            out.print(prefix + "\t{ 'id': '" + id + "', 'input': '" + p.getInput().getId() + "', 'class': '"
                    + p.getClass().getCanonicalName() + "' }");
            if (it.hasNext()) {
                out.println(",");
            } else {
                out.println("");
            }
        }
        out.println(prefix + "  ]");

        out.println(prefix + "}");
        out.flush();
        out.close();
        return sw.toString();
    }
}