package streams.net;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import streams.performance.PerformanceTree;
import streams.performance.ProcessorStatistics;

/**
 * Updater class extends AbstractUpdater with the update function that replaces the consisting data
 * with the incoming performance data.
 */
public class Updater extends AbstractUpdater {

    public Updater(LinkedBlockingQueue<PerformanceReceiver.Update> updates,
                   Map<String, PerformanceTree> performanceTrees) {
        super(updates, performanceTrees);
    }

    /**
     * Updater class uses update method to replace existing performance statistics with the new
     * incoming ones.
     */
    protected void update(PerformanceTree tree, String[] path, ProcessorStatistics stats) {
        tree.update(path, stats);
    }
}
