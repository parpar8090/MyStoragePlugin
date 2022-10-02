package com.parpar8090.mystorage.sqlite;

import com.parpar8090.mystorage.MyStoragePlugin;

import java.io.File;
import java.sql.*;

public class SQLiteConnection {
    private final File folder;
    private final File file;
    private final String url;
    private Connection conn = null;
    public SQLiteConnection(String fileName){
        folder = MyStoragePlugin.getInstance().getDataFolder();
        file = new File(folder, fileName);
        url = "jdbc:sqlite:"+file.getAbsolutePath();

        load();
    }

    private void load(){
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            if(conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeSql(String sql, Object[] values, SQLType[] valueTypes){
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            int i = 0;
            int lastIndex = sql.indexOf("?");
            while(lastIndex != -1){
                pstmt.setObject(i+1, values[i], valueTypes[i]);
                lastIndex = sql.indexOf("?", lastIndex);
                i++;
            }
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeSql(String sql){
        try (Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query){
        try{
            Statement stmt  = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int executeUpdate(String sql){
        try (Statement stmt  = conn.createStatement()){
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void createTableIfNotExists(String table, String[] columns){
        String sql = "CREATE TABLE IF NOT EXISTS `"+table+"` (" + String.join(",\n", columns)+");";
        executeSql(sql);
    }

    public void deleteWithCondition(String table, String sqlCondition){
        String sql = "DELETE FROM `"+table+"` WHERE " + sqlCondition + ";";
        executeUpdate(sql);
    }

    /**
     *
     * @param table the table in which records will be added
     * @param columns the columnts
     * @param values the values in place of the columns. Each value must contain brackets "(" ")" at the beggining and end.
     */
    public void insertOrUpdate(String table, String[] columns, String[] values, String[] updateColumns, String[] updateValues){
        StringBuilder updateColumnsValues = new StringBuilder();
        for (int i = 0; i < updateColumns.length; i++) {
            updateColumnsValues.append(updateColumns[i]).append(" = ").append(updateValues[i]).append(i + 1 == updateColumns.length ? "" : ", ");
        }
        String sql = "INSERT INTO `"+table+"` (" + String.join(", ", columns)+") VALUES "+String.join(", ", values)+" ON DUPLICATE KEY UPDATE "+updateColumnsValues+";";
        executeUpdate(sql);
    }

    public void insertIfNotExists(String table, String[] columns, String[] values){
        String sql = "INSERT INTO `"+table+"` (" + String.join(", ", columns)+") VALUES "+String.join(", ", values)+";";
        executeUpdate(sql);
    }

    public ResultSet selectAllWithCondition(String table, String sqlCondition){
        String query = "SELECT * FROM `"+table+"` WHERE "+sqlCondition+";";
        System.out.println(query);
        return executeQuery(query);
    }

    public int updateAllWithCondition(String table, String sqlCondition, String[] columns, String[] values){
        if(columns.length != values.length){
            throw new IllegalArgumentException("Not all columns have a value");
        }
        StringBuilder columnsValues = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            columnsValues.append(columns[i]).append(" = ").append(values[i]).append(i + 1 == columns.length ? "" : ", ");
        }
        String query = "UPDATE `"+table+"` SET "+columnsValues+" WHERE "+sqlCondition+";";
        return executeUpdate(query);
    }
}
