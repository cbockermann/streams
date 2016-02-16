/**
 * 
 */
package stream.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Process;
import stream.io.Sink;
import streams.application.ComputeGraph;

/**
 * @author chris
 *
 */
public class Supervisor implements ProcessListener {

    static Logger log = LoggerFactory.getLogger(Supervisor.class);

    List<stream.Process> runningProcesses = new ArrayList<stream.Process>();

    final AtomicInteger running = new AtomicInteger(0);
    final AtomicInteger errors = new AtomicInteger(0);
    final AtomicInteger finished = new AtomicInteger(0);

    ComputeGraph dependencies;

    Map<Process, Set<Sink>> processOutlets = new HashMap<Process, Set<Sink>>();

    public Supervisor(ComputeGraph graph) {
        this.dependencies = graph;
    }

    /**
     * @see stream.runtime.ProcessListener#processStarted(stream.Process)
     */
    @Override
    public void processStarted(Process p) {
        if (p instanceof Monitor) {
            log.info("Monitor #{} started", p);
            return;
        }

        log.debug("Process  #{}  started.", p);
        int run = running.incrementAndGet();
        runningProcesses.add(p);

        Set<Sink> sinks = collectSinks(p);
        log.debug("   process #{} is writing to {} sinks", sinks.size());
        processOutlets.put(p, sinks);

        log.debug("{} processes running.", run);

    }

    /**
     * @see stream.runtime.ProcessListener#processError(stream.Process,
     *      java.lang.Exception)
     */
    @Override
    public void processError(Process p, Exception e) {
        errors.incrementAndGet();
    }

    /**
     * @see stream.runtime.ProcessListener#processFinished(stream.Process)
     */
    @Override
    public void processFinished(Process p) {
        int run = running.decrementAndGet();
        finished.incrementAndGet();
        runningProcesses.remove(p);

        log.debug("Process  #{}  finished.", p);
        Set<Sink> outs = processOutlets.get(p);
        if (outs == null) {
            outs = new HashSet<Sink>();
        }

        log.debug("   process has {} outgoing targets: {}", outs.size(), outs);

        Set<Sink> outlets = processOutlets.remove(p);
        for (Sink sink : outlets) {
            int refCount = 0;

            for (Process pr : processOutlets.keySet()) {
                Set<Sink> prOuts = processOutlets.get(pr);
                if (prOuts.contains(sink)) {
                    refCount++;
                }
            }
            if (refCount == 0) {
                log.debug("Reference count of {} is 0, closing sink!", sink);
                try {
                    // the call to "dependencies.remove(p)"
                    // below will automatically call sink.close(), that's why we
                    // commented out the call here
                    // sink.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                log.debug("Reference count for {} is: {}", sink, refCount);
            }
        }

        if (log.isTraceEnabled()) {
            printTargets(p, 0);
        }

        dependencies.remove(p);

        log.debug("{} processes running.", run);
    }

    public void printTargets(Object src, int depth) {

        String prefix = "";
        for (int i = 0; i < depth; i++) {
            prefix += "  ";
        }

        Set<Object> outs = dependencies.getTargets(src);
        for (Object out : outs) {

            log.debug(prefix + " " + out);
            if (out instanceof stream.io.Sink) {
                break;
            } else {
                printTargets(out, depth + 1);
            }
        }
    }

    public int processesRunning() {
        log.debug("Active processes: {}", runningProcesses);
        return running.get();
    }

    public Set<Sink> collectSinks(Object p) {
        Set<Sink> sinks = new HashSet<Sink>();
        Set<Object> outs = dependencies.getTargets(p);

        for (Object out : outs) {
            if (out instanceof Sink) {
                sinks.add((Sink) out);
            } else {
                sinks.addAll(collectSinks(out));
            }
        }

        return sinks;
    }
}