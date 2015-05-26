package jdbcApp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public abstract class jdbcApp extends JFrame {
    private final String _class;
    private statusBar sBar;
    public Container content;
    private static dbConnection dbCon;
    private String propsStr, psRBStr;

    private Properties sysProps, psProps;
    private ResourceBundle psRB;
    
    public final static int DBCON_CONNECTED = 0;
    public final static int DBCON_NULL = 1;
    public final static int DBCON_NOT_CONNECTED = 1;

    public jdbcApp(String propsStr, String psRBStr) {
        this._class = this.getClass().getName();
        this.propsStr = propsStr;
        this.psRBStr = psRBStr;

        try {
            sysProps = this.loadPropsFromFile(propsStr, true);
            psProps = this.loadPropsFromFile(psRBStr, false);

            if(sysProps != null) {
                this.setSize(Integer.parseInt(getSysProperty("sizeX")), Integer.parseInt(getSysProperty("sizeY")));
                this.createdbConnection(getSysProperty("jdbc.server"), getSysProperty("jdbc.username"), getSysProperty("jdbc.password"));
            } else {
                System.out.println("system props not loaded, so using defaults.");
            }
            assignSystemVariables();
        } catch (IOException ioe) { System.out.println(ioe); }


        content = this.getContentPane();
        content.setLayout(new BorderLayout(2, 2));

        sBar = new statusBar(this);
        sBar.setSize(0,10);

        content.add(sBar, BorderLayout.SOUTH);

        WindowListener l = new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                closeApp();
            }
        };
        this.addWindowListener(l);
    }

    public void closeApp() {
        //save settings like window size?....
        System.out.println(_class+" - says goodbye.");
        
        this.writeSystemVariables();
        this.savePropsToFile(propsStr);
        
        if(dbCon != null)
            dbCon.closeConnection();
        System.exit(0);
    }
    
    private Properties loadPropsFromFile(String p1, boolean external) {
        System.out.println(_class+"/loadPropsFromFile - attempting to load "+p1);
        Properties tmp_prop = new java.util.Properties();
        InputStream in = null;

        try {
            if(external) 
                in = new FileInputStream(p1);
            else
                in = this.getClass().getClassLoader().getResourceAsStream(p1);
                
            if (in == null) {
                System.out.println(_class+"/loadProps - "+p1+ " not found!!!");
                tmp_prop = null;
            } else {
                tmp_prop.load(in);
            }
        } catch(IOException ioe) { System.out.println(_class+"/loadProps - "+ioe); }

        return tmp_prop;
    }
    
    private void savePropsToFile(String p1) {
        System.out.println(_class+"/savePropsToFile - attempting to save "+p1);
        OutputStream out = null;
        
        try {
            out = new FileOutputStream(p1);
            sysProps.store(out, "system properties");
            
            
        } catch(IOException ioe) { System.out.println(_class+"/saveFiles - "+ioe); }
    }

    public String getSysProperty(String arg) throws IOException {
        System.out.println(_class+"/getSysProperty - "+arg);
        String s;
        if(sysProps==null) {
            throw new IOException("Props file not loaded!");
        } else {
            s = sysProps.getProperty(arg);
            if(s==null)
                throw new IOException("Null value. Does field exist??");
            
            System.out.println(_class+"/getSysProperty - value is "+s);
            return s;
        }
    }
    
    public Object saveSysProperty(String key, String value) { return sysProps.setProperty(key, value); }
    
    public boolean createdbConnection(String server, String username, String password) {
        dbCon = new dbConnection(server, username, password, sysProps, psProps);
        return true;
    }
    
    public abstract void assignSystemVariables() throws java.io.IOException;
    public abstract void writeSystemVariables();

    /**
     * 
     * 
     * 
     * @return 
     * status codes:
     * 0 : connection is live
     * 1 : connection is null
     * 2 : connection object created but disconnected
     */
    public int getdbConnectionStatus() { 
        if(dbCon==null) {
            return DBCON_NULL;
        } else {
            if(dbCon.isConnected()) {
                return DBCON_CONNECTED;
            } else {
                return DBCON_NOT_CONNECTED;
            }   
        }
    }
    public dbConnection getdbConnection() { return dbCon; }
    public static PreparedStatement getPS(String ps) throws NullPointerException { return dbCon.getPS(ps); }
    
    /**
     * Lets handle some exceptions in imaginative and useful ways!
     * 
     * @param source
     * @param e 
     */
    public void exceptionEncountered(String source, Exception e) {
        if(e instanceof java.sql.SQLException) {
             java.sql.SQLException sqle = (java.sql.SQLException)e;
             int errorCode = sqle.getErrorCode();
             
             
        } else if(e instanceof java.io.IOException) {
            java.io.IOException ioe = (java.io.IOException)e;
            
            
        } else {
            
        }
        
        JOptionPane.showMessageDialog(null, source+"\n"+e.toString(), "Exception", JOptionPane.ERROR_MESSAGE);
    }
}