package jdbcApp.reports;

import javax.swing.JPanel;

/**
 * Simply a JPanel to represent a single ReportObjectParameter, with an additional int
 * which references the sequence int of the ReportObjectParameter it represents, so when all these JPanels
 * are processed when running the report we can set the right value to the right object.
 *
 * @author Seb
 */
public class ReportSelectionFrameParamPanel extends JPanel {
    private int seq_ref;
    
    
    public ReportSelectionFrameParamPanel(int seq_ref) {
        this.seq_ref = seq_ref;
    }
    
    public int getSeqRef() { return this.seq_ref; }
}
