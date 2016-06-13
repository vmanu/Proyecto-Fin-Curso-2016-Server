/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverpptgame;

import com.fasterxml.jackson.databind.ObjectMapper;
import static constantes.ConstantesConexion.NAME_SERVLET_HASHING_JS;
import static constantes.ConstantesConexion.RUTA_SERVLET_HASHING_JS;
import static constantes.ConstantesServer.*;
import static constantes.conexion.ConstantesConexion.USER;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import objetos_seguridad.PasswordHash;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Victor
 */
@WebServlet(name = NAME_SERVLET_HASHING_JS, urlPatterns = {RUTA_SERVLET_HASHING_JS})
public class ServletHashingJS extends HttpServlet {

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
        String op = request.getParameter(OPERATION_OPTION);
        switch (op) {
            case CLAVES:
                String clave = request.getParameter("clave");
                String complemento = request.getParameter("complemento");
                String[] envia = new String[2];
                try {
                    envia[0] = PasswordHash.createHash(clave);
                    envia[1] = PasswordHash.createHash(complemento);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                    Logger.getLogger(ServletHashingJS.class.getName()).log(Level.SEVERE, null, ex);
                }
                response.getWriter().write(new ObjectMapper().writeValueAsString(envia));
                break;
            case USER:
                String claveHasheo = request.getParameter("fraseHash");
                String devuelve = "";
                try {
                    devuelve = new String(Base64.encodeBase64(PasswordHash.cifra(request.getParameter(USER), claveHasheo)));
                } catch (Exception ex) {
                    Logger.getLogger(ServletHashingJS.class.getName()).log(Level.SEVERE, null, ex);
                }
                response.getWriter().write(new ObjectMapper().writeValueAsString(devuelve));
                break;
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
