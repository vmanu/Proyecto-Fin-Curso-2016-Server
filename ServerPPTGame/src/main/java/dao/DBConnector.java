/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import static constantes.ConstantesBaseDatos.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dam2
 */
public class DBConnector {
    
    public Connection getConnection() throws ClassNotFoundException {
        Class.forName(DRIVER);
        Connection connection = null;
        try {
            String url = HOST+DBNAME;
            connection=DriverManager.getConnection(url,USERNAME,PASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }

    public void cerrarConexion(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ex) {
            System.err.println("Error al cerrar la conexion a la base de datos");
        }
    }
}
