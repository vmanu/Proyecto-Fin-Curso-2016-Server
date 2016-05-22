/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.datapptgame.ClaveComplemento;
import java.io.IOException;
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
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String keyHasheada = (String) request.getParameter("claveHasheada");
        String complementoHasheado = (String) request.getParameter("complementoHasheado");
        ClaveComplemento cc = (ClaveComplemento) request.getSession().getAttribute("keysComplements");
        System.out.println("mapper: "+mapper+" o request: "+request);
        System.out.println(" o el parameter "+request.getParameter("claveComplemento"));
        if(cc==null){
            try {
                cc=mapper.readValue(request.getParameter("claveComplemento"), new TypeReference<ClaveComplemento>() {});
            } catch (IOException ex) {
                Logger.getLogger(Utilidades.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
                } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
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
                } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
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
