/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
package fact.plugin;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import stream.data.Data;
import stream.plugin.data.DataObject;

import com.rapidminer.operator.ResultObject;

/**
 * @author chris
 *
 */
public class FactEventObject extends DataObject implements ResultObject {
	
	/** The unique class ID */
	private static final long serialVersionUID = 2749158655439871907L;
	

	/**
	 * @param data
	 */
	public FactEventObject(Data data) {
		super(data);
	}


	/**
	 * @see com.rapidminer.operator.ResultObject#getName()
	 */
	@Override
	public String getName() {
		return "FactEvent";
	}


	/**
	 * @see com.rapidminer.operator.ResultObject#toResultString()
	 */
	@Override
	public String toResultString() {
		return "FactEventObject";
	}


	/**
	 * @see com.rapidminer.operator.ResultObject#getResultIcon()
	 */
	@Override
	public Icon getResultIcon() {
		return null;
	}


	/**
	 * @see com.rapidminer.operator.ResultObject#getActions()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List getActions() {
		return new ArrayList();
	}
}
