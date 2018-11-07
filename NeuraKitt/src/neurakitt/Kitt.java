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
    private final String NEURA;
    private String clave;
    private float bateria;
    private String mapa;
    private String dir_traza;
    private String accion;
    
        
    /**
     * @author Alvaro
     * @param aid
     * @param neura
     * @param mapa
     * @throws Exception 
     * 
     * @author Alejandro
     * @FechaModificacion 04/11/2018
     * @Motivo inicialización de variables en constructor en lugar de en la 
     * definición.
     * 
     * @author Germán
     * @FechaModificacion 05/11/2018
     * @Motivo parametrización del constructor con el nombre del agente NUERA 
     *  y el nombre del mapa a explorar.
     */
    public Kitt(AgentID aid, String neura, String mapa) throws Exception {
        super(aid);
        
        idServidor = new AgentID("Girtab");
        clave = "";
        bateria = 0;
        this.mapa = mapa;
        this.NEURA = neura;
        dir_traza = "./trazas/";
        accion = "";
    }
    
    
    
    private void test() {
        for(int i=0; i<15; i++){
            mensaje = new JsonObject();
            mensaje.add("command", "moveSW");
            mensaje.add("key", clave);
            enviarMensaje(idServidor);
            
            recibirMensaje();
            System.out.println("Respuesta: " + mensaje.toString());
            
            
            if(mensaje.get("result").asString().contains("OK")) {
                recibirMensaje();
                System.out.println("Batería: " + mensaje.get("battery").asFloat());
                bateria = mensaje.get("battery").asFloat() ;
            }
            else {
                System.out.println("");
            }
        }
    }
    
    
    @Override
    /**
     * Comportamiento del agente NEURA
     * 
     * inciarSesion();
     * 
     * MIENTRAS <accion != logout>
     *      recibirMensajes(servidor);
     *      recibirMensajes(NEURA);
     *      if (bateríaBaja)
     *         hacerRefuel();
     *      else
     *         hacerAccionDadaPorNeura();
     *      enviarMensaje(al servidor);
     *      recibirMensaje(servidor)        // Comprobación {OK, BAD...}
     *          
     * cerrarSesion();
     * 
     * @author Alvaro
     * @author Alejandro
     * 
     * 
     * 
     */
    public void execute() {
        login();
        System.out.println("[KITT] Iniciada sesión.");
        
        while(!accion.equals("logout")) {
            recibirMensaje();
            System.out.println("[KITT] Batería: " + mensaje.get("battery").asFloat());
            bateria = mensaje.get("battery").asFloat() ;
            
            recibirMensaje();
            System.out.println("[KITT] Mensaje recibido: " + mensaje.toString());
            if(bateria > 1f)
                accion = mensaje.get("accion").asString();
            else
                accion = "refuel";
            
            mensaje = new JsonObject();
            mensaje.add("command", accion);
            mensaje.add("key", clave);
            enviarMensaje(idServidor);
            System.out.println("[KITT] Mensaje enviado: " + mensaje.toString());
            recibirMensaje();
        }
        
        logout();
        System.out.println("[KITT] Sesión cerrada.");
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
        mensaje = new JsonObject();
        mensaje.add("command",  "login");
        mensaje.add("world",    mapa);
        mensaje.add("battery",  this.getAid().getLocalName());
        mensaje.add("scanner",  NEURA);
        mensaje.add("radar",    NEURA);
        mensaje.add("gps",      NEURA);        
         
        System.out.println("[LOGIN] Enviado: " + mensaje.toString());
        enviarMensaje(idServidor);
                
        recibirMensaje();
        System.out.println("[LOGIN] Respuesta: " + mensaje.toString());

        /**
         * En lugar de hacer logout y login para manejar sesiones anteriores mal
         * acabadas (lo que implica más mensajes encolados y atrasados), simple-
         * mente ignoro la traza anterior y vuelvo a escuchar al servidor,
         * recibiendo la clave de la sesión actual y almacenándola.
         * 
         * Álex
         */
        if (mensaje.toString().contains("trace")) {
            System.out.println("[WARNING] Traza en el login. Reiniciando...");
            recibirMensaje();
            System.out.println("[LOGIN] Respuesta: " + mensaje.toString());
        }
        
        clave = mensaje.get("result").asString();
        System.out.println("[LOGIN] Clave: " + clave);
    }
    
    
    /**
     * @author Alvaro
     * 
     * @author Alejandro
     * @FechaModificacion 04/11/2018
     * @Motivo corregido error en la recepción de la traza
     */
    private void logout() {
//        mensaje = new JsonObject();
//        mensaje.add("command", "logout");
//        mensaje.add("key", clave);
//        
//        System.out.println("[LOGOUT] Enviado: " + mensaje.toString());
//        enviarMensaje(idServidor);

        /* Recibimos la respuesta del servidor y si el resultado es OK guardamos 
           la traza */
        
        recibirMensaje();
        System.out.println("[LOGOUT] Respuesta: " + mensaje.toString());
        
        if (mensaje.get("result").asString().contains("OK")) {
//            recibirMensaje();
            recibirMensaje();
            System.out.println("[LOGOUT] Traza: " + mensaje.toString());
            
            try {
                JsonArray ja = mensaje.get("trace").asArray();
                byte data[] = new byte [ja.size()];
                
                for(int i=0 ; i<data.length; i++)
                    data[i] = (byte) ja.get(i).asInt();
                
                String ruta = dir_traza + mapa + ".png";
                FileOutputStream fos = new FileOutputStream(ruta);
                fos.write(data);
                fos.close();
                System.out.println("[LOGOUT] Traza Guardada");

            } catch (IOException ex) {
                System.err.println("[LOGOUT] Error procesando traza");
                Logger.getLogger(Kitt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}