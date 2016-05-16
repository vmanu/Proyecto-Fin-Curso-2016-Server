/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objetos_seguridad;

import java.util.ArrayList;

/**
 *
 * @author IvPR
 */
public class ClaveComplemento {
    private ArrayList<String> claves;
    private ArrayList<String> complementos;

    public ClaveComplemento() {
        claves=new ArrayList<>();
        complementos=new ArrayList<>();
    }

    public ClaveComplemento(ArrayList<String> claves, ArrayList<String> complementos) {
        this.claves = claves;
        this.complementos = complementos;
    }

    public ArrayList<String> getClaves() {
        return claves;
    }

    public void setClaves(ArrayList<String> claves) {
        this.claves = claves;
    }

    public ArrayList<String> getComplementos() {
        return complementos;
    }

    public void setComplementos(ArrayList<String> complementos) {
        this.complementos = complementos;
    }

    @Override
    public String toString() {
        return "ClaveComplemento{" + "claves=" + claves + ", complementos=" + complementos + '}';
    }
    
}
