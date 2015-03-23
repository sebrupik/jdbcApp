package jdbcApp.components;


import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.table.TableModel;

public class DBEditTableModel extends DefaultTableModel implements TableModelListener {
    public DBEditTableModel() {
        
    }
    
    @Override public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
    } 
    
    @Override public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        String columnName = model.getColumnName(column);
        Object data = model.getValueAt(row, column);
        
        System.out.println("have we detected this?");
        //...// Do something with the data...
    }
    
    @Override public void setValueAt(Object aValue, int row, int column) { 
        super.setValueAt(aValue, row, column);
        //rowData[row][col] = value;
        fireTableCellUpdated(row, column);
    }
}
