package stream.node.shell.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ContainerEditPanel extends VerticalPanel {

	int i = 0;
	final AbsolutePanel area = new AbsolutePanel();
	final VerticalPanel rowPanel = new VerticalPanel();
	final PickupDragController pickup;
	final PickupDragController rowPicker;
	final VerticalPanelDropController rowDrop;

	public ContainerEditPanel() {

		area.setHeight("100%");
		area.setWidth("100%");
		add(area);
		area.add(rowPanel);

		pickup = new PickupDragController(area, false);
		pickup.setBehaviorMultipleSelection(false);

		rowPicker = new PickupDragController(area, false);
		rowPicker.setBehaviorMultipleSelection(false);

		rowDrop = new VerticalPanelDropController(rowPanel);
		rowPicker.registerDropController(rowDrop);

		VerticalPanel rows = new VerticalPanel();
		rows.setSpacing(8);

		setWidth("100%");
		setHeight("100%");
	}

	public void addRow() {
		ProcessElement processElement = new ProcessElement("process-" + (i++));

		rowPicker.makeDraggable(processElement, processElement.getLabel());
		rowPanel.add(processElement);
	}
}