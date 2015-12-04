package jdbcApp;

import jdbcApp.actions.displayDBCDialogAction;
import jdbcApp.actions.displayDBCDialogAction2;
import jdbcApp.actions.displayDBIDialogAction;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        rightP.add(this.buildDBStatus(new JPanel()));
        
        this.add(statusLbl, BorderLayout.CENTER);
        this.add(rightP, BorderLayout.EAST);
    }

    public void setStatus(String s) {
        statusLbl.setText(s);
    }
    
    private JPanel buildDBStatus(JPanel target) {
        target.removeAll();
        
        String[] keys = owner.getdbConnection2().getKeys();
        JButton but;
        
        for (String key : keys) {
            but = new JButton("Icon");
            but.setToolTipText(key);
            but.addActionListener(new displayDBCDialogAction2(owner, key));
            target.add(but);
        }
        
        target.revalidate();
        target.repaint();
        return target;
    } 
}
