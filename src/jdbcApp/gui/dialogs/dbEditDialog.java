package jdbcApp.gui.dialogs;

import jdbcApp.jdbcApp;
import jdbcApp.dbConnection;
import jdbcApp.components.JComboBoxHolder;
//import jdbcApp.components.DBEditTableModel;
import jdbcApp.miscMethods;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.regex.Pattern;
import javax.swing.event.*;
import javax.swing.event.*;
import javax.swing.table.TableModel;


public class dbEditDialog extends JDialog  implements TableModelListener {
    private final String _class;
    jdbcApp owner;
    dbConnection dbCon;
    DatabaseMetaData dbmd;
    ResultSet rSet;
    PreparedStatement selectPS, updatePS, insertPS, deletePS;
    String[] visibleFields, columnTypes;
    int row_count, updatePK;
    Pattern p = Pattern.compile(",");
    private JComboBoxHolder _JCBHolder;
    //Properties props;
    
    JTable databaseTbl;
    
    public dbEditDialog(jdbcApp owner) {
        super(owner, "DB Edit window", true);
        this.owner = owner;
        this._class = this.getClass().getName();
        this.setLayout(new BorderLayout(2,2));
        
        _JCBHolder = new JComboBoxHolder();
        
        this.add(genSelectionPanel(), BorderLayout.NORTH);
        this.add(genTablePanel(), BorderLayout.CENTER);
        this.add(genButtonPanel(), BorderLayout.SOUTH);
        
        initComboBoxes();
        
        this.addWindowListener(new windowCloser(this));
    }
    
    private JPanel genSelectionPanel() {
        JPanel p1 = new JPanel();
        
        _JCBHolder.addItem("TABLE", "SELECT name FROM aim_special_dbedit ORDER BY name", new JComboBox<String>());
        _JCBHolder.getJCBox("TABLE").addActionListener(new selectTableToEditAction(_JCBHolder.getJCBox("TABLE")));
        p1.add(_JCBHolder.getJCBox("TABLE"));
        
        return p1;
    }
    
    private JPanel genTablePanel() {
        JPanel databasePanel = new JPanel(new BorderLayout(1,1));
        
        //databaseTbl = new JTable(new DBEditTableModel());
        databaseTbl = new JTable(new DefaultTableModel() {
            //  Returning the Class of each column will allow different
            //  renderers to be used based on Class
            @Override public Class getColumnClass(int column) {
                    return getValueAt(0, column).getClass();
            } 
            @Override public boolean isCellEditable(int x, int y) { return true; }
        });
        databaseTbl.getModel().addTableModelListener(this);
        
        //databaseTbl.setComponentPopupMenu(genTableMenu());
        
        
        databasePanel.add(new JScrollPane(databaseTbl), BorderLayout.CENTER);
        
        System.out.println(_class+"/genHistoryPanel - exited");
        
        return databasePanel;
    }
    
    private JPanel  genButtonPanel() {
        JPanel bPanel = new JPanel(new GridLayout(1,2));
        
        //JButton newBut = new JButton("New entry");
        JButton closeBut = new JButton("Close");  closeBut.addActionListener(new closeAction(this));
        
        //bPanel.add(newBut);
        bPanel.add(closeBut);
        
        return bPanel;
    }
    
    public JPopupMenu genTableMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteMI = new JMenuItem("Delete");  deleteMI.addActionListener(new popupDeleteAct());
        //JMenuItem updateMI = new JMenuItem("Update");  updateMI.addActionListener(new popupUpdateAct());
        
        menu.add(deleteMI);
        //menu.add(updateMI);
        
