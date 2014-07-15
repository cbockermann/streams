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
package stream.mock;

import java.util.Collection;
import java.util.LinkedList;

import stream.Data;
import stream.io.Barrel;

public class SimpleMockBarrel implements Barrel {

	protected String id;
	protected LinkedList<Data> list = new LinkedList<>();

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	public boolean write(Data item) throws Exception {
		list.add(item);
		return true;
	}

	@Override
	public boolean write(Collection<Data> data) throws Exception {
		return true;
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public Data read() throws Exception {
		return list.pollFirst();
	}

	@Override
	public int clear() {
		int s = list.size();
		list.clear();
		return s;
	}

}
