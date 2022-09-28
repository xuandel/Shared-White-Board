package Remote;


/**
 * @Author Xuande Li
 * @Student_ID 1175020
 * @E-mail_Address xuandel@student.unimelb.edu.au
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RemoteServer extends Remote {
    public boolean checkRoomState() throws RemoteException;
    public void userRegistration(String user, String host, String port, String userService)throws RemoteException;
    public void removeUser(String username) throws RemoteException;
    public void transferMessage(String message) throws RemoteException;
    public void updateBoard(String boardCommand) throws RemoteException;
    public void clearWhiteBoard() throws RemoteException;
    public void updateList() throws RemoteException;
    public Boolean checkNameExist(String newName) throws RemoteException;
    public ArrayList<String> getCommandList() throws RemoteException;
    public void clearCanvas() throws RemoteException;
    public void revoke() throws RemoteException;
    public void leaveRoom(String clientName) throws RemoteException;
    public void showNewDialogue(String message) throws RemoteException;
    public Boolean getPermission(String name) throws RemoteException;
    public void loadByteImage(byte[] byteImage) throws RemoteException;
    public int getLoadState() throws RemoteException;
    public byte[] getManagerImage() throws RemoteException;
    public void initializeRoom() throws RemoteException;
}
