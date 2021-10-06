
package src.example.hello;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

public interface Hello extends Remote {
    public String sayHello() throws RemoteException, ServerNotActiveException;
    public int getId() throws RemoteException, ServerNotActiveException;
    public String printElement(int l, int client_id) throws RemoteException, ServerNotActiveException;
    public boolean concatThem(String str, int l, int client_id) throws RemoteException, ServerNotActiveException;
    public int getArraySize() throws RemoteException, ServerNotActiveException;
    public void insertArrayElement(int l, String str) throws RemoteException;
    public boolean requestReadLock(int l, int client_id) throws RemoteException;
    public boolean requestWriteLock(int l, int client_id) throws RemoteException;
    public void releaseLock(int l, int client_id) throws RemoteException;
    public String fetchElementRead(int l, int client_id) throws RemoteException;
    public String fetchElementWrite(int l, int client_id) throws RemoteException;
    public boolean WriteBackElement(String str, int l, int client_id) throws RemoteException;
}