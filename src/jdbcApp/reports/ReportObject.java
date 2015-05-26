package jdbcApp.reports;

import jdbcApp.dbConnection;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
//import java.beans.PropertyChangeListener;

/**
 * http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/components/ProgressBarDemo2Project/src/components/ProgressBarDemo2.java
 * 
 * @author Seb
 */

public abstract class ReportObject  extends SwingWorker<Void, Void>{
    private final String _class;
    private String name;
    dbConnection dbcon;
    public HashMap<String, ReportObjectParameter> objParamHM;
    private HtmlFactory htmlFact;
    
    public ReportObject(String name, String path, dbConnection dbcon) {
        this.name = name;
        this.dbcon = dbcon;
        
        this._class = this.getClass().getName();
        
        this.htmlFact = new HtmlFactory(path);
        
    }
    
    public abstract void buildParameterObjects();
    public abstract void extractParameters();
    public abstract void clearTempTables();
    public abstract void setComponents(JButton runBut);
    
    /**
     * Iterates through the reportObject HashMap and checks the value of the 'value' variable.
     * Returns false if a null value is found.
     * 
     * @return 
     */
    public boolean parametersSet() {
        ReportSelectionFrameParamPanel rsfpp;
        
        Iterator it = objParamHM.values().iterator();
        while (it.hasNext()) {
            if ( ((ReportObjectParameter)it.next()).value == null) {
                return false;
            }
        }
        return true;
    }
    
    public String getReportName() { return name; }
    public HashMap getReportParameters() { return objParamHM; }
    
    //******************************
    
    public void emptyTable(String tName) {
        if(execute("SELECT * FROM "+tName)) { execute("DELETE FROM "+tName);
        } else { System.out.println(_class+"/emptyTable - table doesn't exist"); }
    }

    public void view() { htmlFact.view(); }

    
    public boolean execute(String s) {
        return dbcon.execute(s);
    }
    
    public ResultSet executeQuery(String q) { 
        return this.executeQuery("unknown", q);
    }
    public ResultSet executeQuery(String title, String q) { 
        return dbcon.executeQuery(q);
    }
    
    public void write(String str) {
        htmlFact.writeString(str);
    }
    public void writeln(String str) {
        write(str+"<P/>");
    }

    public void writeResultSet(String title, ResultSet rs) throws SQLException {
        this.writeResultSet(title, new String[]{}, rs, false);
    }
    public void writeResultSet(String title, String[] headers, ResultSet rs) throws SQLException {
        this.writeResultSet(title, headers, new ResultSet[]{rs}, false);
    }
    public void writeResultSet(String title, boolean meta, ResultSet rs) throws SQLException {
        this.writeResultSet(title, meta, new ResultSet[]{rs}, false); 
    }
    
    public void writeResultSet(String title, ResultSet rs, boolean total) throws SQLException {
        this.writeResultSet(title, new String[]{}, rs, total);
    }
    public void writeResultSet(String title, String[] headers, ResultSet rs, boolean total) throws SQLException {
        this.writeResultSet(title, headers, new ResultSet[]{rs}, total);
    }
    public void writeResultSet(String title, String[] headers, ResultSet rs, boolean total, boolean boxed) throws SQLException {
        this.writeResultSet(title, headers, new ResultSet[]{rs}, total, boxed);
    }
    public void writeResultSet(String title, boolean meta, ResultSet rs, boolean total) throws SQLException {
        this.writeResultSet(title, meta, new ResultSet[]{rs}, total); 
    }
    
    public void writeResultSet(String title, ResultSet[] rs, boolean total) throws SQLException {
        this.writeResultSet(title, new String[]{}, rs, total);
    }
    public void writeResultSet(String title, String[] headers, ResultSet[] rs, boolean total) throws SQLException {
        htmlFact.processResultSet(title, headers, rs, total);
    }
    public void writeResultSet(String title, String[] headers, ResultSet[] rs, boolean total, boolean boxed) throws SQLException {
        htmlFact.processResultSet(title, headers, rs, total, boxed);
    }
    public void writeResultSet(String title, boolean meta, ResultSet[] rs, boolean total) throws SQLException {
        htmlFact.processResultSet(title, meta, rs, total); 
    }
    
    /**
     * I want to print the with the headers listed horizontaly
     */
    public void simpleWriteArrayInt(String qTitle, String[] cols, int[] rsAr, boolean hori) throws SQLException {
        ArrayList rows = new ArrayList<String>();
        //for (int i=0;i<cols.length;i++)
        for(String element : cols)
           rows.add(element);
        
        for (int i=0;i<rsAr.length;i++)
           rows.add(String.valueOf(rsAr[i]));
        
        if(hori)
            htmlFact.writeTableRows(qTitle, rows, cols.length);
        else
            htmlFact.writeTableRows2(qTitle, rows, cols.length);
    }

    
}
