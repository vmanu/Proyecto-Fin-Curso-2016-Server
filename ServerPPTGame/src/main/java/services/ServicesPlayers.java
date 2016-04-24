/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import com.mycompany.datapptgame.Player;
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
    
    public boolean deletePlayer(String player){
        return new Dao().deletePlayer(player);
    }
    
    public boolean insertPlayer(Player player){
        return new Dao().insertPlayer(player);
    }
}
