/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import com.mycompany.datapptgame.Player;
import com.mycompany.datapptgame.User;
import dao.Dao;
import java.util.ArrayList;

/**
 *
 * @author ivanp
 */
public class ServicesPlayers {
    
    public boolean addVictories(String player){
        return new Dao().addVictories(player);
    }
    
    public ArrayList<Player> getPlayers(){
        return new Dao().getPlayers();
    }
    
    public boolean insertPlayer(User player){
        return new Dao().insertPlayer(player);
    }
    
    public ArrayList<Player> getPlayersByVictories(){
        return new Dao().getPlayersByVictories();
    }
    
    public ArrayList<Player> getPlayersByGamesPlayed(){
        return new Dao().getPlayersByGamesPlayed();
    }
    
    public ArrayList<Player> getPlayersByAverage(){
        return new Dao().getPlayersByAverage();
    }
    
    public User getUserByLogin(String login){
        return new Dao().getUserByLogin(login);
    }
}
