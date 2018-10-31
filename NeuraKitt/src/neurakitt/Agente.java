/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neurakitt;

import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 *
 * @author Alvaro
 */
public class Agente extends SingleAgent {
    //Evita creación de objetos Json durante la ejecución del agente 
    protected JsonObject mensaje;
    
    //Evita crear ACLMessage durante la ejecución del agente, reutiliza objeto. 
    protected ACLMessage mensaje_respuesta;
    protected ACLMessage mensaje_salida;
    
    
    /**
     * 
     * @author Alvaro
     * @param aid
     * @throws Exception
     */
    public Agente(AgentID aid) throws Exception {
        super(aid);
    }
    
    @Override
    /**
     * @author Alvaro
     */
    public void execute() {
        
        
        
        
    }
    
    void DecodificarMensaje(JsonObject sensor){
        
    }
    
}
