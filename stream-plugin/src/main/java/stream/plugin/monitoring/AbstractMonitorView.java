/**
 * 
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
