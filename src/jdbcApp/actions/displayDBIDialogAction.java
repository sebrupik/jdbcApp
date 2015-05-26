package jdbcApp.actions;

import jdbcApp.jdbcApp;
import jdbcApp.gui.dialogs.dbConnectionInfoDialog;

import java.awt.event.*;
import javax.swing.AbstractAction;

public class displayDBIDialogAction extends AbstractAction {
    jdbcApp owner;

    public displayDBIDialogAction(jdbcApp owner) {
        this.owner = owner;
    }
    @Override public void actionPerformed(ActionEvent e) {
        //if(owner.getdbConnection().isConnected()) {
        if(owner.getdbConnectionStatus()==jdbcApp.DBCON_CONNECTED) {
            dbConnectionInfoDialog dbcd = new dbConnectionInfoDialog(owner.getdbConnection());
            dbcd.setVisible(true);
        }
    }
}
