/**
 * 
 */
package streams.profiler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.ProcessorList;

/**
 * @author chris
 *
 */
public class ProxyInjection {

    static Logger log = LoggerFactory.getLogger(ProxyInjection.class);

    public ProxyNode inject(Processor p) {
        if (p instanceof ProcessorList) {
            log.info("Found processor-list: {}", p);
            return inject((ProcessorList) p);
        }

        return new ProxyNode(p);
    }

    public ProxyNode inject(ProcessorList list) {

        ProxyNode tree = new ProxyNode(list);
        for (int i = 0; i < list.getProcessors().size(); i++) {
            Processor p = list.getProcessors().get(i);
            ProxyNode proxy = inject(p);
            tree.children.add(proxy);
            list.getProcessors().set(i, proxy);
        }

        return tree;
    }

    public class ProxyNode extends Proxy {
        final List<ProxyNode> children = new ArrayList<ProxyNode>();

        public ProxyNode(Processor p) {
            super(p);
        }
    }
}
