/**
 * 
 */
package stream.node.shell.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class ConfigList extends VerticalPanel {

	final ContainerServiceAsync containerService = GWT
			.create(ContainerService.class);
	final FlexTable table = new FlexTable();

	public ConfigList() {

		FlowPanel titlePanel = new FlowPanel();
		titlePanel.addStyleName("title-panel");
		Label title = new Label("Configurations");
		title.addStyleName("title");
		titlePanel.add(title);
		add(titlePanel);
		FlowPanel buttons = new FlowPanel();
		Button reload = new Button("Reload");
		reload.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reload();
			}
		});
		buttons.add(reload);

		Button upload = new Button("Upload");
		buttons.add(upload);

		add(buttons);

		table.setWidget(0, 0, new Label("Name"));
		table.setWidget(0, 1, new Label("URI"));
		table.setWidget(0, 2, new Label(""));
		table.getRowFormatter().addStyleName(0, "tableHeader");
		add(table);
		reload();
	}

	public void reload() {

		containerService
				.getConfigurations(new AsyncCallback<List<Configuration>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(List<Configuration> result) {
						set(result);
					}
				});
	}

	public void clear() {
		set(new ArrayList<Configuration>());
	}

	public void set(Collection<Configuration> infos) {

		int row = 1;
		for (final Configuration info : infos) {
			table.getRowFormatter().addStyleName(row, "objectRow");
			table.setWidget(row, 0, new Label(info.getName()));
			table.setWidget(row, 1, new Label(info.getFile() + ""));

			FlowPanel actions = new FlowPanel();
			actions.addStyleName("objectActions");

			Label label = new Label("start");
			label.addStyleName("objectAction");
			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.alert("Shutting down container...");
					containerService.startContainer(info.getFile(),
							new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Failure: "
											+ caught.getMessage());
								}

								@Override
								public void onSuccess(Void result) {
									clear();
									reload();
								}
							});
				}
			});
			actions.add(label);
			table.setWidget(row, 2, actions);
			row++;
		}

		while (row < table.getRowCount()) {
			table.removeRow(row);
		}
	}
}