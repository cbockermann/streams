/**
 * 
 */
package streams.runtime;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class Signals extends Thread {

    static Logger log = LoggerFactory.getLogger(Signals.class);
    final static ArrayList<Hook> hooks = new ArrayList<Hook>();

    static Signals listener = null;

    static {
        init();
    }

    private synchronized static void init() {
        if (listener == null) {
            log.info("Registering ShutdownListener...");
            listener = new Signals();
            Runtime.getRuntime().addShutdownHook(listener);
        }
    }

    public void run() {
        for (Hook hook : hooks) {
            hook.signal(0);
        }
    }

    public static synchronized void register(Hook hook) {
        if (!hooks.contains(hook)) {
            hooks.add(hook);
        }
    }
}