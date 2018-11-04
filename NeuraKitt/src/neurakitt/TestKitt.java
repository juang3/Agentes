/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author Alvaro, Germán
 */
public class TestKitt extends Agente {
    
    
    private AgentID idServidor ;
    private String nombreNeura;
    private String mapa;
    private String clave ;
    private float bateria ;
    private String accion;
    
    
    /**
     * @author Alvaro
     * @param aid
     * @throws Exception 
     */
    public TestKitt(AgentID aid, AgentID neura, String mapa) throws Exception {
        super(aid);
        idServidor = new AgentID("Girtab");
        nombreNeura = neura.getLocalName();
        this.mapa = mapa;
        accion = "";
        
        System.out.println("Agente Kitt creandose");
    }
    
    @Override
    /**
     * @author Alvaro, German
     */
    public void execute() {
        login(mapa);  // Enviamos mensaje de logueo e informamos de la respuesta recibida.
  
        /* Escuchamos a neura para recibir la acción a realizar */
        while (!"logout".equals(accion)){
            
            /* Escuchamos al servidor para recibir la batería */
            recibirMensaje();
            bateria = mensaje.get("battery").asFloat() ;
            System.out.println("[KITT] Recibimos bateria del servidor: "+ mensaje.toString());
            
            /* Escuchamos la decisión de Neura */
            recibirMensaje();
            accion = mensaje.get("accion").asString();
            
            System.out.println("[KITT] Neura me ha enviado: "+ mensaje.toString());
            System.out.println("[KITT] Neura me ha enviado: "+ accion);
            
            
            /*  Habiendo escuchado a ambos.
             * Al servidor para saber la batería.
             * A Neura para saber la acción a realizar.
             * Kitt decide si realizar la acción o realizar refuel.
             */
            if("logout".equals(accion)){
                System.out.println("[KITT] Neura ha detectado que hemos llegado al destino ");
                // accion = "logout";
                // break;
            }
            /* No hemos llegado al destino, decido si refuel o accion de Neura */
            else if (bateria == 1.0) {
                accion = "refuel";
                System.out.println("[KITT] Se decide hacer refuel (nv. de bateria: "+ bateria +")");
            }
            /* Lo último es realizar la acción que Neura propone */
            else {                    
                System.out.println("[KITT] Se va a realizar la acción que Neura propone: "+ accion);
            }
            
            // Creando el mensaje a enviar al servidor
            mensaje = new JsonObject();
            mensaje.add("command", accion); 
            mensaje.add("key", clave);
                
            // Se envia el mensaje al servidor
            enviarMensaje(idServidor);
                
            /**
             * @Observación:
             *  Enviado el mensaje, ahora debemos esperar a recibir respuesta del servidor.
             *  Que puede ser:
             *      OK:             Mensaje recibido correctamente.
             *      CRASHED:        El agente se ha estrellado.
             *      BAD_COMMAND:    El Commando enviado es desconocido.
             *      BAD_PROTOCOL:   El formato Json no es el correcto.
             *      BAD_KEY:        NO se ha incluido la clave correctamente.
             */
            
            recibirMensaje();
            /* Recibimos la respuesta del servidor */
            if     (mensaje.toString().contains("OK"))
                System.out.println("Mensaje enviado correctamente ");
            else if(mensaje.toString().contains("BAD_KEY"))
                System.out.println("Mensaje enviado con clave erronea ");
            else if(mensaje.toString().contains("BAD_COMMAND"))
                System.out.println("Mensaje enviado con comando desconocido ");
            else if(mensaje.toString().contains("BAD_PROTOCOL"))
                System.out.println("Mensaje enviado con formato Json incorrecto ");
            else if(mensaje.toString().contains("CRASHED"))
                System.out.println("Mensaje enviado pero agente estrellado ");
            else{
                System.out.println("Respuesta desconocida: "+ mensaje.toString());
            }    
        }
        
        getTraza();
    }
    
    /**
     * @author Alvaro, Juan Germán
     */
    private boolean login(String mapa) {
        
        
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
            /* Preferentemente prefiero ignorar el mensaje */
            
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
    private void getTraza() {
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

