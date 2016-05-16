/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dam2
 */
public class SeguridadDAO {
    
    public ArrayList<String> getClaves(){
        ArrayList<String> keys=new ArrayList<>();
        Connection connection=null;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = "SELECT * FROM POSIBLES_CLAVES";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            //STEP 5: Extract data from result set
            while (rs.next()) {
                String pass=rs.getString("CLAVE");
                keys.add(pass);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SeguridadDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SeguridadDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            con.cerrarConexion(connection);
        }
        return keys;
    }
    
    public ArrayList<String> getComplementos(){
        ArrayList<String> compls=new ArrayList<>();
        Connection connection=null;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = "SELECT * FROM POSIBLES_COMPLEMENTO";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            //STEP 5: Extract data from result set
            while (rs.next()) {
                String complemento=rs.getString("COMPLEMENTO");
                compls.add(complemento);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SeguridadDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SeguridadDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            con.cerrarConexion(connection);
        }
        return compls;
    }
}
