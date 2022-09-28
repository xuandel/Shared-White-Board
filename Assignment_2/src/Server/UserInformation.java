package Server;


/**
 * @Author Xuande Li
 * @Student_ID 1175020
 * @E-mail_Address xuandel@student.unimelb.edu.au
 */

import Remote.RemoteClient;

public class UserInformation {
    public String userName;
    public RemoteClient rc;

    public UserInformation(String userName, RemoteClient rc) {
        this.userName = userName;
        this.rc = rc;
    }

    public String getUserName() {
        return userName;
    }

    public RemoteClient getRc() {
        return rc;
    }

}
