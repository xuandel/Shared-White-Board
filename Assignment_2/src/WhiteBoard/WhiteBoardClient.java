package WhiteBoard;


/**
 * @Author Xuande Li
 * @Student_ID 1175020
 * @E-mail_Address xuandel@student.unimelb.edu.au
 */

import Client.JoinWhiteBoard;
import Remote.RemoteServer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;

public class WhiteBoardClient extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
    protected RemoteServer rs;
    protected String userName;
    protected JoinWhiteBoard joinWhiteBoard;
    Color color = Color.black;
    Graphics2D g;
    int x0, y0, x1, y1;
    double x3;
    String shape = "line";
    JPanel actionPanel, colorPanel;
    PaintCanvasClient mainPanel;
    String text;
    JTabbedPane userChatPane;
    JTextArea userMessagePanel, userTextArea, currentColor;
    JTextField currentShape;
    JScrollPane scrollPane;
    JButton currentColorButton;

    public WhiteBoardClient(JoinWhiteBoard joinWhiteBoard, RemoteServer rs, String userName) {
        // define the default remote server
        this.joinWhiteBoard = joinWhiteBoard;
        this.rs = rs;
        this.userName = userName;

        // define three new panels
        actionPanel = new JPanel();
        colorPanel = new JPanel();
        mainPanel = new PaintCanvasClient(this.joinWhiteBoard);

        // set size and location for the main window
        setTitle("Shared White Board - User - " + userName);
        this.setSize(1280, 720);
        this.setLocationRelativeTo(null);

        currentShape = new JTextField("Current Shape: Line");
        currentShape.setPreferredSize(new Dimension(150,40));
        currentShape.setEditable(false);
        currentShape.setHorizontalAlignment(JTextField.CENTER);
        currentShape.setBackground(Color.WHITE);
        actionPanel.add(currentShape);
        currentColor = new JTextArea("Current Color");
        colorPanel.add(currentColor);

        // add current color
        currentColorButton = new JButton();
        currentColorButton.setPreferredSize(new Dimension(40, 40));
        currentColorButton.setBackground(Color.BLACK);
        colorPanel.add(currentColorButton);

        JTextArea colorArea = new JTextArea("Color List");
        colorPanel.add(colorArea);

        // add buttons for shapes
        String[] shapeList = {"line", "circle", "triangle", "rectangle", "text"};
        for (int i = 0; i < shapeList.length; i++) {
            JButton jButton = new JButton(shapeList[i]);
            jButton.addActionListener(this);
            actionPanel.add(jButton);
        }

        // add buttons for colors
        Color[] colourList = {Color.BLACK, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.MAGENTA, Color.GREEN,
                Color.ORANGE, Color.PINK, Color.WHITE, Color.LIGHT_GRAY, Color.BLUE, Color.RED, new Color(10, 30, 50)
                , new Color(50, 40, 0), new Color(200, 25, 90), new Color(150, 50, 0), new Color(100, 200, 140)};
        for (int i = 0; i < colourList.length; i++) {
            JButton jButton = new JButton();
            jButton.addActionListener(this);
            jButton.setPreferredSize(new Dimension(40, 40));
            jButton.setBackground(colourList[i]);
            colorPanel.add(jButton);
        }

        // set chat and users panel
        userChatPane = new JTabbedPane();

        // set messages
        userMessagePanel = new JTextArea();
        userMessagePanel.setEditable(false);
        userMessagePanel.setAutoscrolls(true);
        scrollPane = new JScrollPane(userMessagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setSize(300, 200);

        // set input
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        JScrollPane inputScrollPane = new JScrollPane(textArea);


        // add post button
        JButton postButton = new JButton("Enter");
        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    rs.transferMessage(userName + ": " + textArea.getText());
                    textArea.setText("");
                } catch (RemoteException ex) {
                    disconnectionException();
                }
            }
        });

        // set user menu
        userTextArea = new JTextArea();
        userTextArea.setText("Manager: \n" + this.userName + "\n---------\nUsers:\n");
        userTextArea.setEditable(false);

        // add name input panel
        JTextArea nameArea = new JTextArea();
        textArea.setLineWrap(true);
        JScrollPane nameScrollPane = new JScrollPane(nameArea);

        // add remove button
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String removeUser = nameArea.getText();
                    if (rs.checkNameExist(removeUser)) {
                        rs.transferMessage(removeUser + " been removed from the user list.");
                        rs.removeUser(removeUser);
                    } else {
                        JOptionPane.showMessageDialog(null, "No this user exists, please check the name list", "Reminder", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (RemoteException ex) {
                    disconnectionException();
                }
                nameArea.setText("");
            }
        });


        // set layout of chat panel and user panel
        JPanel chatPanel = new JPanel();
        JPanel userPanel = new JPanel();
        GridBagLayout chatLayout = new GridBagLayout();
        chatPanel.setLayout(chatLayout);
        userPanel.setLayout(chatLayout);

        GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
        scrollPaneConstraints.gridwidth = 10;
        scrollPaneConstraints.gridheight = 8;
        scrollPaneConstraints.weightx = 10;
        scrollPaneConstraints.weighty = 8;
        scrollPaneConstraints.gridx = 0;
        scrollPaneConstraints.gridy = 0;
        scrollPaneConstraints.fill = GridBagConstraints.BOTH;
        chatPanel.add(scrollPane, scrollPaneConstraints);
        userPanel.add(userTextArea, scrollPaneConstraints);

        GridBagConstraints inputConstraints = new GridBagConstraints();
        inputConstraints.gridwidth = 8;
        inputConstraints.gridheight = 2;
        inputConstraints.weightx = 8;
        inputConstraints.weighty = 2;
        inputConstraints.gridx = 0;
        inputConstraints.gridy = 8;
        inputConstraints.fill = GridBagConstraints.BOTH;
        chatPanel.add(inputScrollPane, inputConstraints);

        GridBagConstraints postConstraints = new GridBagConstraints();
        postConstraints.gridwidth = 2;
        postConstraints.gridheight = 2;
        postConstraints.weightx = 2;
        postConstraints.weighty = 2;
        postConstraints.gridx = 8;
        postConstraints.gridy = 8;
        postConstraints.fill = GridBagConstraints.BOTH;
        chatPanel.add(postButton, postConstraints);


        // set tab
        userChatPane.addTab("Chat Room", chatPanel);
        userChatPane.addTab("User List", userPanel);

        // set background color for three panels
        mainPanel.setBackground(Color.white);

        // set location for each panel
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(actionPanel, BorderLayout.NORTH);
        this.add(colorPanel, BorderLayout.SOUTH);
        this.add(userChatPane, BorderLayout.EAST);


        // set default closing and visibility
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    rs.leaveRoom(userName);
                    System.exit(0);
                } catch (RemoteException ex) {
                    addNewDialogue("The manager has left the room, so you are removed from the room.");
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        this.setVisible(true);
        mainPanel.setVisible(true);


        // add mouse listener
        mainPanel.addMouseListener(this);
        mainPanel.addMouseMotionListener(this);
    }

    public void painting(String com) {
        String[] par = com.split("#");
        String[] sc = par[0].split("@");
        String tempShape = sc[0];
        int tempColor = Integer.parseInt(sc[1]);
        int tempx0 = Integer.parseInt(par[1]);
        int tempy0 = Integer.parseInt(par[2]);
        Graphics2D tempG = (Graphics2D) mainPanel.getGraphics();
        tempG.setColor(new Color(tempColor));

        switch (tempShape) {
            case "line":
                tempG.drawLine(tempx0, tempy0, Integer.parseInt(par[3]), Integer.parseInt(par[4]));
                break;
            case "text":
                tempG.drawString(par[3], tempx0, tempy0);
                break;
            case "circle":
                tempG.drawOval(tempx0, tempy0, Integer.parseInt(par[3]) - tempx0, Integer.parseInt(par[4]) - tempy0);
                break;
            case "triangle":
                tempG.drawPolygon(new int[]{tempx0, Integer.parseInt(par[3]), Integer.parseInt(par[5])}, new int[]{tempy0, Integer.parseInt(par[4]), Math.max(tempy0, Integer.parseInt(par[4]))}, 3);
                break;
            case "rectangle":
                tempG.drawRect(tempx0, tempy0, Integer.parseInt(par[3]) - tempx0, Integer.parseInt(par[4]) - tempy0);
                break;
        }
    }

    public void addNewMessage(String newMessage) {
        userMessagePanel.append(newMessage + "\n");
    }

    public void addNewClient(String newClient) {
        userTextArea.setText(newClient);
    }

    public void removeReminder() {
        JOptionPane.showMessageDialog(null, "You have been kicked by the manager.", "Remove Reminder", JOptionPane.ERROR_MESSAGE);
    }

    public void addNewDialogue(String newD) {
        JOptionPane.showMessageDialog(null, newD, "Reminder", JOptionPane.WARNING_MESSAGE);
        if (newD.equals("The manager has left the room, so you are removed from the room.")) {
            System.exit(0);
        }
    }

    public void loadImage(byte[] newImage) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(newImage);
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            JLabel picLabel = new JLabel(new ImageIcon(bufferedImage));
            mainPanel.removeAll();
            mainPanel.add(picLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearCanvas() {
        mainPanel.removeAll();
//            BufferedImage bufferedImage = ImageIO.read(new File("Empty.jpg"));
//            JLabel picLabel = new JLabel(new ImageIcon(bufferedImage));
//            mainPanel.add(picLabel);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("")) {
            // get the current color information
            JButton jButton = (JButton) e.getSource();
            color = jButton.getBackground();
            currentColorButton.setBackground(color);
        } else {
            JButton jButton = (JButton) e.getSource();
            shape = jButton.getActionCommand();
            currentShape.setText("Current Shape: " + shape);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        x0 = e.getX();
        y0 = e.getY();
        if (shape.equals("text")) {
            text = JOptionPane.showInputDialog("Input the text here.");
            if (!(text == null)) {
                try {
                    rs.updateBoard("text@" + color.getRGB() + "#" + String.valueOf(x0) + "#" + String.valueOf(y0) + "#" + String.valueOf(text));
                } catch (RemoteException ex) {
                    disconnectionException();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // set default size of each shape when pressed without dragging
        g = (Graphics2D) mainPanel.getGraphics();
        x0 = e.getX();
        y0 = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        g.setColor(color);
        x1 = e.getX();
        y1 = e.getY();
        int width = x1 - x0;
        int height = y1 - y0;

        if (shape.equals("circle") || shape.equals("rectangle")) {
            if (width < 0) {
                int temp = x0;
                x0 = x1;
                x1 = temp;
            }
            if (height < 0) {
                int temp = y0;
                y0 = y1;
                y1 = temp;
            }
        }

        if (shape.equals("line")) {
            try {
                rs.updateBoard("line@" + color.getRGB() + "#" + String.valueOf(x0) + "#" + String.valueOf(y0) + "#" + String.valueOf(x1) + "#" + String.valueOf(y1));
            } catch (RemoteException ex) {
                disconnectionException();
            }
        } else if (shape.equals("circle")) {
            try {
                rs.updateBoard("circle@" + color.getRGB() + "#" + String.valueOf(x0) + "#" + String.valueOf(y0) + "#" + String.valueOf(x1) + "#" + String.valueOf(y1));
            } catch (RemoteException ex) {
                disconnectionException();
            }
        } else if (shape.equals("triangle")) {

            if (height < 0) {
                x3 = 2 * (float) x1 - (float) x0;
            } else {
                x3 = 2 * (float) x0 - (float) x1;
            }
            try {
                rs.updateBoard("triangle@" + color.getRGB() + "#" + String.valueOf(x0) + "#" + String.valueOf(y0) + "#" + String.valueOf(x1) + "#" + String.valueOf(y1) + "#" + String.valueOf((int) x3));
            } catch (RemoteException ex) {
                disconnectionException();
            }
        } else if (shape.equals("rectangle")) {
            try {
                rs.updateBoard("rectangle@" + color.getRGB() + "#" + String.valueOf(x0) + "#" + String.valueOf(y0) + "#" + String.valueOf(x1) + "#" + String.valueOf(y1));
            } catch (RemoteException ex) {
                disconnectionException();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void disconnectionException() {
        try {
            rs.initializeRoom();
        } catch (RemoteException ex) {
            System.out.println("The server has disconnected by mistake, the white board has been closed, please try later.");
            System.exit(0);
        }
        System.out.println("The manager has disconnected by mistake, the white board has been closed, please try later.");
        System.exit(0);
    }
}
