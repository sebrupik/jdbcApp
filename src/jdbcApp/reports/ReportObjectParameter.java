
package jdbcApp.reports;

/**
 * A single ReportObject will contain multiple ReportObjectParameters. These are retrieved by
 * the ReportSelectionFrame to dynamically build the interface allowing the user to give
 * values for the required parameters.
 * 
 * @author Seb
 */
public class ReportObjectParameter {
    public int seq;
    public String label;
    public String type;
    public String desc;
    public Object value;
          
    
    
    public ReportObjectParameter(int seq, String label, String type, String desc) {
        this.seq = seq;
        this.label = label;
        this.type = type;
        this.desc = desc;
        this.value = null;
    }
    
    public void setValue(Object value) { this.value = value; }
}
