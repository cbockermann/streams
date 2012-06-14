/**
 * 
 */
package stream.node.service.renderer;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Statistics;
import stream.learner.MetaDataService;
import stream.learner.MetaDataService.StreamInfo;
import stream.node.servlets.Template;
import stream.statistics.StatisticsHistory;
import stream.util.MD5;

/**
 * @author chris
 * 
 */
public class MetaDataServiceRenderer implements
		ServiceRenderer<MetaDataService> {

	static Logger log = LoggerFactory.getLogger(MetaDataServiceRenderer.class);
	DateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	DecimalFormat numberFormat = new DecimalFormat("0.##");

	/**
	 * @see stream.server.renderer.ServiceRenderer#renderToHtml(stream.service.Service)
	 */
	@Override
	public String renderToHtml(String name, MetaDataService service) {
		StringBuilder sb = new StringBuilder();

		Map<String, Class<?>> schema = service.getMetaData();
		Map<String, Statistics> stats = service.getMetaDataStatistics();
		String[] stKeys = new String[] { "minimum", "maximum", "average" };

		StreamInfo info = service.getStreamInformation();

		sb.append("<div class='list'>");
		sb.append("<div class='title'>MetaDataService <i>" + name
				+ "</i></div>");

		StatisticsHistory history = service.getStreamHistory();
		if (history != null) {
			String id = MD5.md5("" + System.nanoTime());
			sb.append("<div id='"
					+ id
					+ "' style='height: 291px; width: 611px; padding: 4px; margin: auto; margin-bottom: 20px; margin-top: 20px;'></div>");

			StringBuilder data = new StringBuilder();
			data.append("series: [{");
			data.append("name: 'items',\n");
			data.append("data: [");
			List<Statistics> list = new ArrayList<Statistics>(history.getData());
			Iterator<Statistics> it = list.iterator();
			while (it.hasNext()) {
				Statistics s = it.next();
				data.append(s.get("items"));
				if (it.hasNext())
					data.append(",");
			}
			data.append("]\n");
			data.append("}]");

			// log.info("Data:\n{}", data);

			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put("container.id", id);
				params.put("series", data.toString());
				Template template = new Template("/timeline.html");
				sb.append(template.expand(params));
				sb.append("\n\n\n\n\n");

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.info("got no history!");
		}

		sb.append("<div class='streamInfo'>");
		sb.append("<p>" + info.numberOfItems()
				+ " items have been processed so far, average rate is "
				+ numberFormat.format(info.rate()) + " items per second.");
		if (info.firstItem() != null && info.lastItem() != null) {
			sb.append("<p>The first item of this stream was observed at <i>"
					+ fmt.format(info.firstItem())
					+ "</i>, the last items has been processed at <i>"
					+ fmt.format(info.lastItem()) + "</i>.</p>");
		}
		sb.append("</div>");
		sb.append("<table>");
		sb.append("<tr>");
		sb.append("<th>Attribute</th>");
		sb.append("<th>Type</th>");
		sb.append("<th>Statistics</th>");
		sb.append("</tr>");
		for (String key : schema.keySet()) {
			sb.append("<tr>");
			sb.append("<td><a href='/service/" + name + "?display=" + key
					+ "'>");
			sb.append(key);
			sb.append("</a></td>");

			sb.append("<td>");
			sb.append(schema.get(key).getCanonicalName());
			sb.append("</td>");

			sb.append("<td>");
			if (Number.class.isAssignableFrom(schema.get(key))) {

				Statistics st = stats.get(key);
				for (int i = 0; i < stKeys.length; i++) {
					sb.append("<span class='label'>" + stKeys[i] + ": "
							+ "</span>");
					sb.append(st.get(stKeys[i]));
					if (i + 1 < stKeys.length)
						sb.append("<br/>");
				}
			} else {

				sb.append("<span class='label'>elements: </span>");

				Map<Serializable, Long> top = service.getTopElements(key);
				if (!top.isEmpty()) {
					if (top.keySet().iterator().next() instanceof Comparable) {
						top = new TreeMap<Serializable, Long>(top);
					}
				}

				Iterator<Serializable> it = top.keySet().iterator();
				while (it.hasNext()) {
					Serializable val = it.next();
					Long count = top.get(val);
					sb.append(val.toString());
					sb.append(" (");
					sb.append(count);
					sb.append(")");
					if (it.hasNext())
						sb.append(", ");
				}
			}
			sb.append("</td>");

			sb.append("</tr>");
		}
		sb.append("</div>");

		return sb.toString();
	}
}
