/**
 * 
 */
package stream.service;

import javax.swing.JComponent;

/**
 * @author Christian Bockermann
 * 
 */
public interface DashboardService extends Service {

	/**
	 * Adds the provided widget to the dashboard.
	 * 
	 * @param id
	 * @param widget
	 */
	public String addWidget(String id, JComponent widget);
}
