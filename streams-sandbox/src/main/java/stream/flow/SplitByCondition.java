/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.flow;

import java.util.ArrayList;
import java.util.Collection;
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
	public boolean write(Data item) throws Exception {
		for (RoutingEntry entry : routes) {
			if (entry.condition == null || entry.condition.matches(ctx, item)) {
				log.debug("Sending item to sink {}", entry.sink);
				entry.sink.write(item);

				if (!multiply) {
					log.debug("non-multiplying split, doing first-match-delivery");
					return true;
				}
			}
		}

		log.warn("No data-flow matching item {} in split {}. Item will be discarded!", item, id);
		return true;
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

		@Override
		public boolean contains(String key) {
			// TODO Auto-generated method stub
			return false;
		}

		/**
		 * @see stream.Context#getId()
		 */
		@Override
		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean write(Collection<Data> data) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see stream.io.Sink#init()
	 */
	@Override
	public void init() throws Exception {
	}
}