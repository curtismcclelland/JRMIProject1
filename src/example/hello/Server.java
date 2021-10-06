//Current issues associated are taking in the command line from a file.... compilation issues involving the interface. 
package src.example.hello;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
//import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Server extends UnicastRemoteObject implements Hello {

    public String[] strings;
    public int[] readLockClient;
    public int[] writeLockClient;

    public Server(int n) throws RemoteException{
        super();
        strings = new String[n];

        //strings[0] = "Hayden";
        //strings[1] = "Curtis";
        //strings[2] = "Jack";
        //strings[3] = "Grant";
        //strings[4] = "Garrison";
        //strings[5] = "Bristol";
        //strings[6] = "Spencer";

        readLockClient = new int[n];
        Arrays.fill(readLockClient, 0);
        writeLockClient = new int[n];
        Arrays.fill(writeLockClient, 0);
    }

    /**
     * Fills the array of the remote server from the input file
     * @param fromFile
     */
    public void fillServerArray(String[] toCopy){
        for (int i = 0; i<toCopy.length; i++){
            this.strings[i] = toCopy[i];
        }
    }

    /**
     * Returns the length of the array held in the server
     */
    @Override
    public int getArraySize() throws RemoteException, ServerNotActiveException {
        return this.strings.length;
    }
    /**
     * Returns the id of the client that is connected to the server
     */
    @Override
    public int getId() throws RemoteException{
        String clientHost = "";
        try {
            clientHost = UnicastRemoteObject.getClientHost();
        /*
         * According to the rules specified in the javax.management.remote
         * package description, a numeric IPv6 address (detected by the
         * presence of otherwise forbidden ":" character) forming a part
         * of the connection id must be enclosed in square brackets.
         */
            if (clientHost.contains(":")) {
                clientHost = "[" + clientHost + "]";
            }
        } catch (ServerNotActiveException e) {
        System.out.println(e);
        }
        String clientHost2 = clientHost.replace(".", "");
        int clientReturn = Integer.parseInt(clientHost2);
        System.out.println(clientHost);
        return clientReturn;
    }
    /**
     * Returns a hello message to the client when they connect.
     */
    @Override
    public String sayHello() throws RemoteException{
        String clientHost = "";
        try {
            clientHost = UnicastRemoteObject.getClientHost();
        /*
         * According to the rules specified in the javax.management.remote
         * package description, a numeric IPv6 address (detected by the
         * presence of otherwise forbidden ":" character) forming a part
         * of the connection id must be enclosed in square brackets.
         */
            if (clientHost.contains(":")) {
                clientHost = "[" + clientHost + "]";
            }
        } catch (ServerNotActiveException e) {
        System.out.println(e);
        }
        //clientHost = clientHost.replace(".", "");
        return "Hello client connecting from " + clientHost + "!";
    }

    /**
     * Inserts an element into the array at the given index
     */
    @Override
    public void insertArrayElement(int l, String str){
        strings[l] = str;
    }
    
    /**
     * Requests a read lock for the client given that there is not already a read or write lock on that element. 
     */
    @Override
    public boolean requestReadLock(int l, int client_id){
        boolean result = false;
        if (readLockClient[l] == 0 && writeLockClient[l] == 0){
            readLockClient[l] = client_id;
            result = true;
        }
        else if (readLockClient[l] == client_id){
            result = true;
        }
        return result;
    }
    /**
     * Requests a write lock for the client given that there is not already a read or write lock on that element. 
     */
    @Override
    public boolean requestWriteLock(int l, int client_id){
        boolean result = false;
        if (writeLockClient[l] == 0 && readLockClient[l] == 0){
            writeLockClient[l] = client_id;
            result = true;
        }
        else if (writeLockClient[l] == client_id){
            result = true;
        }
        return result;
    }

    /**
     * Releases the locks held on that element of the array by that client.
     */
    @Override
    public void releaseLock(int l, int client_id){
        if (readLockClient[l] == client_id){
            readLockClient[l] = 0;
        }
        if (writeLockClient[l] == client_id){
            writeLockClient[l] = 0;
        }
    }

    /**
     * Given that a read lock has been established, an element is returned to the client.
     */
    @Override
    public String fetchElementRead(int l, int client_id){
        String toReturn = "";
        if (requestReadLock(l, client_id)){
            toReturn = strings[l];
        }
        return toReturn;
    }

    /**
     * Given that a write lock has been established, an element is returned to the client. 
     */
    @Override
    public String fetchElementWrite(int l, int client_id){
        String toReturn = "";
        if (requestWriteLock(l, client_id)){
            toReturn = strings[l];
        }
        return toReturn;
    }

    /**
     * Given that there is a write lock established on the element of the server, the input 
     * string is written back to the server held array.
     */
    @Override
    public boolean WriteBackElement(String str, int l, int client_id){
        boolean toReturn = false;
        if (writeLockClient[l]==client_id){
            strings[l] = str;
            toReturn = true;
        }
        return toReturn;
    }

    /**
     * Returns an element to be printed to the client, given that a lock is held on that element.  
     */
    @Override
    public String printElement(int l, int client_id) throws RemoteException, ServerNotActiveException{
        String toReturn = "";
        if (readLockClient[l] ==client_id){
            toReturn = strings[l];
        }
        return toReturn;
    }

    /**
     * Given that the client holds a write lock on the given indexed element of the server, the input string is concatenated
     * at the given index. 
     */
    @Override 
    public boolean concatThem(String str, int l, int client_id) throws RemoteException, ServerNotActiveException{
        boolean toReturn = false;
        if (writeLockClient[l]==client_id){
            strings[l] = strings[l]+ str;
            toReturn = true;
        }
        return toReturn;
    }

    public static void main(String args[]) throws RemoteException, AlreadyBoundException, IOException{

        if(args.length < 1) {
            System.out.println("Error, usage: java Server inputfile");
        System.exit(1);
        }

        Scanner fileInput = new Scanner(new FileInputStream(args[0]));
        String bindName = "";
        int arraySize = 0;
        String[] toCopy = new String[0];

        while (fileInput.hasNext()){
            bindName = fileInput.next();
            arraySize = fileInput.nextInt();
            toCopy = new String[arraySize];
            for (int i = 0; i < arraySize; i++){
                toCopy[i] = fileInput.next();
            }
        }
        
        Server server = new Server(arraySize);
        server.fillServerArray(toCopy);

        try{
            Registry registry = LocateRegistry.createRegistry(7777);
            //IMPLEMENT ARRAY SIZE BELOW
            
            registry.bind(bindName, server);
            System.out.println("The App is up and running");
            
        }catch(Exception e){
            System.out.println("It aint working dawg");
        }
        

    }


}