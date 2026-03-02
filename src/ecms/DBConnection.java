/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecms;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author 2tree
 */
public class DBConnection {
    public static Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");

            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521/FREE",
                    "C##treesa",
                    "pass"
            );

            return con;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
