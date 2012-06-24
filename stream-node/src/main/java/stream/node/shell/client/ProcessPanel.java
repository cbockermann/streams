/**
 * 
 */
package stream.node.shell.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author chris
 * 
 */
public class ProcessPanel extends HorizontalPanel {

	String name;
	HorizontalPanel elements = new HorizontalPanelWithSpaces();

	public ProcessPanel() {
		setStylePrimaryName("process-panel");
		Label spacerLabel = new Label("");
		spacerLabel.setStylePrimaryName("process-panel-spacer");
		super.add(spacerLabel);
	}

	@Override
	public void add(Widget w) {
		super.insert(w, getWidgetCount() - 1);
	}

	@Override
	public void insert(Widget w, int beforeIndex) {
		if (beforeIndex == getWidgetCount()) {
			beforeIndex--;
		}
		super.insert(w, beforeIndex);
	}
}
