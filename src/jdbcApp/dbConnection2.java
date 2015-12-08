package jdbcApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import jdbcApp.exceptions.NullDBConnectionException;

/**
 * Holds multiple JDBC connection objects, and allows manipulation of their configuration
 * and provides method for their use.
 * 
 * Created for the v6macassocgui application
 * 
 * 
 * @author snr
 */
public class dbConnection2 {
    private final String _class;
    private jdbcApp owner;
    private Properties props;
    
    private HashMap<String, dbCon2Object> dbConHMap;
    
    public dbConnection2(jdbcApp owner, Properties sysProps) {
        this._class = this.getClass().getName();
        this.owner = owner;
        this.props = sysProps;
        
        dbConHMap = new HashMap<>();
        
        createConnectionContainers();
    }
    
    /**
     * Reads the properties file and creates the Hashmap containers for the previously configured DB connections
     * specified in the properties file.
     * 
     */
    private void createConnectionContainers() {
        System.out.println("createConnectionContainers entered...");
        String name;
        Iterator it = ((Set<String>)props.stringPropertyNames()).iterator();
        while(it.hasNext()) {
            name = (String)it.next();
            if(name.startsWith("db.")) {
                name = name.substring(name.indexOf(".")+1, name.lastIndexOf(".")); //the name of the DB connection object
                
                if(!dbConHMap.containsKey(name)) {
                    try {
                    System.out.println("Adding dbconnobject "+ name);
                    dbConHMap.put(name, new dbCon2Object(new String[]{name,
                                                                      props.getProperty("db."+name+".server"),
                                                                      props.getProperty("db."+name+".username")},
                                                                    owner.loadPropsFromFile(props.getProperty("db."+name+".psfile"),false), null));
                    } catch (java.lang.NullPointerException npe) { owner.log(Level.SEVERE, _class, "createConnectionContainers", npe); }
                    System.out.println("Was the new dbconobject successfully added : "+dbConHMap.containsKey(name));
                }
            }
        }
    }
    
    public void createConnection(String dbName, String server, String usr, String pwd) { 
        dbCon2Object tmpDBCO = getDBCO(dbName);
        this.closeConnection(tmpDBCO);
        this.clearPreparedStatements(tmpDBCO);
        try {
            tmpDBCO.connection = DriverManager.getConnection("jdbc:mysql://"+server+"?user="+usr+"&password="+pwd);
            tmpDBCO.connection.setAutoCommit(false);
            
            this.createPreparedStatements(tmpDBCO);
        } catch (SQLException sqle) { owner.log(Level.SEVERE, _class, "createConnection", sqle);
        }
    }
    
    public void closeConnection(dbCon2Object dbco) {
        if(dbco.connection != null) {
            try {
                System.out.println(_class+"/closeConnection - attempting");
                dbco.connection.close();
                System.out.println(_class+"/closeConnection - done");
            } catch(SQLException sqle) { owner.log(Level.SEVERE, _class, "closeConnection", sqle); }
        }
    }
        
    public void clearPreparedStatements(dbCon2Object dbco) {
        System.out.print(_class+"/clearPreparedStatements....");
        if(dbco.psHash!=null)
            dbco.psHash.clear();
        System.out.println("DONE");
    }
    
    public void createPreparedStatements(dbCon2Object dbco) {
        if(dbco.psHash != null & !dbco.psHash.isEmpty()) {  // no point building it again!
            dbco.psHash = new HashMap<>();
            String tS;
            
            for (Iterator i = dbco.psProps.stringPropertyNames().iterator(); i.hasNext();) {
                tS = (String)i.next();
                dbco.psHash.put(tS, this.createPreparedStatement(dbco, dbco.psProps.getProperty(tS)));
            }
        }
    }
    
    public PreparedStatement getPS(String dbName, String psname) {
        return getDBCO(dbName).psHash.get(psname);
    }
    
    public PreparedStatement createPreparedStatement(dbCon2Object dbco, String s) throws NullDBConnectionException {
        System.out.println(_class+"/createPreparedStatement - attepting to create PS : "+s);
        if(dbco.connection !=null) { 
            try {
                return dbco.connection.prepareStatement(s);
            } catch (SQLException sqle) { owner.log(Level.SEVERE, _class, "createPreparedStatement", sqle); }
        }
        throw new NullDBConnectionException(_class+"/createPreparedStatement - dbcon is null!");
    }
    
    public dbCon2Object getDBCO(String name) {
        return dbConHMap.get(name);
    }
    
    public String[] getKeys() {
        return dbConHMap.keySet().toArray(new String[0]);
    }
    
    public void closeAllConnections() {
        String[] keys = this.getKeys();
        
        for (String key : keys) 
            this.closeConnection(dbConHMap.get(key));
    }
}