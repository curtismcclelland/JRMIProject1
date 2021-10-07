
package src.example.hello;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.Scanner;


//Decided for sake of this implimentation to take command line input inside of the methods
//of the class instead of having them as parameters passed in. Implemented a static Scanner
//object of the class to use in all of the methods. 

public class Client {
    int client_id;
    Hello stub;
    String currentCheckout;
    
    public static Scanner scan = new Scanner(System.in);
    

    public Client(String bindName, String hostName) {
        try {
            //System.out.println(bindName+"/"+hostName);
            this.stub = (Hello) Naming.lookup(bindName+"/"+hostName);
            
            //This code lets the client see in console that they have connected to the remote server
            String response = stub.sayHello();
            System.out.println(response);

            //This sets the clientId that is used in the method headers in this class, using the ip that
            //the server recieved from the client
            int callingId = stub.getId();
            this.setId(callingId);
            //This sets the current checkout string as blank.
            this.currentCheckout = "";
            

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Sets the elements of the client object from the input file. 
     */
    public void setElements(String bindName, String hostName){
        
    }

    /**
     * This method sets the id for the client object
     * @param id - takes in an ID integer
     */
    public void setId(int id){
        this.client_id = id;
    }
    
    /**
     * The control method for the client class interacting with the server
     * @throws RemoteException
     * @throws ServerNotActiveException
     */
    public void methodCall() throws RemoteException, ServerNotActiveException{
        
        System.out.println("----------------------");
        System.out.println("1: Get Array Capacity\n2: Fetch Element Read\n3: Fetch Element Write\n4: Print Element\n5: Concatenate\n6: Writeback\n7: Release Lock\n8: Exit");
        int choice = scan.nextInt();
        
        switch(choice){
            case 1: this.Get_Array_Capacity();
                break;
            case 2: this.Fetch_Element_Read();
                break;
            case 3: this.Fetch_Element_Write();
                break;
            case 4: this.Print_Element();
                break;
            case 5: this.Concatenate();
                break;
            case 6: this.Writeback();
                break;
            case 7: this.Release_Lock();
                break;
            case 8: System.exit(0);
                break;
            default: System.out.println("Enter a valid number choice");
                break;
        }
        
        methodCall();
    }
    /**
     * This method releases the locks that the client holds on a specific index of the array.
     * @throws RemoteException
     * @throws ServerNotActiveException
     */
    public void Release_Lock() throws RemoteException, ServerNotActiveException{
        System.out.println("\nEnter the index of the string you would like to release locks on.");
        int index = scan.nextInt();
        int arraySize = stub.getArraySize();
        if (index < arraySize){
            stub.releaseLock(index, client_id);
            this.currentCheckout = "";
        }
        else{
            System.out.println("The array is of size "+arraySize+", you are out of bounds.");
        }
        
    }
    /** 
    * This method gets the capacity of the array held by the remote server. 
    * @throws RemoteException
    * @throws ServerNotActiveException
    */
    public void Get_Array_Capacity() throws RemoteException, ServerNotActiveException{
        System.out.println(stub.getArraySize());
        
    }

    /**
     * This method fetches an element at an index given that the client holds a read lock on that 
     * element.
     * @throws RemoteException
     * @throws ServerNotActiveException
     */
    public void Fetch_Element_Read() throws RemoteException, ServerNotActiveException{
        System.out.println("\nEnter the index of the string you would like to fetch in read mode.");
        int index = scan.nextInt();
        int arraySize = stub.getArraySize();
        if (index < arraySize){
            String returnedString = stub.fetchElementRead(index, this.client_id);
            if (returnedString != ""){
                this.currentCheckout = returnedString;
                
                System.out.println("Successful Fetch Read on string at index: "+index);
            }
            else{
                System.out.println("Fetch Read Failure. R/W Lock held by on this index by another client.");
            }
        }
        else{
            System.out.println("The array is of size "+arraySize+", you are out of bounds.");
        }
        
    }
    /**
     * This method fetches an element at an index given that the client holds a read lock on that 
     * element.
     * @throws RemoteException
     * @throws ServerNotActiveException
     */
    public void Fetch_Element_Write() throws RemoteException, ServerNotActiveException{
        System.out.println("\nEnter the index of the string you would like to fetch in write mode.");
        int index = scan.nextInt();
        int arraySize = stub.getArraySize();
        if (index < arraySize){
            String toReturn = stub.fetchElementWrite(index, client_id);
            if (toReturn != ""){
                this.currentCheckout = toReturn;
                
                System.out.println("Successful Fetch Write on string at index: "+index);
            }
            else{
                System.out.println("Fetch Write Failure. R/W Lock held by on this index by another client.");
            }
        }
        else{
            System.out.println("The array is of size "+arraySize+", you are out of bounds.");
        }
        
    }
    /**
     * Prints the currently checked out element, if there is no currently checked out string,
     * blank string is returned. 
     * @throws RemoteException
     * @throws ServerNotActiveException
     */
    public void Print_Element() throws RemoteException, ServerNotActiveException{
        System.out.println("\nEnter the index of the string you would like to print.");
        int index = scan.nextInt();
        int arraySize = stub.getArraySize();
        if (index<arraySize){
            String result = stub.printElement(index, this.client_id);
            if (result != ""){
                System.out.println(result);
            }
            else{
                System.out.println("Client did not hold a lock on this element.");
            }
        }else{
            System.out.println("The array is of size: "+ arraySize +", you are out of bounds");
        }
        
    }

    /**
     * Given there is a currently a write lock on the index 
     * @throws RemoteException
     * @throws ServerNotActiveException
     */
    public void Concatenate() throws RemoteException, ServerNotActiveException{
        System.out.println("\nEnter the index of the string you would like to concat to.");
        int index = scan.nextInt();
        System.out.println("Enter the string you would like to add at that index\n");
        String toAdd = scan.next();
        //String totalInput = this.currentCheckout + toAdd;
        int arraySize = stub.getArraySize();
        if (index < arraySize){ 
            boolean result = stub.concatThem(toAdd, index, this.client_id);
            if (result){
                System.out.println(toAdd + " was successfully concatenated to the string at index "+index);
            }else{
                System.out.println("There was not a write lock on this element. Unsuccessful concatenation.");
            }
        }
        else{
            System.out.println("The array is of size "+arraySize+", you are out of bounds.");
        }
        
    }
    /**
     * Given that there is a write lock on the index in question, the proposed string is written 
     * into the array in the remote server. 
     * @throws RemoteException
     * @throws ServerNotActiveException
     */
    public void Writeback() throws RemoteException, ServerNotActiveException{
        System.out.println("\nEnter the index of the string you would like to read.");
        int index = scan.nextInt();
        System.out.println("Enter the string you would like to place at that index");
        String toAdd = scan.nextLine();
        int arraySize = stub.getArraySize();
        if (index < arraySize){ 
            boolean resulting = stub.WriteBackElement(toAdd, index, client_id);
            if (resulting){
                System.out.println("Successfully wrote back.");
            }
            else{
                System.out.println("Unsuccessful writeback");
            }
        }
        else{
            System.out.println("The array is of size "+arraySize+", you are out of bounds.");
        }
        
    }


    public static void main(String[] args) throws RemoteException, ServerNotActiveException, FileNotFoundException {
        if(args.length < 1) {
            System.out.println("Error, usage: java Server inputfile");
        System.exit(1);
        }

        String hostName = "";
        String bindName = "";

        Scanner fileInput = new Scanner(new FileInputStream(args[0]));

        while (fileInput.hasNext()){
            bindName = fileInput.next();
            hostName = fileInput.next();
        }

        Client tester = new Client(hostName, bindName);    
        //tester.setElements(hostName, bindName);
        System.out.println("Pick what function you would like to call");
        tester.methodCall();
    }
    
}