/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import com.mycompany.datapptgame.ClaveComplemento;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import objetos_seguridad.PasswordHash;
import serverpptgame.ServletDB;

/**
 *
 * @author Victor
 */
public class Utilidades {
    public static String getClaveCifrado(HttpServletRequest request) {
        int indexKey = 0, indexCompl = 0;
        String keyHasheada = (String) request.getParameter("claveHasheada");
        String complementoHasheado = (String) request.getParameter("complementoHasheado");
        ClaveComplemento cc = (ClaveComplemento) request.getSession().getAttribute("keysComplements");
        boolean encontradaKey = false;
        boolean encontradoCompl = false;
        System.out.println("recibimos en clavecifrado: "+keyHasheada+" . . "+complementoHasheado+" . . "+cc);
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
        System.out.println("PARA CIFRAR ES: "+paraCifrar);
        return paraCifrar;
    }
}