        return menu;
    }
    
    //************************* other methods
    @Override public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        //String columnName = model.getColumnName(column);
        
        if(row > -1 & column > -1) {
            //Object data = model.getValueAt(row, column);
            try {
                if(row == row_count) { ///we are on the last row so insert any data and refresh
                    insertPS.clearParameters();
                    for(int i=0; i<model.getColumnCount(); i++) 
                        insertPS.setObject(i+1, model.getValueAt(row, i));
                    owner.getdbConnection().executeUpdate(insertPS);
                    insertPS.clearParameters();
                    refreshTable();
                } else {
                    if(rSet.absolute(row+1)) 
                        updatePK = rSet.getInt("priKey");
                        
                    updatePS.clearParameters();
                    for(int i=0; i<model.getColumnCount(); i++) {
                        //System.out.println("*** "+model.getValueAt(row, i));
                        updatePS.setObject(i+1, model.getValueAt(row, i));
                    }
                    updatePS.setInt(updatePS.getParameterMetaData().getParameterCount(), updatePK);
                    owner.getdbConnection().executeUpdate(updatePS);
                    updatePS.clearParameters();
                }
            } catch (SQLException sqle) { owner.log(java.util.logging.Level.SEVERE, _class, "tableChanged", sqle); }
        }
    }
    
    public void populateFields(ResultSet r) {
        System.out.println(_class+"/populateFields - starting");
        rSet = r;
        Object[] obj;
        
        DefaultTableModel dtm = (DefaultTableModel)databaseTbl.getModel();
        clearTable();
        //String[] colNames = new String[]{"Date", "Type", "Duration"};
        
        for(int i=0; i<visibleFields.length; i++)
            dtm.addColumn(visibleFields[i]);
        
        if(rSet !=null) {
            try {
                row_count=0;
                while(rSet.next()) {
                    obj = new Object[visibleFields.length];
                    for (int i=0;i<obj.length; i++) {
                        obj[i] = r.getObject(visibleFields[i]);
                    }
                    dtm.addRow(obj);
                    row_count++;
                    System.out.println(_class+"/populateFields - justr added a row");
                }
                dtm.addRow(createBlankRow());
            } catch(SQLException sqle) { owner.log(java.util.logging.Level.SEVERE, _class, "populatesField", sqle); }
        }
        System.out.println(_class+"/populateFields - finished");
    }
    
    private Object[] createBlankRow() {
        Object[] b = new Object[columnTypes.length];
        
        for (int i=0; i<b.length; i++) {
            if(columnTypes[i].equals("string")) {
                b[i] = "";
            } else if(columnTypes[i].equals("boolean")) {
                b[i] = false;
            }
        }
        
        return b;
    }
    
    public void refreshTable() {
        this.populateFields(owner.getdbConnection().executeQuery(selectPS));
    }
    
    public void clearTable() {
        System.out.println(_class+"/clearTable - entered");
        DefaultTableModel dtm = (DefaultTableModel)databaseTbl.getModel();
        dtm.setColumnCount(0);
        dtm.setRowCount(0);
        System.out.println(_class+"/clearTable - exited");
    }
    
    private void doTheMagic(String selected) {
        ResultSet r = owner.getdbConnection().executeQuery("SELECT * FROM aim_special_dbedit WHERE name='"+selected+"'");
        
        try {
            while(r.next()) {
                visibleFields = p.split(r.getString("columns_visible"));
                columnTypes = p.split(r.getString("column_types"));
                selectPS = owner.getdbConnection().createPreparedStatement(r.getString("selectPS"));
                insertPS = owner.getdbConnection().createPreparedStatement(r.getString("insertPS"));
                updatePS = owner.getdbConnection().createPreparedStatement(r.getString("updatePS"));
                deletePS = owner.getdbConnection().createPreparedStatement(r.getString("deletePS"));

                this.populateFields(owner.getdbConnection().executeQuery(selectPS));
            }
        } catch (SQLException sqle) { owner.log(java.util.logging.Level.SEVERE, _class, "dotheMagic", sqle); }
        
    }
    
    private void initComboBoxes() {
        _JCBHolder.refreshJCBoxes(owner.getdbConnection());
    }
    
    @Override public void dispose() {
        owner.saveSysProperty("sizeX.dbEditDialog", String.valueOf(this.getSize().width));
        owner.saveSysProperty("sizeY.dbEditDialog", String.valueOf(this.getSize().height));
                    
        super.dispose();
    }
    
    //************************* actions
    
    class selectTableToEditAction extends AbstractAction {
        JComboBox source;
        public selectTableToEditAction(JComboBox source) {
            this.source = source;
        }
        
        @Override public void actionPerformed(java.awt.event.ActionEvent e) {
            doTheMagic((String)source.getSelectedItem());
        }
    }
    
    class popupDeleteAct extends AbstractAction {
        @Override public void actionPerformed(java.awt.event.ActionEvent e) {
            int row = databaseTbl.getSelectedRow();
            
            try {
                int result = JOptionPane.showConfirmDialog(null, "Really delete entry?", "Confirm delete", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if(result==0) {
                    if(rSet.absolute(row+1)) {
                        deletePS.setInt(1, rSet.getInt("priKey"));

                        owner.getdbConnection().executeUpdate(deletePS);
                        deletePS.clearParameters();
                    }
                }
            } catch(SQLException sqle) { owner.log(java.util.logging.Level.SEVERE, _class, "popupDeleteAct", sqle); 
            }
            
            refreshTable();
        }
    }
    
    class windowCloser extends WindowAdapter {
        dbEditDialog parent;

        public windowCloser(dbEditDialog parent) {
            this.parent = parent;
        }
        @Override public void windowClosing(WindowEvent e) {
            parent.dispose();
        }
    }
    
    class closeAction extends AbstractAction {
        dbEditDialog parent;
        public closeAction(dbEditDialog parent) { this.parent = parent; }
        @Override public void actionPerformed(java.awt.event.ActionEvent e) { parent.dispose(); }
    }
}