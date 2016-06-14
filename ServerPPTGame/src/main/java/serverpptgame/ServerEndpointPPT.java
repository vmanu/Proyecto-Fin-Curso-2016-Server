/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverpptgame;

import ServletAware.ServletAwareConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mycompany.datapptgame.OpcionJuego;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.datapptgame.MetaMessage;
import com.mycompany.datapptgame.TypeMessage;
import com.mycompany.datapptgame.GameType;
import modelo.Partida;
import com.mycompany.datapptgame.Player;
import com.mycompany.datapptgame.Result;
import com.mycompany.datapptgame.RoundsNumber;
import static constantes.ConstantesConexion.RUTA_WEBSOCKET;
import static constantes.ConstantesServer.*;
import static constantes.conexion.ConstantesConexion.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import services.ServicesPlayers;

/**
 * Endpoint del Websocket
 *
 * @author Victor e Ivan
 */
@ServerEndpoint(value = RUTA_WEBSOCKET, configurator = ServletAwareConfig.class)
public class ServerEndpointPPT {

    private static final long TIEMPO_ESPERA_MILLIS = 30000;
    private EndpointConfig config;

    //<editor-fold defaultstate="collapsed" desc="METODOS WEBSOCKET">
    /**
     * Gestiona la apertura de una nueva sesion (conexion)
     *
     * @param s
     * @param config
     */
    @OnOpen
    public void onOpen(Session s, EndpointConfig config) {
        this.config = config;
        Player p = new Player();
        p.setNumberOfRounds(RoundsNumber.NONE);
        p.setTipoJuego(GameType.NONE);
        p.setPlaying(false);
        p.setNamePlayer(s.getRequestParameterMap().get(USER).get(0));
        s.getUserProperties().put(PLAYER, p);
        s.getUserProperties().put(ESCOGIDO, false);
    }

    /**
     * Gestiona el cierre de una session y la comunicacion con todos aquellos a
     * los que estuviera unido mediante la partida
     *
     * @param s
     */
    @OnClose
    public void onClose(Session s) {
        String seVa = ((Player) s.getUserProperties().get(PLAYER)).getNamePlayer();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Partida partida = (Partida) s.getUserProperties().get(PARTIDA);
        if (partida != null) {
            int i = 0;
            if (partida.getJugadores().get(0).getNamePlayer().equals(seVa)) {
                i = 1;
            }
            cerrarPartidaPorDesconexion(s, partida.getJugadores().get(i).getNamePlayer(), mapper);
        }
    }

