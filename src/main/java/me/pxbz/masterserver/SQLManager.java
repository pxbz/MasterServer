package me.pxbz.masterserver;

import java.net.InetAddress;
import java.sql.*;

public class SQLManager {


    public static void connect() {
        Connection conn = null;
        String dbServer = "";
        int dbPort = 0;
        String dbName = "servercore";
        String userName = "";
        String password = "";
        String url = String.format("jdbc:mysql://%s:%d?user=%s&password=%s",
                dbServer, dbPort, userName, password);
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();

            String sqlusedb = "use " + dbName;
            stmt.executeUpdate(sqlusedb);

            // create table
            String sql = "CREATE TABLE if not exists servercoreconfig(`key` varchar(64), `value` varchar(16), PRIMARY KEY(`key`))";
            int result = stmt.executeUpdate(sql);

            // insert data
            if (result != -1) {
                PreparedStatement pStmt = conn.prepareStatement(
                        "set @newkey=?,@newvalue=?");
                pStmt.setString(1, "centralserverip");
                pStmt.setString(2, InetAddress.getLocalHost().toString().split("/")[1]);
                result = pStmt.executeUpdate();
                conn.createStatement().executeUpdate("insert into servercoreconfig(`key`, `value`) values (@newkey, @newvalue) on duplicate key update `value`=@newvalue");
            }

        } catch (SQLException e) {
            System.out.println("MySQL connection had an exception");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
