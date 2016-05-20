/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverpptgame;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.datapptgame.ClaveComplemento;
import com.mycompany.datapptgame.User;
import dao.Dao;
import java.io.IOException;
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
import org.apache.commons.codec.binary.Base64;
import objetos_seguridad.PasswordHash;
import services.ServicesPlayers;

/**
 *
 * @author ivanp
 */
@WebServlet(name = "controllerLogin", urlPatterns = {"/login"})
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
        Dao dao = new Dao();
        boolean validated = false;
        try {
            User user = null;
            ObjectMapper mapper = new ObjectMapper();
            String userToDecode = request.getParameter("user");
            byte[] base64 = Base64.decodeBase64(userToDecode.getBytes("UTF-8"));
            System.out.println("PASS PARA CIFRAR USERS " + (String) request.getSession().getAttribute("passLogin"));
            user = mapper.readValue(PasswordHash.descifra(base64, (String) request.getSession().getAttribute("passLogin")), new TypeReference<User>() {
            });
            System.out.println(user.getLogin() + " --- " + user.getPass());
            User u = dao.getUserByLogin(user.getLogin(), user.getPass());
            //CORRECCION: ASI A MACHETE? SIN PREGUNTAR SI EL RESULTADO HA SIDO NULL?? PORQUE COMO METAS UN LOGIN QUE NO ESTÉ REGISTRADO ES LO QUE VAS A OBTENER... UN BONITO NULLPOINTEREXCEPTION EN EL GLASSFISH por la siguientes lineas, condicionalas a que no sean null, y si es null, pon directamente validate a false y los dos souts que tienes a continuacion, comentalos o peta el que usa al usuario.
            validated = PasswordHash.validatePassword(user.getPass(), u.getPass());
            System.out.println("VALIDATED EN CONTROLLER LOGIN " + validated);
            //CORRECCION: ESTO PUEDE PETAR (USAS U sin comprobar si null
            System.out.println("user es" + u.getLogin() + " " + u.getPass());
            if (validated) {
                request.getSession().setAttribute("login", "true");
                response.getWriter().print("SI");
            } else {
                request.getSession().setAttribute("login", "false");
                response.getWriter().print("NO");
            }
            //response.setContentType("text/html;charset=UTF-8");
        } catch (Exception ex) {
            Logger.getLogger(ServletLogin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getClaveCifrado(HttpServletRequest request) {
        int indexKey = 0, indexCompl = 0;
        ServicesPlayers sp = new ServicesPlayers();
        String keyHasheada = (String) request.getParameter("claveHasheada");
        String complementoHasheado = (String) request.getParameter("complementoHasheado");
        System.out.println("KeyHasheada " + keyHasheada);
        ClaveComplemento cc = (ClaveComplemento) request.getSession().getAttribute("keysComplements");
        boolean encontradaKey = false;
        boolean encontradoCompl = false;
        String paraCifrar = "", key = "", complemento = "";
        if (cc.getClaves() != null) {
            while (indexKey < cc.getClaves().size() && !encontradaKey) {
                key = cc.getClaves().get(indexKey);
                try {
                    if (PasswordHash.validatePassword(key, keyHasheada)) {
                        encontradaKey = true;
                    } else {
                        indexKey++;
                    }
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(ServletDB.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeySpecException ex) {
                    Logger.getLogger(ServletDB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (cc.getComplementos() != null) {
            while (indexCompl < cc.getComplementos().size() && !encontradoCompl) {
                complemento = cc.getComplementos().get(indexCompl);
                try {
                    if (PasswordHash.validatePassword(complemento, complementoHasheado)) {
                        encontradoCompl = true;
                    } else {
                        indexCompl++;
                    }
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(ServletDB.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeySpecException ex) {
                    Logger.getLogger(ServletDB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (encontradoCompl && encontradaKey) {
            paraCifrar = key + complemento;
        }
        return paraCifrar;
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
