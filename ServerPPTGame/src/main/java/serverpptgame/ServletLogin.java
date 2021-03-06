/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverpptgame;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.datapptgame.User;
import static constantes.ConstantesConexion.NAME_SERVLET_LOGIN;
import static constantes.ConstantesConexion.RUTA_SERVLET_LOGIN;
import static constantes.ConstantesServer.*;
import static constantes.conexion.ConstantesConexion.NO;
import static constantes.conexion.ConstantesConexion.SI;
import static constantes.conexion.ConstantesConexion.URL_LOGIN;
import static constantes.conexion.ConstantesConexion.USER;
import static constantes.conexion.ConstantesConexion.UTF_8;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import objetos_seguridad.PasswordHash;
import services.ServicesPlayers;
import static utilities.Utilidades.getClaveCifrado;

/**
 *
 * @author ivanp
 */
@WebServlet(name = NAME_SERVLET_LOGIN, urlPatterns = {RUTA_SERVLET_LOGIN})
public class ServletLogin extends HttpServlet {

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
        ServicesPlayers sp = new ServicesPlayers();
        response.addHeader(PERMISSION_ACCESS_JAVASCRIPT, LOCATION_ACCESS_JAVASCRIPT);
        boolean validated = false;
        try {
            User user = null;
            ObjectMapper mapper = new ObjectMapper();
            String userToDecode = request.getParameter(USER);
            byte[] base64 = Base64.decodeBase64(userToDecode.getBytes(UTF_8));
            String descifrado = PasswordHash.descifra(base64, getClaveCifrado(request));
            user = mapper.readValue(descifrado, new TypeReference<User>() {
            });
            User u = sp.getUserByLogin(user.getLogin());
            if (u != null) {
                validated = PasswordHash.validatePassword(user.getPass(), u.getPass());
            }
        } catch (Exception ex) {
            Logger.getLogger(ServletLogin.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (validated) {
            request.getSession().setAttribute(URL_LOGIN, "true");
            response.getWriter().print(SI);
        } else {
            request.getSession().setAttribute(URL_LOGIN, "false");
            response.getWriter().print(NO);
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