    /**
     * Gestiona la recepcion del mensaje, evaluando primero el tipo de mensaje y
     * en base a ello ejecutando los mensajes respectivos
     *
     * @param msg
     * @param s
     */
    @OnMessage
    public void echoText(String msg, Session s) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            MetaMessage meta = mapper.readValue(msg, new TypeReference<MetaMessage>() {
            });
            Player p = (Player) s.getUserProperties().get(PLAYER);
            switch (meta.getType()) {
                case CONEXION:
                    Player recogida = mapper.readValue(mapper.writeValueAsString(meta.getContent()), new TypeReference<Player>() {
                    });
                    p.setNumberOfRounds(recogida.getNumberOfRounds());
                    p.setTipoJuego(recogida.getTipoJuego());
                    String user = (String) s.getUserProperties().get(USER);
                    if (user == null || (user != null && !user.equals(recogida.getNamePlayer()))) {
                        s.getUserProperties().put(USER, recogida.getNamePlayer());
                    }
                    search(s, p, mapper);
                    break;
                case PARTIDA:
                    OpcionJuego opcion = mapper.readValue(mapper.writeValueAsString(meta.getContent()), new TypeReference<OpcionJuego>() {
                    });
                    if (opcion.getResult() != null && opcion.getResult() != Result.EMPATA) {
                        ServicesPlayers dbController = new ServicesPlayers();
                        Partida partida = (Partida) s.getUserProperties().get(PARTIDA);
                        if (partida != null) {
                            String nombreRival;
                            if ((!partida.getJugadores().get(0).getNamePlayer().equals(p.getNamePlayer()))) {
                                nombreRival = partida.getJugadores().get(0).getNamePlayer();
                            } else {
                                nombreRival = partida.getJugadores().get(1).getNamePlayer();
                            }
                            dbController.addRounds(p.getNamePlayer());
                            dbController.addRounds(nombreRival);
                            if (opcion.getResult() == Result.GANA) {
                                dbController.addVictories(p.getNamePlayer());
                            } else {
                                dbController.addVictories(nombreRival);
                            }
                        }
                    }
                    enviarEleccion(p.getNamePlayer(), opcion, s, mapper, damePartida(s));
                    break;
                case DESCONEXION:
                    Partida partida = (Partida) s.getUserProperties().get(PARTIDA);
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
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="METODOS FUNCIONALIDADES">
    /**
     * Metodo que obtiene la partida guardada en una determinada session
     *
     * @param s
     * @return
     */
    public Partida damePartida(Session s) {
        return (Partida) s.getUserProperties().get(PARTIDA);
    }

    /**
     * Metodo que se encarga de enviar un mensaje al emparejado del usuario
     * emisor, obteniendo a la pareja a traves del objeto Partida
     *
     * @param nombre
     * @param opcion
     * @param s
     * @param mapper
     * @param partida
     */
    public void enviarEleccion(String nombre, OpcionJuego opcion, Session s, ObjectMapper mapper, Partida partida) {
        String nombreObjetivo;
        if (partida != null) {
            if (partida.getJugadores().get(0).getNamePlayer().equals(nombre)) {
                nombreObjetivo = partida.getJugadores().get(1).getNamePlayer();
            } else {
                nombreObjetivo = partida.getJugadores().get(0).getNamePlayer();
            }
            boolean sal = false;
            ArrayList<Session> sesiones = new ArrayList(s.getOpenSessions());
            for (int i = 0; i < sesiones.size() && !sal; i++) {
                String playerRevisado = ((Player) sesiones.get(i).getUserProperties().get(PLAYER)).getNamePlayer();
                if (playerRevisado.equals(nombreObjetivo)) {
                    sal = true;
                    MetaMessage mm = new MetaMessage();
                    mm.setType(TypeMessage.RESPUESTA);
                    mm.setContent(opcion);
                    String mmString;
                    try {
                        mmString = mapper.writeValueAsString(mm);
                        sesiones.get(i).getBasicRemote().sendText(mmString);
                    } catch (JsonProcessingException ex) {
                        Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    /**
     * Busca una pareja de partida al usuario a quien perteneza la session,
     * cumpliendo unas normas de emparejamiento
     *
     * @param ses
     * @param n
     * @param mapper
     */
    public void search(Session ses, Player n, ObjectMapper mapper) {
        Partida p = null;
        boolean sal = false;
        Iterator it = ses.getOpenSessions().iterator();
        long timeInicial = System.currentTimeMillis();
        while (!sal && (System.currentTimeMillis() - timeInicial < TIEMPO_ESPERA_MILLIS)) {
            Session sessions = null;
            if (!it.hasNext()) {
                it = ses.getOpenSessions().iterator();
            }
            sessions = (Session) it.next();
            Partida game = (Partida) sessions.getUserProperties().get(PARTIDA);
            if (compruebaSiNoNombreEnPartida(game, n.getNamePlayer())) {
                try {
                    Player player = (Player) sessions.getUserProperties().get(PLAYER);
                    if (encuentraPartida(player, n) && !((boolean) ses.getUserProperties().get("escogido"))) {
                        ses.getUserProperties().put("escogido", true);
                        if (!(boolean) sessions.getUserProperties().get("escogido")) {
                            sessions.getUserProperties().put("escogido", true);
                            if (n.getNumberOfRounds() == RoundsNumber.ANY) {
                                boolean ambosRandoms = false;
                                if (comparaDosAnyGameTypes(player.getTipoJuego(), n.getTipoJuego())) {
                                    //AMBOS SON RANDOMS
                                    n.setNumberOfRounds(RoundsNumber.values()[((int) (Math.random() * 3))]);
                                    n.setTipoJuego(GameType.values()[((int) (Math.random() * 3))]);
                                    ambosRandoms = true;
                                } else {
                                    //SOLO n ES RANDOM
                                    n.setNumberOfRounds(player.getNumberOfRounds());
                                    n.setTipoJuego(player.getTipoJuego());
                                }
                                MetaMessage mm = new MetaMessage();
                                mm.setType(TypeMessage.CONFIGURACION);
                                mm.setContent(n);
                                String mmString = mapper.writeValueAsString(mm);
                                ses.getBasicRemote().sendText(mmString);
                                if (ambosRandoms) {
                                    sessions.getBasicRemote().sendText(mmString);
                                }
                            } else if (player.getNumberOfRounds() == RoundsNumber.ANY) {
                                //SOLO player ES RANDOM
                                player.setNumberOfRounds(n.getNumberOfRounds());
                                player.setTipoJuego(n.getTipoJuego());
                                MetaMessage mm = new MetaMessage();
                                mm.setType(TypeMessage.CONFIGURACION);
                                mm.setContent(n);
                                String mmString = mapper.writeValueAsString(mm);
                                sessions.getBasicRemote().sendText(mmString);
                            }
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
                            ses.getUserProperties().put(PARTIDA, p);
                            ses.getUserProperties().put(PLAYER, n);
                            sessions.getUserProperties().put(PLAYER, player);
                            sessions.getUserProperties().put(PARTIDA, p);
                        } else {
                            ses.getUserProperties().put(ESCOGIDO, false);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                sal = true;
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

    /**
     * Gestiona el envio del mensaje correspondiente a la pareja del usuario
     * cuya session ha sido desconectada
     *
     * @param s
     * @param nombrePareja
     * @param mapper
     */
    public void cerrarPartidaPorDesconexion(Session s, String nombrePareja, ObjectMapper mapper) {
        boolean sal = false;
        ArrayList<Session> sessions = new ArrayList(s.getOpenSessions());
        for (int i = 0; i < sessions.size() && !sal; i++) {
            if (((Player) sessions.get(i).getUserProperties().get(PLAYER)).getNamePlayer().equals(nombrePareja)) {
                try {
                    MetaMessage mt = new MetaMessage();
                    mt.setType(TypeMessage.DESCONEXION);
                    sessions.get(i).getBasicRemote().sendText(mapper.writeValueAsString(mt));
                    sessions.get(i).getUserProperties().put(PARTIDA, null);
                    sal = true;
                } catch (IOException ex) {
                    Logger.getLogger(ServerEndpointPPT.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CONDICIONES BÚSQUEDA PARTIDA">
    /**
     * Evalua si un determinado nombre está entre los jugadores (Player)
     * pertenecientes a una determinada Partida
     *
     * @param p
     * @param name
     * @return
     */
    public boolean compruebaSiNoNombreEnPartida(Partida p, String name) {
        return p == null || !(p != null && (p.getJugadores().get(0).getNamePlayer().equals(name) || p.getJugadores().get(1).getNamePlayer().equals(name)));
    }

    /**
     * Compara si los dos String recibidos son iguales o no
     *
     * @param n1
     * @param n2
     * @return
     */
    public boolean comparaNombres(String n1, String n2) {
        return n1.equals(n2);
    }

    /**
     * Compara si los dos Enums son iguales o si alguno de los dos es ANY
     *
     * @param rn1
     * @param rn2
     * @return
     */
    public boolean comparaRondas(RoundsNumber rn1, RoundsNumber rn2) {
        return rn1 == rn2 || rn1 == RoundsNumber.ANY || rn2 == RoundsNumber.ANY;
    }

    /**
     * Compara si los dos enums son ANY
     *
     * @param rn1
     * @param rn2
     * @return
     */
    public boolean comparaDosAnyRounds(RoundsNumber rn1, RoundsNumber rn2) {
        return rn1 == RoundsNumber.ANY && rn2 == RoundsNumber.ANY;
    }

    /**
     * Realiza la comprobación de los valores de los Enums para comprobar si se
     * ajustan entre si
     *
     * @param rn1
     * @param rn2
     * @return
     */
    public boolean comprobacionComunRounds(RoundsNumber rn1, RoundsNumber rn2) {
        return (comparaRondas(rn1, rn2));
    }

    /**
     * Compara si los dos Enums son iguales o si alguno de los dos es ANY
     *
     * @param gt1
     * @param gt2
     * @return
     */
    public boolean comparaGameTypes(GameType gt1, GameType gt2) {
        return gt1 == gt2 || gt1 == GameType.ANY || gt2 == GameType.ANY;
    }

    /**
     * Compara si los dos enums son ANY
     *
     * @param gt1
     * @param gt2
     * @return
     */
    public boolean comparaDosAnyGameTypes(GameType gt1, GameType gt2) {
        return gt1 == GameType.ANY && gt2 == GameType.ANY;
    }

    /**
     * Realiza la comprobación de los valores de los Enums para comprobar si se
     * ajustan entre si
     *
     * @param gt1
     * @param gt2
     * @return
     */
    public boolean comprobacionComunGameTypes(GameType gt1, GameType gt2) {
        return comparaGameTypes(gt1, gt2);
    }

    /**
     * Comprueba si es un Player que NO está buscando juego aún
     *
     * @param player
     * @return
     */
    public boolean comprobarNone(Player player) {
        return player.getNumberOfRounds() != RoundsNumber.NONE && player.getTipoJuego() != GameType.NONE;
    }

    /**
     * Conjunto de condiciones para cumplir los requisitos de interconexion
     * entre dos jugadores
     *
     * @param player
     * @param n
     * @return
     */
    public boolean encuentraPartida(Player player, Player n) {
        return player != null && (!comparaNombres(player.getNamePlayer(), n.getNamePlayer()) && comprobarNone(player) && comprobacionComunRounds(player.getNumberOfRounds(), n.getNumberOfRounds())
                && comprobacionComunGameTypes(player.getTipoJuego(), n.getTipoJuego()) && !player.isPlaying());
    }
    // </editor-fold>

}
