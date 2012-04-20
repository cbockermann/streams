/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.plugin.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.Serializable;
import java.lang.reflect.Array;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.plugin.data.DataObject;

import com.rapidminer.gui.renderer.AbstractRenderer;
import com.rapidminer.gui.renderer.DefaultComponentRenderable;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.report.Reportable;

/**
 * @author chris
 * 
 */
public class DataObjectRenderer extends AbstractRenderer {
	static Logger log = LoggerFactory.getLogger(DataObjectRenderer.class);

	Component component;
	DataObject event;

	/**
	 * @see com.rapidminer.gui.renderer.DefaultTextRenderer#getVisualizationComponent(java.lang.Object,
	 *      com.rapidminer.operator.IOContainer)
	 */
	@Override
	public Component getVisualizationComponent(Object renderable,
			IOContainer ioContainer) {

		JEditorPane resultText = new JEditorPane();
		resultText.setContentType("text/html");
		resultText.setBorder(javax.swing.BorderFactory.createEmptyBorder(11,
				11, 11, 11));
		resultText.setEditable(false);
		resultText.setBackground((new JLabel()).getBackground());

		log.info("Preparing visualization-component for object {}", renderable);
		log.info("   object is of class {}", renderable.getClass());

		if (renderable instanceof DataObject) {
			log.info("Need to render FactEventObject!");
			event = (DataObject) renderable;
			resultText.setText(toHtml(event));
		} else {
			log.info("Don't know how to render object {} of class {}",
					renderable, renderable.getClass());
			resultText.setText(toHtml(renderable));
		}

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(resultText, BorderLayout.NORTH);
		// panel.add( eventPanel, BorderLayout.CENTER );

		component = new JScrollPane(panel);
		return component;
	}

	public String toHtml(Object object) {
		StringBuffer s = new StringBuffer("<html><body>");
		s.append("<h1>" + object.getClass() + "</h1>\n");
		s.append("<pre>");
		s.append(object.toString());
		s.append("</pre>");
		s.append("</body></html>");
		return s.toString();
	}

	public String toHtml(DataObject event) {

		StringBuffer s = new StringBuffer("<html>");
		s.append("<table>");
		for (String key : event.keySet()) {
			s.append("<tr>");
			s.append("<td><b>" + key + "</b></td>");
			s.append("<td><code>");
			Serializable val = event.get(key);
			if (val.getClass().isArray()) {
				Class<?> type = val.getClass().getComponentType();
				int len = Array.getLength(val);
				s.append(type.getName() + "[" + len + "]");

				try {
					s.append("  (values: ");
					for (int i = 0; i < len && i < 4; i++) {
						Object o = Array.get(val, i);
						if (o == null)
							s.append("null");
						else
							s.append(o.toString());

						if (i + 1 < len)
							s.append(", ");
					}

					if (len > 4)
						s.append("...)");
					else
						s.append(")");

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				s.append(val.toString());
			}
			s.append("</code>");
			s.append("  (Class:" + val.getClass() + ")");
			s.append("</td></tr>\n");
		}
		s.append("</table>");
		s.append("</html>");
		return s.toString();
	}

	/**
	 * @see com.rapidminer.gui.renderer.Renderer#getName()
	 */
	@Override
	public String getName() {
		return "DataObjectRenderer";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rapidminer.gui.renderer.Renderer#createReportable(java.lang.Object,
	 * com.rapidminer.operator.IOContainer, int, int)
	 */
	@Override
	public Reportable createReportable(Object renderable,
			IOContainer ioContainer, int desiredWidth, int desiredHeight) {
		log.info("Calling createReportable()...");
		return new DefaultComponentRenderable(component);
	}
}