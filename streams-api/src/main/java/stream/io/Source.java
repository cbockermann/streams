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
package stream.io;

/**
 * @author Christian Bockermann,Hendrik Blom
 * 
 */
public interface Source<D> {

    /**
     * @return the id of this stream
     */
    public abstract String getId();

    /**
     * @param id
     *            the id of the stream
     */
    public abstract void setId(String id);

    /**
     * This method will be called by the stream runtime at initialization time.
     * Opening files, URLs or database connections is usually performed in this
     * method.
     * 
     * @throws Exception
     */
    public abstract void init() throws Exception;

    /**
     * Returns the next datum from this stream.
     * 
     * @return
     * @throws Exception
     */
    public abstract D read() throws Exception;

    /**
     * This method is called by the stream runtime environment as the process
     * container is shut down. This can be used to close file handles, streams
     * or database connections.
     * 
     */
    public abstract void close() throws Exception;

}