package jdbcApp;

import java.awt.BorderLayout;
import java.util.logging.Level;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;

/**
 *
 * @author snr
 * @date 2015-11-30
 */
public abstract class jdbcAppDesktop extends jdbcApp {
    
    public JDesktopPane jdp;
    
    private final String _CLASS;
    
    public jdbcAppDesktop (String propsStr, String psRBStr, java.util.logging.Logger myLogger) {
        super(propsStr, psRBStr, myLogger);
        
        this._CLASS = this.getClass().getName();
    }
    
    protected void genMainPanel() { 
        content.add(genMenuBar(), BorderLayout.NORTH);
        content.add(genDesktop(), BorderLayout.CENTER);
    }
    
    private JDesktopPane genDesktop() {
        jdp = new JDesktopPane();
 
        return jdp;
    }
     
    protected abstract JMenuBar genMenuBar(); 
    
    private void attemptAddingJIF(JInternalFrame jif) {
        if(jif != null) {
            if(!frameExists(jif)) {
                jif.setVisible(true);
                jdp.add(jif);
             
                log(Level.INFO, _CLASS, "attemptAddingJIF", "frame added??");
 
                try {
                    jif.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    System.out.println(e); 
                }
            } else {
                log(Level.INFO, _CLASS, "attemptAddingJIF", "Frame exisits, nulling object!");
                jif = null;
            }
        }
    }
     
    public boolean frameExists(JInternalFrame obj) {
        JInternalFrame[] allFrames = jdp.getAllFrames();
         
        //for (int i=0; i<allFrames.length; i++) {
        for(JInternalFrame frame : allFrames) {
            if(frame.toString().equals(obj.toString()))
                return true;
            else
                log(Level.INFO, _CLASS, "frameExists", "JInternalFrame not already on the desktop!");
        }
        return false;
    }
     
    public int findFrame(String query) {
        int index=-1;
        JInternalFrame[] allFrames = jdp.getAllFrames();
         
        for (int i=0; i<allFrames.length; i++) {
            if(allFrames[i].toString().contains(query))
                return i;
            else
                log(Level.INFO, _CLASS, "findFrame", "JInternalFrame not already on the desktop!");
        }
        return index;
    }
}