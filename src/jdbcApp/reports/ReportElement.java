package jdbcApp.reports;

import java.sql.*;

abstract public class ReportElement {
    String output;
    ResultSet inputRS;
    
    public ReportElement(ResultSet inputRS) {
        this.inputRS = inputRS;
    }
    
    public abstract void process();
}
