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
            AgentID idKitt  = new AgentID("KITT19");
            AgentID idNeura = new AgentID("NEURA9");
            
//            AgentID idServidor = new AgentID("Girtab");
            String mapa = "map11";
            boolean anilloExterior = false;
            int tope = 5000;
//            int tope = Integer.MAX_VALUE;
//            int tiempoDeOlvido = 300;
            int tiempoDeOlvido = Integer.MAX_VALUE;


            // Creando conexión con el servidor
            AgentsConnection.connect("isg2.ugr.es", 6000, "Girtab", "Geminis", "France", false);
            System.out.println("Conectado a isg2.ugr.es");
            
            
            TestKitt test_kitt = new TestKitt(idKitt, idNeura, mapa, anilloExterior, tiempoDeOlvido);
            //System.out.println("Agente kitt creado");
            
            
            System.out.println("Agentes en pie");

            //Kitt KITT = new Kitt(idKitt, idNeura, idServidor, mapa);
//            System.out.println("Agente KITT creado");
            Neura NEURA = new Neura(idNeura, idKitt, anilloExterior, tope, tiempoDeOlvido);
//            System.out.println("Agente NEURA creado");
            
            // Comenzar actividad.
            System.out.println("Despertando a los agentes Kitt y Neura");
            NEURA.start();
            test_kitt.start();
            //KITT.start();

            
        } catch (Exception ex) {
            System.out.println("Excepción " + ex.toString());
            Logger.getLogger(NeuraKitt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
