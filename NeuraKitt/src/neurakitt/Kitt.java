package neurakitt;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.AgentID;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alvaro
 * 
 * @author Alejandro
 * @FechaModificacion 01/11/2018
 * @Motivo eliminados imports inncesarios, corregidos algunos comenttarios y 
 * añadida la HU 4
 */
public class Kitt extends Agente {
    
    /**
     * Atributos de Kitt
     */
    private AgentID idServidor ;
    private String clave ;
    private float bateria ;
    
        
    /**
     * @author Alvaro
     * @param aid
     * @throws Exception 
     */
    public Kitt(AgentID aid) throws Exception {
        super(aid);
    }
    
    @Override
    /**
     * @author Alvaro
     * @author Alejandro
     */
    public void execute() {
        
        //System.out.println("Hola, soy Kitt");
        
        mensaje = login();  // Recibimos el mensaje al logearnos
        
        //System.out.println("respuesta = " + mensaje.get("trace").asString());
        System.out.println("Logeado correctamente");
        //System.out.println("Respuesta: "+ mensaje_respuesta.getContent());

        /* Si nos hemos logeado correctamente, guardamos la clave, recibimos del servidor la batería y determinamos la acción a llevar a cabo*/
        
        if ( !mensaje.get("result").asString().equals("BAD_MAP") && !mensaje.get("result").asString().equals("BAD_PROTOCOL") ) {
            clave = mensaje.get("result").asString();
            System.out.println("Clave almacenada correctamente");
            
            /* Escuchamos al servidor para recibir la batería */
            
            recibirMensaje();
            System.out.println("Recibimos bateria del servidor: " + mensaje_respuesta.getContent());
            bateria = mensaje.get("battery").asFloat() ;
            
            /* Escuchamos a neura para recibir la acción a realizar */
            
            recibirMensaje();
            System.out.println("Recibimos acción de neura: " + mensaje_respuesta.getContent());
                
            /* 
               Mientras que la acción a realizar no sea la de hacer logout, 
               determinamos qué acción es, la llevamos a cabo y volvemos a 
               escuchar a neura (TO DO - INCOMPLETO)
            */
            
            while ( !mensaje.get("accion").equals("logout") ) {

                mensaje = new JsonObject();
                /* Si no me queda batería hago refuel */
                
                if (bateria == 1) {
                    mensaje.add("command", "refuel");                                                          
                }
                
                /* En caso contrario le envío al servidor la acción proporcionada por NEURA */
                
                else {
                    String accion = mensaje.get("accion").asString() ;                    
                    mensaje.add("command", accion);                               
                }
                
                mensaje.add("key", clave);
                mensaje_respuesta.setContent(mensaje.asString());
                enviarMensaje(idServidor);
                
                /* Recibimos la respuesta del servidor */
                                        
                if (!mensaje.get("result").asString().equals("OK"))
                    System.err.println("Error al realizar la acción");
                           
                /* El servidor vuelve a enviar las percepciones por lo que tenemos que recibir la batería */
                recibirMensaje();
                System.out.println("Recibimos bateria del servidor: " + mensaje_respuesta.getContent());
                bateria = mensaje.get("battery").asFloat() ;
                
                /* Escuchamos de nuevo a neura */
                
                recibirMensaje(); 
                System.out.println("Recibimos acción de neura: " + mensaje_respuesta.getContent());
                
            }
  
            /* Si hemos recibido un logout como acción de neura se lo enviamos al servidor */
            logout();          
            
        }else{
            System.out.println("Logueado incorrectamente");
        }
           
        
    }
    
    /**
     * @author Alvaro
     */
    private JsonObject login() {
        
        idServidor = new AgentID("Girtab"); // el agente del servidor es Geminis o Girtab?
        
        /* Creamos el mensaje */
        
        mensaje = new JsonObject(); 
        mensaje.add("command", "login");
        mensaje.add("world", "map1");
        mensaje.add("battery", "kitt");
        mensaje.add("radar", "neura");
        mensaje.add("scanner", "neura");
        mensaje.add("gps", "neura");        
       
        enviarMensaje(idServidor);
                
        /* Recibimos la respuesta del servidor */
        
        recibirMensaje();
        System.out.println("Respuesta: "+ mensaje_respuesta.getContent());

        /*  Si la respuesta del servidor es la traza es porque nuestro agente estaba zombie.
            En ese caso mandamos un logout para que lo saque y hacemos login de nuevo.
            El segundo login nunca entrará al if por lo que la recursividad aparente está 
            controlada.
        */

        if (mensaje.get("trace").isTrue()) {
            //System.out.println("Llamamos a logout");
            logout();
            //System.out.println("Despues del logout");
            login();
        }
                
        return mensaje ;
        
    }
    
    /**
     * @author Alvaro
     */
    private void logout() {

        /* Creamos el mensaje */
        
        mensaje = new JsonObject();
        mensaje.add("command", "logout");
        mensaje.add("key", clave);
        
        enviarMensaje(idServidor);

        /* Recibimos la respuesta del servidor y si el resultado es OK guardamos la traza */
        
        try {
            mensaje_respuesta = this.receiveACLMessage();
            mensaje = Json.parse(mensaje_respuesta.getContent()).asObject();
            if (mensaje.get("result").asString().equals("OK")) {

                System.out.println("Recibiendo traza");
                JsonArray ja = mensaje.get("trace").asArray();
                byte data[] = new byte [ja.size()];
                for (int i = 0 ; i < data.length; i++) {
                    data[i] = (byte) ja.get(i).asInt();
                }
                FileOutputStream fos = new FileOutputStream("mitraza.png");
                fos.write(data);
                fos.close();

                System.out.println("Traza Guardada");
            }

        } catch (InterruptedException | IOException ex) {
            System.err.println("Error al recibir la respuesta o al crear la salida con la traza");
            Logger.getLogger(Kitt.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}