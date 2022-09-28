package Remote;


/**
 * @Author Xuande Li
 * @Student_ID 1175020
 * @E-mail_Address xuandel@student.unimelb.edu.au
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteClient extends Remote {
    public void update(String updateCommand) throws RemoteException;
    public void updateMessage(String newMessage) throws RemoteException;
    public void updateUserList(List<String> nameList) throws RemoteException;
    public void removeUser() throws RemoteException;
    public void newDialogue(String message) throws RemoteException;
    public void clear() throws RemoteException;
    public void getByteImage(byte[] newImage) throws RemoteException;
    public Boolean requestPermission(String name) throws RemoteException;
    public byte[] getManagerByteImage() throws RemoteException;
    public void getCommand() throws RemoteException;
}
