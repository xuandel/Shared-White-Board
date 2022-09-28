package Client;


/**
 * @Author Xuande Li
 * @Student_ID 1175020
 * @E-mail_Address xuandel@student.unimelb.edu.au
 */

import Remote.RemoteClient;
import Remote.RemoteServer;
import WhiteBoard.WhiteBoardClient;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class JoinWhiteBoard extends UnicastRemoteObject implements RemoteClient {
    protected WhiteBoardClient newBoard;
    private String portNumber = "11750";
    private String hostName = "localhost";
    private String userName = "XD_Client";
    private String serviceName = "WhiteBoardService";
    private String userServiceName = "WhiteBoardSharingService";
    private RemoteServer rs;
    private ArrayList<String> commandList;

    protected JoinWhiteBoard() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws RemoteException {
        JoinWhiteBoard newWhiteBoard = new JoinWhiteBoard();

        if (args.length == 1) {
            newWhiteBoard.userName = args[0];
        } else if (args.length == 3) {
            newWhiteBoard.hostName = args[0];
            newWhiteBoard.portNumber = args[1];
            newWhiteBoard.userName = args[2];
        } else if (args.length == 2 || args.length > 3) {
            System.out.println("Please check the input argument size, it should be 1 with the userName or " +
                    "3 with hostname, port number, and userName in order.");
            System.exit(0);
        }

        newWhiteBoard.boardRegister();
    }

    public void boardRegister() {
        try {
            // RMI connection
            Naming.rebind("rmi://" + hostName + ":" + portNumber + "/" + this.userServiceName, this);
//            LocateRegistry.getRegistry("192.168.3.12", Integer.parseInt(portNumber));
            rs = (RemoteServer) Naming.lookup("rmi://" + hostName + ":" + portNumber + "/" + serviceName);
            System.out.println("White Board Client is running...");
            System.out.println("The user is " + userName);

            if (rs.checkNameExist(this.userName)) {
                System.out.println("The user name already exists, please try later.");
                System.exit(0);
            }

            System.out.println("Waiting for manager to process request...");
            if (!rs.getPermission(userName)) {
                System.out.println("The manager does not allow you to join the white board or the room does not exist, please try later.");
                System.exit(0);
            }

            // register the manager to the user list
            rs.userRegistration(userName, hostName, portNumber, this.userServiceName);

            // setup the white board gui
            newBoard = new WhiteBoardClient(this, rs, userName);
            System.out.println("White board GUI setup successfully...");

            // update come in information
            rs.transferMessage(userName + " has come into the white board.");

            // update the user menu when new client generated
            rs.updateList();


            Thread t = new Thread(() -> {
                try {
                    if (rs.getLoadState() == 1) {
                        commandList = new ArrayList<>();
                        getByteImage(rs.getManagerImage());
                    } else {
                        // load the current white board figure
                        commandList = rs.getCommandList();

                        for (String command : commandList) {
                            newBoard.painting(command);
                        }
                    }
                } catch (RemoteException e) {
                    System.out.println("Cannot find the connection, please try later.");
                    System.exit(0);
                }
            });
            t.start();

        } catch (RemoteException | MalformedURLException e) {
            System.out.println("Connection failed, please check the configuration and try again.");
            System.exit(0);
        } catch (NotBoundException e) {
            System.out.println("Object not bounded, please try another one.");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public ArrayList<String> getCommandList() {
        return commandList;
    }

    @Override
    public void update(String updateCommand) throws RemoteException {
        newBoard.painting(updateCommand);
        commandList.add(updateCommand);
    }

    @Override
    public void updateMessage(String newMessage) throws RemoteException {
        newBoard.addNewMessage(newMessage);
    }

    @Override
    public void updateUserList(List<String> nameList) throws RemoteException {
        String message = "Manager: \n";
        for (int i = 0; i < nameList.size(); i++) {
            if (i == 0) {
                message = message + nameList.get(0) + "\n------------------\nUsers:\n";
            } else {
                message = message + nameList.get(i) + "\n";
            }
        }
        newBoard.addNewClient(message);
    }

    @Override
    public void removeUser() throws RemoteException {
        newBoard.removeReminder();

        Thread t = new Thread(() -> {
            System.exit(0);
        });
        t.start();
    }

    @Override
    public void newDialogue(String message) throws RemoteException {
        newBoard.addNewDialogue(message);
    }

    @Override
    public void clear() throws RemoteException {
        newBoard.clearCanvas();
        commandList.clear();
    }

    @Override
    public void getByteImage(byte[] newImage) throws RemoteException {
        commandList.clear();
        newBoard.loadImage(newImage);
    }

    @Override
    public Boolean requestPermission(String name) throws RemoteException {
        return null;
    }

    @Override
    public byte[] getManagerByteImage() throws RemoteException {
        return new byte[0];
    }

    @Override
    public void getCommand() throws RemoteException {
        commandList = rs.getCommandList();
    }
}
