/**
 * 
 */
package stream.runtime;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import streams.runtime.Hook;
import streams.runtime.Signals;

/**
 * @author chris
 *
 */
public class LifeCycleManager implements Hook {

    static Logger log = LoggerFactory.getLogger(LifeCycleManager.class);
    List<LifeCycle> lifeCycles = new ArrayList<LifeCycle>();

    public void register(LifeCycle obj) {
        if (lifeCycles.contains(obj)) {

        }
    }

    public void initAll(ApplicationContext context) throws Exception {
        for (LifeCycle obj : lifeCycles) {
            obj.init(context);
        }
    }

    public void finishAll() {
        for (LifeCycle obj : lifeCycles) {
            try {
                obj.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @see streams.runtime.Hook#signal(int)
     */
    @Override
    public void signal(int flags) {
        if (flags == Signals.SHUTDOWN) {
            finishAll();
        }
    }

}
