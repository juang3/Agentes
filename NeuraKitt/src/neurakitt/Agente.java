/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neurakitt;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

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
    
    boolean ReciboYDecodificoMensaje(){
        try{
            mensaje_respuesta = this.receiveACLMessage();
        }
        catch(InterruptedException ex){
            System.err.println(this.getName() + " Error en la recepción del mensaje. ");
            return false;

        }
        
        mensaje = Json.parse(mensaje_respuesta.getContent()).asObject();
        return true;
    }
    
    // Evita que el agente realice constantemente la codificación de los mensajes.
    void CoficicoYEnvioMensaje(AgentID destinatario){
        mensaje_salida = new ACLMessage();          // Limpia
        mensaje_salida.setSender(this.getAid());    // Emisor
        mensaje_salida.setReceiver(destinatario);   // Receptor
        mensaje_salida.setContent(mensaje.toString()); // Contenido del mensaje
        this.send(mensaje_salida);                  // Enviando el mensaje.
    }
    
}
