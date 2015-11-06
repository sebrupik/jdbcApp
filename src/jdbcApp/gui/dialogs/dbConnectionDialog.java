/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdbcApp.gui.dialogs;

import jdbcApp.jdbcApp;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class dbConnectionDialog extends JDialog {
    private final String _class;
    private jdbcApp _owner;
    JTextField serverAdrTxt, usrTxt;
    JPasswordField pwdTxt;
    String serverIP, userName, pwdStr;
    JButton conBut, cancelBut;
    int sizeX, sizeY;

    public dbConnectionDialog(jdbcApp owner) {
        super(owner, "MySQL Connection", true);
        this._class = this.getClass().getName();
        this._owner = owner;
        try {
            serverIP = owner.getSysProperty("jdbc.server");
            userName = owner.getSysProperty("jdbc.username");
            pwdStr = owner.getSysProperty("jdbc.password");
            //sizeX = Integer.parseInt(owner.getSysProperty("sizeX.dbConnection"));
            //sizeY = Integer.parseInt(owner.getSysProperty("sizeY.dbConnection"));
            this.setSize(Integer.parseInt(owner.getSysProperty("sizeX.dbConnectionDialog")), Integer.parseInt(owner.getSysProperty("sizeY.dbConnectionDialog")));
        } catch(IOException ioe) { System.out.println(_class+"/dbConnectionDialog - "+ioe); }


        System.out.println("***************************-------------------------"+serverIP+" -- "+userName);

        Container cp = this.getContentPane();

        JPanel titlePanel = new JPanel(new BorderLayout(0,0));
        titlePanel.setBackground(new java.awt.Color(178,212,249));
        JLabel titleLbl = new JLabel("Connection settings for MySQL server", javax.swing.SwingConstants.LEADING);
        titleLbl.setForeground(Color.blue);
        titleLbl.setBorder(BorderFactory.createLoweredBevelBorder());
        titlePanel.add(titleLbl, BorderLayout.CENTER);

        JPanel dbconPanel = new JPanel(new GridLayout(3, 2));

        serverAdrTxt = new JTextField(serverIP, 20);
        usrTxt = new JTextField(userName, 20);
        usrTxt.addFocusListener(new selectTextListener(usrTxt));
        //final JTextField pwdTxt = new JTextField("", 20);
        pwdTxt = new JPasswordField(20);
        pwdTxt.addFocusListener(new selectTextListener(pwdTxt));
        pwdTxt.addKeyListener(new keyListener());

        //pwdTxt.requestFocusInWindow();

        dbconPanel.add(new JLabel("Server Address : "));
        dbconPanel.add(serverAdrTxt);
        dbconPanel.add(new JLabel("User Name : "));
        dbconPanel.add(usrTxt);
        dbconPanel.add(new JLabel("Password : "));
        dbconPanel.add(pwdTxt);

        JPanel buttonPanel = new JPanel(new GridLayout(1,2));

        conBut = new JButton("Connect");
        cancelBut = new JButton("Cancel");

        buttonPanel.add(conBut);
        buttonPanel.add(cancelBut);

        cp.add(titlePanel, BorderLayout.NORTH);
        cp.add(dbconPanel, BorderLayout.CENTER);
        cp.add(buttonPanel, BorderLayout.SOUTH);


        /*WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("someone has clicked on the cross...."+dbCon);
                if(dbCon==null) {
                    JOptionPane.showMessageDialog(conBut, "Connection dialog closed, with no active connection. Shuting down." , "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        };*/
        this.addWindowListener(new windowCloser(owner, conBut));

        this.pack();
        pwdTxt.grabFocus();


        Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dSize = this.getPreferredSize();
        int width = dSize.width;
        int height = dSize.height;

        this.setLocation(scrnSize.width/2 - (width/2),
                                scrnSize.height/2 - (height/2));


        //conBut.addActionListener(new connectAction(dbConDialog, serverAdrTxt, usrTxt, pwdTxt));
        conBut.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) {
                connect();
            }
        });
        cancelBut.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) {
                dispose();
                //System.exit(0);
                _owner.closeApp();
            }
        });
    }

    private void connect() {
        _owner.createdbConnection(serverAdrTxt.getText(), usrTxt.getText(), String.valueOf(pwdTxt.getPassword()));
        if(!_owner.getdbConnection().isConnected()) {
            JOptionPane.showMessageDialog(conBut, "Connection to SQL server failed." , "Error", JOptionPane.ERROR_MESSAGE);
            //owner.dbConnectionLost(true);
        } else {
            JOptionPane.showMessageDialog(conBut, "Connection to SQL server successful." , "Info", JOptionPane.INFORMATION_MESSAGE);
            //owner.dbConnectionLost(false);
            //if(!owner.dbCon.checkUserClearance(usrTxt.getText(), _tablePrefix)) {
            //    JOptionPane.showMessageDialog(conBut, "Your user name is not permitted to access this projects DB table. Exiting." , "Error", JOptionPane.ERROR_MESSAGE);
            //    System.exit(0);
            
            //save the settings
            _owner.saveSysProperty("jdbc.server", serverAdrTxt.getText());
            _owner.saveSysProperty("jdbc.username", usrTxt.getText());
            _owner.saveSysProperty("jdbc.password", String.valueOf(pwdTxt.getPassword()));
            _owner.saveSysProperty("sizeX.dbConnectionDialog", String.valueOf(this.getSize().width));
            _owner.saveSysProperty("sizeY.dbConnectionDialog", String.valueOf(this.getSize().height));
        }
        this.dispose();
    }

    //*************************

    class windowCloser extends WindowAdapter {
        jdbcApp owner;
        JButton target;

        public windowCloser(jdbcApp owner, JButton target) {
            this.owner = owner;
        }
        @Override public void windowClosing(WindowEvent e) {
            System.out.println("someone has clicked on the cross...."+owner.getdbConnection());
            if(owner.getdbConnection()==null) {
                JOptionPane.showMessageDialog(target, "Connection dialog closed, with no active connection. Shuting down." , "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    class keyListener implements java.awt.event.KeyListener {
        @Override public void keyTyped(KeyEvent e) {
	    int key = e.getKeyCode();
            if (key == KeyEvent.VK_ENTER) {
                System.out.println("1");
            }
        }

        @Override public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_ENTER) {
                System.out.println("2");
                connect();
            }
        }

        @Override public void keyReleased(KeyEvent e) { }
    }

    class selectTextListener implements java.awt.event.FocusListener {
        JTextField target;
        public selectTextListener(JTextField target) { this.target = target; }
        @Override public void focusGained(FocusEvent e) { target.selectAll(); }
        @Override public void focusLost(FocusEvent e) { }
    }
}
