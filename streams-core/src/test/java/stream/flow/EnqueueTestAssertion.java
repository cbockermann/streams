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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.QueueService;

/**
 * @author chris
 * 
 */
public class EnqueueTestAssertion implements QueueService {

	static Logger log = LoggerFactory.getLogger(EnqueueTestAssertion.class);
	public static List<Data> collection = new ArrayList<Data>();

	String id;

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		collection.clear();
	}

	/**
	 * @see stream.io.QueueService#poll()
	 */
	@Override
	public Data poll() {
		return null;
	}

	/**
	 * @see stream.io.QueueService#take()
	 */
	@Override
	public Data take() {
		return null;
	}

	/**
	 * @see stream.io.QueueService#enqueue(stream.Data)
	 */
	@Override
	public boolean enqueue(Data item) {
		synchronized (collection) {
			log.info("Item added to global collection: {}", item);
			collection.add(item);
		}
		return true;
	}

	/**
	 * @see stream.io.QueueService#level()
	 */
	@Override
	public int level() {
		return collection.size();
	}

	/**
	 * @see stream.io.QueueService#capacity()
	 */
	@Override
	public int capacity() {
		return Integer.MAX_VALUE;
	}

	/**
	 * @see stream.io.Sink#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see stream.io.Sink#write(stream.Data)
	 */
	@Override
	public boolean write(Data item) throws Exception {
		return this.enqueue(item);
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * @see stream.io.Sink#write(java.util.Collection)
	 */
	@Override
	public boolean write(Collection<Data> data) throws Exception {
		return false;
	}

	/**
	 * @see stream.io.Sink#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.io.Sink#init()
	 */
	@Override
	public void init() throws Exception {
	}

	/**
	 * @see stream.io.Queue#getSize()
	 */
	@Override
	public Integer getSize() {
		return Integer.MAX_VALUE;
	}

	/**
	 * @see stream.io.Barrel#clear()
	 */
	@Override
	public int clear() {
		int sz = collection.size();
		collection.clear();
		return sz;
	}

	/**
	 * @see stream.io.Source#read()
	 */
	@Override
	public Data read() throws Exception {
		if (collection.isEmpty())
			return null;
		return collection.get(0);
	}

	@Override
	public void setCapacity(Integer limit) {
	}

	@Override
	public Integer getCapacity() {
		return Integer.MAX_VALUE;
	}
}