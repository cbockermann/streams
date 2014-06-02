package stream.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Context;
import stream.Data;
import stream.ProcessContext;

public class AssertSubContext extends AbstractProcessor {

	Logger log = LoggerFactory.getLogger(AssertSubContext.class);
	private String[] keys;
	private String ctx;

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String getContext() {
		return ctx;
	}

	public void setContext(String ctx) {
		this.ctx = ctx;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		if (keys == null)
			keys = new String[0];
	}

	@Override
	public Data process(Data data) {
		if (Context.PROCESS_CONTEXT_NAME.equals(ctx))
			for (String key : keys) {
				if (context.get(key) == null) {
					data.put("@subContext:complete", false);
					log.info("Key: {} is missing", key);
					return data;
				}
			}

		if (Context.DATA_CONTEXT_NAME.equals(ctx))
			for (String key : keys) {
				if (data.get(key) == null) {
					data.put("@subContext:complete", false);
					return data;
				}
			}

		data.put("@subContext:complete", true);
		return data;
	}
}
