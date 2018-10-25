/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neurakitt;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author Alvaro
 */
public class NeuraKitt {

    /**
     * @author Alvaro
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        AgentsConnection.connect("Girtab", 5672, "Girtab", "Geminis", "France", false);
        Agente neura = new Agente(new AgentID("neura"));
        Agente kitt = new Agente(new AgentID("kitt"));
        neura.start();
        kitt.start();
    }
    
}
