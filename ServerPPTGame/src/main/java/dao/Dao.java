/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.mycompany.datapptgame.Player;
import com.mycompany.datapptgame.User;
import static constantes.ConstantesBaseDatos.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import objetos_seguridad.PasswordHash;

/**
 *
 * @author Victor
 */
public class Dao {

    public boolean addVictories(String player) {
        Connection connection = null;
        int ok = 0, victories = 0;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = SELECT_ADD_VICTORIES;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, player);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                victories = rs.getInt("won");
            }
            sql = UPDATE_ADD_VICTORIES;
            victories++;
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, victories);
            pstmt.setString(2, player);
            ok = pstmt.executeUpdate();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.cerrarConexion(connection);
        }
        return ok != 0;
    }
    
    public boolean addRounds(String player) {
        Connection connection = null;
        int ok = 0, rounds = 0;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = SELECT_ADD_ROUNDS;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, player);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rounds = rs.getInt("played");
            }
            sql = UPDATE_ADD_ROUNDS;
            rounds++;
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, rounds);
            pstmt.setString(2, player);
            ok = pstmt.executeUpdate();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.cerrarConexion(connection);
        }
        return ok != 0;
    }

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        Connection connection = null;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = SELECT_GET_PLAYERS;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("login");
                //String pass=rs.getString("pass");
                int victories = rs.getInt("won");
                //Player p = new Player(name, pass, victories);
                int numPartidas = rs.getInt("played");
                Player p = new Player(name, numPartidas, victories);
                players.add(p);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.cerrarConexion(connection);
        }
        return players;
    }

    public boolean insertPlayer(User p) {
        Connection connection = null;
        int ins = 0, insDATAP;
        boolean ingresadoDataPlayer = false;
        DBConnector con = new DBConnector();
        String sql="";
        PreparedStatement stmt;
        ResultSet rs;
        int lastId = -1;
        try {
            connection = con.getConnection();
            sql = SELECT_INSERT_PLAYERS;
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            sql = INSERT_DATA_PLAYER;
            stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            insDATAP = stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                lastId = rs.getInt(1);
            }
            ingresadoDataPlayer = true;
            sql = INSERT_PLAYER;
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, p.getLogin());
            stmt.setString(2, PasswordHash.createHash(p.getPass()));
            stmt.setInt(3, lastId);
            ins = stmt.executeUpdate();
        } catch (ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            if (ingresadoDataPlayer) {
                try {
                    sql = DELETE_FOR_ROLLBACK;
                    stmt = connection.prepareStatement(sql);
                    stmt.setInt(1, lastId);
                    stmt.executeUpdate();
                    sql=ALTER_TABLE;
                    stmt = connection.prepareStatement(sql);
                    stmt.setInt(1, lastId);
                    stmt.executeUpdate();
                } catch (SQLException ex1) {
                    Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.cerrarConexion(connection);
        }
        return ins != 0;
    }

    public ArrayList<Player> getPlayersByVictories() {
        ArrayList<Player> players = new ArrayList<>();
        Connection connection = null;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = SELECT_GET_BY_VICTORIES;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("login");
                int victories = rs.getInt("won");
                int numPartidas = rs.getInt("played");
                Player p = new Player(name, numPartidas, victories);
                players.add(p);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.cerrarConexion(connection);
        }
        return players;
    }

    public ArrayList<Player> getPlayersByGamesPlayed() {
        ArrayList<Player> players = new ArrayList<>();
        Connection connection = null;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = SELECT_GET_BY_PLAYED;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("login");
                int victories = rs.getInt("won");
                int numPartidas = rs.getInt("played");
                Player p = new Player(name, numPartidas, victories);
                players.add(p);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.cerrarConexion(connection);
        }
        return players;
    }

    public ArrayList<Player> getPlayersByAverage() {
        ArrayList<Player> players = new ArrayList<>();
        Connection connection = null;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = SELECT_GET_BY_AVERAGE;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("login");
                int victories = rs.getInt("won");
                int numPartidas = rs.getInt("played");
                Player p = new Player(name, numPartidas, victories);
                players.add(p);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.cerrarConexion(connection);
        }
        return players;
    }

    public User getUserByLogin(String login) {
        User u = null;
        Connection connection = null;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = SELECT_GET_USER_BY_LOGIN;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String loginU = rs.getString("LOGIN");
                String pass = rs.getString("PASS");
                u = new User(loginU, pass);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.cerrarConexion(connection);
        }
        return u;
    }
}
