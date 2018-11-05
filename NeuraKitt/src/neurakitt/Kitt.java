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
 * @Motivo eliminados imports inncesarios, corregidos algunos comentarios y 
 *  añadida la HU 4
 */
public class Kitt extends Agente {
    
    /**
     * Atributos de KITT
     */
    private final AgentID idServidor;
    private final String nombreNeura;
    private String mapa;
    private String clave;
    private float bateria;
    private String accion;
    
        
    /**
     * @author Alvaro
     * @param aid
     * @throws Exception 
     * 
     * @author Alejandro
     * @FechaModificacion 04/11/2018
     * @Motivo inicialización de variables en constructor en lugar de en la 
     * definición.
     */
    public Kitt(AgentID aidKitt, AgentID aidNeura, AgentID aidServidor, String mapa) throws Exception {
        super(aidKitt);
        
        nombreNeura = aidNeura.getLocalName();
        idServidor = aidServidor;
        this.mapa = mapa;
        
        clave = "";
        bateria = -1;
    }
    
    
    @Override
    /**
     * @author Alvaro
     * @author Alejandro
     */
    public void execute() {
        login();  // Enviamos mensaje de logueo e informamos de la respuesta recibida.
        System.out.println("Realizado login.");
  
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
                // accion = "logout";       // Ya en si, la accion es logout
                // break;
                //logout();
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
        
//        mensaje = login();  // Recibimos el mensaje al logearnos
//        
//        //System.out.println("respuesta = " + mensaje.get("trace").toString());
//        System.out.println("Logeado correctamente");
//        //System.out.println("Respuesta: "+ mensaje_respuesta.getContent());
//
//        /* Si nos hemos logeado correctamente, guardamos la clave, recibimos del servidor la batería y determinamos la acción a llevar a cabo*/
//        
//        if ( !mensaje.get("result").toString().equals("BAD_MAP") && !mensaje.get("result").toString().equals("BAD_PROTOCOL") ) {
//            clave = mensaje.get("result").toString();
//            System.out.println("Clave almacenada correctamente");
//            
//            /* Escuchamos al servidor para recibir la batería */
//            
//            recibirMensaje();
//            System.out.println("Recibimos bateria del servidor: " + mensaje_respuesta.getContent());
//            bateria = mensaje.get("battery").asFloat() ;
//            
//            /* Escuchamos a neura para recibir la acción a realizar */
//            
//            recibirMensaje();
//            System.out.println("Recibimos acción de neura: " + mensaje_respuesta.getContent());
//                
//            /* 
//               Mientras que la acción a realizar no sea la de hacer logout, 
//               determinamos qué acción es, la llevamos a cabo y volvemos a 
//               escuchar a neura (TO DO - INCOMPLETO)
//            */
//            
//            while ( !mensaje.get("accion").equals("logout") ) {
//
//                mensaje = new JsonObject();
//                /* Si no me queda batería hago refuel */
//                
//                if (bateria == 1) {
//                    mensaje.add("command", "refuel");                                                          
//                }
//                
//                /* En caso contrario le envío al servidor la acción proporcionada por NEURA */
//                
//                else {
//                    String accion = mensaje.get("accion").toString() ;                    
//                    mensaje.add("command", accion);                               
//                }
//                
//                mensaje.add("key", clave);
//                mensaje_respuesta.setContent(mensaje.toString());
//                enviarMensaje(idServidor);
//                
//                /* Recibimos la respuesta del servidor */
//                                        
//                if (!mensaje.get("result").toString().equals("OK"))
//                    System.err.println("Error al realizar la acción");
//                           
//                /* El servidor vuelve a enviar las percepciones por lo que tenemos que recibir la batería */
//                recibirMensaje();
//                System.out.println("Recibimos bateria del servidor: " + mensaje_respuesta.getContent());
//                bateria = mensaje.get("battery").asFloat() ;
//                
//                /* Escuchamos de nuevo a neura */
//                
//                recibirMensaje(); 
//                System.out.println("Recibimos acción de neura: " + mensaje_respuesta.getContent());
//                
//            }
//  
//            /* Si hemos recibido un logout como acción de neura se lo enviamos al servidor */
//            logout();          
//            
//        }else{
//            System.out.println("Logueado incorrectamente");
//        }
    
    
    
    /**
     * @author Alvaro
     * 
     * @author Alejandro
     * @FechaModificacion 04/11/2018
     * @Motivo manejo de conexiones anteriores mal acabadas. Migración del 
     *  almacenamiento de la clave de sesión desde el método execute() a aquí.
     *  Cambiado método a void
     */
    private boolean login() {
        System.out.println("En el login");
        
        /* Creamos el mensaje */
        mensaje = new JsonObject(); 
        mensaje.add("command"   ,"login");
        mensaje.add("world"     ,mapa);
        mensaje.add("battery"   ,this.getAid().getLocalName());
        mensaje.add("radar"     ,nombreNeura);
        mensaje.add("scanner"   ,nombreNeura);
        mensaje.add("gps"       ,nombreNeura);         

         
        System.out.println("Enviado: " + mensaje.toString());
        enviarMensaje(idServidor);
                
        /* Recibimos la respuesta del servidor */
        recibirMensaje();

        /** Las posibles respuesas son: trace, password, BAD_MAP, BAD_PROTOCOL
         * TRACE:           Ocurre cuando interrumpimos la comunicación abruptamente con el servidor
         * BAD_MAP:         Cuando escribimos mal nombre del mapa al que queremos loguearnos
         * BAD_PROTOCOL:    Cuando escribimos mal el formato del Json.
         */
        if (mensaje.toString().contains("trace")){
           /**
            * En lugar de hacer logout y login para manejar sesiones anteriores mal
            * acabadas (lo que implica más mensajes encolados y atrasados), simple-
            * mente ignoro la traza anterior y vuelvo a escuchar al servidor,
            * recibiendo la clave de la sesión actual y almacenandola.
            */
            System.out.println("Traza en el login. Reiniciando...");
            recibirMensaje();
            System.out.println("Respuesta: " + mensaje.toString());
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
        System.out.println("Clave: " + clave);
        
        return true;
    }
    
    
    /**
     * @author Alvaro
     * 
     * @author Alejandro
     * @FechaModificacion 04/11/2018
     * @Motivo corregido error en la recepción de la traza
     */


/*
    private void logout() {

        System.out.println("En el logout");
        
        // Creamos el mensaje
        mensaje = new JsonObject();
        mensaje.add("command", "logout");
        mensaje.add("key", clave);
        
        System.out.println("Enviado: " + mensaje.toString());
        enviarMensaje(idServidor);

        // Recibimos la respuesta del servidor y si el resultado es OK guardamos la traza 
        
        recibirMensaje();
        System.out.println("Respuesta: " + mensaje.toString());
        
        if (mensaje.get("result").asString() == "OK") {
            
            recibirMensaje();
            System.out.println("Traza: " + mensaje.toString());

            
            try {
                JsonArray ja = mensaje.get("trace").asArray();
                byte data[] = new byte [ja.size()];
                
                for(int i=0 ; i<data.length; i++)
                    data[i] = (byte) ja.get(i).asInt();
                
                FileOutputStream fos = new FileOutputStream("./mitraza.png");
                fos.write(data);
                fos.close();
                System.out.println("Traza Guardada");

            } catch (IOException ex) {
                System.err.println("Error procesando traza");
                Logger.getLogger(Kitt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
*/
    /**
     * @author Alvaro
     */
    private void getTraza() {
        try {
            /* Recibimos la respuesta del servidor */
//            mensaje_respuesta = this.receiveACLMessage();
//            mensaje = Json.parse(mensaje_respuesta.getContent()).asObject();
            recibirMensaje();
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
        } catch (IOException ex) {
            System.err.println("Error al recibir la respuesta o al crear la salida con la traza");
            Logger.getLogger(Kitt.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}