/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serverpptgame;

import ServletAware.ServletAwareConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mycompany.datapptgame.OpcionJuego;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.datapptgame.MetaMessage;
import com.mycompany.datapptgame.TypeMessage;
import com.mycompany.datapptgame.GameType;
import com.mycompany.modelpptgame.Partida;
import com.mycompany.datapptgame.Player;
import com.mycompany.datapptgame.Result;
import com.mycompany.datapptgame.RoundsNumber;
import controller.ControladorBD;
import dao.Dao;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.HandshakeResponse;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.core.Request;
import services.ServicesPlayers;

/**
 *
 * @author ivanp
 */
@ServerEndpoint(value = "/ppt", configurator = ServletAwareConfig.class)
public class ServerEndpointPPT {

    private static final long TIEMPO_ESPERA_MILLIS = 30000;
    private EndpointConfig config;
    private static final Logger log = Logger.getGlobal();//LoggerFactory.getLogger(ServerEndpointPPT.class);

    //<editor-fold defaultstate="collapsed" desc="METODOS WEBSOCKET">
    @OnOpen
    public void onOpen(Session s, EndpointConfig config) {
        this.config = config;
        Player p = new Player();
        p.setNumberOfRounds(RoundsNumber.NONE);
        p.setTipoJuego(GameType.NONE);
        p.setPlaying(false);
        p.setNamePlayer(s.getRequestParameterMap().get("user").get(0));
        s.getUserProperties().put("player", p);
        s.getUserProperties().put("escogido", false);
        System.out.println("OnOpen");
        System.out.println("player: " + p);
//        HttpServletRequest request=(HttpServletRequest)s.getUserProperties().get("request");
//        HttpServletResponse response=(HttpServletResponse)s.getUserProperties().get("response");
//        try {
//            request.getRequestDispatcher("/index.html").forward(request, response);
//        } catch (ServletException ex) {
//            Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }

    @OnClose
    public void onClose(Session s) {
        String seVa = ((Player) s.getUserProperties().get("player")).getNamePlayer();
        System.out.println("UNO QUE SE VA: " + seVa);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Partida partida = (Partida) s.getUserProperties().get("partida");
        System.out.println("Partida " + partida);
        if (partida != null) {
            int i = 0;
            if (partida.getJugadores().get(0).getNamePlayer().equals(seVa)) {
                i = 1;
            }
            System.out.println("i vale: " + i);
            cerrarPartidaPorDesconexion(s, partida.getJugadores().get(i).getNamePlayer(), mapper);
        }
    }

    @OnMessage
    public void echoText(String msg, Session s) {
        System.out.println("MSG es: " + msg);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            MetaMessage meta = mapper.readValue(msg, new TypeReference<MetaMessage>() {
            });
            Player p = (Player) s.getUserProperties().get("player");
            switch (meta.getType()) {
                case CONEXION:
                    Player recogida = mapper.readValue(mapper.writeValueAsString(meta.getContent()), new TypeReference<Player>() {
                    });
                    p.setNumberOfRounds(recogida.getNumberOfRounds());
                    p.setTipoJuego(recogida.getTipoJuego());
                    String user = (String) s.getUserProperties().get("user");
                    if (user == null || (user != null && !user.equals(recogida.getNamePlayer()))) {
                        System.out.println("Se mete porque nombre no coincide");
                        s.getUserProperties().put("user", recogida.getNamePlayer());
                    }
                    search(s, p, mapper);
                    break;
                case PARTIDA:
                    OpcionJuego opcion = mapper.readValue(mapper.writeValueAsString(meta.getContent()), new TypeReference<OpcionJuego>() {
                    });
                    System.out.println("opcionJuego: " + opcion.getOpcion() + " de: " + p.getNamePlayer());
                    if (opcion.getResult() != null && opcion.getResult() != Result.EMPATA) {
                        HttpSession httpSession = (HttpSession) config.getUserProperties().get("httpSession");
                        if (opcion.getResult() != Result.GANA) {
                            httpSession.setAttribute("player", p);
                        } else {
                            Partida partida = (Partida) s.getUserProperties().get("partida");
                            if ((partida.getJugadores().get(0).getNamePlayer() != p.getNamePlayer())) {
                                httpSession.setAttribute("player", partida.getJugadores().get(0).getNamePlayer());
                            } else {
                                httpSession.setAttribute("player", partida.getJugadores().get(1).getNamePlayer());
                            }
                        }
                        ServletDB sdb = new ServletDB();
                        HttpServletRequest request = ((HttpServletRequest) config.getUserProperties().get("request"));
                        request.setAttribute("op", "update");
                        HttpServletResponse response = (HttpServletResponse) config.getUserProperties().get("response");
                        sdb.processRequest(request, response);
                    }
                    enviarEleccion(p.getNamePlayer(), opcion, s, mapper, damePartida(s));
                    break;
                case DESCONEXION:
                    Partida partida = (Partida) s.getUserProperties().get("partida");
                    if (partida != null) {
                        int i = 0;
                        if (partida.getJugadores().get(0).getNamePlayer().equals(p.getNamePlayer())) {
                            i = 1;
                        }
                        cerrarPartidaPorDesconexion(s, partida.getJugadores().get(i).getNamePlayer(), mapper);
                    }
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServletException ex) {
            Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="METODOS FUNCIONALIDADES">
    public Partida damePartida(Session s) {
        return (Partida) s.getUserProperties().get("partida");
    }

    public void enviarEleccion(String nombre, OpcionJuego opcion, Session s, ObjectMapper mapper, Partida partida) {
        String nombreObjetivo;
        System.out.println("VEAMOS: " + nombre + " buscando y encuentra: " + partida);
        if (partida.getJugadores().get(0).getNamePlayer().equals(nombre)) {
            nombreObjetivo = partida.getJugadores().get(1).getNamePlayer();
        } else {
            nombreObjetivo = partida.getJugadores().get(0).getNamePlayer();
        }
        boolean sal = false;
        ArrayList<Session> sesiones = new ArrayList(s.getOpenSessions());
        for (int i = 0; i < sesiones.size() && !sal; i++) {
            String playerRevisado = ((Player) sesiones.get(i).getUserProperties().get("player")).getNamePlayer();
            if (playerRevisado.equals(nombreObjetivo)) {
                sal = true;
                MetaMessage mm = new MetaMessage();
                mm.setType(TypeMessage.RESPUESTA);
                mm.setContent(opcion);
                String mmString;
                try {
                    mmString = mapper.writeValueAsString(mm);
                    sesiones.get(i).getBasicRemote().sendText(mmString);
                    System.out.println("Ha entrado y enviado la respuesta");
                } catch (JsonProcessingException ex) {
                    Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void search(Session ses, Player n, ObjectMapper mapper) {
        Partida p = null;
        boolean sal = false;
        //ArrayList<Session> sessions=new ArrayList(ses.getOpenSessions());
        Iterator it = ses.getOpenSessions().iterator();
        long timeInicial = System.currentTimeMillis();
        while (!sal && (System.currentTimeMillis() - timeInicial < TIEMPO_ESPERA_MILLIS)) {
            Session sessions = null;
            if (!it.hasNext()) {
                it = ses.getOpenSessions().iterator();
            }
            sessions = (Session) it.next();
            Partida game = (Partida) sessions.getUserProperties().get("partida");
            if (compruebaSiNoNombreEnPartida(game, n.getNamePlayer())) {
                try {
                    /*if(i==sessions.size()){
                     i=0;
                     sessions=new ArrayList(ses.getOpenSessions());
                     }*/
                    Player player = (Player) sessions.getUserProperties().get("player");
                    if (encuentraPartida(player, n)&&!((boolean)ses.getUserProperties().get("escogido"))) {
                        ses.getUserProperties().put("escogido", true);
                        if (!(boolean) sessions.getUserProperties().get("escogido")) {
                            sal = true;
                            p = new Partida();
                            p.addPlayer(player);
                            p.addPlayer(n);
                            player.setPlaying(true);
                            n.setPlaying(true);
                            MetaMessage mm = new MetaMessage();
                            mm.setType(TypeMessage.NOMBRE);
                            mm.setContent(n.getNamePlayer());
                            String mmString = mapper.writeValueAsString(mm);
                            mm.setContent(player.getNamePlayer());
                            String mmString2 = mapper.writeValueAsString(mm);
                            sessions.getBasicRemote().sendText(mmString);
                            ses.getBasicRemote().sendText(mmString2);
                            ses.getUserProperties().put("partida", p);
                            ses.getUserProperties().put("player", n);
                            sessions.getUserProperties().put("player", player);
                            sessions.getUserProperties().put("partida", p);
                            sessions.getUserProperties().put("escogido",true);
                            System.out.println("SE HA UNIDO A LOS SIGUIENTES JUGADORES: " + player.getNamePlayer() + " y " + n.getNamePlayer());
                        } else {
                            ses.getUserProperties().put("escogido", false);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (!sal) {
            MetaMessage desc = new MetaMessage();
            desc.setType(TypeMessage.DESCONEXION);
            try {
                ses.getBasicRemote().sendText(mapper.writeValueAsString(desc));
            } catch (JsonProcessingException ex) {
                Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void cerrarPartidaPorDesconexion(Session s, String nombrePareja, ObjectMapper mapper) {
        boolean sal = false;
        System.out.println("EN CERRARPARTIDADESCON: " + nombrePareja);
        ArrayList<Session> sessions = new ArrayList(s.getOpenSessions());
        System.out.println("tamaño de sessions: " + sessions.size());
        for (int i = 0; i < sessions.size() && !sal; i++) {
            System.out.println("session[" + i + "]: " + ((Player) sessions.get(i).getUserProperties().get("player")).getNamePlayer());
            if (((Player) sessions.get(i).getUserProperties().get("player")).getNamePlayer().equals(nombrePareja)) {
                try {
                    MetaMessage mt = new MetaMessage();
                    mt.setType(TypeMessage.DESCONEXION);
                    sessions.get(i).getBasicRemote().sendText(mapper.writeValueAsString(mt));
                    sessions.get(i).getUserProperties().put("partida", null);
                    sal = true;

                } catch (IOException ex) {
                    Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CONDICIONES BÚSQUEDA PARTIDA">
    public boolean compruebaSiNoNombreEnPartida(Partida p, String name) {
        return p == null || !(p != null && (p.getJugadores().get(0).getNamePlayer().equals(name) || p.getJugadores().get(1).getNamePlayer().equals(name)));
    }

    public boolean comparaNombres(String n1, String n2) {
        return n1.equals(n2);
    }

    public boolean comparaRondas(RoundsNumber rn1, RoundsNumber rn2) {
        return rn1 == rn2 || rn1 == RoundsNumber.ANY || rn2 == RoundsNumber.ANY;
    }

    public boolean comparaDosAnyRounds(RoundsNumber rn1, RoundsNumber rn2) {
        return rn1 == RoundsNumber.ANY && rn2 == RoundsNumber.ANY;
    }

    public boolean comprobacionComunRounds(RoundsNumber rn1, RoundsNumber rn2) {
        return (comparaRondas(rn1, rn2) && !comparaDosAnyRounds(rn1, rn2));
    }

    public boolean comparaGameTypes(GameType gt1, GameType gt2) {
        return gt1 == gt2 || gt1 == GameType.ANY || gt2 == GameType.ANY;
    }

    public boolean comparaDosAnyGameTypes(GameType gt1, GameType gt2) {
        return gt1 == GameType.ANY && gt2 == GameType.ANY;
    }

    public boolean comprobacionComunGameTypes(GameType gt1, GameType gt2) {
        return (comparaGameTypes(gt1, gt2) && !comparaDosAnyGameTypes(gt1, gt2));
    }

    public boolean comprobarNone(Player player) {
        return player.getNumberOfRounds() != RoundsNumber.NONE && player.getTipoJuego() != GameType.NONE;
    }

    public boolean encuentraPartida(Player player, Player n) {
        return !comparaNombres(player.getNamePlayer(), n.getNamePlayer()) && comprobarNone(player) && comprobacionComunRounds(player.getNumberOfRounds(), n.getNumberOfRounds())
                && comprobacionComunGameTypes(player.getTipoJuego(), n.getTipoJuego()) && !player.isPlaying();
    }
    // </editor-fold>

}
