/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import config.Configuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.sqlite.SQLiteConfig;

/**
 *
 * @author dam2
 */
public class DBConnector {
    public static final String DRIVER = "org.sqlite.JDBC";

    public Connection getConnection() throws ClassNotFoundException {
        Class.forName(DRIVER);
        Connection connection = null;
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(Configuration.getInstance().getDburl(), config.toProperties());
        } catch (SQLException ex) {
            System.err.println("Error al abrir la conexiÃ³n a la base de datos");
        }
        return connection;
    }

    public void cerrarConexion(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ex) {
            System.err.println("Error al cerrar la conexiÃ³n a la base de datos");
        }
    }
}
