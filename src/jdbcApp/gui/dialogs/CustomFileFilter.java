/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbcApp.gui.dialogs;


import java.io.File;
 
public class CustomFileFilter extends SuffixAwareFilter {
    private String suf, desc;
    public CustomFileFilter(String suf, String desc) {
        this.suf = suf;
        this.desc = desc;
    }
    
    public boolean accept(File f) {
        boolean accept = super.accept(f);

        if( ! accept) {
            String suffix = getSuffix(f);
            if(suffix != null)
                accept = super.accept(f) || suffix.equals(suf);
        }
        return accept;
    }
    public String getDescription() {
        //return "Report Files(*.xml)";
        return desc+"(*."+suf+")";
    }
}


