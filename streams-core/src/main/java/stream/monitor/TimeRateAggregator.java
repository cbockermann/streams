package stream.monitor;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;

public class TimeRateAggregator extends AbstractProcessor {

	TimeRateService[] services;

	public TimeRateService[] getServices() {
		return services;
	}

	public void setServices(TimeRateService[] services) {
		this.services = services;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
	}

	@Override
	public Data process(Data data) {
		double result = 0;
		for (TimeRateService s : services) {
			result += s.getTimeRate();
		}

		data.put("agg:timeRate", result);
		return data;
	}
}
