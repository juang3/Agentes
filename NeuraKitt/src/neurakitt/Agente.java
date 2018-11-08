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
    
    //Control de iteraciones
    int iteracionActual;
    int iteracionesTope;
    int antiguedad;
        
    /**
     * 
     * @author Alvaro
     * @param aID
     * @throws Exception
     * 
     * @author Alejandro
     * @FechaModificacion 04/11/2018
     * @Motivo añadida inicialización de variables
     */
    public Agente(AgentID aID) throws Exception {
        super(aID);
        
        mensaje = new JsonObject();
        mensaje_respuesta = new ACLMessage();
        mensaje_salida = new ACLMessage();
        
        System.out.println("Agente "+this.getAid().getLocalName()+" creado ");
        
        iteracionesTope = 0;
        iteracionActual =0;
        antiguedad =0;
        
    }
    
    
    @Override
    /**
     * @author Alvaro
     */
    public void execute() {
        /* VOID - to be OVERRIDE */
    }
    
    
    /**
     * recibirMensaje encapsula la rutina de drecibir y dar formato al contenido
     * guardándolo en la variable mensaje.
     * también muestra por pantalla información relevante para el seguimiento 
     * de la comunicación entre los agentes.
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
//            System.out.println("Recibiendo mensaje");
            mensaje_respuesta = this.receiveACLMessage();
            System.out.println("["+mensaje_respuesta.getReceiver().getLocalName()+"]"
                    +"\t mensaje recibido "+"de "+mensaje_respuesta.getSender().getLocalName()
                    +"\t\t contenido: "+ mensaje_respuesta.getContent());
        }
        catch(InterruptedException ex){
            System.err.println(this.getName() + " Error en la recepción del mensaje. ");
            return false;
        }
        
        mensaje = Json.parse(mensaje_respuesta.getContent()).asObject();
        return true;
    }
    
    
    /**
     * enviarMensaje encapsula la rutina de dar formato a la variable 
     * mensaje_salida con el contenido de "mensaje",
     * añadir el remitente y el destinatario
     * también muestra por pantalla información relevante para el seguimiento 
     * de la comunicación entre los agentes.
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
        mensaje_salida.setReceiver(destinatario);       // Receptor (objeto dado como parámetro)
        mensaje_salida.setContent(mensaje.toString());  // Contenido del mensaje
        this.send(mensaje_salida);                      // Enviando el mensaje.
        
        System.out.println("["+mensaje_salida.getSender().getLocalName()
                +"]\t mensaje enviado a "
                + mensaje_salida.getReceiver().getLocalName()
                + "\t\t contenido: "+ mensaje_salida.getContent());
    }
}
