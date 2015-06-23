package jdbcApp;

import jdbcApp.exceptions.NullDBConnectionException;

import java.io.*;
import java.util.Hashtable;
import java.util.Properties;
//import java.util.ResourceBundle;
import java.sql.*;

public class dbConnection {
    private final String _class;
    private final String _server="127.0.0.1";
    private final String _username="root";
    private final String _password="password";

    private String server, username, password;
    private Connection dbcon;
    private Hashtable<String, PreparedStatement> psHash;
    Properties props, psProps;
    //ResourceBundle psRB;

    public dbConnection(String server, String username, String password, Properties props, Properties psProps)  {
        this.server = server;
        this.username = username;
        this.password = password;
        this.props = props;
        this.psProps = psProps;

        this._class = this.getClass().getName();

        this.createConnection(server, username, password);
        
        //collectPreparedStatements();
    }


    public void closeConnection() {
        if(dbcon != null) {
            try {
                System.out.println(_class+"/closeDBConnection - attempting");
                dbcon.close();
                System.out.println(_class+"/closeDBConnection - done");
            } catch(SQLException sqle) { System.out.println(_class+"/"+sqle); }
        }
    }

    public boolean recreateConnection() {
        this.createConnection(server, username, password);

        return true;
    }


    public void createConnection(String server, String usr, String pwd) { 
        closeConnection();
        clearPreparedStatements();
        try {
            dbcon = DriverManager.getConnection("jdbc:mysql://"+server+"?user="+usr+"&password="+pwd);
            dbcon.setAutoCommit(false);
            //this.saveProperties("lastSQLServerIP", server);
            //this.saveProperties("lastSQLuser", usr);
        } catch (SQLException sqle) { 
            System.out.println(_class+"/createConection - ");
            System.out.println("SQLException: " + sqle.getMessage()); 
            System.out.println("SQLState: " + sqle.getSQLState()); 
            System.out.println("VendorError: " + sqle.getErrorCode()); 
        }
    }

    /**
     * consults the resourceBundle 'psRB', retrieves each key relating
     * to a preparedstatements. initally a Stirng value of "NULL" is saved as the
     * keys value. This is done as it will speed up boot times since PreparedStatements
     * a not created initially. Instread when getPS is called, if a String value
     * of "NULL" is returned then the PreparedStatements is created and stored,
     * facialitaing runtime retrevial instead of boot time.
     *
     *@ modified 2005-11-03
     */
    /*private void collectPreparedStatements() {
        System.out.println(_class+"/collectPreparedStatements - starting");
        if(psHash == null)
            psHash = new Hashtable();
        String key;

        for (java.util.Enumeration e = psProps.propertyNames() ; e.hasMoreElements() ;) {
            key = (String)e.nextElement();
            if(key.substring(0,3).equals("ps_")) {
                psHash.put(key, "NULL");
                System.out.println(_class+"/collectPreparedStatements adding "+psProps.getProperty(key));
            }
        }
        System.out.println(_class+"/collectPreparedStatements - finished");
    }*/

    private void clearPreparedStatements() {
        System.out.print(_class+"/clearPreparedStatements....");
        if(psHash!=null)
            psHash.clear();
        System.out.println("DONE");
    }

    public PreparedStatement getPS(String psname) {
        System.out.println(_class+"/getPS - returning PS : "+psname);
        PreparedStatement p = null;
        if(psHash == null)
            psHash = new Hashtable<String, PreparedStatement>();

        String item = psProps.getProperty(psname);
        if(item==null) {
            System.err.println("PreparedStatement "+psname+" missing!! -:FATAL:- Exiting.");
            System.exit(0);
        } else {
            psHash.put(psname, this.createPreparedStatement(item));
            p = (PreparedStatement)psHash.get(psname);
        }
        return p;
    }

    /*public PreparedStatement getPS(String psname) {
        System.out.println(_class+"/getPS - returning PS : "+psname);
        PreparedStatement p = null;
        if(psHash.isEmpty())
            collectPreparedStatements();

        if(!psHash.containsKey(psname)) {
            System.err.println("PreparedStatement "+psname+" missing!! -:FATAL:- Exiting.");
            System.exit(0);
        } else {
            Object obj = psHash.get(psname);
            if( obj.getClass().getName().equals("java.lang.String") )
                psHash.put(psname, this.createPreparedStatement(psProps.getProperty(psname)));


            p = (PreparedStatement)psHash.get(psname);
        }
        return p;
    }*/

    public PreparedStatement createPreparedStatement(String s) throws NullDBConnectionException {
        System.out.println(_class+"/createPreparedStatement - attepting to create PS : "+s);
        if(dbcon !=null) {
            try {
                return dbcon.prepareStatement(s);
            } catch (SQLException sqle) {
                System.out.println(_class+"/getPreparedStatment - ");
                System.out.println("SQLException: " + sqle.getMessage());
                System.out.println("SQLState: " + sqle.getSQLState());
                System.out.println("VendorError: " + sqle.getErrorCode());
            }
        }
        throw new NullDBConnectionException(_class+"/createPreparedStatement - dbcon is null!");
    }

