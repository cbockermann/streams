package stream.io;

import java.util.Collection;

import stream.Data;

/**
 * @author Hendrik Blom
 *
 */
public class TerminalSink implements Sink {

	protected String id;


	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	public boolean write(Data item) throws Exception {
		return true;
	}

	@Override
	public boolean write(Collection<Data> data) throws Exception {
		return true;
	}

	@Override
	public void close() throws Exception {
	}

	

}
