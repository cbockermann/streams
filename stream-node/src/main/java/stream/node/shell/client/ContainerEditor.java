package stream.node.shell.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ContainerEditor implements EntryPoint {

	int last = 0;

	final VerticalPanel content = new VerticalPanel();
	final FlowPanel buttons = new FlowPanel();
	final TabPanel tabs = new TabPanel();
	final ContainerEditPanel editor = new ContainerEditPanel();
	final ContainerViewPanel view = new ContainerViewPanel();

	VerticalPanel processes = new VerticalPanel();

	public ContainerEditor() {

		content.addStyleName("container-editor");

		content.setPixelSize(1024, 800);

		content.add(buttons);
		Button add = new Button("Add");
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				view.add(new Element("Element-" + (last++)));
			}
		});
		buttons.add(add);

		editor.addStyleName("container-editor-textarea");
		tabs.add(editor, "Editor");
		tabs.add(view, "View");

		// tabs.setWidth( "100%" );
		// tabs.setHeight( "100%" );
		tabs.setPixelSize(1016, 780);
		content.add(tabs);
	}

	@Override
	public void onModuleLoad() {
		// XMLEditor editor = new XMLEditor();
		// RootPanel.get("editor").add(editor);
		if (RootPanel.get("editor-tools") != null)
			showView();
	}

	public void showView() {
		HorizontalPanel tools = new HorizontalPanel();

		RootPanel.get("editor-tools").add(tools);

		processes.setWidth("100%");
		processes.setHeight("400px");
		final ProcessElement process = new ProcessElement("p0");

		Button add = new Button("Add");
		add.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ElementWidget ew = new ElementWidget(new Element("Rename"));
				process.add(ew);
			}
		});
		tools.add(add);

		final ContainerEditPanel editor = new ContainerEditPanel();
		editor.addRow();

		Button b = new Button("Add Process");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// ProcessElement pp = new ProcessElement();
				// processes.add(pp);
				editor.addRow();
			}

		});
		tools.add(b);

		// process.add(new ElementWidget(new Element("Rename Keys")));
		processes.add(process);

		RootPanel.get("editor").add(editor);
		// RootPanel.get("editor").add(processes);

		if (RootPanel.get("editor") != null
				&& RootPanel.get("this-does-not-exit") != null) {
			AbsolutePanel area = new AbsolutePanel();
			area.setPixelSize(1024, 768);
			area.addStyleName("container-view-area");

			// ensure the document BODY has dimensions in standards mode
			RootPanel.get("editor").add(area); // setPixelSize(600, 600);
			RootPanel.get("editor").addStyleName("container-view");

			// create a DragController to manage drag-n-drop actions
			// note: This creates an implicit DropController for the boundary
			// panel
			PickupDragController dragController = new PickupDragController(
					area, true);

			HTML header = new HTML("Stream: <b>twitter</b>");
			header.addStyleName("container-element-title");
			FlowPanel p = new FlowPanel();
			p.addStyleName("container-element");
			p.add(header);
			SimplePanel content = new SimplePanel();
			content.addStyleName("container-element-content");
			content.add(new Label("Test"));
			p.add(content);
			p.setPixelSize(180, 120);

			area.add(p, 200, 200);
			dragController.makeDraggable(p, header);

			/*
			 * Label label = new Label("ABC");
			 * label.addStyleName("container-element"); area.add(label, 80, 80);
			 * dragController.makeDraggable(label);
			 */

			// add a new image to the boundary panel and make it draggable
			/*
			 * Image img = new Image(
			 * "http://code.google.com/webtoolkit/logo-185x175.png");
			 * area.add(img, 40, 30);
			 * 
			 * dragController.makeDraggable(img);
			 */
		} else {
			// view.add(new Element("Test"));
			// RootPanel.get("ContainerEditor").add(view);
		}

	}
}
