/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverpptgame;

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
import com.mycompany.datapptgame.User;
import objetos_seguridad.PasswordHash;
import org.apache.commons.codec.binary.Base64;
import services.ServicesPlayers;
import utilities.Utilidades;
import static utilities.Utilidades.getClaveCifrado;

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
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:8383");
        try {
            ServicesPlayers sp=new ServicesPlayers();
            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            User u;
            Player p;
            String op = (String) request.getParameter("op");
            ObjectMapper mapper = new ObjectMapper();
            switch (op) {
                case "put":
                    String usuarioRaw = request.getParameter("user");
                    System.out.println("usuarioRAW " + usuarioRaw);
                    byte[] base64 = Base64.decodeBase64(usuarioRaw.getBytes("UTF-8"));
                    System.out.println("Base64 " + base64);
                    String descifrado = PasswordHash.descifra(base64, getClaveCifrado(request));
                    System.out.println("descifrado: " + descifrado);
                    u = mapper.readValue(descifrado, new TypeReference<User>() {
                    });
                    System.out.println("player dice ser: " + u);
                    if (sp.insertPlayer(u)) {
                        response.getWriter().write("SI");
                    } else {
                        response.getWriter().write("NO");
                    }
                    break;
                case "addRounds":
                    p = (Player) request.getSession().getAttribute("player");
                    sp.addRounds(p.getNamePlayer());
                    break;
                case "addVictories":
                    p = (Player) request.getSession().getAttribute("player");
                    sp.addVictories(p.getNamePlayer());
                    break;
//                case "get":
//                    request.setAttribute("players", sp.getPlayers());
//                    response.getWriter().write("EL GET DEVUELVE "+sp.getPlayers());
//                    System.out.println("Saliendo de get");
//                    break;
                case "getByVictories":
                    request.setAttribute("playersByVictories", sp.getPlayersByVictories());
                    response.getWriter().write(mapper.writeValueAsString(sp.getPlayersByVictories()));
                    System.out.println("Saliendo de get");
                    break;
                case "getByRounds":
                    request.setAttribute("playersByRounds", sp.getPlayersByGamesPlayed());
                    response.getWriter().write(mapper.writeValueAsString(sp.getPlayersByGamesPlayed()));
                    System.out.println("Saliendo de get");
                    break;
                case "getByAverage":
                    request.setAttribute("playersByAverage", sp.getPlayersByAverage());
                    response.getWriter().write(mapper.writeValueAsString(sp.getPlayersByAverage()));
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
