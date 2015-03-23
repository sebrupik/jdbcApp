package jdbcApp.dialogs;

import jdbcApp.dbConnection;

import java.awt.*;
import java.sql.*;
//import java.util.Properties;
import javax.swing.*;

public class dbConnectionInfoDialog extends JDialog {
    dbConnection dbCon;
    DatabaseMetaData dbmd;
    //Properties props;
    
    public dbConnectionInfoDialog(dbConnection dbCon) {
        this.dbCon = dbCon;
        this.setLayout(new GridLayout(0, 2));
        
        try {
            this.dbmd = dbCon.getDBMD();
            add(new JLabel("DB Product")); add(new JLabel(dbmd.getDatabaseProductName()));
            add(new JLabel("Driver name")); add(new JLabel(dbmd.getDriverName()));
            add(new JLabel("Roolback supported")); add(new JLabel(String.valueOf(dbmd.supportsBatchUpdates())));
            
            dbCon.getClientInfo();
            
            
        } catch (SQLException sqle) { System.out.println(sqle); }
        
    }
}
