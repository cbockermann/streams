/**
 * 
 */
package stream.generator;

import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractStream;
import stream.io.SourceURL;

/**
 * @author chris
 * 
 */
public class SinusWave extends AbstractStream {

	Double index = 0.0d;
	Double amplitude = 1.0;
	Double frequency = 1.0;
	String key = "sin(t)";

	public SinusWave() {
		super((SourceURL) null);
	}

	/**
	 * @see stream.io.AbstractStream#read()
	 */
	@Override
	public Data readNext() throws Exception {
		Data instance = DataFactory.create();
		Double value = amplitude * Math.sin(frequency * index);
		instance.put("t", index);
		instance.put(key, value);
		index += 0.01;
		return instance;
	}

	/**
	 * @return the amplitude
	 */
	public Double getAmplitude() {
		return amplitude;
	}

	/**
	 * @param amplitude
	 *            the amplitude to set
	 */
	public void setAmplitude(Double amplitude) {
		this.amplitude = amplitude;
	}

	/**
	 * @return the frequency
	 */
	public Double getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency
	 *            the frequency to set
	 */
	public void setFrequency(Double frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
}
