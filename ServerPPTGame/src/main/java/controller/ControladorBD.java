/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.Dao;

/**
 *
 * @author Victor
 */
public class ControladorBD {
    public boolean agregarVictoria(String user){
        return new Dao().addVictories(user);
    }
}
