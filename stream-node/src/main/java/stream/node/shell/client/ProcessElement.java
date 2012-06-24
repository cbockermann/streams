/**
 * 
 */
package stream.node.shell.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author chris
 * 
 */
public class ProcessElement extends FlowPanel {

	final Label label;
	final ProcessPanel panel = new ProcessPanel();

	public ProcessElement(String name) {
		label = new Label(name);
		panel.add(label);
		addStyleName("process");
		setWidth("100%");
		setHeight("60px");
		super.add(panel);
	}

	public Label getLabel() {
		return label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.FlowPanel#add(com.google.gwt.user.client
	 * .ui.Widget)
	 */
	@Override
	public void add(Widget w) {
		panel.add(w);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.FlowPanel#insert(com.google.gwt.user.client
	 * .ui.IsWidget, int)
	 */
	@Override
	public void insert(IsWidget w, int beforeIndex) {
		panel.insert(w, beforeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.FlowPanel#insert(com.google.gwt.user.client
	 * .ui.Widget, int)
	 */
	@Override
	public void insert(Widget w, int beforeIndex) {
		panel.insert(w, beforeIndex);
	}
}