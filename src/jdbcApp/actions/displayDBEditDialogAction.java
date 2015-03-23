package jdbcApp.actions;

import jdbcApp.jdbcApp;
import jdbcApp.gui.dialogs.dbEditDialog;

import java.awt.event.*;
import javax.swing.AbstractAction;

public class displayDBEditDialogAction extends AbstractAction {
    final String _class;
    jdbcApp owner;
    int sizeX = 300;
    int sizeY = 200;

    public displayDBEditDialogAction(jdbcApp owner) {
        this.owner = owner;
        this._class = this.getClass().getName();
    }
    public void actionPerformed(ActionEvent e) {
        try {
            sizeX = Integer.valueOf(owner.getSysProperty("sizeX.dbEditDialog"));
            sizeY = Integer.valueOf(owner.getSysProperty("sizeY.dbEditDialog"));
        } catch (java.io.IOException ioe) { owner.exceptionEncountered(_class, ioe); 
        } catch (java.lang.NumberFormatException nfe) { owner.exceptionEncountered(_class, nfe); }
        
        dbEditDialog dbed = new dbEditDialog(owner);
        dbed.setSize(sizeX, sizeY);
        dbed.setVisible(true);
    }
}
