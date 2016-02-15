/**
 * 
 */
package streams.runtime;

import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.Source;
import stream.runtime.ProcessContainer;

/**
 * @author chris
 *
 */
public class SoftShutdown extends Thread {

    static Logger log = LoggerFactory.getLogger(SoftShutdown.class);
    final ProcessContainer pc;

    public SoftShutdown(ProcessContainer pc) {
        this.pc = pc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        Set<Source> roots = pc.computeGraph().getRootSources();
        log.info("Graph has {} root source elements: ", roots.size(), roots);
        Iterator<Source> it = roots.iterator();
        while (it.hasNext()) {
            Source source = it.next();
            log.info("Removing element '{}' from compute-graph", source);

            Set<Object> to = pc.computeGraph().getTargets(source);
            log.info("{} elements are reading from {}", to.size(), source.getId());

            try {
                source.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                it.remove();
            }

            log.info("Root sources remaining: {}", roots);

            // List<LifeCycle> lifeCycles = pc.computeGraph().remove(source);
            // for (LifeCycle lf : lifeCycles) {
            // try {
            // log.debug("Finishing LifeCycle object {}", lf);
            // // lf.finish();
            // } catch (Exception e) {
            // log.error("Failed to end LifeCycle object {}: {}", lf,
            // e.getMessage());
            // if (log.isDebugEnabled())
            // e.printStackTrace();
            // } finally {
            // pc.lifeCycles().remove(lf);
            // }
            // }
        }

        // Iterator<LifeCycle> it = pc.lifeCycles().iterator();
        // while (it.hasNext()) {
        // LifeCycle lc = it.next();
        //
        // try {
        // log.debug("Finishing life-cycle object {}", lc);
        // lc.finish();
        // } catch (Exception e) {
        // log.error("Failed to end life-cycle object {}: {}", lc,
        // e.getMessage());
        // if (log.isDebugEnabled())
        // e.printStackTrace();
        // } finally {
        // it.remove();
        // }
        // }
    }
}
