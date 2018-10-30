/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
     * @author Alvaro, Juan Germán, 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            
            System.out.println("Creando conexión");
            AgentsConnection.connect("isg2.ugr.es", 6000, "Girtab", "Geminis", "France", false);
            System.out.println("Conxión creada");
            
            Neura neura = new Neura(new AgentID("neura"));
            System.out.println("\nAgente neura creado");
            
            Kitt kitt = new Kitt(new AgentID("kitt"));
            System.out.println("Agente kitt creado");
            
            System.out.println("Despertando a los agentes Kitt y Neura");
            neura.start();
            kitt.start();
            System.out.println("Agentes en pie");
        } catch (Exception ex) {
            System.out.println("FALLA AQUI");
            Logger.getLogger(NeuraKitt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
