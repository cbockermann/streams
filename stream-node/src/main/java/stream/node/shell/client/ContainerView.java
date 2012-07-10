/**
 * 
 */
package stream.node.shell.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author chris
 * 
 */
public class ContainerView extends HorizontalPanel implements EntryPoint {

	final VerticalPanel menu = new VerticalPanel();
	final FlowPanel content = new FlowPanel();

	final ContainerList list = new ContainerList();
	final ConfigList configs = new ConfigList();

	public ContainerView() {

		addStyleName("view");

		list.reload();
		configs.reload();

		menu.addStyleName("container-menu");
		content.addStyleName("content-panel");

		menu.add(new ClickLabel("Configurations", configs));
		menu.add(new ClickLabel("Active Containers", list));
		content.add(list);

		add(menu);
		add(content);
	}

	/**
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {
		if (RootPanel.get("ContainerView") != null) {
			RootPanel.get("ContainerView").add(this);
		}
	}

	public void setContent(Widget widget) {
		content.clear();
		content.add(widget);
	}

	public final class ClickLabel extends Label implements ClickHandler {
		final Widget widget;

		public ClickLabel(String txt, final Widget widget) {
			super(txt);
			this.widget = widget;
			addStyleName("objectAction");
			addClickHandler(this);
		}

		/**
		 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent event) {
			setContent(widget);
		}
	}
}