package stream.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.ProcessContext;

/**
 * @author Hendrik Blom
 * 
 */
public class CheckDataAvailability extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(CheckDataAvailability.class);

	protected String[] keys;
	protected String scope;

	public CheckDataAvailability() {
		super();
		scope = "data";
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public Data process(Data input) {
		StringBuilder sb = new StringBuilder();
		boolean complete = true;

		if (scope.equals("data")) {
			for (String key : keys) {
				if (input.get(key) == null) {
					sb.append(key);
					sb.append("\n");
					complete = false;
				}
			}

		}
		if (scope.equals("process")) {
			for (String key : keys) {
				if (context.get(key) == null) {
					sb.append(key);
					sb.append("\n");
					complete = false;
				}
			}

		}
		if (!complete) {
			log.info("Not all data with the defined keys are aavailable. Missing keys:\n"
					+ sb.toString());
			input.put("dataavailable", false);
		} else {
			log.info("All data are available.");
			input.put("dataavailable", true);

		}
		return input;
	}
}