package WhiteBoard;

import Remote.RemoteServer;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * @Author Xuande Li
 * @Student_ID 1175020
 * @E-mail_Address xuandel@student.unimelb.edu.au
 */
public class PaintCanvas extends JPanel {
    protected RemoteServer rs;

    public PaintCanvas(RemoteServer rs) {
        this.rs = rs;
    }

    public void paint(Graphics g) {
        super.paint(g);

        try {
            ArrayList<String> comList = rs.getCommandList();
            for (String com : comList) {
                String[] par = com.split("#");
                String[] sc = par[0].split("@");
                String tempShape = sc[0];
                int tempColor = Integer.parseInt(sc[1]);
                int tempx0 = Integer.parseInt(par[1]);
                int tempy0 = Integer.parseInt(par[2]);
                g.setColor(new Color(tempColor));

                switch (tempShape) {
                    case "line":
                        g.drawLine(tempx0, tempy0, Integer.parseInt(par[3]), Integer.parseInt(par[4]));
                        break;
                    case "text":
                        g.drawString(par[3], tempx0, tempy0);
                        break;
                    case "circle":
                        g.drawOval(tempx0, tempy0, Integer.parseInt(par[3]) - tempx0, Integer.parseInt(par[4]) - tempy0);
                        break;
                    case "triangle":
                        g.drawPolygon(new int[]{tempx0, Integer.parseInt(par[3]), Integer.parseInt(par[5])}, new int[]{tempy0, Integer.parseInt(par[4]), Math.max(tempy0, Integer.parseInt(par[4]))}, 3);
                        break;
                    case "rectangle":
                        g.drawRect(tempx0, tempy0, Integer.parseInt(par[3]) - tempx0, Integer.parseInt(par[4]) - tempy0);
                        break;
                }
            }
        } catch (RemoteException e) {
            System.out.println("Painting failed, please check the connection.");
            e.printStackTrace();
            System.exit(0);
        }
    }
}
