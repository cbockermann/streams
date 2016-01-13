/**
 * 
 */
package streams.queues;

import static org.junit.Assert.fail;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class ClosingBugIssue36 {

    static Logger log = LoggerFactory.getLogger(ClosingBugIssue36.class);

    @Test
    public void test() {
        try {

            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    fail("Test failed - timed out while waiting for process container to finish!");
                }

            }, 60 * 1000L);

            URL url = ClosingBugIssue36.class.getResource("/queues/queue-closing-bug-issue-36.xml");
            stream.run.main(url);
            timer.cancel();

        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }
}