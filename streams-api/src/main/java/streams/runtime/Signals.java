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

    public final static int SHUTDOWN = 0;

    static Logger log = LoggerFactory.getLogger(Signals.class);
    final static ArrayList<Hook> hooks = new ArrayList<Hook>();

    static Signals listener = new Signals();

    static {
        init();
    }

    private synchronized static void init() {
        if (listener == null) {
            log.debug("Registering ShutdownListener...");
            listener = new Signals();
            Runtime.getRuntime().addShutdownHook(listener);
        }
    }

    public void run() {
        log.debug("Running hooks...");
        for (Hook hook : hooks) {
            log.debug("Signaling hook {}", hook);
            hook.signal(0);
        }
    }

    public static synchronized void register(Hook hook) {
        if (!hooks.contains(hook)) {
            hooks.add(hook);
        }
    }
}