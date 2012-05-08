/**
 * 
 */
package stream.plugin;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import stream.data.Data;
import stream.data.DataFactory;
import stream.learner.PredictionService;

/**
 * @author chris
 * 
 */
public class CheckRegistry {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.setProperty("java.rmi.server.hostname", "127.0.0.1");
		Registry reg = LocateRegistry.getRegistry(9105);
		System.out.println("using registry " + reg);
		String[] keys = reg.list();
		for (String service : keys) {
			System.out.println("   " + service);
		}

		PredictionService o = (PredictionService) reg.lookup("NaiveBayes");
		System.out.println("Found: " + o);

		Data item = DataFactory.create();
		item.put("Temperature", "63.0");
		item.put("Humidity", "78.0");
		item.put("Outlook", "rain");
		item.put("Wind", "true");

		Serializable prediction = o.predict(item);
		System.out.println("predict( " + item + " ) = " + prediction);
	}
}
