package test_CUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class NumeronDB {

    String driverClassName = "org.postgresql.Driver"; // ここからいつもの
    String url = "jdbc:postgresql://localhost/test";
    String user = "dbpuser";
    String password = "hogehoge";
    Connection connection;

    PreparedStatement prepStmt_I; // INSERT用

    String strPrepSQL_I = "INSERT INTO netpro VALUES(?, ?)";

    NumeronDB() {
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(url, user, password);

            prepStmt_I = connection.prepareStatement(strPrepSQL_I);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(String name, int pass) {

        try {
            prepStmt_I.setString(1, name);
            prepStmt_I.setInt(2, pass);
            prepStmt_I.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
