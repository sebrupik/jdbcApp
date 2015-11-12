package jdbcApp.actions;

import jdbcApp.jdbcApp;
import jdbcApp.gui.dialogs.dbConnectionDialog;

import java.awt.event.*;
import javax.swing.AbstractAction;

public class displayDBCDialogAction extends AbstractAction {
    final String _class;
    jdbcApp owner;
    int sizeX = 300;
    int sizeY = 200;

    public displayDBCDialogAction(jdbcApp owner) {
        this.owner = owner;
        this._class = this.getClass().getName();
    }
    @Override public void actionPerformed(ActionEvent e) {
        try {
            sizeX = Integer.valueOf(owner.getSysProperty("sizeX.dbConnectionDialog"));
            sizeY = Integer.valueOf(owner.getSysProperty("sizeY.dbConnectionDialog"));
        } catch (java.io.IOException ioe) { owner.log(java.util.logging.Level.SEVERE, _class, "actionPerformed", ioe); 
        } catch (java.lang.NumberFormatException nfe) { owner.log(java.util.logging.Level.SEVERE, _class, "actionPerformed", nfe); }
        
        dbConnectionDialog dbcd = new dbConnectionDialog(owner);
        dbcd.setSize(sizeX, sizeY);
        dbcd.setVisible(true);
    }
}
