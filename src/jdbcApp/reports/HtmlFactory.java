package jdbcApp.reports;

import jdbcApp.gui.dialogs.HTMLDisplayDialog;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.table.*;

/**
 * My final bright spark of inspiration for the PIDB, since people don't like 
 * the rawness of the outputted information it makes sense to use HTML as it
 * has fairly basic formatting and it can be printed via a variety of readily 
 * available software packages without any converting....a'la CSV
 *
 * @ date       2005-08-29
 */
public class HtmlFactory {
    private String _class;
    private BufferedWriter bwrite;
    
    private String fileName, suffix = ".html", reportDir = "./reports/";
    
    
    public HtmlFactory(String fileName) {
        this._class = getClass().toString();
        openFile(fileName);
    }
    
    
    /**
     * Iterates through an ArrayList of Object arrays.
     * Each object array has three elements:
     * 1)Title of the report entry
     * 2)java type of the object to be processed
     * 3)java object to be processed
     *
     * @ date       2005-08-29
     */
    public void processReport(String title, ArrayList reportContents) {
        writeln("<HTML>");
        writeln("<HEAD>");
        //writeln("<TITLE>"+title+"</TITLE>");
        writeln("<TABLE width=100%><TR bgcolor=\"#b2d4f9\"><TH><B>"+title+"</B></TH></TR></TABLE>");
        writeln("</HEAD>");
        writeln("<B>"+title+"</B>");
        writeln("");
        
        for (java.util.ListIterator li = reportContents.listIterator() ; li.hasNext() ;) {
            Object[] obj = (Object[])li.next();
            if( ((String)obj[1]).equals("HASHTABLE") ) {
                this.processHashtable((String)obj[0], (Hashtable)obj[2], null);
            } else if( ((String)obj[1]).equals("DEFAULTTABLEMODEL") ) {
                this.processTable((String)obj[0], (DefaultTableModel)obj[2]);
            }
        }
        
        writeln("</TABLE>");
        writeln("</HTML>");
        
        view();
    }
    
    public void view() {
        closeFile();
        HTMLDisplayDialog dd = new HTMLDisplayDialog();
        dd.setPage("file:///"+this.getFile().getAbsolutePath());
        dd.setVisible(true);
    }
    
    public void reset() {
        openFile(fileName);
    }
    
    
    
    public void processResultSet(String title, boolean meta, ResultSet[] rs, boolean total) throws SQLException {
        if(meta) {
            ArrayList al = new ArrayList();
            ResultSetMetaData rsmd;
            for(int i=0;i<rs.length; i++) {
                rsmd = rs[i].getMetaData();
                
                for(int j=0; j<rsmd.getColumnCount(); j++) {
                    System.out.println("********************************* "+rsmd.getColumnName(j+1));
                    al.add(rsmd.getColumnName(j+1));
                }
            }
            this.processResultSet(title, (String[])al.toArray(new String[al.size()]), rs, total);
        }
    }
    
    public void processResultSet(String title, String[] header, ResultSet[] rs, boolean total) {
        this.processResultSet(title, header, rs, total, true);
    }
    
    public void processResultSet(String title, String[] header, ResultSet[] rs, boolean total, boolean boxed) {
        System.out.println(_class+"/processResultSet");
        int tot=0;
        writeln("<B>"+title+"</B>");
        if(boxed)
            writeln("<TABLE BORDER=1>");
        else
            writeln("<TABLE>");
        
        try {
            if(header.length>0) {
                writeln("<TR>");
                for(int i=0;i<header.length; i++) 
                    writeln("<TH>"+header[i]+"</TH>");
                writeln("</TR>");
            }
            for(int x=0; x<rs.length; x++) {
                ResultSetMetaData rsmd = rs[x].getMetaData();
                int cols = rsmd.getColumnCount();
                while(rs[x].next()) {
                    writeln("<TR>");
                    for(int i=0;i<cols;i++) {
                        writeln("<TD>"+rs[x].getObject(i+1)+"</TD>");
                        if(total & i == cols-1)
                            tot += rs[x].getInt(i+1);
                    }
                    writeln("</TR>");
                }
            }
            if(total)
                write("<TR><TD><B>Total</B></TD><TD>"+tot+"</TD></TR>");
        } catch(SQLException sqle) { System.out.println(_class+"/processResultSet - "+sqle); }
        writeln("</TABLE>");
        writeln("<P/>");
    }
    
    public void processTableSingle(String title, DefaultTableModel dtm) {
        writeln("<HTML>");
        writeln("<HEAD>");
        writeln("<TABLE width=100%><TR bgcolor=\"#b2d4f9\"><TD><B>"+title+"</B></TD></TR></TABLE>");
        writeln("</HEAD>");
        
        this.processTable(title, dtm);
        
        writeln("</HTML>");
        
        closeFile();
        HTMLDisplayDialog dd = new HTMLDisplayDialog();
        dd.setPage("file:///"+this.getFile().getAbsolutePath());
        dd.setVisible(true);
    }
    public void processTable(String title, DefaultTableModel dtm) {
        writeln("<B>"+title+"</B>");
        writeln("");
        writeln("<TABLE BORDER=1>");
        writeln("<TR bgcolor=\"#ff7200\">");
        for(int col=0;col<dtm.getColumnCount();col++) {
            writeln("<TD>"+dtm.getColumnName(col)+"</TD>");
        }
        writeln("</TR>");
        for(int row=0;row<dtm.getRowCount();row++) {
            writeln("<TR>");
            for(int col=0;col<dtm.getColumnCount();col++) {
                writeln("<TD>"+dtm.getValueAt(row, col)+"</TD>");
            }
            writeln("</TR>");
        }
        writeln("</TABLE>");
        writeln("<P/>");
    }
    
