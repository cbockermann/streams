package streams.net;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import streams.performance.PerformanceTree;
import streams.performance.ProcessorStatistics;

/**
 * Merger class extends AbstractUpdater with the update function that merges the consisting data
 * with the incoming performance data.
 */
public class Merger extends AbstractUpdater {

    public Merger(LinkedBlockingQueue<PerformanceReceiver.Update> updates,
                  Map<String, PerformanceTree> performanceTrees) {
        super(updates, performanceTrees);
    }

    /**
     * Merger class uses Update method to add new data or merge the incoming data with the existing
     * ones.
     */
    protected void update(PerformanceTree tree, String[] path, ProcessorStatistics stats) {
        tree.addOrMerge(path, stats);
    }
}
