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
import java.util.logging.Level;
import java.util.logging.Logger;
//import org.sqlite.SQLiteConfig;

/**
 *
 * @author dam2
 */
public class DBConnector {
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    private String USERNAME="servidor";
    private String PASSWORD="server_PPTG@me";
    private String HOST="www.servidor-pptgame.rhcloud.com/phpmyadmin";
    private String PORT="3306";
    private String DBNAME="PPTGAME";

    public Connection getConnection() throws ClassNotFoundException {
        Class.forName(DRIVER);
        
        Connection connection = null;
        try {
            System.out.println("Before get config... ");
            //String url = "jdbc:mysql://node7553-servidorpptgame.jelastic.cloudhosted.es/PPTGAME";
            String url = "jdbc:mysql://localhost:9090/PPTGAME";
            System.out.println("Connecting to... "+url);
            connection=DriverManager.getConnection(url,USERNAME,PASSWORD);
            System.out.println("Conectado= "+connection);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
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
