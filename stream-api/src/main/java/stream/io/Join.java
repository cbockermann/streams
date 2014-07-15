/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
package stream.io;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import cern.colt.GenericSorting;
import cern.colt.Swapper;
import cern.colt.function.IntComparator;

/**
 * 
 * 
 * @author Hendrik Blom
 * 
 */
public class Join extends AbstractQueue {

	private static final Logger log = LoggerFactory.getLogger(Join.class);

	protected AtomicBoolean closed = new AtomicBoolean(false);

	private int count;

	private int reads;
	private int read;
	private String[] readQueue;
	private Data[] dataQueue;
	private long[] accs;

	private Set<String> streams;
	private String index;
	private String sync;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	private Map<String, ArrayBlockingQueue<Data>> queues = new ConcurrentHashMap<String, ArrayBlockingQueue<Data>>();

	private Swapper swapper = new Swapper() {
		@Override
		public void swap(int a, int b) {
			long t = accs[a];
			accs[a] = accs[b];
			accs[b] = t;

			Data d = dataQueue[a];
			dataQueue[a] = dataQueue[b];
			dataQueue[b] = d;

			String s = readQueue[a];
			readQueue[a] = readQueue[b];
			readQueue[b] = s;
		}
	};

	private IntComparator comp = new IntComparator() {
		public int compare(int a, int b) {
			return accs[a] == accs[b] ? 0 : (accs[a] < accs[b] ? -1 : 1);
		}
	};

	/** Lock held by take, poll, etc */
	private final ReentrantLock takeLock = new ReentrantLock();

	public String[] getStreams() {
		return streams.toArray(new String[streams.size()]);
	}

	public void setStreams(String[] streams) {
		this.streams = new HashSet<String>();
		for (String unit : streams) {
			this.streams.add(unit);
		}
	}

	public String getSync() {
		return sync;
	}

	public void setSync(String sync) {
		this.sync = sync;

	}

	public Join() {
		super();
		count = 0;
		read = 0;
	}

	/**
	 * Creates a {@code LinkedBlockingQueue} with the given (fixed) capacity.
	 * 
	 * @param capacity
	 *            the capacity of this queue
	 * @throws IllegalArgumentException
	 *             if {@code capacity} is not greater than zero
	 */
	public Join(int capacity) {
		this();
		if (capacity <= 0)
			throw new IllegalArgumentException();
		this.capacity = capacity;
	}

	public int size() {
		int min = Integer.MAX_VALUE;
		for (ArrayBlockingQueue<Data> queue : queues.values()) {
			if (min > queue.size())
				min = queue.size();
		}
		return min;

	}

	public int remainingCapacity() {
		return capacity - size();
	}

	/**
	 * @see stream.io.QueueService#enqueue(stream.Data)
	 */
	public boolean enqueue(Data data) {

		if (data == null)
			return false;

		if (closed.get())
			return false;

		// unit
		final Serializable s2 = data.get(sync);
		String unit = null;
		if (s2 != null)
			unit = s2.toString();
		// if (streams.contains(unit)) {
		try {
			// Serializable s = data.get(index);
			// if (s != null && s instanceof Long)
			// log.info("data from {}: {}", unit, (Long) s);
			ArrayBlockingQueue<Data> queue = queues.get(unit);
			if (queue != null) {
				queue.put(data);
				return true;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// }
		return false;
	}

	/**
	 * @see stream.io.Source#init()
	 */
	@Override
	public void init() throws Exception {
		if (getCapacity() < 1)
			throw new IllegalArgumentException("Invalid queue-capacity '"
					+ getCapacity() + "'!");
		if (index == null || index.isEmpty())
			throw new IllegalArgumentException("Index is not specified");
		if (streams == null || streams.size() == 0)
			throw new IllegalArgumentException("Index is not specified");
		if (sync == null || sync.isEmpty())
			throw new IllegalArgumentException("Index is not specified");
		for (String unit : streams) {
			queues.put(unit, new ArrayBlockingQueue<Data>(capacity));
		}

		reads = streams.size();
		readQueue = new String[reads];
		dataQueue = new Data[reads];
		accs = new long[reads];
		int j = 0;
		for (String unit : streams) {
			readQueue[j] = unit;
			j++;
		}
	}

	/**
	 * @see stream.io.Stream#close()
	 */
	public void close() throws Exception {
		log.debug("Closing queue '{}'...", getId());
		if (closed.get()) {
			log.debug("Queue '{}' already closed.", getId());
			return;
		}
		// log.debug("queue: {}", queue);
		closed.getAndSet(true);

	}

	/**
	 * @see stream.io.AbstractStream#readItem(stream.Data)
	 */
	@Override
	public Data read() throws Exception {
		//
		// StringBuilder sb = new StringBuilder();
		// sb.append("##########");
		// sb.append(this.id);
		// sb.append("##########\n");
		//
		// for (Map.Entry<String, ArrayBlockingQueue<Data>> q :
		// queues.entrySet()) {
		// sb.append(q.getKey());
		// sb.append(":");
		// sb.append(q.getValue().size());
		// sb.append("\n");
		// }
		// log.info(sb.toString());

		log.trace("Reading from queue {}", getId());
		// init (angenommen units ist String array)

		final ReentrantLock takeLock = this.takeLock;
		takeLock.lockInterruptibly();

		Data result = null;
		try {
			if (closed.get() && count == 0) {
				log.debug("Queue '{}' is closed and empty => null", getId());
				return null;
			}
			if (count == 0) {
				read = 0;
				// Read accs
				for (int i = 0; i < reads; i++) {
					dataQueue[i] = queues.get(readQueue[i]).take();
					Serializable s = dataQueue[i].get(index);
					if (s != null && s instanceof Long) {
						accs[i] = (Long) s;
						// log.info("read from {} ts {}", readQueue[i], (Long)
						// s);
					}
				}
				// Sort accs and readqueues
				GenericSorting.quickSort(0, streams.size(), comp, swapper);

				// ?
				count = 0;
				for (int i = 1; i < streams.size(); i++) {
					if (accs[i - 1] == accs[i])
						count = i;
					else
						break;
				}

				++count;
				reads = count;

			}
			--count;
			result = dataQueue[read];

			++read;
		} finally {
			takeLock.unlock();
		}
		return result;
	}

	/**
	 * @see stream.io.QueueService#poll()
	 */
	public Data poll() {
		throw new IllegalAccessError("Not Implemented");
	}

	public Data take() {
		try {
			return read();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @see stream.io.Sink#write(stream.Data)
	 */
	@Override
	public boolean write(Data item) throws Exception {
		return enqueue(item);
	}

	@Override
	public boolean write(Collection<Data> data) throws Exception {
		throw new NotImplementedException();
	}

	/**
	 * @see stream.io.Barrel#clear()
	 */
	@Override
	public int clear() {
		for (ArrayBlockingQueue<Data> queue : queues.values()) {
			queue.clear();
		}
		return -1;
	}

	/**
	 * @see stream.io.QueueService#level()
	 */
	@Override
	public int level() {
		return size();
	}

	/**
	 * @see stream.io.QueueService#capacity()
	 */
	@Override
	public int capacity() {
		return capacity;
	}

	@Override
	public Integer getSize() {
		return size();
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
	}

	public String toString() {
		return "stream.io.BlockingQueue['" + id + "']";
	}

}