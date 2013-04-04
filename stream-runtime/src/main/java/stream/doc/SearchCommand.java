/**
 * 
 */
package stream.doc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import stream.doc.helper.DocIndex;
import stream.doc.helper.DocIndex.Result;
import stream.shell.ShellCommand;

/**
 * @author chris
 * 
 */
public class SearchCommand implements ShellCommand {

	/**
	 * @see stream.shell.ShellCommand#execute(java.util.List)
	 */
	@Override
	public void execute(List<String> args) throws Exception {

		if (args.isEmpty()) {
			System.out.println("'search' command requires query term!");
			System.out.println();
		}

		StringBuffer query = new StringBuffer();
		Iterator<String> it = args.iterator();
		while (it.hasNext()) {
			query.append(it.next());
			query.append(" ");
		}

		DocIndex index = DocIndex.getInstance();
		List<Result> results = index.search(query.toString());
		for (Result result : results) {
			System.out.println(" " + result.getClassName() + "  (score: "
					+ result.score + ")");
		}
	}

	public static void main(String[] args) throws Exception {
		SearchCommand cmd = new SearchCommand();
		cmd.execute(Arrays.asList("key"));
	}
}