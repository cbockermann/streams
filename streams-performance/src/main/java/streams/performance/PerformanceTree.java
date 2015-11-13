/**
 * 
 */
package streams.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

/**
 * Performance tree class is collector class for processor statistics. Possible tree can be as
 * following:
 * --> application
 *      --> process
 *          --> processor:0
 *          --> processor:1
 *          ...
 *
 * @author chris
 */
public class PerformanceTree {

	static Logger log = LoggerFactory.getLogger(PerformanceTree.class);

    /**
     *
     */
    final String id;

    /**
     * Link to a parent tree (contains one node more). The 'root' tree has no parent (null).
     */
    final PerformanceTree parent;

    /**
     * List of performance trees. First one is the highest node in the tree with each next tree in
     * the list containing a subtree. It goes on until some performance tree contains no further
     * children trees (leaf) and such trees contain statistics about processors.
     */
    final List<PerformanceTree> sibblings = new ArrayList<PerformanceTree>();

    /**
     * Processor statistics for a given performance tree.
     */
    ProcessorStatistics statistics;

	public PerformanceTree(String id, PerformanceTree parent) {
		this.id = id;
		this.parent = parent;
	}

    /**
     * Update method is called in PerformanceReceiver.Updater every time when there is new processor
     * statistics available. This method is updating the list of all performance trees and goes
     * recursively through all the children performance tree nodes.
     *
     * @param path String representation of performance tree
     * @param data new processor statistics data
     */
    public void update(String[] path, ProcessorStatistics data) {
		int depth = depth();
		log.debug("Looking for node at level '{}'", depth());
		if (depth < path.length) {
			String next = path[depth];
			PerformanceTree down = getChild(next);
			if (down == null) {
				log.info("Creating new child tree for '{}' at node '{}'", next, id);
				down = new PerformanceTree(next, this);
				sibblings.add(down);
			}

			down.update(path, data);
		} else {
			log.debug("Updating data for node '{}'", id);
			statistics = data;
		}
	}

    /**
     * Find child node with a given id in a performance tree.
     *
     * @param id node id to be find
     * @return child node if such node exists, otherwise null
     */
    public PerformanceTree getChild(String id) {
		for (PerformanceTree child : sibblings) {
			if (child.id.equals(id)) {
				return child;
			}
		}
		return null;
	}

    /**
     * Calculate recursively depth of a performance tree using the depth of its parent.
     *
     * @return depth of performance tree
     */
    public int depth() {
		if (parent == null) {
			return 0;
		} else {
			return parent.depth() + 1;
		}
	}

    /**
     * Print collected statistics to system output.
     */
	public void print() {
		for (int i = 0; i < depth(); i++) {
			System.out.print("  ");
		}
		if (depth() < 1) {
			System.out.println("-->" + id);
		} else {
			final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			final DecimalFormat f = new DecimalFormat("0.0000", dfs);
			Double secs = (endInterval() - startInterval()) / 1000.0;
			Long items = allItemsProcessed();

			if (statistics != null) {
				secs = statistics.processingTime() / 1000.0d;
                System.out.println("-->" + id + " >>  "
                        + this.statistics.itemsProcessed() + " items processed in "
                        + f.format(statistics.processingTime) + " ms  during overall interval of "
                        + (statistics.end() - statistics.start()) + " ms  => "
                        + f.format(items.doubleValue() / secs) + " items/second");
            } else {
				System.out.println("-->" + id + "  :: " + allItemsProcessed()
                        + " items processed in " + f.format(secs)
						+ " seconds => " + f.format(items.doubleValue() / secs) + " items/second");
			}
		}
		for (PerformanceTree ch : sibblings) {
			ch.print();
		}
	}

	public double startInterval() {
		Double min = Double.MAX_VALUE;
		if (statistics != null) {
			min = Math.min(statistics.start(), min);
		}

		for (PerformanceTree tree : sibblings) {
			min = Math.min(tree.startInterval(), min);
		}

		return min;
	}

	public double endInterval() {
		Double max = null;
		if (statistics != null) {
			max = statistics.end() * 1.0;
		}

		for (PerformanceTree tree : sibblings) {
			if (max == null) {
				max = tree.endInterval();
			}

			max = Math.max(tree.endInterval(), max);
		}

		return max;
	}

    /**
     * Calculate recursively number of all processed items.
     *
     * @return number of processed items
     */
    public long allItemsProcessed() {
		long items = 0;

		if (statistics == null && !sibblings.isEmpty()) {
			for (PerformanceTree tree : sibblings) {
				items += tree.allItemsProcessed();
			}
			return items;
		} else {

			if (statistics != null) {
				return statistics.itemsProcessed();
			}
		}
		return items;
	}
}
