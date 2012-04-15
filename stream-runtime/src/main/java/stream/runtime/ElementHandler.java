package stream.runtime;

import org.w3c.dom.Element;

/**
 * 
 * @author Hendrik Blom
 * 
 */
public interface ElementHandler {

	public void handleElement(ProcessContainer container, Element element);
}
