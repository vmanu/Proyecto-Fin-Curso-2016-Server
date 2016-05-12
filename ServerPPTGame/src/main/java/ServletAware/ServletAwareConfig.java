/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServletAware;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Clase que gestiona la conexion entre servlet y websocket
 * @author Victor e Ivan
 */
public class ServletAwareConfig extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        config.getUserProperties().put("httpSession", httpSession);
        config.getUserProperties().put("request", request);
        config.getUserProperties().put("response", response);
        /*
        if(httpSession.getAttribute("login")!=null){
            config.getUserProperties().put("login", true);
            config.getUserProperties().put("player", true);
        }
        */
    }
}
