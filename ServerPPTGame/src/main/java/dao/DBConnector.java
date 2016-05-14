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
    private String USERNAME="adminH3ig8b2";
    private String PASSWORD="bdIuys-gZmKw";
    private String HOST="www.servidor-pptgame.rhcloud.com/phpmyadmin";
    private String PORT="3306";
    private String DBNAME="PPTGAME";

    public Connection getConnection() throws ClassNotFoundException {
        //Class.forName(DRIVER);
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
        } catch (SQLException ex) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        Connection connection = null;
        try {
            /*try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(Configuration.getInstance().getDburl(), config.toProperties());
            } catch (SQLException ex) {
            System.err.println("Error al abrir la conexiÃ³n a la base de datos");
            }*/
            System.out.println("Before get config... ");
            //Configuration config=Configuration.getInstance();
            //System.out.println("Configuration es "+config);
            String url = String.format("jdbc:mysql:servidor-pptgame.rhcloud.com/phpmyadmin/#PMAURL-16:sql.php?db=PPTGAME");
            System.out.println("Connecting to... "+url);
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
            System.err.println("Error al cerrar la conexiÃ³n a la base de datos");
        }
    }
}
