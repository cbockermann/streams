package stream.node.shell.client;

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ContainerViewPanel extends SimplePanel {

	Map<Element, ElementWidget> widgets = new HashMap<Element, ElementWidget>();

	final AbsolutePanel area = new AbsolutePanel();
	private PickupDragController dragController;

	public ContainerViewPanel() {
		this.setPixelSize(1024, 640);
		area.setPixelSize(1000, 600);
		area.addStyleName("container-view-area");
		dragController = new PickupDragController(area, true);

		this.setWidget(area);
	}

	public void add(Element el) {
		ElementWidget w = createWidget(el);
		if (w != null) {
			// widgets.put(el, w);
			area.add(w, this.getOffsetWidth() / 2, this.getOffsetHeight() / 2);
			dragController.makeDraggable(w);

			// dropController.drop(w, this.getOffsetWidth() / 2,
			// this.getOffsetHeight() / 2);
		}
	}

	public ElementWidget createWidget(Element element) {
		return new ElementWidget(element);
	}
}
