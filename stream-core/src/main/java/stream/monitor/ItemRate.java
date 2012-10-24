/**
 * 
 */
package stream.monitor;

import java.text.DecimalFormat;

import stream.Data;
import stream.Processor;

/**
 * @author chris
 * 
 */
public class ItemRate implements Processor {

	Long total = 0L;
	Long start = null;

	Integer every = 1000;

	/**
	 * @return the every
	 */
	public Integer getEvery() {
		return every;
	}

	/**
	 * @param every
	 *            the every to set
	 */
	public void setEvery(Integer every) {
		this.every = every;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (input != null) {

			try {
				total++;
				if (start == null) {
					start = System.currentTimeMillis();
				}

				if (total % every == 0) {
					Long dur = System.currentTimeMillis() - start;
					DecimalFormat fmt = new DecimalFormat("0.000");
					String rate = fmt.format((total.doubleValue() / (dur
							.doubleValue() / 1000.0d)));

					System.out.println(total + " items processed => " + rate
							+ " items per second.");
				}
			} catch (Exception e) {

			}
		}

		return input;
	}

}
