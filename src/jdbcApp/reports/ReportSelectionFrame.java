package jdbcApp.reports;

import jdbcApp.jdbcApp;
import jdbcApp.gui.actions.DateTxtCheck2;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.EtchedBorder;
import jdbcApp.miscMethods;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


public class ReportSelectionFrame extends JInternalFrame implements ActionListener, PropertyChangeListener  {
    private final String _class;
    private HashMap<String, ReportObject> reportObjectHM;
    private JList<String> reportObjectList;
    private JScrollPane reportObjectSP;
    private JPanel paramTargetPanel;
    
    JButton runBut;
    JProgressBar progressBar;
    private ReportObject reportObj;
    
    jdbcApp owner;
    
    public ReportSelectionFrame(jdbcApp owner, String title) {
        super(title, true, true, true, true);
        
        this._class = this.getClass().getName();
        this.owner = owner;
        this.reportObjectHM = new HashMap<String, ReportObject>();
        this.setLayout(new BorderLayout(2,2));
        
        
        this.add(genMainPanel(), BorderLayout.CENTER);
        this.add(progressBar, BorderLayout.SOUTH);
    }
    
    private JPanel genMainPanel() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel p = new JPanel(gridbag);
        paramTargetPanel = new JPanel(); paramTargetPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        runBut = new JButton("Run report");
        
        runBut.addActionListener(this);
        
        
        progressBar = new JProgressBar();
        
        //reportObjectList = new JList<String>(new String[]{"blah", "blah blah"});
        reportObjectSP = new JScrollPane(genReportObjectList());
        
        buildConstraints(constraints, 0, 0, 1, 1, 50, 100);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(reportObjectSP, constraints);
        p.add(reportObjectSP);
        buildConstraints(constraints, 1, 0, 1, 2, 100, 100);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(paramTargetPanel, constraints);
        p.add(paramTargetPanel);
        
        buildConstraints(constraints, 0, 1, 1, 1, 50, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(runBut, constraints);
        p.add(runBut);
        
        //buildConstraints(constraints, 0, 2, 2, 1, 100, 20);
        //constraints.fill = GridBagConstraints.HORIZONTAL;
        //gridbag.setConstraints(progressBar, constraints);
        //p.add(progressBar);
        
        return p;
    }
    
