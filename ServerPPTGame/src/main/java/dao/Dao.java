/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.mycompany.datapptgame.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Victor
 */
public class Dao {

    public boolean addVictories(String player) {
        Connection connection=null;
        int ok=0, victories=0;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = "SELECT victories FROM PLAYERS";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                victories = rs.getInt("victories");
            }
            sql = "UPDATE PLAYERS set victories=? where login=?";
            victories++;
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, victories);
            pstmt.setString(2, player);
            ok=pstmt.executeUpdate();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            con.cerrarConexion(connection);
        }
        return ok!=0;
    }
    
    public ArrayList<Player> getPlayers(){
        ArrayList<Player> players = new ArrayList<>();
        Connection connection=null;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = "SELECT login,pass,victories FROM PLAYERS";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String name=rs.getString("login");
                //String pass=rs.getString("pass");
                int victories=rs.getInt("victories");
                //Player p = new Player(name, pass, victories);
                Player p = new Player(name, victories);
                players.add(p);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            con.cerrarConexion(connection);
        }
        return players;
    }
    
    public boolean deletePlayer(String n){
        Connection connection=null;
        int del=0;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = "DELETE from PLAYERS where login=?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, n);        
            del=stmt.executeUpdate();
            //STEP 5: Extract data from result set
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            con.cerrarConexion(connection);
        }
        return del!=0;
    }
    
    public boolean insertPlayer(Player p){
        System.out.println("ENTRAMOS EN INSERT");
        Connection connection=null;
        int ins=0;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = "insert into LOGIN(login) values (?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, p.getNamePlayer());
            //stmt.setString(2, p.getPass());
            ins=stmt.executeUpdate();
            //STEP 5: Extract data from result set
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            con.cerrarConexion(connection);
        }
        return ins!=0;
    }
}
