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
package stream.plugin.monitoring;

import java.awt.Component;

import javax.swing.JPanel;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class AbstractMonitorView extends JPanel implements Dockable {

	/** The unique class ID */
	private static final long serialVersionUID = -9149075373356121771L;
	final DockKey DOCK_KEY;

	public AbstractMonitorView(String name) {
		DOCK_KEY = new DockKey(name);
	}

	/**
	 * @see com.vlsolutions.swing.docking.Dockable#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/**
	 * @see com.vlsolutions.swing.docking.Dockable#getDockKey()
	 */
	@Override
	public DockKey getDockKey() {
		return DOCK_KEY;
	}
}
