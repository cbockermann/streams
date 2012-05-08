/**
 * 
 */
package stream.plotter;

import java.rmi.RemoteException;

import stream.service.Service;
import stream.statistics.StatisticsHistory;

/**
 * <p>
 * A plot service implementation provides a single plot. The plot is a history
 * object, i.e. a sequence of tuples.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface PlotService extends Service {

	public StatisticsHistory getPlot() throws RemoteException;
}
