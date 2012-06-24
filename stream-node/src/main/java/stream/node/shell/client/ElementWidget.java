/**
 * 
 */
package stream.node.shell.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author chris
 * 
 */
public class ElementWidget extends VerticalPanel {

	final Element element;
	final HTML header;
	final SimplePanel content = new SimplePanel();

	public ElementWidget(Element element) {
		this.element = element;
		this.header = new HTML("Element: " + element.getName());

		header.addStyleName("container-element-title");
		addStyleName("container-element");
		add(header);
		content.addStyleName("container-element-content");
		content.add(new Label("Test"));
		add(content);
	}

	public HTML getHeader() {
		return header;
	}
}
