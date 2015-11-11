/**
 * 
 */
package streams.performance;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class PerformanceTree {

	static Logger log = LoggerFactory.getLogger(PerformanceTree.class);
	final String id;
	final PerformanceTree parent;
	final List<PerformanceTree> sibblings = new ArrayList<PerformanceTree>();
	ProcessorStatistics value;

	public PerformanceTree(String id, PerformanceTree parent) {
		this.id = id;
		this.parent = parent;
	}

	public void update(String[] path, ProcessorStatistics data) {
		int depth = depth();
		log.debug("Looking for node at level '{}'", depth());
		if (depth < path.length) {
			String next = path[depth];
			PerformanceTree down = getChild(next);
			if (down == null) {
				log.info("Creating new child for '{}' at node '{}'", next, id);
				down = new PerformanceTree(next, this);
				sibblings.add(down);
			}

			down.update(path, data);
		} else {
			log.debug("Updating data for node '{}'", id);
			value = data;
		}
	}

	public PerformanceTree getChild(String id) {
		for (PerformanceTree ch : sibblings) {
			if (ch.id.equals(id)) {
				return ch;
			}
		}
		return null;
	}

	public void add(PerformanceTree child) {
		sibblings.add(child);
	}

	public boolean isLeaf() {
		return sibblings.isEmpty();
	}

	public ProcessorStatistics value() {
		return null;
	}

	public int depth() {
		if (parent == null) {
			return 0;
		} else {
			return parent.depth() + 1;
		}
	}

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

			if (value != null) {
				secs = value.processingTime() / 1000.0d;
				System.out.println("-->" + id + " >>  " + this.value.itemsProcessed() + " items processed in "
						+ f.format(value.processingTime) + " ms  during overall interval of "
						+ (value.end() - value.start()) + " ms  => " + f.format(items.doubleValue() / secs)
						+ " items/second");
			} else {

				System.out.println("-->" + id + "  :: " + allItemsProcessed() + " items processed in " + f.format(secs)
						+ " seconds => " + f.format(items.doubleValue() / secs) + " items/second");
			}
		}
		for (PerformanceTree ch : sibblings) {
			ch.print();
		}
	}

	public double startInterval() {
		Double min = Double.MAX_VALUE;
		if (value != null) {
			min = Math.min(value.start(), min);
		}

		for (PerformanceTree t : sibblings) {
			min = Math.min(t.startInterval(), min);
		}

		return min;
	}

	public double endInterval() {
		Double max = null;
		if (value != null) {
			max = value.end() * 1.0;
		}

		for (PerformanceTree t : sibblings) {
			if (max == null) {
				max = t.endInterval();
			}

			max = Math.max(t.endInterval(), max);
		}

		return max;
	}

	public long allItemsProcessed() {
		long items = 0;

		if (value == null && !this.sibblings.isEmpty()) {
			for (PerformanceTree t : sibblings) {
				items += t.allItemsProcessed();
			}
			return items;
		} else {

			if (this.value != null) {
				return value.itemsProcessed();
			}
		}
		return items;
	}
}
