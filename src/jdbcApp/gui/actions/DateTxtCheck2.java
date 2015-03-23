/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbcApp.gui.actions;

import java.util.GregorianCalendar;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import jdbcApp.miscMethods;

/**
 * It's DateTxtCheck for the new generation!
 * 
 * @author Seb
 */
public class DateTxtCheck2 implements java.awt.event.FocusListener {
    private JTextField tf;
    private String _dateFormat, _dateNull;
    public DateTxtCheck2(JTextField tf, String _dateFormat, String _dateNull) { this.tf= tf;  this._dateFormat = _dateFormat; this._dateNull = _dateNull; }

    @Override public void focusGained(java.awt.event.FocusEvent e) { tf.selectAll(); }
    @Override public void focusLost(java.awt.event.FocusEvent e) {
        try {
            //is it in null date format 0001-01-01
            if (!tf.getText().equals(_dateFormat)) {  

                //is it in the correct format dd-MM-yyyy 
                if (miscMethods.convertTextToDate(tf.getText(), _dateFormat, _dateNull) == null) {
                    JOptionPane.showMessageDialog(tf, "This date is in the wrong format: "+tf.getText());
                    tf.setText(_dateFormat);
                } else {
                    GregorianCalendar gNow = new GregorianCalendar();
                    GregorianCalendar gThen = new GregorianCalendar();

                    java.sql.Date d1 = miscMethods.convertTextToDate(tf.getText(), _dateFormat, _dateNull);
                    java.sql.Date now = new java.sql.Date(gNow.getTimeInMillis());
                    gThen.setTimeInMillis(d1.getTime());

                    if (d1.compareTo(now) > 0 ) {
                        JOptionPane.showMessageDialog(tf, "This date is in the future: "+tf.getText());
                        System.out.println("This date is in the future: "+tf.getText());
                        //tf.grabFocus();
                    }

                    if ( (gThen.get(java.util.Calendar.YEAR) - gNow.get(java.util.Calendar.YEAR)) <= -100 ) { //in the past
                        JOptionPane.showMessageDialog(tf, "This date is in the distant past!: "+tf.getText());
                    } 
                }
            } else {
                tf.setText(_dateFormat);
            }
        } catch (java.text.ParseException pe) { 
            System.out.println("customProPanelDBDialog/dateTxtCheck - "+pe); 
            JOptionPane.showMessageDialog(tf, "This date is in the wrong format: "+tf.getText());
            tf.setText(_dateFormat); 
            //tf.grabFocus();
        }
    }
}