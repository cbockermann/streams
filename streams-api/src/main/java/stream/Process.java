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
package stream;

import java.util.List;
import java.util.Map;

import stream.io.Sink;
import stream.io.Source;
import stream.runtime.LifeCycle;

/**
 * <p>
 * This interface defines an abstract process. A process is an active element
 * that will continuously read from a source and emit processed items to a sink
 * (if such a sink is attached).
 * </p>
 * 
 * @author Christian Bockermann, Hendrik Blom
 * 
 */
public interface Process<D> extends LifeCycle {

    /**
     * The data source of this process.
     * 
     * @param The
     *            data source of this process.
     */
    public void setInput(Source<D> ds);

    /**
     * The data source of this process.
     * 
     * @return The data source of this process.
     */
    public Source<D> getInput();

    /**
     * 
     * @param sink
     */
    public void setOutput(Sink<D> sink);

    /**
     * 
     * @return
     */
    public Sink<D> getOutput();

    public void add(Processor p);

    public void remove(Processor p);

    public List<Processor> getProcessors();

    public D process(D item);

    public void execute() throws Exception;

    public Map<String, String> getProperties();

}