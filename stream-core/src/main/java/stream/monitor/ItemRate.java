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