    /**
     * iterates through the hashtable in the order specified by the elements of the
     * String array 'order'
     * If the hashtable doesn't contain any of the elements in order, nothing will be printed.
     * If order is null then the contents of the hashtable will be printed in the order they
     * were stored in the hashtable.
     */
    public void processHashtable(String title, Hashtable hash, String[] order) {
        System.out.println(_class+"/processHashtable");
        writeln("<B>"+title+"</B>");
        writeln("<TABLE BORDER=1>");
        
        if(order!=null) {
            for(int i=0;i<order.length;i++) {
                writeln("<TR><TD>"+order[i]+"</TD>");
                if(hash.get(order[i]) == null) 
                    writeln("<TD>0</TD></TR>");
                else 
                    writeln("<TD>"+hash.get(order[i])+"</TD></TR>");
            }
        } else {
            String key;
            for (Enumeration e = hash.keys() ; e.hasMoreElements() ;) {
                key = (String)e.nextElement();
                writeln("<TR><TD>"+key+"</TD><TD>"+hash.get(key)+"</TD></TR>");
            }
        }
        writeln("</TABLE>");
        writeln("<P/>");
    }
    
    public void processString(String title, String tt, String value) {
        writeln("<TABLE BORDER=1>");
        writeln("<TR><TH title='"+tt+"'>"+title+"</TH></TR>");
        writeln("<TR><TD>"+value+"</TD></TR>");
        writeln("</TABLE>");
        writeln("<P/>");
    }
    
    public void writeString(String str) {
        writeln(str);
    }
    /**
     * simply write the string provided.
     * This is the easiest way for other classes to submit there own HTML
     * which needs no further processing, just saving to file.
     */
    public void writeStringComplete(String str) {
        writeString(str);
        closeFile();
        HTMLDisplayDialog dd = new HTMLDisplayDialog();
        dd.setPage("file:///"+this.getFile().getAbsolutePath());
        dd.setVisible(true);
    }
    
    public void writeTableRows(String title, ArrayList rows, int width) {
        Object[] obj;
        String rStr ="";
        writeln("<B>"+title+"</B>");
        writeln("<TABLE BORDER=1>");
        
        for(int i=0;i<rows.size();i++) {
            obj = (Object[])rows.get(i);
            rStr="<TR>";
            for(int r=0;r<obj.length;r++) {
                rStr+="<TD>"+obj[r]+"</TD>";
            }
            rStr+="</TR>";
            writeln(rStr);
        }
        writeln("</TABLE>");
        writeln("<P/>");
    }
    
    
    public void writeTableRows2(String title, ArrayList rows, int height) {
        Object[] obj;
        String rStr ="";
        int offset = 0;
        writeln("<B>"+title+"</B>");
        writeln("<TABLE BORDER=1>");
        int index =-1;
        
        for(int i=0;i<rows.size();i++)
            System.out.println(rows.get(i));
        
        //don't need to include the headers in the number of columns
        int numcols = (rows.size()-height)/height;
        for(int y=0; y<height; y++) {
            rStr="<TR>";
            offset = height+(numcols*y);
            rStr+="<TD>"+(String)rows.get(y)+"</TD>";
            for (int x=0; x<numcols; x++) {
                rStr+="<TD>"+(String)rows.get(offset+x)+"</TD>";
            }
            rStr+="</TR>";
            writeln(rStr);
        }
        writeln("</TABLE>");
        writeln("<P/>");
    }
    
    
    //*****************************************************
    
    public void write(String str) {
        try { bwrite.write(str);
        } catch(IOException ioe) { System.out.println(_class+"/write(String) - "+ioe); }
    }
     public void writeln(String str) {
        try { bwrite.write(str+"\n");
        } catch(IOException ioe) { System.out.println(_class+"/write(String) - "+ioe); }
    }
    
    public File getFile() { return new File(reportDir+fileName+suffix); }
    public void openFile(String fileName) {
        this.fileName = fileName;
        try {
            if(! new File(reportDir).exists())
                new File(reportDir).mkdir();
            bwrite = new BufferedWriter(new FileWriter(reportDir+fileName+suffix));
        } catch(java.io.IOException ioe) { System.out.println(_class+"/openFile "+ioe); }
    }
    public void closeFile() {
        try {
            bwrite.flush();
            bwrite.close();
        } catch(java.io.IOException ioe) { System.out.println(_class+"/closeFile "+ioe); }
    }   
}