/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package constantes;

/**
 *
 * @author Victor
 */
public class ConstantesBaseDatos {
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String USERNAME="servidor";
    public static final String PASSWORD="server_PPTG@me";
    public static final String HOST="jdbc:mysql://node7553-servidorpptgame.jelastic.cloudhosted.es/";
    public static final String DBNAME="PPTGAME";
    public static final String SELECT_ADD_VICTORIES="SELECT won FROM DATA_PLAYER dp, LOGIN log where dp.ID_PLAYER=log.ID_PLAYER and log.LOGIN=?";
    public static final String SELECT_ADD_ROUNDS="SELECT played FROM DATA_PLAYER dp, LOGIN log where dp.ID_PLAYER=log.ID_PLAYER and log.LOGIN=?";
    public static final String SELECT_GET_PLAYERS="SELECT * FROM DATA_PLAYER dp, LOGIN log where dp.ID_PLAYER=log.ID_PLAYER";
    public static final String SELECT_INSERT_PLAYERS="select login from LOGIN";
    public static final String SELECT_GET_BY_VICTORIES="SELECT * FROM DATA_PLAYER dp, LOGIN log where dp.ID_PLAYER=log.ID_PLAYER order by won DESC LIMIT 10";
    public static final String SELECT_GET_BY_PLAYED="SELECT * FROM DATA_PLAYER dp, LOGIN log where dp.ID_PLAYER=log.ID_PLAYER order by played DESC LIMIT 10";
    public static final String SELECT_GET_BY_AVERAGE="SELECT * FROM DATA_PLAYER dp, LOGIN log where dp.ID_PLAYER=log.ID_PLAYER order by (won/played) DESC LIMIT 10";
    public static final String SELECT_GET_USER_BY_LOGIN="SELECT * FROM LOGIN WHERE login=?";
    public static final String SELECT_CLAVES_SECURITY="SELECT * FROM POSIBLES_CLAVES";
    public static final String SELECT_COMPLEMENTOS_SECURITY="SELECT * FROM POSIBLES_COMPLEMENTO";
    public static final String DELETE_FOR_ROLLBACK="DELETE FROM `data_player` WHERE `data_player`.`ID_PLAYER` = ?";
    public static final String ALTER_TABLE="ALTER TABLE `data_player` AUTO_INCREMENT=?";
    public static final String UPDATE_ADD_VICTORIES="UPDATE DATA_PLAYER dp, LOGIN log set won=? where dp.ID_PLAYER=log.id_player and log.LOGIN=?";
    public static final String UPDATE_ADD_ROUNDS="UPDATE DATA_PLAYER dp, LOGIN log set played=? where dp.ID_PLAYER=log.id_player and log.LOGIN=?";
    public static final String INSERT_DATA_PLAYER="INSERT into DATA_PLAYER (won, played, coins) values(0,0,0)";
    public static final String INSERT_PLAYER="insert into LOGIN(login,pass,id_player) values (?,?,?)";
}
