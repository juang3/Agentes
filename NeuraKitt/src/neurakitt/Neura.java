/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neurakitt;

import es.upv.dsic.gti_ia.core.AgentID;

/**
 *
 * @author Alvaro
 */
public class Neura extends Agente {
    
    /**
     * @author Alvaro
     * @param aid
     * @throws Exception 
     */
    public Neura(AgentID aid, AgentID idKitt) throws Exception {
        super(aid);
    }
    
    @Override
    /**
     * @author Alvaro
     */
    public void execute(){
        System.out.println("Hola, soy Neura");
    }
    
}
