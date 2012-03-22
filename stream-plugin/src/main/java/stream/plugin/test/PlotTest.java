/**
 * 
 */
package stream.plugin.test;

import java.awt.BorderLayout;
import java.util.Random;

import javax.swing.JFrame;

import stream.data.Data;
import stream.data.DataImpl;
import stream.plotter.StreamPlotPanel;

/**
 * @author chris
 * 
 */
public class PlotTest {

	static Random rnd = new Random();

	static String[] keys = new String[] { "X1", "X2", "X3" };

	public static Data createRandomData() {
		Data st = new DataImpl();

		for (String key : keys) {
			st.put(key, rnd.nextDouble());
		}

		String val = "XYZ";
		if (rnd.nextBoolean())
			val = "ABC";

		st.put("ABC", val);

		return st;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		JFrame f = new JFrame();
		StreamPlotPanel plotPanel = new StreamPlotPanel();
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(plotPanel, BorderLayout.CENTER);

		f.setSize(1024, 480);
		f.setVisible(true);

		plotPanel.setSteps(5);

		int i = 0;

		while (true) {
			Data st = createRandomData();
			plotPanel.dataArrived(st);

			if (i == 10)
				plotPanel.removeKey("X2");

			i++;
			Thread.sleep(1000);
		}
	}
}
