package jdbcApp.actions;

import jdbcApp.jdbcApp;
import jdbcApp.gui.dialogs.dbConnectionDialog2;

import java.awt.event.*;
import javax.swing.AbstractAction;

public class displayDBCDialogAction2 extends AbstractAction {
    final String _class;
    jdbcApp owner;
    private String name;
    int sizeX = 300;
    int sizeY = 200;

    public displayDBCDialogAction2(jdbcApp owner, String name) {
        this.owner = owner;
        this.name = name;
        this._class = this.getClass().getName();
    }
    @Override public void actionPerformed(ActionEvent e) {
        try {
            sizeX = Integer.valueOf(owner.getSysProperty("sizeX.dbConnectionDialog"));
            sizeY = Integer.valueOf(owner.getSysProperty("sizeY.dbConnectionDialog"));
        } catch (java.io.IOException ioe) { owner.log(java.util.logging.Level.SEVERE, _class, "actionPerformed", ioe); 
        } catch (java.lang.NumberFormatException nfe) { owner.log(java.util.logging.Level.SEVERE, _class, "actionPerformed", nfe); }
        
        dbConnectionDialog2 dbcd = new dbConnectionDialog2(owner, name); 
        dbcd.setSize(sizeX, sizeY);
        dbcd.setVisible(true);
    }
}
