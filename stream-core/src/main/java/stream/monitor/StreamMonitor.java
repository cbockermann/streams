package stream.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.ProcessContext;
import stream.io.DweetWriter;
import stream.service.Service;

/**
 * @author Hendrik Blom
 *
 */
public abstract class StreamMonitor extends AbstractProcessor implements
		Service {

	static Logger logger = LoggerFactory.getLogger(TimeRate.class);

	protected Boolean log;
	protected Boolean dweet;
	protected String id = "";
	protected DweetWriter dweetWriter;
	protected String thing;
	protected String machine;
	protected String[] keys;

	public StreamMonitor() {
		log = true;
		dweet = false;
		id = "";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getLog() {
		return log;
	}

	public void setLog(Boolean log) {
		this.log = log;
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		if (dweet && keys != null) {
			dweetWriter = new DweetWriter();
			dweetWriter.setThing(thing);
			dweetWriter.setMachine(machine);
			dweetWriter.setId(this.id);
			dweetWriter.setKeys(keys);
			dweetWriter.init(this.context);
		}
	}

}
