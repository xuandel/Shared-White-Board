package Server;


/**
 * @Author Xuande Li
 * @Student_ID 1175020
 * @E-mail_Address xuandel@student.unimelb.edu.au
 */

import Remote.RemoteServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class server {
    // Class used for start the server
    public static void main(String[] args) {
        String serverIPAddress = "192.168.3.12"; // default ip address
        int serverPort = 11750; // default serer port

        // check the input port
        if (args.length == 1) {
            String arg = args[0];
            if (arg.matches("[0-9]+")) {
                int tempPort = Integer.parseInt(arg);
                if (1024 < tempPort & tempPort < 65535) {
                    serverPort = tempPort;
                } else {
                    System.out.println("Please notice that the port number should be between 1024 and 65535.");
                    System.exit(0);
                }
            } else {
                System.out.println("Please notice that the port number should be the digits between 1024 and 65535.");
                System.exit(0);
            }
        } else if (args.length > 1) {
            System.out.println("Please use default configuration or input one argument as the port number.");
            System.exit(0);
        }

        // get the local host
        try {
            serverIPAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Local host cannot be found.");
        }

        // try to start the server and bind the new object
        try {
            RemoteServer rs = new WhiteBoardServer();
//            System.setProperty("java.rmi.server.hostname","127.0.0.1");
            LocateRegistry.createRegistry(serverPort);
            Naming.rebind("rmi://" + serverIPAddress + ":" + serverPort + "/WhiteBoardService", rs);
            System.out.println("White Board Server is running...");
            System.out.println("The ip address is " + serverIPAddress + ", and the port number is " + serverPort + ".");
        } catch (Exception e) {
            System.out.println("Registration failed, please check the configuration and run again.");
            System.exit(0);
        }
    }
}
