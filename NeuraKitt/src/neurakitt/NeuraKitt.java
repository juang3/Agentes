package neurakitt;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alvaro
 */
public class NeuraKitt {

    /**
     * @author Alvaro, Juan Germán
     * @param args the command line arguments
     * 
     * @author Alejandro
     * @FechaModificacion 01/11/2018
     * @Motivo añadida traza en caso de excepción
     */
    public static void main(String[] args) {
        Logger.global.setLevel(Level.OFF);
        
        try {
            // Identificadores de los agentes
            AgentID idKITT  = new AgentID("KITT_B");
            AgentID idNEURA = new AgentID("NEURA_B");
            
            // Mapa a explorar
            String mapa = "map1";
            
            // Creando conexión con el servidor
            AgentsConnection.connect("isg2.ugr.es", 6000, "Girtab", "Geminis",
                                      "France", false);
            System.out.println("Conectado a isg2.ugr.es");
            
            
            Kitt KITT   = new Kitt(idKITT, idNEURA.getLocalName(), mapa);
            System.out.println("Agente KITT creado");
            Neura NEURA = new Neura(idNEURA, idKITT);
            System.out.println("Agente NEURA creado");
            
            // Comenzar actividad.
            NEURA.start();
            KITT.start();
            
        } catch (Exception ex) {
            System.out.println("Excepción " + ex.toString());
            Logger.getLogger(NeuraKitt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
