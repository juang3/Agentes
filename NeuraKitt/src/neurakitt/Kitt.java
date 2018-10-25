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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alvaro
 */
public class Kitt extends Agente {
    
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
     */
    public void execute() {
        
        /* Enviamos mensaje de login al servidor */
        
        ACLMessage outbox = new ACLMessage(); // mensaje a enviar
        outbox.setSender(this.getAid()); // establecemos emisor
        AgentID idServidor = new AgentID("Geminis"); // el agente del servidor es Geminis o Girtab?
        outbox.setReceiver(idServidor); // establecemos destinatario
        
        /* Creamos el mensaje */
        
        JsonObject mensaje = new JsonObject(); 
        mensaje.add("command", "login");
        mensaje.add("world", "map1");
        mensaje.add("radar", "neura");
        mensaje.add("battery", "kitt");
        mensaje.add("gps", "neura");        
        outbox.setContent(mensaje.asString());
        
        this.send(outbox); // lo enviamos
        
        /* Recibimos la respuesta del servidor */
        
        ACLMessage respuesta = null;
        try {
            respuesta = this.receiveACLMessage();
        } catch (InterruptedException ex) {
            System.err.println("Error al recibir la respuesta");
            Logger.getLogger(Kitt.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /* Si nos hemos logeado correctamente, guardamos la clave y escuchamos a neura con la siguiente acción a realizar */
        
        mensaje = Json.parse(respuesta.getContent()).asObject();
        if ( !mensaje.get("result").asString().equals("BAD_MAP") && !mensaje.get("result").asString().equals("BAD_PROTOCOL") ) {
            String clave = mensaje.get("result").asString();    
            try {
                /* Escuchamos a neura para que nos diga la acción a realizar */
                respuesta = this.receiveACLMessage();
            } catch (InterruptedException ex) {
                System.err.println("Error al recibir la respuesta");
                Logger.getLogger(Kitt.class.getName()).log(Level.SEVERE, null, ex);
            }
            mensaje = Json.parse(respuesta.getContent()).asObject();
                
            /* Mientras que la acción a realizar no sea la de hacer logout, determinamos qué acción es, la llevamos a cabo 
               y volvemos a escuchar a neura (TO DO - INCOMPLETO) */
            
            while ( !mensaje.get("accion").equals("logout") ) {

                // RELLENAR AQUÍ
                    
                try {
                    respuesta = this.receiveACLMessage(); // Escuchamos de nuevo a neura
                } catch (InterruptedException ex) {
                    System.err.println("Error al recibir la respuesta");
                    Logger.getLogger(Kitt.class.getName()).log(Level.SEVERE, null, ex);
                }
                mensaje = Json.parse(respuesta.getContent()).asObject();
                    
            }
  
            /* Si hemos recibido un logout como acción de neura se lo enviamos al servidor */
            
            outbox = new ACLMessage();
            outbox.setSender(this.getAid());
            outbox.setReceiver(idServidor); // establecemos destinatario

            /* Creamos el mensaje */
            
            mensaje = new JsonObject();
            mensaje.add("command", "logout");
            mensaje.add("key", clave);
            outbox.setContent(mensaje.asString());

            this.send(outbox); // lo enviamos

            /* Recibimos la respuesta del servidor y si el resultado es OK guardamos la traza */
            try {
                respuesta = this.receiveACLMessage();
                mensaje = Json.parse(respuesta.getContent()).asObject();
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
    
}
