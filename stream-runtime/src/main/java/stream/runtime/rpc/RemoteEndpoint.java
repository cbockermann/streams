package stream.runtime.rpc;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteEndpoint extends Remote {

	public Serializable call(String methodName, String signature,
			List<Serializable> args) throws RemoteException;
}
