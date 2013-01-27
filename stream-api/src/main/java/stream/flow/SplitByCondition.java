/**
 * 
 */
package stream.flow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Context;
import stream.Data;
import stream.expressions.Condition;
import stream.io.Sink;

/**
 * @author chris
 * 
 */
public class SplitByCondition extends AbstractSplit {

	static Logger log = LoggerFactory.getLogger(SplitByCondition.class);
	final List<RoutingEntry> routes = new ArrayList<RoutingEntry>();
	final Context ctx = new SplitContext();
	boolean multiply = false;

	/**
	 * @see stream.flow.Split#getConditions()
	 */
	@Override
	public List<Condition> getConditions() {
		List<Condition> conditions = new ArrayList<Condition>();
		for (RoutingEntry re : routes) {
			conditions.add(re.condition);
		}
		return Collections.unmodifiableList(conditions);
	}

	/**
	 * @see stream.flow.Split#add(stream.expressions.Condition, stream.io.Sink)
	 */
	@Override
	public void add(Condition condition, Sink sink) {
		routes.add(new RoutingEntry(condition, sink));
	}

	/**
	 * @see stream.io.Sink#write(stream.Data)
	 */
	@Override
	public void write(Data item) throws Exception {
		for (RoutingEntry entry : routes) {
			if (entry.condition == null || entry.condition.matches(ctx, item)) {
				log.debug("Sending item to sink {}", entry.sink);
				entry.sink.write(item);

				if (!multiply) {
					log.debug("non-multiplying split, doing first-match-delivery");
					return;
				}
			}
		}

		log.warn(
				"No data-flow matching item {} in split {}. Item will be discarded!",
				item, id);
	}

	public class RoutingEntry {
		public final Condition condition;
		public final Sink sink;

		public RoutingEntry(Condition c, Sink s) {
			condition = c;
			sink = s;
		}
	}

	public class SplitContext implements Context {

		/**
		 * @see stream.Context#resolve(java.lang.String)
		 */
		@Override
		public Object resolve(String variable) {
			return null;
		}
	}
}