/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.modelpptgame;

import com.mycompany.datapptgame.Player;
import java.util.ArrayList;

/**
 *
 * @author ivanp
 */
public class Partida {

    private ArrayList<Player> jugadores;

    public Partida() {
        jugadores = new ArrayList<>();
    }

    public Partida(ArrayList<Player> jugadores) {
        this.jugadores = jugadores;
    }

    public ArrayList<Player> getJugadores() {
        return jugadores;
    }

    public void setJugadores(ArrayList<Player> jugadores) {
        this.jugadores = jugadores;
    }

    public void addPlayer(Player p) {
        if (jugadores.size() < 2) {
            jugadores.add(p);
        }
    }
    
    public void resetPlayers(){
        jugadores.clear();
    }

    @Override
    public String toString() {
        return "Partida{" + "jugadores=" + jugadores + '}';
    }
}
