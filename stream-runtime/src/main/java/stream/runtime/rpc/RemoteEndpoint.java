package stream.runtime.rpc;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteEndpoint extends Remote {

	public Serializable call( String methodName, Serializable... args ) throws RemoteException;
}
