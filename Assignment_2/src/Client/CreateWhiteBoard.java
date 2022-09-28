package Client;


/**
 * @Author Xuande Li
 * @Student_ID 1175020
 * @E-mail_Address xuandel@student.unimelb.edu.au
 */

import Remote.RemoteClient;
import Remote.RemoteServer;
import WhiteBoard.WhiteBoard;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class CreateWhiteBoard extends UnicastRemoteObject implements RemoteClient {
    protected WhiteBoard newBoard;
    private String portNumber = "11750";
    private String hostName = "192.168.3.12";
    private String userName = "XD_Manager";
    private String serviceName = "WhiteBoardService";
    private String userServiceName = "WhiteBoardSharingService";
    private RemoteServer rs;

    protected CreateWhiteBoard() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws RemoteException {
        CreateWhiteBoard newWhiteBoard = new CreateWhiteBoard();

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
            rs = (RemoteServer) Naming.lookup("rmi://" + hostName + ":" + portNumber + "/" + serviceName);
            System.out.println("White Board Manager is running...");
            System.out.println("The user is " + userName);


            if (rs.checkRoomState()) {
                System.out.println("The room is already been used, please try using another one.");
                System.exit(0);
            }

            // register the manager to the user list
            rs.userRegistration(userName, hostName, portNumber, this.userServiceName);

            // setup the white board gui
            newBoard = new WhiteBoard(rs, userName);
            System.out.println("White board GUI setup successfully...");

            // update the user menu when new client generated
            rs.updateList();

        } catch (RemoteException | MalformedURLException e) {
            System.out.println("Connection failed, please check the configuration and try again.");
            System.exit(0);
        } catch (NotBoundException e) {
            System.out.println("Object not bounded, please try another one.");
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void update(String updateCommand) throws RemoteException {
        newBoard.painting(updateCommand);
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
    }

    @Override
    public void newDialogue(String message) throws RemoteException {

    }

    @Override
    public void clear() throws RemoteException {
        newBoard.clearCanvas();
    }

    @Override
    public void getByteImage(byte[] newImage) throws RemoteException {

    }

    @Override
    public Boolean requestPermission(String name) throws RemoteException {
        return newBoard.getPermission(name);
    }

    @Override
    public byte[] getManagerByteImage() throws RemoteException {
        return newBoard.getCurrentByteImage();
    }

    @Override
    public void getCommand() throws RemoteException {

    }
}
