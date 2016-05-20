/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import com.mycompany.datapptgame.Player;
import java.util.ArrayList;

/**
 * Gestiona los jugadores implicados en una partida, guardado los objetos Player
 * que los definen, pudiendo asi acceder con una busqueda a su session y otros
 * valores que son usados en el servidor
 * @author Victor e Ivan
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

    /**
     * Agrega un nuevo player a la lista
     * @param p 
     */
    public void addPlayer(Player p) {
        if (jugadores.size() < 2) {
            jugadores.add(p);
        }
    }
    
    /**
     * limplia la lista de jugadores
     */
    public void resetPlayers(){
        jugadores.clear();
    }

    @Override
    public String toString() {
        return "Partida{" + "jugadores=" + jugadores + '}';
    }
}