    public int executeUpdate(PreparedStatement ps) throws NullDBConnectionException {  return this.executeUpdate(new PreparedStatement[]{ps}); }
    public int executeUpdate(PreparedStatement[] ps) throws NullDBConnectionException {
        Statement stmt = null;
        int updates = 0;
        if (dbcon != null) {
            try {
                stmt = dbcon.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                for(int i=0; i<ps.length; i++) {
                    System.out.println(ps[i]);
                    updates += ps[i].executeUpdate();
                }
                dbcon.commit();
                //savepointTable.addRow(new Object[]{dbcon.setSavepoint(), ps[0].toString()});
                //System.out.println("dbCon columncount is "+savepointTable.getColumnCount());
                return updates;
            } catch (SQLException sqle) {
                try { dbcon.rollback(); } catch (SQLException sqle2) { System.out.println(_class+"/executeUpdate(PS)(rollback) - "+sqle2); }
                System.out.println(_class+"/executeUpdate(PS) - ");
                System.out.println("SQLException: " + sqle.getMessage());
                System.out.println("SQLState: " + sqle.getSQLState());
                System.out.println("VendorError: " + sqle.getErrorCode());
            } finally {
                if (stmt != null) {
                    try { stmt.close(); } catch (SQLException sqle) { System.out.println(_class+"/executeUpdate(PS)(finally) - "+sqle); }
                }
                stmt = null;
            }

        } else { throw new NullDBConnectionException(); }
        throw new NullDBConnectionException();
    }

    public ResultSet executeQuery(String q) throws NullDBConnectionException {
        System.out.println(q);
        Statement stmt = null;
        if (dbcon != null) {
            try {
                stmt = dbcon.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                return stmt.executeQuery(q);
            } catch (SQLException sqle) {
                System.out.println(_class+"/executeQuery(S) - ");
                System.out.println("SQLException: " + sqle.getMessage());
                System.out.println("SQLState: " + sqle.getSQLState());
                System.out.println("VendorError: " + sqle.getErrorCode());
            }
        } else { throw new NullDBConnectionException(); }
        throw new NullDBConnectionException();
    }
    
    public ResultSet executeQuery(PreparedStatement ps) throws NullDBConnectionException {
        System.out.println(ps);
        //Statement stmt = null;
        if (dbcon != null) {
            try {
                //stmt = dbcon.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                return ps.executeQuery();
            } catch (SQLException sqle) {
                System.out.println(_class+"/executeQuery(PS) - ");
                System.out.println("SQLException: " + sqle.getMessage());
                System.out.println("SQLState: " + sqle.getSQLState());
                System.out.println("VendorError: " + sqle.getErrorCode());
            }
        } else { throw new NullDBConnectionException(); }
        throw new NullDBConnectionException();
    }
    
    public boolean execute(String s) throws NullDBConnectionException {
        System.out.println(s);
        Statement stmt = null;
        if (dbcon != null) {
            try {
                System.out.println("hello 1");
                stmt = dbcon.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                System.out.println("hello 2");
                return stmt.execute(s);
            } catch (SQLException sqle) { 
                System.out.println(_class+"/execute(s) - ");
                System.out.println("SQLException: " + sqle.getMessage()); 
                System.out.println("SQLState: " + sqle.getSQLState()); 
                System.out.println("VendorError: " + sqle.getErrorCode()); 
            } 
        } else { throw new NullDBConnectionException(); }
        //throw new NullDBConnectionException();
        return false;  //added 2006/12/05  possibly revert back to above line
    }


    private void saveProperties(String key, String value) {
        try {
            java.security.AccessController.checkPermission(new FilePermission("client.properties","write"));
            try {
                props.setProperty(key, value);
                props.store( new FileOutputStream(new File("client.properties")), "");
            } catch (IOException ioe) { System.out.println(ioe); }

        } catch(java.lang.SecurityException se) { System.out.println(_class+"/saveProperties - "+se); }
    }

    public String getProperty(String prop) {
        if (!props.isEmpty()) {
            System.out.println("returning prop : "+props.getProperty(prop));
            return props.getProperty(prop);
        }
        throw new NullPointerException("There appears to be no properties file.");
    }

    public boolean isConnected() {
        if(dbcon != null) {
            try { return !dbcon.isClosed();
            } catch(SQLException sqle) { System.out.println(sqle); return false; }
        } else { return false; }
    }

    public void getClientInfo() {
        System.out.println(_class+"/getClientInfo - ");
        try {
            Properties prop = dbcon.getClientInfo();
            for (java.util.Enumeration e = prop.propertyNames(); e.hasMoreElements();)
                System.out.println(e.nextElement());
        } catch(SQLException sqle) { System.out.println(_class+"/getClientInfo - "+sqle); }
    }

    public DatabaseMetaData getMetaData() throws SQLException { return this.dbcon.getMetaData(); }
    public DatabaseMetaData getDBMD() throws SQLException { return dbcon.getMetaData(); }
    
    public String getConnectionUserNameShort() throws SQLException {
        String s = this.getMetaData().getUserName();
        return s.substring(0, s.indexOf("@"));
    }

}
