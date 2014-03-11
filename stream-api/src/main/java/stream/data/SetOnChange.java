package stream.data;

import stream.ProcessContext;
import stream.flow.OnChange;

/**
 * @author Hendrik Blom
 *
 */
public class SetOnChange extends OnChange {

	private SetValue sv;

	String setKey;
	String[] scope;
	String value;

	public SetOnChange() {
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
