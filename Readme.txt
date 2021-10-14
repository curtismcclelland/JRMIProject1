Curtis McClelland
Bristol King
Spencer King

Preface: JRMI library is purposed for intranet communication between server/client objects. In order
to run this project your client devices must be on the same network as the device running the server. 

TO COMPILE
1. From JRMIProject1 directory: 
2. javac src/example/hello/Hello.java
3. javac src/example/hello/Server.java
4. javac src/example/hello/Client.java

TO RUN
1. In order to run this project, cd into JRMIProject1 directory.

2. Update the clientInput.txt file replacing the IP (everything after 
rmi:// and before :7777 with the IPV4 IP address of the device that will
run the Server class)

3. To run the server, in command line in this folder run:
	java src/example/hello/Server src/serverInput.txt
	
4. To run the client, in command line in this folder run: 
	java src/example/hello/Client src/clientInput.txt

Javadoc provided in the ExampleJRMI folder. 
