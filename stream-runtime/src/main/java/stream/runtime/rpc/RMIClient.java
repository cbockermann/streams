package stream.runtime.rpc;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.service.Service;

public class RMIClient implements RemoteNamingService {

	static Logger log = LoggerFactory.getLogger(RMIClient.class);
	final Registry registry;
	RemoteNamingService namingService;

	public RMIClient(int port) throws Exception {
		this("127.0.0.1", port);
	}

	public RMIClient(String host, int port) throws Exception {
		registry = LocateRegistry.getRegistry(host, port);
		log.info("Registry is: {}", registry);
		namingService = (RemoteNamingService) registry.lookup("@ns");
		log.info("NamingService is: {}", namingService);
	}

	@Override
	public <T extends Service> T lookup(String ref, Class<T> serviceClass)
			throws Exception {
		return namingService.lookup(ref, serviceClass);
	}

	@Override
	public void register(String ref, Service p) throws Exception {
	}

	@Override
	public void unregister(String ref) throws Exception {
	}

	@Override
	public Map<String, String> list() throws Exception {
		return namingService.list();
	}

	@Override
	public Map<String, String> getServiceInfo(String name)
			throws RemoteException {
		return namingService.getServiceInfo(name);
	}

	@Override
	public Serializable call(String name, String method, String signature,
			Serializable... args) throws RemoteException {
		return namingService.call(name, method, signature, args);
	}
}
