/**
 * 
 */
package stream.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Process;
import stream.app.ComputeGraph;

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

    public Supervisor(ComputeGraph graph) {
        this.dependencies = graph;
    }

    /**
     * @see stream.runtime.ProcessListener#processStarted(stream.Process)
     */
    @Override
    public void processStarted(Process p) {
        log.info("Process  #{}  started.", p);
        int run = running.incrementAndGet();
        runningProcesses.add(p);
        log.info("{} processes running.", run);
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

        log.info("Process  #{}  finished.", p);
        Set<Object> outs = dependencies.getTargets(p);
        log.info("   process has {} outgoing targets: {}", outs.size(), outs);

        log.info("{} processes running.", run);
    }

    public int processesRunning() {
        log.info("Active processes: {}", runningProcesses);
        return running.get();
    }
}