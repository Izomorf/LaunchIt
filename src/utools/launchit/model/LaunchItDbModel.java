/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utools.launchit.model;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import static utools.launchit.LaunchItApp.getLogFileHandler;
import utools.launchit.LaunchItDbEntry;
import utools.launchit.Pair;
import utools.launchit.db.LaunchItDatabase;

/**
 *
 * @author ruinmaxk
 */
public class LaunchItDbModel extends AbstractTableModel {
    private static Logger log = Logger.getLogger(LaunchItDbModel.class.getName());
    
    int count = 0;
    private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
    List columnNames = new ArrayList<String>();
    List<LaunchItDbEntry> values = new ArrayList<LaunchItDbEntry>();
    private List<LaunchItDbEntry> dbValues = new ArrayList<LaunchItDbEntry>();
    ResultSet rs;
    int rowCount;
   
    public LaunchItDbModel() {
        try {
            log.addHandler(getLogFileHandler());
        } catch (SecurityException e) {
            log.log(Level.SEVERE,
                "Cannot create log file due to Security Exception: ", e);
        }
        values = LaunchItDatabase.getInstance().querySelectAll();
        columnNames = LaunchItDatabase.getInstance().getColumnNames();
        rowCount = LaunchItDatabase.getInstance().getRowCount();
        
        dbValues = new ArrayList<LaunchItDbEntry>(values);
    }
    
    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames.get(columnIndex + 1).toString();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return values.get(rowIndex).getValue(LaunchItDatabase.getInstance()
                .getColumnNameByColumnIndex(columnIndex));
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        log.info(String.format("Setting value at %d, %d to %s",
                rowIndex, columnIndex, (String)aValue));
        values.get(rowIndex).setValue(LaunchItDatabase.getInstance()
                .getColumnNameByColumnIndex(columnIndex), (String)aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public int getColumnCount() {
        return 2;
    }
    
    public LaunchItDbEntry getRow(int rowIndex) {
        return values.get(rowIndex);
    }

    public void insert (LaunchItDbEntry webResource) {
        insert(webResource, -1);
    }
    
    public void insert (LaunchItDbEntry webResource, int index) {
        if (index == -1) {
            values.add(webResource);
        } else {
            values.add(index, webResource);
        }
        rowCount++;
        fireTableRowsInserted(0, rowCount);
    }
    
    public void delete (LaunchItDbEntry webResource) {
        values.remove(webResource);
        rowCount--;
        fireTableDataChanged();
    }
 
    public void refresh() {
        values = LaunchItDatabase.getInstance().querySelectAll();
        columnNames = LaunchItDatabase.getInstance().getColumnNames();
    }
    
    public void addEmptyRow() {
        log.info(String.format("Added empty row with index %d",
                rowCount));
        values.add(rowCount, new LaunchItDbEntry());
        fireTableRowsInserted(rowCount, rowCount + 1);
        rowCount++;
    }
    
    public void clearTable() {
        log.info(String.format("Clearing table..."));
        values.clear();
        fireTableRowsDeleted(0, rowCount);
        rowCount = 0;
    }
    
    public void queryQuickSearch(String stringToSearch) {
        clearTable();
        Set<String> keys;
        String value;
        for (LaunchItDbEntry resource : dbValues) {
            keys = resource.getKeys();
            for (String key : keys) {
                value = resource.getValue(key);
                if (value.contains(stringToSearch)) {
                    values.add(resource);
                    rowCount++;
                    break;
                }
            }
        }
        fireTableRowsUpdated(0, rowCount);
    }
}
