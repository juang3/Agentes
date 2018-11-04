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
        
        System.out.println("Agente Kitt creandose");
        
    }
    
    @Override
    /**
     * @author Alvaro
     */
    public void execute() {
        
        login(mapa);  // Enviamos mensaje de logueo e informamos de la respuesta recibida.

        /* Si nos hemos logeado correctamente, guardamos la clave, recibimos del servidor la batería y determinamos la acción a llevar a cabo*/
        
        if ( !mensaje.get("result").asString().equals("BAD_MAP") && !mensaje.get("result").asString().equals("BAD_PROTOCOL") ) {
            
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
                    String accion = mensaje.get("accion").asString() ;                    
                    mensaje.add("command", accion);                               
                }
                
                mensaje.add("key", clave);
//                mensaje_respuesta.setContent(mensaje.asString());
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
            System.out.println("MAL");
        }
           
        
    }
    
    /**
     * @author Alvaro, Juan Germán
     */
    private boolean login(String mapa) {
        
        idServidor = new AgentID("Girtab");
        
        /* Creamos el mensaje */
        mensaje = new JsonObject(); 
        mensaje.add("command"   ,"login");
        mensaje.add("world"     ,mapa);
        mensaje.add("battery"   ,this.getAid().getLocalName());
        mensaje.add("radar"     ,nombreNeura);
        mensaje.add("scanner"   ,nombreNeura);
        mensaje.add("gps"       ,nombreNeura);        
        
        /* Enviamos el mensaje */
        enviarMensaje(idServidor);
        System.out.println("[KITT] El mensaje enviado al servidor es: "+ mensaje.toString());                 
        
        /* Recibimos la respuesta del servidor */
        recibirMensaje();
        System.out.println("[KITT] Respuesta del servidor por el login: "+ mensaje.toString());

        /** Las posibles respuesas son: trace, password, BAD_MAP, BAD_PROTOCOL
         * TRACE:           Ocurre cuando interrumpimos la comunicación abruptamente con el servidor
         * BAD_MAP:         Cuando escribimos mal nombre del mapa al que queremos loguearnos
         * BAD_PROTOCOL:    Cuando escribimos mal el formato del Json.
         */
        if (mensaje.toString().contains("trace")){
            
            /* Preferentemente prefiero ignorar el mensaje
             * y volver a escuchar al servidor para recibir la clave
             */
            recibirMensaje();
            System.out.println("[KITT] La nueva respuesta es: " + mensaje.toString());
            
        }
        /* Comprobando si el mensaje recibido es BAD_MAP e informando de ello */
        else if(mensaje.get("result").asString().contains("BAD_MAP")){
            System.out.println("[KITT] Se ha escrito mal el nombre del mapa "+ mapa);
            return false;
        }
        /* Comprobando si el mensaje recibido es BAD_PROTOCOL e informa de ello */
        else if(mensaje.get("result").asString().contains("BAD_PROTOCOL")){
            System.out.println("[KITT] Se ha escrito mal el mensaje a enviar:\n" + mensaje.toString());
            return false;
        }
        
        /* En este momento las repuestas BAD_* han sido descartadas, por tanto 
         * la clave se ha recibido.
         */
        System.out.println("En este momento he recibido la clave como respuesta ");
        clave = mensaje.get("result").asString();
        return true;
        
    }
    
    /**
     * @author Alvaro
     */
    private void logout() {

        /* Creamos el mensaje */
        mensaje = new JsonObject();
        mensaje.add("command", "logout");
        mensaje.add("key", clave);
        
        /* Enviamos mensaje al servidor */
        System.out.println("Mensaje logout enviado al servidor: "+ mensaje.toString());
        enviarMensaje(idServidor);

        try {
            /* Recibimos la respuesta del servidor */
            mensaje_respuesta = this.receiveACLMessage();
            mensaje = Json.parse(mensaje_respuesta.getContent()).asObject();
            System.out.println("Mensaje recibido, del servidor, tras el logout: " + mensaje.toString());
            
            /* Cuando la respuesta es OK, guardamos la traza */
            if (mensaje.get("result").toString().contains("OK")) {
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

