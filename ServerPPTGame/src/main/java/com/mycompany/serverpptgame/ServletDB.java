/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serverpptgame;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.datapptgame.Player;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mycompany.datapptgame.ClaveComplemento;
import objetos_seguridad.PasswordHash;
import services.ServicesPlayers;

/**
 *
 * @author ivanp
 */
@WebServlet(name = "ServletDB", urlPatterns = {"/ServletDB"})
public class ServletDB extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("EN EL SERVLET");
        try {
            int indexKey=0,indexCompl=0;
            ServicesPlayers sp=new ServicesPlayers();
            String keyHasheada=(String)request.getParameter("claveHasheada");
            String complementoHasheado=(String)request.getParameter("complementoHasheado");
            System.out.println("KeyHasheada "+keyHasheada);
            String kc=keyHasheada.concat(complementoHasheado);
            ClaveComplemento cc=(ClaveComplemento)request.getSession().getAttribute("keysComplements");
            boolean encontradaKey=false;
            boolean encontradoCompl=false;
            String paraCifrar="",key="",complemento="";
            if(cc.getClaves()!=null){
                while(indexKey<cc.getClaves().size()&&!encontradaKey){
                    key=cc.getClaves().get(indexKey);
                    try {
                        if(PasswordHash.validatePassword(key,keyHasheada )){
                            encontradaKey=true;
                        }else{
                            indexKey++;
                        }
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(ServletDB.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvalidKeySpecException ex) {
                        Logger.getLogger(ServletDB.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if(cc.getComplementos()!=null){
                while(indexCompl<cc.getComplementos().size()&&!encontradoCompl){
                    complemento=cc.getComplementos().get(indexCompl);
                    try {
                        if(PasswordHash.validatePassword(complemento,complementoHasheado)){
                            encontradoCompl=true;
                        }else{
                            indexCompl++;
                        }
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(ServletDB.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvalidKeySpecException ex) {
                        Logger.getLogger(ServletDB.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if(encontradoCompl&&encontradaKey){
                paraCifrar=key.concat(complemento);
            }
                ObjectMapper om=new ObjectMapper();
                om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                Player p;
                String op = (String)request.getParameter("op");
                switch(op){
                    case "put":
                        p=(Player)request.getSession().getAttribute("player");
                        sp.insertPlayer(p);
                        break;
                    case "update":
                        p=(Player)request.getSession().getAttribute("player");
                        sp.addVictories(p.getNamePlayer());
                        break;
                    case "get":
                        request.setAttribute("players", sp.getPlayers());
                        response.getWriter().write("EL GET DEVUELVE "+sp.getPlayers());
                        System.out.println("Saliendo de get");
                        break;
                    case "getByVictories":
                        request.setAttribute("playersByVictories", sp.getPlayersByVictories());
                        response.getWriter().write("EL GET DEVUELVE "+sp.getPlayers());
                        System.out.println("Saliendo de get");
                        break;
                    case "getByRounds":
                        request.setAttribute("playersByRounds", sp.getPlayersByGamesPlayed());
                        response.getWriter().write("EL GET DEVUELVE "+sp.getPlayers());
                        System.out.println("Saliendo de get");
                        break;
                    case "getByAverage":
                        request.setAttribute("playersByAverage", sp.getPlayersByAverage());
                        response.getWriter().write("EL GET DEVUELVE "+sp.getPlayers());
                        System.out.println("Saliendo de get");
                        break;
                }
            } catch (Exception ex) {
                Logger.getLogger(ServletDB.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
