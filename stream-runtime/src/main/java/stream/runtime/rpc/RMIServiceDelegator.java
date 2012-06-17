package stream.runtime.rpc;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RMIServiceDelegator implements InvocationHandler, Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -6450480644005423871L;

	static Logger log = LoggerFactory.getLogger(RMIServiceDelegator.class);

	/** The final remote endpoint for invocation */
	final RemoteEndpoint endpoint;

	public RMIServiceDelegator(RemoteEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		try {

			if (method.getName().equals("toString") && args == null) {
				return this.toString();
			}

			log.trace("received invoke-request, method: {}, args: {}",
					method.getName(), args);
			log.trace("   object reference is: {}", proxy);

			if (args != null
					&& !(args.getClass().getComponentType() instanceof Serializable)) {
				log.error("Arguments are not serializable!");
			}

			Serializable[] params = null;

			if (args != null) {
				params = new Serializable[args.length];
				for (int i = 0; i < args.length; i++) {
					params[i] = (Serializable) args[i];
				}
			}

			Object result = endpoint.call(method.getName(), params);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}