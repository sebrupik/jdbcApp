/*
 * JComboxBoxHolder.java
 *
 * Created on 24 June 2009, 16:24
 */
package jdbcApp.components;

import jdbcApp.dbConnection;
import jdbcApp.miscMethods;

import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.*;

/**
 * An object used for holding all the JComboBoxes in a projectPanel.
 * Provides numerous functions to cut down on repeated code in the projectPanel class
 *
 * @author  srupik
 */
public class JComboBoxHolder {
    String _class;
    private JComboBox[] JCBoxes;
    private Hashtable<String, enhancedJCBox> items;
    
    /** Creates a new instance of JComboxBoxHolder */
    public JComboBoxHolder() {
        this._class = this.getClass().getName();
        items = new Hashtable<String, enhancedJCBox>();
    }
    
    public void refreshJCBoxes(dbConnection con) {
        System.out.println(_class+"/refreshJCBoxes - entered");
        for (Enumeration<enhancedJCBox> e = items.elements(); e.hasMoreElements();) {
           ((enhancedJCBox)e.nextElement()).update(con);
           System.out.println("++");
        }
        System.out.println(_class+"/refreshJCBoxes - exited");
    }
    
    public void clearFields() {
        System.out.println(_class+"/clearFields - entered");
        for (Enumeration<enhancedJCBox> e = items.elements(); e.hasMoreElements();) {
           ((enhancedJCBox)e.nextElement()).clearField();
           System.out.println("==");
        }
        System.out.println(_class+"/clearFields - exited");
    }
    
    public void addItem(String name, String source, JComboBox<String> target) { this.addItem(name, source, target, true); }
    public void addItem(String name, String source, JComboBox<String> target, boolean ps) { this.addItem(name, source, target, ps, false); }
    public void addItem(String name, String source, JComboBox<String> target, boolean ps, boolean edit) { items.put(name, new enhancedJCBox(name, source, target, ps, edit)); }
    
    public void addItem(String name, java.sql.PreparedStatement prep_stat, JComboBox<String> target) { this.addItem(name, prep_stat, target, true); }
    public void addItem(String name, java.sql.PreparedStatement prep_stat, JComboBox<String> target, boolean ps) { this.addItem(name, prep_stat, target, ps, false); }
    public void addItem(String name, java.sql.PreparedStatement prep_stat, JComboBox<String> target, boolean ps, boolean edit) { items.put(name, new enhancedJCBox(name, prep_stat, target, ps, edit)); }
    
    public void setSelectedItem(String name, Object item) { 
        if( ((java.util.Set)items.keySet()).contains(name) )
            ((enhancedJCBox)items.get(name)).setSelectedItem(item); 
        else
            System.out.println(_class+"/setSelectedItem - I don't recognise the following key : "+name);
    }
    
    public Object getSelectedItem(String name) { return ((enhancedJCBox)items.get(name)).getSelectedItem(); }
    public JComboBox getJCBox(String name) { return ((enhancedJCBox)items.get(name)).getTarget(); }  
    
    public int getSize() {
        return items.size();
    }
    
    public void setEnabled(boolean tof) {
        System.out.println(_class+"/setEnabled - entered");
        for (Enumeration<enhancedJCBox> e = items.elements(); e.hasMoreElements();) {
           ((enhancedJCBox)e.nextElement()).setEnabled(tof);
           System.out.println("==");
        }
        System.out.println(_class+"/setEnabled - finished");
    }
}


/**
 * A fancy object which essentialy holds a JComoboBox and provides methods to carry out
 * the most common PIDB JComboBox methods we all know and love.
 * Has the additional feature of temporarily adding items to the list, in the event that they
 * are not stored in the list by default (interface from the DB tables from which they are created). 
 * This would occur if an entry had been deleted from the default list but was still present in the
 * DB data tables.
 *
 */
class enhancedJCBox {
    String name;
    String source;
    java.sql.PreparedStatement prep_stat;
    String _class;
    JComboBox<String> target;
    boolean ps; //add please select
    boolean edit;
    private String _pleaseSelect = "Please Select";
    
    public enhancedJCBox(String name, String source, JComboBox<String> target) {
        this(name, source, target, true, false);
    }
    
    public enhancedJCBox(String name, String source, JComboBox<String> target, boolean ps, boolean edit) {
        this.name = name;
        this.source = source;
        this.target = target;
        this.ps = ps;
        this.edit = edit;
        this._class = this.getClass().getName();
        
        System.out.println("enhancedJCBox - init - "+name);
    }
    
    public enhancedJCBox(String name, java.sql.PreparedStatement prep_stat, JComboBox<String> target, boolean ps, boolean edit) {
        this.name = name;
        this.prep_stat = prep_stat;
        this.target = target;
        this.ps = ps;
        this.edit = edit;
        this._class = this.getClass().getName();
        
        if(edit)
            target.setEditable(true);
        
        System.out.println("enhancedJCBox - init - "+name);
    }
     
    public void update(dbConnection con) {
        target.removeAllItems();
        if(ps)
            target.addItem(_pleaseSelect);
        
        if(source != null)
            target = miscMethods.addColumnToComboBox(target, con.executeQuery(source));
        else if(prep_stat != null)
            target = miscMethods.addColumnToComboBox(target, con.executeQuery(prep_stat));
        else
            System.out.println(_class+"/update - both source and prep_stat were null, so nothing has been added to the taget!");
            
    }
    
    public void setSelectedItem(Object item) {
        System.out.println(_class+"/setSelectedItem - "+item+" for enhancedJCBox: "+name);
        if(item == null) {
            System.out.println(_class+"/setSelectedItem - "+item+" Item is null. Not much I can do with that!");
        } else { 
            if( !this.contains((DefaultComboBoxModel)target.getModel(), item)) {
                if(ps) {
                    target.setSelectedItem(_pleaseSelect);
                } else {
                    System.out.println(_class+"/setSelectedItem - I don't have the following item so I'll add it : "+item);
                    target.addItem(item.toString());
                }
            }
            target.setSelectedItem(item.toString());
        }
        
    }
    
    private boolean contains(DefaultComboBoxModel model, Object item) {
        int size = model.getSize();
        
        for(int i=0; i<size; i++) {
            if(model.getElementAt(i).equals(item))
                return true;
        }
        return false;
    }
    
    public void clearField() { if(target.getItemCount() >=1) target.setSelectedIndex(0); }
    public void setEnabled(boolean tof) { target.setEnabled(tof); }
    
    public Object getSelectedItem() { return target.getSelectedItem(); }
    public String getName() { return name; }
    public String getSource() { return source; }
    public JComboBox getTarget() { return target; }
}