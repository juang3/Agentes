package neurakitt;

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
    private String clave;
    private float bateria;
    
        
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
    public Kitt(AgentID aid) throws Exception {
        super(aid);
        
        idServidor = new AgentID("Girtab");
        clave = "";
        bateria = 0;
    }
    
    
    @Override
    /**
     * @author Alvaro
     * @author Alejandro
     */
    public void execute() {
        
        System.out.println("Hola, soy KITT");
        
        login();
        System.out.println("Realizado login.");
        
        recibirMensaje();
        System.out.println("Batería: " + mensaje.get("battery").asFloat());
        bateria = mensaje.get("battery").asFloat() ;
        
        logout();
        System.out.println("Realizado logout.");

        
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
    }
    
    
    /**
     * @author Alvaro
     * 
     * @author Alejandro
     * @FechaModificacion 04/11/2018
     * @Motivo manejo de conexiones anteriores mal acabadas. Migración del 
     *  almacenamiento de la clave de sesión desde el método execute() a aquí.
     *  Cambiado método a void
     */
    private void login() {
        System.out.println("En el login");
        
        /* Creamos el mensaje */
        mensaje = new JsonObject();
        mensaje.add("command",  "login");
        mensaje.add("world",    "map1");
        mensaje.add("battery",  "KITT");
        // mensaje.add("radar",    "neura");
        // mensaje.add("scanner",  "neura");
        // mensaje.add("gps",      "neura");        

         
        System.out.println("Enviado: " + mensaje.toString());
        enviarMensaje(idServidor);
                
        /* Recibimos la respuesta del servidor */
        recibirMensaje();
        System.out.println("Respuesta: " + mensaje.toString());

        /**
         * En lugar de hacer logout y login para manejar sesiones anteriores mal
         * acabadas (lo que implica más mensajes encolados y atrasados), simple-
         * mente ignoro la traza anterior y vuelvo a escuchar al servidor,
         * recibiendo la clave de la sesión actual y almacenandola.
         */
        if (mensaje.toString().contains("trace")) {
            System.out.println("Traza en el login. Reiniciando...");
            recibirMensaje();
            System.out.println("Respuesta: " + mensaje.toString());
        }
        
        clave = mensaje.get("result").asString();
        System.out.println("Clave: " + clave);
    }
    
    
    /**
     * @author Alvaro
     * 
     * @author Alejandro
     * @FechaModificacion 04/11/2018
     * @Motivo corregido error en la recepción de la traza
     */
    private void logout() {

        System.out.println("En el logout");
        
        /* Creamos el mensaje */
        mensaje = new JsonObject();
        mensaje.add("command", "logout");
        mensaje.add("key", clave);
        
        System.out.println("Enviado: " + mensaje.toString());
        enviarMensaje(idServidor);

        /* Recibimos la respuesta del servidor y si el resultado es OK guardamos la traza */
        
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

}