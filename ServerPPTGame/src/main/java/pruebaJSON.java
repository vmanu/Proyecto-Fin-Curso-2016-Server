
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.datapptgame.GameType;
import com.mycompany.datapptgame.MetaMessage;
import com.mycompany.datapptgame.Player;
import com.mycompany.datapptgame.RoundsNumber;
import com.mycompany.datapptgame.TypeMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author IvPR
 */
public class pruebaJSON {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            MetaMessage mm=new MetaMessage();
            Player p=new Player("jfjbj", GameType.JUEGO3, RoundsNumber.TRES, true, 2);
            mm.setContent(p);
            mm.setType(TypeMessage.NOMBRE);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            System.out.println(mapper.writeValueAsString(mm));
        } catch (JsonProcessingException ex) {
            Logger.getLogger(pruebaJSON.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
