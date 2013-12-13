package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.ProcessContext;
import stream.service.Service;

/**
 * This represents a not distinct index of the stream
 * 
 * @author Hendrik Blom
 * 
 */
public abstract class Index extends AbstractProcessor implements Service {

	static Logger log = LoggerFactory.getLogger(Index.class);

	protected String id;
	protected String indexId;
	protected String indexKey;

	public Index() {
		id = "";
		try {
			reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getId() {
		return id;
	}

	public String getIndexKey() {
		return indexKey;
	}

	public void setIndexKey(String indexKey) {
		this.indexKey = indexKey;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		indexId = "@index:" + id;
	}

}
