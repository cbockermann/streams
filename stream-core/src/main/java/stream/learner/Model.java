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
package stream.learner;

import java.io.Serializable;

import stream.Data;

/**
 * <p>
 * This interface defines the basic structure of a model. All implementations of
 * specific models must implement this class.
 * </p>
 * 
 * <p>
 * For output methods implement {@link PredictionModel} or
 * {@link DescriptionModel}.
 * </p>
 * 
 * @author beckers, homburg, mueller, schulte, skirzynski
 * 
 */
public interface Model extends Serializable {

	public String getName();

	public Data process(Data item);
}