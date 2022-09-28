package Server;


/**
 * @Author Xuande Li
 * @Student_ID 1175020
 * @E-mail_Address xuandel@student.unimelb.edu.au
 */

import Remote.RemoteClient;
import Remote.RemoteServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Objects;

public class WhiteBoardServer extends UnicastRemoteObject implements RemoteServer {
    private int loadState = 0;
    private ArrayList<String> chatList;
    private ArrayList<String> nameList;
    private ArrayList<String> commandList;
    private ArrayList<UserInformation> userList;

    protected WhiteBoardServer() throws RemoteException {
        super();
        chatList = new ArrayList<>(); // initialize the chat list to store users' information
        commandList = new ArrayList<>(); // initialize the command list to store users' information
        userList = new ArrayList<>(); // initialize the user list to store users' information
    }

    @Override
    public void clearCanvas() throws RemoteException {
        loadState = 0;
        Thread t = new Thread(() -> {
            commandList.clear();
        });
        t.start();
        for (UserInformation ui : userList) {
            ui.getRc().clear();
        }
    }

    @Override
    public void revoke() throws RemoteException {
        if (!commandList.isEmpty()){
            commandList.remove(commandList.size() - 1);
            Thread t = new Thread(() -> {
                try {
                    transferMessage("The manager has revoked the last command.");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
            t.start();
            for (int i = 1; i < userList.size(); i++) {
                userList.get(i).getRc().getCommand();
            }
        }
    }

    @Override
    public void leaveRoom(String clientName) throws RemoteException {
        for (int i = 0; i < userList.size(); i++) {
            if (Objects.equals(userList.get(i).getUserName(), clientName)) {
                int finalI = i;
                userList.remove(finalI);
                Thread t = new Thread(() -> {
                    try {
                        updateList();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
                t.start();
                transferMessage(clientName + " has left the room.");
                return;
            }
        }
    }

    @Override
    public void showNewDialogue(String message) {
        for (UserInformation ui : userList) {
            Thread t = new Thread(() -> {
                try {
                    ui.getRc().newDialogue(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        }
    }

    @Override
    public synchronized Boolean getPermission(String name) throws RemoteException {
        if (userList.size() == 0) {
            return false;
        }
        return userList.get(0).getRc().requestPermission(name);
    }

    @Override
    public synchronized void loadByteImage(byte[] byteImage) throws RemoteException {
        loadState = 1;
        commandList.clear();
        for (int i = 1; i < userList.size(); i++) {
            userList.get(i).getRc().getByteImage(byteImage);
        }
    }

    @Override
    public int getLoadState() throws RemoteException {
        return loadState;
    }

    @Override
    public byte[] getManagerImage() throws RemoteException {
        return userList.get(0).getRc().getManagerByteImage();
    }

    @Override
    public void initializeRoom() throws RemoteException {
        commandList.clear();
        userList.clear();
        nameList.clear();
        loadState = 0;
    }

    @Override
    public boolean checkRoomState() throws RemoteException {
        // check whether the room is not empty
        return userList.size() != 0;
    }

    @Override
    public synchronized void userRegistration(String user, String host, String port, String userService) throws RemoteException {
        try {
            RemoteClient newRC = (RemoteClient) Naming.lookup("rmi://" + host + ":" + port + "/" + userService);
            userList.add(new UserInformation(user, newRC));
        } catch (MalformedURLException | NotBoundException e) {
            System.out.println("The service is not bound or something wrong with the connection.");
        }
    }

    @Override
    public void removeUser(String username) throws RemoteException {
        for (int i = 0; i < userList.size(); i++) {
            if (Objects.equals(userList.get(i).getUserName(), username)) {
                int finalI = i;
                Thread t = new Thread(() -> {
                    userList.remove(finalI);
                    try {
                        updateList();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
                t.start();
                userList.get(i).getRc().removeUser();
                return;
            }
        }
    }

    @Override
    public synchronized void transferMessage(String message) throws RemoteException {
        chatList.add(message);
        for (UserInformation ui : userList) {
            ui.getRc().updateMessage(message);
        }
    }

    @Override
    public void updateBoard(String boardCommand) throws RemoteException {
        commandList.add(boardCommand);
        for (UserInformation ui : userList) {
            ui.getRc().update(boardCommand);
        }
    }

    @Override
    public void clearWhiteBoard() throws RemoteException {
        transferMessage("The manager has left the room.");
        System.out.println("The manager " + userList.get(0).getUserName() + " has left the room.");

        for (UserInformation ui : userList) {
            Thread t = new Thread(() -> {
                try {
                    ui.getRc().newDialogue("The manager has left the room, so you are removed from the room.");
                } catch (RemoteException e) {
                    System.out.println("The manager has left the room, " + ui.getUserName() + " has disconnected...");
                }
            });
            t.start();
        }

        commandList.clear();
        userList.clear();
        nameList.clear();
        loadState = 0;
    }


    @Override
    public void updateList() throws RemoteException {
        nameList = new ArrayList<>();

        for (UserInformation ui : this.userList) {
            nameList.add(ui.getUserName());
        }

        for (UserInformation ui : this.userList) {
            ui.getRc().updateUserList(nameList);
        }
    }

    @Override
    public synchronized Boolean checkNameExist(String newName) throws RemoteException {
        for (UserInformation ui : userList) {
            if (Objects.equals(ui.getUserName(), newName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<String> getCommandList() throws RemoteException {
        return commandList;
    }
}
