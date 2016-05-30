package streams.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import streams.net.PerformanceReceiver.Update;
import streams.performance.PerformanceTree;
import streams.performance.ProcessorStatistics;

/**
 * Abstract Updater class to unify functionality for both updateable and mergeable performance
 * statistics.
 */
public abstract class AbstractUpdater extends Thread {

    static Logger log = LoggerFactory.getLogger(AbstractUpdater.class);

    /**
     * Map of all collected performance statistics represented by performance trees.
     */
    static Map<String, PerformanceTree> performanceTrees = new LinkedHashMap<>();

    static int updateCount = 0;

    /**
     * Queue of the incoming updates.
     */
    static LinkedBlockingQueue<Update> updates = new LinkedBlockingQueue<>();

    public AbstractUpdater(LinkedBlockingQueue<Update> updates,
                           Map<String, PerformanceTree> performanceTrees) {
        AbstractUpdater.updates = updates;
        AbstractUpdater.performanceTrees = performanceTrees;
    }

    public void run() {
        while (true) {
            try {
                Update update = updates.take();

                String[] path = update.path.split("/");
                String app = path[0];

                PerformanceTree tree = performanceTrees.get(app);
                if (tree == null) {
                    tree = new PerformanceTree("", null);
                    performanceTrees.put(app, tree);
                    log.debug("Creating new performance tree for application '{}'", app);
                }

                update(tree, path, update.stats);

                //TODO: make possible to vary this number for printing
                if (updateCount % 10 == 0) {
                    tree.print();
                }

            } catch (Exception e) {
                log.error("Updater thread has been stopped or was interrupted by some exception:" + e);
            }
        }
    }

    /**
     * Update method is used in {@link Updater} and {@link Merger} for two different ways of
     * collecting performance statistics.
     */
    protected abstract void update(PerformanceTree tree, String[] path, ProcessorStatistics stats);
}