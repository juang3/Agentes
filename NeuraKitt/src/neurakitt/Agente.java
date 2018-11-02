package neurakitt;

import com.eclipsesource.json.Json;
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
    
    /**
     * @author: Germán, Alvaro
     * @return Devuelve éxito o no si se ha realizado corectamente 
     * 
     * @author Alejandro
     * @FechaModificacion 01/11/2018
     * @Motivo Las funciones y métodos deben ser siempre verbos. Cambio 
     * ReciboYDecodificoMensaje() por recibirMensaje()
     */
    protected boolean recibirMensaje(){
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
    
    /**
     * Crea un mensaje y se lo envía al destinatario que recibe como parámetro
     * 
     * @author: Germán, Alvaro
     * @param destinatario 
     * 
     * @author Alejandro
     * @FechaModificacion 01/11/2018
     * @Motivo Las funciones y métodos deben ser siempre verbos. Cambio 
     * DecodificoYEnvioMensaje() por enviarMensaje(). Cambiado valor de retorno 
     * a void
     */
    protected void enviarMensaje(AgentID destinatario){
        mensaje_salida = new ACLMessage();              // Limpia
        mensaje_salida.setSender(this.getAid());        // Emisor (objeto que invoca)
        mensaje_salida.setReceiver(destinatario);       // Receptor (obejto dado como parámetro)
        mensaje_salida.setContent(mensaje.toString());  // Contenido del mensaje
        this.send(mensaje_salida);                      // Enviando el mensaje.
    }
    
    /**
     * @author Germán
     * @return Devuelve true si se ha ignorado corretamente el mensaje.
     */
    protected boolean ignorarMensaje() {
        try {
            mensaje_respuesta = this.receiveACLMessage();
            System.out.println("Mensaje ignorado: "+ mensaje_respuesta.toString());
            return true;    
        } 
        catch (InterruptedException ex) {
            System.err.println(this.getName() + " Error en la recepción del mensaje. ");
            System.out.println("No se ha podido ignorar el mensaje: "+ mensaje_respuesta.toString());
            return false;
        }
        
    }
    
}
