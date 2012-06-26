/**
 * 
 */
package stream.node.shell.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class ContainerList implements EntryPoint {

	final ContainerServiceAsync containerService = GWT
			.create(ContainerService.class);
	final FlexTable table = new FlexTable();
	final VerticalPanel content = new VerticalPanel();

	public ContainerList() {

		FlowPanel buttons = new FlowPanel();
		Button reload = new Button("Reload");
		reload.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reload();
			}
		});
		buttons.add(reload);
		content.add(buttons);

		table.setWidget(0, 0, new Label("Name"));
		table.setWidget(0, 1, new Label("URI"));
		table.getRowFormatter().addStyleName(0, "tableHeader");
		content.add(table);
	}

	public void reload() {

		containerService.list(new AsyncCallback<List<ContainerInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(List<ContainerInfo> result) {
				set(result);
			}
		});
	}

	public void clear() {
		set(new ArrayList<ContainerInfo>());
	}

	public void set(Collection<ContainerInfo> infos) {

		int row = 1;
		for (ContainerInfo info : infos) {
			table.setWidget(row, 0, new Label(info.getName()));
			table.setWidget(row, 1, new Label(info.getUri() + ""));
			row++;
		}

		while (row > table.getRowCount()) {
			table.removeRow(row);
		}
	}

	/**
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {
		if (RootPanel.get("ContainerList") != null) {
			RootPanel.get("ContainerList").add(content);
		}
	}
}
