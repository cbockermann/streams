package stream.runtime.rpc;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import stream.service.NamingService;

public interface RemoteNamingService extends NamingService, Remote {

	public Map<String, String> getServiceInfo(String name)
			throws RemoteException;

	public Serializable call(String name, String method, String signature,
			Serializable... args) throws RemoteException;
}
