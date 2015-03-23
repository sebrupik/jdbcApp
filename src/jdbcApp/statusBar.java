package jdbcApp;

import jdbcApp.actions.displayDBCDialogAction;
import jdbcApp.actions.displayDBIDialogAction;

import java.awt.*;
import javax.swing.*;

public class statusBar extends JPanel {
    private jdbcApp owner;
    private JProgressBar jpb;
    private JLabel statusLbl;
    private JButton connectBut, infoBut;

    public statusBar(jdbcApp owner) {
        this.owner = owner;
        this.setLayout(new BorderLayout(2,2));

        jpb = new JProgressBar();
        statusLbl = new JLabel();
        connectBut = new JButton("Connect");
        infoBut = new JButton("Info");

        connectBut.addActionListener(new displayDBCDialogAction(owner));
        infoBut.addActionListener(new displayDBIDialogAction(owner));

        JPanel rightP = new JPanel();
        rightP.add(jpb);
        rightP.add(infoBut);
        rightP.add(connectBut);


        this.add(statusLbl, BorderLayout.CENTER);
        this.add(rightP, BorderLayout.EAST);
    }

    public void setStatus(String s) {
        statusLbl.setText(s);
    }
}
