/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.mycompany.datapptgame.Player;
import com.mycompany.datapptgame.User;
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
            String sql = "SELECT won FROM DATA_PLAYER dp, LOGIN log where dp.ID_PLAYER=log.ID_PLAYER and log.LOGIN=?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, player);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                victories = rs.getInt("won");
            }
            sql = "UPDATE DATA_PLAYER dp, LOGIN log set won=? where dp.ID_PLAYER=log.id_player and log.LOGIN=?";
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

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        Connection connection = null;
        DBConnector con = new DBConnector();
        try {
            connection = con.getConnection();
            String sql = "SELECT * FROM DATA_PLAYER dp, LOGIN log where dp.ID_PLAYER=log.ID_PLAYER";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("login");
                //String pass=rs.getString("pass");
                int victories = rs.getInt("won");
                //Player p = new Player(name, pass, victories);
                Player p = new Player(name, victories);
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
        System.out.println("ENTRAMOS EN INSERT");
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
            //NO INSERTAR SI YA EXISTE
            //SI NO EXISTE, INSERTAR EN DATA_PLAYER Y LUEGO INSERTAR EN LOGIN
//            String sql = "select login from LOGIN where login=?";
            sql = "select login from LOGIN";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            ArrayList<String> loginsEnBD = new ArrayList();
//            if(rs.getString("login")!=null){
//                
//            }
//            while (rs.next()) {
//                String login = rs.getString("login");
//                loginsEnBD.add(login);
//            }
//            if (loginsEnBD.contains(p.getNamePlayer())) {
//                //YA EXISTE!!
//            } else {
//            connection.setAutoCommit(false);
            sql = "INSERT into DATA_PLAYER (won, played, coins) values(0,0,0)";
            stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            insDATAP = stmt.executeUpdate();
            System.out.println("insDATAP " + insDATAP);
//            sql = "SELECT LAST_INSERT_ID()";
//            rs = stmt.executeQuery(sql);
//            int lastId = rs.getInt(1);
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                lastId = rs.getInt(1);
            }
            ingresadoDataPlayer = true;
            System.out.println("lastID "+lastId);
            sql = "insert into LOGIN(login,pass,id_player) values (?,?,?)";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, p.getLogin());
            stmt.setString(2, PasswordHash.createHash(p.getPass()));
            stmt.setInt(3, lastId);
            ins = stmt.executeUpdate();
            System.out.println("NO PETA CHUMACHO");
//            if (ins == 0) {
//                System.out.println("ENTRAMOS EN CERO");
//                connection.rollback();
//            }
//            }

            //stmt.setString(2, p.getPass());
            //STEP 5: Extract data from result set
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
//            try {
//                connection.rollback();
//            } catch (SQLException ex1) {
//                Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex1);
//            }
            System.out.println("ENTRAMOS EN SQLException");
            if (ingresadoDataPlayer) {
                try {
                    sql = "DELETE FROM `data_player` WHERE `data_player`.`ID_PLAYER` = ?";
                    stmt = connection.prepareStatement(sql);
                    stmt.setInt(1, lastId);
                    stmt.executeUpdate();
                    sql="ALTER TABLE `data_player` AUTO_INCREMENT=?";
                    stmt = connection.prepareStatement(sql);
                    stmt.setInt(1, lastId);
                    stmt.executeUpdate();
                } catch (SQLException ex1) {
                    System.out.println("ENTRAMOS EN SQLException Interno");
                    Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
//            try {
//                connection.setAutoCommit(true);
//            } catch (SQLException ex) {
//                Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
//            }
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
            String sql = "SELECT * FROM DATA_PLAYER dp, LOGIN log where dp.ID_PLAYER=log.ID_PLAYER order by won DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("login");
                //String pass=rs.getString("pass");
                int victories = rs.getInt("won");
                //Player p = new Player(name, pass, victories);
                Player p = new Player(name, victories);
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
            String sql = "SELECT * FROM DATA_PLAYER dp, LOGIN log where dp.ID_PLAYER=log.ID_PLAYER order by played DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("login");
                //String pass=rs.getString("pass");
                int victories = rs.getInt("won");
                //Player p = new Player(name, pass, victories);
                Player p = new Player(name, victories);
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
            String sql = "SELECT * FROM DATA_PLAYER dp, LOGIN log where dp.ID_PLAYER=log.ID_PLAYER order by (won/played) DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("login");
                //String pass=rs.getString("pass");
                int victories = rs.getInt("won");
                //Player p = new Player(name, pass, victories);
                Player p = new Player(name, victories);
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
            String sql = "SELECT * FROM LOGIN WHERE login=?";
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
