/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neurakitt;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alvaro, Germán
 */
public class TestKitt extends Agente {
    
    
    private AgentID idServidor ;
    private String nombreNeura;
    private String mapa;
    private String clave ;
    private float bateria ;
    
    
    /**
     * @author Alvaro
     * @param aid
     * @throws Exception 
     */
    public TestKitt(AgentID aid, AgentID neura, String mapa) throws Exception {
        super(aid);
        nombreNeura = neura.getLocalName();
        this.mapa = mapa;
        
    }
    
    @Override
    /**
     * @author Alvaro
     */
    public void execute() {
        
        //System.out.println("Hola, soy Kitt");
        
        mensaje = login(mapa);  // Recibimos el mensaje al logearnos

        /* Si nos hemos logeado correctamente, guardamos la clave, recibimos del servidor la batería y determinamos la acción a llevar a cabo*/
        
        if ( !mensaje.get("result").asString().equals("BAD_MAP") && !mensaje.get("result").asString().equals("BAD_PROTOCOL") ) {
            clave = mensaje.get("result").asString();
            System.out.println("Clave almacenada correctamente");
            
            /* Escuchamos al servidor para recibir la batería */
            
            recibirMensaje();
            System.out.println("Recibimos bateria del servidor: "+ mensaje_respuesta.getContent());
            bateria = mensaje.get("battery").asFloat() ;
            
            /* Escuchamos a neura para recibir la acción a realizar */

            recibirMensaje();
                
            /* Mientras que la acción a realizar no sea la de hacer logout, determinamos qué acción es, la llevamos a cabo 
               y volvemos a escuchar a neura (TO DO - INCOMPLETO) */
            
            while ( !mensaje.get("accion").equals("logout") ) {

                /* Si no me queda batería hago refuel */
                
                if (bateria == 1) {
               
                    /* Le enviamos al servidor el mensaje refuel */
                    /* Creamos el mensaje */

                    mensaje = new JsonObject();
                    mensaje.add("command", "refuel");
                    mensaje.add("key", clave);
                    mensaje_respuesta.setContent(mensaje.asString());
            
                    enviarMensaje(idServidor);
                                        
                    /* Recibimos la respuesta del servidor */
                    
                    recibirMensaje();
                    
                    if (!mensaje.get("result").asString().equals("OK"))
                        System.err.println("Error al hacer refuel");
                  
                }
                
                /* Realizo la acción que me diga neura */
                
                else {

                    // RELLENAR AQUÍ
                    
                }
                
                /* Escuchamos de nuevo a neura */
                
                recibirMensaje();
                
            }
  
            /* Si hemos recibido un logout como acción de neura se lo enviamos al servidor */
            logout();          
            
        }else{
            System.out.println("MAL");
        }
           
        
    }
    
    /**
     * @author Alvaro
     */
    private JsonObject login(String mapa) {
        
        idServidor = new AgentID("Girtab"); // el agente del servidor es Geminis o Girtab?
        
        /* Creamos el mensaje */
        
        mensaje = new JsonObject(); 
        mensaje.add("command"   ,"login");
        mensaje.add("world"     ,mapa);
        mensaje.add("battery"   ,this.getAid().getLocalName());
        mensaje.add("radar"     ,nombreNeura);
        mensaje.add("scanner"   ,nombreNeura);
        mensaje.add("gps"       ,nombreNeura);        
        
        enviarMensaje(idServidor);
        
        /* El método anterior ya envia el mensaje */
        // this.send(mensaje_salida); // lo enviamos                   
        
        /* Recibimos la respuesta del servidor */
        recibirMensaje();
        System.out.println("Respuesta: "+ mensaje_respuesta.getContent());

        
//        if (mensaje.get("trace").isTrue()) {
        if (mensaje.toString().contains("trace")){
            /* Hacemos logout */
            System.out.println("Llamamos a logout");
            // logout();
            System.out.println("Despues del logout");
            
            /* Preferentemente prefiero ignorar el mensaje */
            ignorarMensaje();
            
        }
        
        recibirMensaje();
        System.out.println("Respuesta: "+ mensaje_respuesta.getContent());
        
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
        // mensaje_salida.setContent(mensaje.asString());
        
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