    private JList genReportObjectList() {
        DefaultListModel<String> model  = new DefaultListModel<String>();
        
        Iterator it = reportObjectHM.entrySet().iterator();
        while (it.hasNext()) {
            model.addElement( ((ReportObject)it.next()).getReportName() );
        }
        
        reportObjectList = new JList<String>(model);
        reportObjectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        reportObjectList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (! e.getValueIsAdjusting())
                    updateSelectedReport();
            }
        });
        
        return reportObjectList;
    }
    
    /**
     * Invoked when the user presses the run button.
     */
    public void actionPerformed(ActionEvent evt) {
        progressBar.setIndeterminate(true);
        runBut.setEnabled(false);
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        
        if(retrieveUserEnterParamterValues()==true) {
            System.out.println(_class+"/runBut.action - run the report!!!");
            String selection = (String)reportObjectList.getSelectedValue();

            reportObj = reportObjectHM.get(selection);
            reportObj.addPropertyChangeListener(this);
            reportObj.execute();
        }
    }
    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("prop change??");
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setIndeterminate(false);
            progressBar.setValue(progress);
            
            //taskOutput.append(String.format("Completed %d%% of task.\n", progress));
        }
    }
    
    /**
     * A reportObject has been selected, so update the reportObjectParameter panel
     * 
     */
    private void updateSelectedReport() {
        String selection = (String)reportObjectList.getSelectedValue();
        
        if(reportObjectHM.containsKey(selection)) {
            paramTargetPanel = this.genParameterPanel(paramTargetPanel, reportObjectHM.get(selection).getReportParameters());
            
            paramTargetPanel.revalidate();
            paramTargetPanel.repaint();
        } else {
            System.out.println("The selected report: "+selection+" , doesn't exist.");
        }
    }
    
    /**
     * Is passed the reportObjectParameter HashMap from the selected ReportObject to build
     * the parameterPanel from.
     * 
     * @param hm
     * @return 
     */
    private JPanel genParameterPanel(JPanel target, HashMap hm) {
        target.removeAll();
        target.setLayout(new GridLayout(hm.size(), 1));
        JTextField tf = null;
        ReportObjectParameter rop;
        ReportSelectionFrameParamPanel rsfpp;
        
        
        
        
        Iterator it = hm.values().iterator();
        while (it.hasNext()) {
            rop = (ReportObjectParameter)it.next();
            //target.add(new JLabel(rop.label));
            
            try {
                if(rop.type.equals("DATE")) {
                    tf = new JTextField(owner.getSysProperty("_date_format")); tf.addFocusListener(new DateTxtCheck2(tf, owner.getSysProperty("_date_format"), owner.getSysProperty("_date_null")));
                } else if (rop.type.equals("SOMETHING_ELSE")) {
                    tf = new JTextField("");
                }
            } catch (java.io.IOException ioe) { owner.exceptionEncountered(_class+"/refresh", ioe); }
            
            if(tf!=null) {
                rsfpp = new ReportSelectionFrameParamPanel(rop.seq);
                rsfpp.add(new JLabel(rop.label));
                rsfpp.add(tf);
                
                target.add(rsfpp);
            }
        }
        return target;
    }
    
    public boolean addReportObject(ReportObject ro) {
        System.out.println(_class+"/addReportObject - "+ro.getReportName());
        if(reportObjectHM != null) {
            ro.setComponents(runBut);
            reportObjectHM.put(ro.getReportName(), ro);
            this.validateReportObjectList();
            return true;
        }
        return false;
    }
    
    /**
     * Check that the reportObject List matches the contents of the reportObject HashMap.
     * Add any missing entries.
     * 
     */
    private void validateReportObjectList() {
        System.out.println(_class+"/validateReportObjectList - starting");
        DefaultListModel<String> model = (DefaultListModel<String>)reportObjectList.getModel();
        ReportObject ro;
        
        Iterator it = reportObjectHM.values().iterator();
        while (it.hasNext()) {
            ro = (ReportObject)it.next();
            System.out.println(ro.getReportName());
            
            if(! model.contains(ro.getReportName())) 
                model.addElement( ro.getReportName() );
        }
        System.out.println(_class+"/validateReportObjectList - finished");
    }
    
    private boolean retrieveUserEnterParamterValues() {
        boolean tof = false;
        ReportSelectionFrameParamPanel rsfpp;
        int seq;
        for (int i=0; i<paramTargetPanel.getComponentCount(); i++) {
            rsfpp = (ReportSelectionFrameParamPanel)paramTargetPanel.getComponent(i);
            seq = rsfpp.getSeqRef();
            
            String selection = (String)reportObjectList.getSelectedValue();
        
            if(reportObjectHM.containsKey(selection)) {
                System.out.println("OK, we found the selection string");
                String value = null;
                
                //System.out.println(rsfpp.getComponent(1).getClass().toString());
                //System.out.println(rsfpp.getComponent(1).getClass().getName());
                
                if(rsfpp.getComponent(1).getClass().getName().equals("javax.swing.JTextField")) {
                    value = ((JTextField)rsfpp.getComponent(1)).getText();
                }
                
                if (value!=null) {
                    tof = this.assignValueToParameter(reportObjectHM.get(selection).getReportParameters(), rsfpp.getSeqRef(), value);
                    System.out.println("suceeded in setting param value? : "+ tof );
                }
                if(tof==false)
                    return false;
            }
        }
        return tof;
    }
        
    /**
     * Itterates through a HashMap of ReportObjectParameters, checking against its 'seq' int value.
     * When a match is found it converts the user supplied value and casts it to the correct value depending
     * on the the current ReportObjectParameters 'type' value
     * 
     */
    private boolean assignValueToParameter(HashMap roParams, int seq, String value) {
        Iterator it = roParams.values().iterator();
        ReportObjectParameter rop = null;
        while (it.hasNext()) {
            rop = (ReportObjectParameter)it.next();
            
            try {
                if(rop.seq == seq) {
                    if(rop.type.equals("DATE")) {
                        rop.value = miscMethods.convertTextToDate(value, owner.getSysProperty("_date_format"), owner.getSysProperty("_date_null"));
                        System.out.println("I'm setting a DATE value");
                    }
                }
            } catch(java.io.IOException ioe) {
                owner.exceptionEncountered(_class+"/assignValueToParameter", ioe);
            } catch(java.text.ParseException pe) {
                owner.exceptionEncountered(_class+"/assignValueToParameter", pe);
            }
        }
        
        return true;
    }
    
    public void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy) {
        gbc.gridx = gx;
        gbc.gridy = gy;
        gbc.gridwidth = gw;
        gbc.gridheight = gh;
        gbc.weightx = wx;
        gbc.weighty = wy;
    }
}