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
        try {
            // Identificadores de los agentes

            AgentID idKitt  = new AgentID("KITT000000");
            AgentID idNeura = new AgentID("NEURA000000");
            
            // Creando conexión con el servidor
            System.out.println("Creando conexión");
            AgentsConnection.connect("isg2.ugr.es", 6000, "Girtab", "Geminis", "France", false);
            System.out.println("Conexión creada");
            
            
            Neura neura = new Neura(idNeura, idKitt);
            System.out.println("\nAgente neura creado");
            
            Kitt kitt = new Kitt(idKitt);
            //TestKitt test_kitt = new TestKitt(idKitt, idNeura, "map1");
            //Kitt kitt = new Kitt(idKitt); 
            System.out.println("Agente kitt creado");
            
            System.out.println("Despertando a los agentes Kitt y Neura");
            neura.start();
            kitt.start();
            //test_kitt.start();
            System.out.println("Agentes en pie");
            
        } catch (Exception ex) {
            System.out.println("Excepción " + ex.toString());
            Logger.getLogger(NeuraKitt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
