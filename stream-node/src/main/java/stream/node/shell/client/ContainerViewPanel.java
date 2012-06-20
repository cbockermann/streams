package stream.node.shell.client;

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ContainerViewPanel extends AbsolutePanel {

	Map<Element,Widget> widgets = new HashMap<Element,Widget>();
	
	private PickupDragController dragController;
	
	
	public ContainerViewPanel(){
		this.setPixelSize( 1024, 640 );
		dragController = new PickupDragController( this, true );
	}
	
	public void add( Element el ){
		Widget w = createWidget( el );
		if( w != null ){
			dragController.makeDraggable( w );
			widgets.put( el, w );
			add( w, this.getOffsetWidth() / 2, this.getOffsetHeight() / 2 );
		}
	}
	
	public Widget createWidget( Element element ){
		
		FlowPanel p = new FlowPanel();
		p.addStyleName( "container-element" );
		p.setWidth( "120px" );
		p.setHeight( "80px" );
		p.add( new Label( element.getName() ) );
		return p;
	}
}
