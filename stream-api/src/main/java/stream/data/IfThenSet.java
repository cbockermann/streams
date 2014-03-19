package stream.data;

import stream.ProcessContext;
import stream.flow.If;

/**
 * @author Hendrik Blom
 *
 */
public class IfThenSet extends If {

	private SetValue sv;

	String setKey;
	String[] scope;
	String value;

	public IfThenSet() {
		this.sv = new SetValue();
	}

	public String getSetKey() {
		return sv.getKey();
	}

	public void setSetKey(String setKey) {
		sv.setKey(setKey);
	}

	public String[] getScope() {
		return sv.getScope();
	}

	public void setScope(String[] scope) {
		sv.setScope(scope);
	}

	public String getValue() {
		return sv.getValue();
	}

	public void setValue(String value) {
		sv.setValue(value);
	}

	@Override
	public void init(ProcessContext context) throws Exception {
		super.getProcessors().add(sv);
		super.init(context);
	}

}
