package stream.test;

import stream.AbstractProcessor;
import stream.Data;

public class SystemPropertyProcessor extends AbstractProcessor {

	private String property;

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;

	}

	@Override
	public Data process(Data data) {
		System.setProperty("property", this.property);
		return data;
	}
}
