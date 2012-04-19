package stream.runtime;

import org.w3c.dom.Element;

/**
 * 
 * @author Hendrik Blom
 * 
 */
public interface ElementHandler {

	public String getKey();

	public boolean handlesElement(Element element);

	public void handleElement(ProcessContainer container, Element element)
			throws Exception;
}
