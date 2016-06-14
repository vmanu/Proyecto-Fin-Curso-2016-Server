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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mycompany.datapptgame.User;
import static constantes.ConstantesConexion.NAME_SERVLET_DB;
import static constantes.ConstantesConexion.RUTA_SERVLET_DB;
import static constantes.ConstantesServer.*;
import static constantes.conexion.ConstantesConexion.*;
import objetos_seguridad.PasswordHash;
import org.apache.commons.codec.binary.Base64;
import services.ServicesPlayers;
import static utilities.Utilidades.getClaveCifrado;

/**
 *
 * @author ivanp
 */
@WebServlet(name = NAME_SERVLET_DB, urlPatterns = {RUTA_SERVLET_DB})
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
        response.addHeader(PERMISSION_ACCESS_JAVASCRIPT, LOCATION_ACCESS_JAVASCRIPT);
        try {
            ServicesPlayers sp=new ServicesPlayers();
            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            User u;
            Player p;
            String op = (String) request.getParameter(OPERATION_OPTION);
            ObjectMapper mapper = new ObjectMapper();
            switch (op) {
                case URL_AGREGAR_USUARIO:
                    String usuarioRaw = request.getParameter(USER);
                    byte[] base64 = Base64.decodeBase64(usuarioRaw.getBytes(UTF_8));
                    String descifrado = PasswordHash.descifra(base64, getClaveCifrado(request));
                    u = mapper.readValue(descifrado, new TypeReference<User>() {
                    });
                    if (sp.insertPlayer(u)) {
                        response.getWriter().write(SI);
                    } else {
                        response.getWriter().write(NO);
                    }
                    break;
                case URL_GET_BY_VICTORIES:
                    response.getWriter().write(mapper.writeValueAsString(sp.getPlayersByVictories()));
                    break;
                case URL_GET_BY_ROUNDS:
                    response.getWriter().write(mapper.writeValueAsString(sp.getPlayersByGamesPlayed()));
                    break;
                case URL_GET_BY_AVERAGE:
                    response.getWriter().write(mapper.writeValueAsString(sp.getPlayersByAverage()));
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
