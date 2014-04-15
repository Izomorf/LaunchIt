/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utools.launchit.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static utools.launchit.LaunchItApp.getLogFileHandler;
import utools.launchit.LaunchItConstants;
import utools.launchit.LaunchItDbEntry;

/**
 *
 * @author ruinmaxk
 */
public class LaunchItDatabase {
    private static Logger log = Logger.getLogger(LaunchItDatabase.class.getName());
    private static LaunchItDatabase db;
    
    private static Connection connection;
    
    private static HashMap<Integer, String> columnNames = new HashMap<Integer, String>();
    private static int columnCount = 0;
    private final String QUERY_TABLE_INFO = String.format("PRAGMA table_info(%s)",
            LaunchItConstants.APPS_TABLE);
    
    private static int rowCount = 0;
    
    private LaunchItDatabase() {
        try {
            log.addHandler(getLogFileHandler());
        } catch (SecurityException e) {
            log.log(Level.SEVERE,
                "Cannot create log file due to Security Exception: ", e);
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE,
                "org.sqlite.JDBC class not found: ", e);
            return;
        }
        
        try {          
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s",
                    LaunchItConstants.DB_NAME));
        } catch (SQLException e) {
            log.log(Level.SEVERE,
                "Database access error: Cannot establish connection to database "
                        + LaunchItConstants.DB_NAME, e);
            return;
        }
        
        try {
            Class.forName("org.sqlite.JDBC");
            
            Statement stat = connection.createStatement();
            stat.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %s (%s, %s);",
                    LaunchItConstants.APPS_TABLE, LaunchItConstants.COLUMN_ALIAS,
                    LaunchItConstants.COLUMN_PATH));
            ResultSet rs = stat.executeQuery(QUERY_TABLE_INFO);
            try {
                columnNames.put(columnCount++, "rowid");
                while(rs.next()) {
                    columnNames.put(columnCount++, rs.getString("name"));
                }
            } finally {
                rs.close();
            }
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception while creating LaunchIt DB: ", e);
        }
        log.info("LaunchIt DB created");
    }
    
    public static LaunchItDatabase getInstance() {
        if (db == null) {
            db = new LaunchItDatabase();
        } 
        return db;
    }
    
    public int insert(LaunchItDbEntry res) {
        return insert(res.getValue(LaunchItConstants.COLUMN_ALIAS),
                res.getValue(LaunchItConstants.COLUMN_PATH));
    }
    
    public int insert(String... values) {
        try {          
            PreparedStatement prep = connection.prepareStatement("insert into apps values (?, ?, ?)");
            for (int i = 0; i < values.length; i++) {
                prep.setString(i + 1, values[i]);
            }
            prep.addBatch();
            
            connection.setAutoCommit(false);
            prep.executeBatch();
            connection.setAutoCommit(true);
            log.log(Level.SEVERE, "Successfully inserted " + values[0] + values[1] + values[2]);
        } catch(Exception e) {
            System.out.println("Insert error");
        }
        return 0;
    }
    
    public int update(int rowId, String column, String newValue) {
        try {
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", LaunchItConstants.DB_NAME));
            
            PreparedStatement prep = connection
                    .prepareStatement(String.format("update %s set %s = ? where %s = ?",
                            LaunchItConstants.APPS_TABLE, column, LaunchItConstants.COLUMN_ROWID));
            
            prep.setString(1, newValue);
            prep.setInt(2, rowId);
            prep.addBatch();
            connection.setAutoCommit(false);
            prep.executeBatch();
            connection.setAutoCommit(true);
            connection.close();
        } catch(Exception e) {
            System.out.println("Update error");
        }
        return 0;
    }
    
    public List<LaunchItDbEntry> query(String query) {
        rowCount = 0;
        List<LaunchItDbEntry> ResultPKRowEntityList = new ArrayList<LaunchItDbEntry>();
        ResultSet rs;
        try {
            Statement stat = connection.createStatement();
            try {
                rs = stat.executeQuery(query);
                try {
                    while (rs.next()) {
                        rowCount++;
                        LaunchItDbEntry entity = new LaunchItDbEntry();
                        for (int i = 0; i < columnCount; i++) {
                            entity.setValue(columnNames.get(i), rs.getString(i + 1));
                        }
                        ResultPKRowEntityList.add(entity);
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stat.close();
            }
        } catch (SQLException e) {
        }
        return ResultPKRowEntityList;
    }
    
    public List<LaunchItDbEntry> querySelectAll() {
        return query(String.format("SELECT rowid,* FROM %s", LaunchItConstants.APPS_TABLE));
    }
    
    public static List getColumnNames() {
        ArrayList<String> columnNamesList = new ArrayList<String>();
        for (Map.Entry<Integer, String> entry : columnNames.entrySet()) {
            columnNamesList.add(entry.getValue());
        }
        return columnNamesList;
    };
    
    public static int getRowCount() {
        return rowCount;
    };
    
    public int getColumnIndexByColumnName(String columnName) {
        for (Map.Entry<Integer, String> entry : columnNames.entrySet()) {
            if (entry.getValue().equals(columnName)) {
                return entry.getKey();
            }
        }
        return -1;
    }
    
    public String getColumnNameByColumnIndex(int columnIndex) {
        return columnNames.get(columnIndex + 1);
    }

    public void delete(int rowId) {        
        try{
            PreparedStatement prep = connection.prepareStatement(String
                    .format("DELETE from %s where %s=?",
                            LaunchItConstants.APPS_TABLE, LaunchItConstants.COLUMN_ROWID));
            prep.setInt(1, rowId);
            connection.setAutoCommit(false);
            prep.executeUpdate();
            connection.setAutoCommit(true);
        } catch(Exception e) {
            System.out.println("Deletion error");
        }
    }
    
    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.log(Level.SEVERE,
                "Database access error: Cannot close connection to database " 
                        + LaunchItConstants.DB_NAME, e);
        }
    }
}
