package jdbcApp;

import jdbcApp.actions.displayDBCDialogAction;
import jdbcApp.actions.displayDBIDialogAction;

import java.awt.*;
import javax.swing.*;

public class statusBar extends JPanel {
    private jdbcApp owner;
    private JProgressBar jpb;
    private JLabel statusLbl;
    private JButton connectBut, connectBut2, infoBut;
    private JMenuBar mb;
    private JMenu dbMenu;

    public statusBar(jdbcApp owner) {
        this.owner = owner;
        
        this.initComponents();
    }
    
    private void initComponents() {
        this.setLayout(new BorderLayout(2,2));
        
        jpb = new JProgressBar();
        statusLbl = new JLabel();
        connectBut = new JButton("Connect");
        infoBut = new JButton("Info");
        
        mb = new JMenuBar();
        mb.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        dbMenu = new JMenu("DB2");
        dbMenu.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        mb.add(dbMenu);

        
        connectBut.addActionListener(new displayDBCDialogAction(owner));
        
        infoBut.addActionListener(new displayDBIDialogAction(owner));
        
        
        
        JPanel rightP = new JPanel();
        rightP.add(jpb);
        rightP.add(infoBut);
        rightP.add(connectBut);
        rightP.add(mb);
        
        this.add(statusLbl, BorderLayout.CENTER);
        this.add(rightP, BorderLayout.EAST);
        
        buildDBMenu();
    }

    public void setStatus(String s) {
        statusLbl.setText(s);
    }
    
    public void buildDBMenu() {
        String[] keys = owner.getdbConnection2().getKeys();
        JMenu tempM;
        
        dbMenu.removeAll();
        
        for (String key : keys) {
            System.out.println("adding menuitem - " +key);
            tempM = new JMenu(key);
            tempM.add(new JMenuItem(key + " Connect"));
            tempM.add(new JMenuItem(key + " Status"));
            dbMenu.add(tempM);
            
            mb.updateUI();
        }
    }
}
