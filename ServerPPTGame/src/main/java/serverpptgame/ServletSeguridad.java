/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverpptgame;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.SeguridadDAO;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mycompany.datapptgame.ClaveComplemento;
import static constantes.ConstantesServer.*;

/**
 *
 * @author dam2
 */
@WebServlet(name = "ControllerSeguridad", urlPatterns = {"/seguridad"})
public class ServletSeguridad extends HttpServlet {

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
        SeguridadDAO dao = new SeguridadDAO();
        //CORRECCION: AQUI FALTARIA CONTROLAR QUE EL USUARIO HA HECHO LOGIN
        ArrayList<String> complementos = (ArrayList<String>) request.getSession().getAttribute("compls");
        ArrayList<String> keys = (ArrayList<String>) request.getSession().getAttribute("keys");
        ArrayList<String> keysBD = dao.getClaves();
        ArrayList<String> complementosBD = dao.getComplementos();
        System.out.println("COMPLEMENTOS BD " + complementosBD);
        if (keys == null || keys.size() == 0 || complementos == null || complementos.size() == 0) {
            if (keys == null) {
                keys = new ArrayList<>();
            }
            if (complementos == null) {
                complementos = new ArrayList<>();
            }

            for (int i = 0; i < 5; i++) {
                boolean buscandoKey = true;
                boolean buscandoCompl = true;
                while (buscandoKey) {
                    int randomKeys = (int) (Math.random() * (keysBD.size()));
                    if (!keys.contains(keysBD.get(randomKeys))) {
                        keys.add(keysBD.get(randomKeys));
                        buscandoKey = false;
                    }
                }
                while (buscandoCompl) {
                    int randomComplementos = (int) (Math.random() * (complementosBD.size()));
                    if (!complementos.contains(complementosBD.get(randomComplementos))) {
                        complementos.add(complementosBD.get(randomComplementos));
                        System.out.println("complementos added " + complementos);
                        buscandoCompl = false;
                    }
                }
            }
            System.out.println("CLAVES " + keys);
            System.out.println("COMPLEMENTOS " + complementos);
            ClaveComplemento cc = new ClaveComplemento();
            cc.setClaves(keys);
            cc.setComplementos(complementos);
            request.getSession().setAttribute("keysComplements", cc);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String vuelta = mapper.writeValueAsString(cc);
            response.getWriter().print(vuelta);
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
