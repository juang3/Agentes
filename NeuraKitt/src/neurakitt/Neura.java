package neurakitt;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;

/**
 * @author Alvaro
 * 
 * @author Alejandro
 * @FechaModificación: 01/11/2018
 * @Motivo: java permite el carácter Ñ sin problema
 */
public class Neura extends Agente {
    /**
     * Atributos iniciales
     * 
     * @auhor Alvaro
     * 
     * @author Alejandro
     * @FechaModificación 01/11/2018
     * @Motivo variables final. Cambio los atributos a privados ya que son
     * propios del agente. Realizar el acceso mediante métodos get, si procede.
     * 
     * He creado un ENUM con las posibles acciones del agente en lugar de 
     * almacenarlas en un vector.
     * 
     * @FechaModificación 03/11/2018
     * @Motivo inclusión de la nueva clase Casilla para la gestión de la memoria
     * del agente, que sustituye a la matriz GPS[ int [3] ].
     */
    
    // Alcance de los sensores. 5x5
    private final int TAM_ENTORNO  = 25;
    private final int NUM_SENSORES = 3;
    private final int TAM_RADANNER = 9;
    // Destinados a reconocer el mapa.    
    private ArrayList<Float>    scanner;
    private ArrayList<Integer>  radar;
    private ArrayList<Float>    radanner;
    // Destinados a memoria    
    private ArrayList<Casilla> memoria;
    // private int gps[]   = new int[3];                   // [x, y, nº veces]
    
    // Destinados al radanner
    private ArrayList<String> movimientos;
    private String accion;


    // Destinados al movimiento  
    /**************************************************************************/
    // private Accion miAccion;                 A USAR MÁS TARDE
    /**************************************************************************/
        
    // Identificador del agente KITT
    private final AgentID idKITT;    
    

    /** CONSTRUCTOR
     * 
     * @author  Alvaro, Germán
     * @param   aID       identificador de Neura
     * @param   idKITT    identificador de receptor de los mensajes de Neura
     * @throws  Exception 
     * 
     * 
     * @author  Alejandro
     * @FechaModificación 31/11/2018
     * @Motivo  inclusión de la clase Casilla. Movidas TODAS las inicializaciones
     *  al constructor, en lugar de en la definición de los miembros.
     */
    public Neura(AgentID aID, AgentID idKITT) throws Exception {
        super(aID);
        
        scanner         = new ArrayList(TAM_ENTORNO);
        radar           = new ArrayList(TAM_ENTORNO);
        radanner        = new ArrayList(TAM_RADANNER);
        memoria         = new ArrayList();
        
       /**
        * Contiene la información del scanner y del radar,
        * reducida a los 8 movimientos.
        */
        movimientos     = new ArrayList(TAM_RADANNER);
        accion          = "";
        this.idKITT     = idKITT;
        
        
        movimientos.add("moveNW");
        movimientos.add("moveN");
        movimientos.add("moveNE");
        movimientos.add("moveW");
        movimientos.add("logout");
        movimientos.add("moveE");
        movimientos.add("moveSW");
        movimientos.add("moveS");
        movimientos.add("moveSE");
        
        for(int i=0; i<TAM_RADANNER; i++)
            radanner.add(Float.POSITIVE_INFINITY); 
    }
    
    
    @Override
    /**
     * Comportamiento del agente NEURA
     * 
     * MIENTRAS <condición de parada>
     *      recibirMensajes(servidor)
     *      procesarMensaje(extraer GPS, RADAR, SCANNER del JSON)
     *      procesarInformación()
     *      decidirAcción()
     *      enviarAcción(a KITT)
     * 
     * 
     * @author Alejandro
     * @FechaModificación 01/11/2018
     * 
     * @HU 5.2 y 5.3
     */
    public void execute(){   
        // while (miAccion != Accion.logout)
        while (accion != "logout"){
            // procesarMensaje();       GERMÁN
            actualizarSensores();
            procesarInformacion();
            getSensores();
            
            accion = getAccion();
            mensaje = new JsonObject();
            mensaje.add("accion", accion);
            enviarMensaje(idKITT);
            
            System.out.println(" [NEURA] identificador de Kitt: "+ idKITT.toString());
            System.out.println(" [NEURA] Accion a realizar: " + accion);
            System.out.println(" [NEURA] Contenido del mensaje: "+ mensaje.toString());
            System.out.println(" [NEURA] Mensaje enviado a Kitt: "+ mensaje_salida.getContent());
        }
        
    }
    
    
    /** Funciones Auxiliares ****************************************/ 
    
    /**
     * @author: Germán
     * Reflexión.
     * Previos:
     *  El radar representa la información del entorno con 0,1,2 (libre, obstáculo, 
     *  destino)
     *  El scanner representa la información mediante un número real que simboliza
     *  la distancia al destino.
     * 
     *  Si quiero extraer la información de ambos, debo mirar a ambos, ello me 
     *  implica trabajar con ambos vectores simultaneamente, lo que es un foco 
     *  de posibles errores.
     *  
     *  Por ello he considerado unificar la información en un solo vector,
     *  multiplicando ambos vectores componente a componente.
     *  Tal como están ahora mismo el producto resultante es un vector que muestra
     *  los obstáculos con información irrelevante, además de perderse la ubicación
     *  del destino.
     * 
     *  Por tanto, considero cambiar los valores:
     *      0, libre,       a  1
     *      1, obstáculo,   a  0
     *      2, destino,     a -1
     * 
     *  Multiplicando ambos vectores, el resultado sería información de la 
     *  distancia al destino y de simbolizar el destino con un valor negativo.
     *  Que a afectos de buscar la celda de menor valor ésta sería negativa.
     * 
     *  Considerada la tranformación del radar, ahora toca procesar la del scanner,
     *  pues la distancia al destino cuando se está sobre él es 0.
     *  y con este cambio no sería perceptible. Por este motivo debería 
     *  revisar todo el scanner hasta encontrar 0 e intercambiarlo por un valor 
     *  positivo.
     * 
     *  Ejemplo: 1, motivo: radar*scanner = (-1)*(1) es negativo.
     */
   
    /**
     * Transforma la informacion del scanner para mejor gestión de ésta.
     * 
     * @author  Juan Germán Gómez Gómez.
     * @note    La representación: 0 libre, 1 obstáculo, 2 destino
     * no la considero adecuada por la reflexión anterior.
     * Por ello realizaré el cambio: 1 libre, 0 obstáculo, -1 destino.
     * 
     * 
     * @author Alejandro
     * @FechaModificación 01/11/2018
     * @Motivo Nombre más significativo. Cambio de ProcesarRadarYEscanner() a
     * procesarInformacion()
     * 
     * 
     */
    private void procesarInformacion(){
        int traslacion;
        
        for(int i=0; i<scanner.size(); i++){
            // scanner[i] = (1 - radar[i])*scanner[i];
            traslacion = 1 - radar.get(i);
            
            if(traslacion < 0)                  // Es celda destino
                scanner.set(i, -1f);
            else
                scanner.set(i, traslacion*scanner.get(i));
        }
        
        actualizarRadanner();
    }
   
    
    /**
    * Asigna a cada posición el contenido procesado del Radar y del Scanner.
    * 
    * @author  Germán
    * @note Reflexión
    *   Los movimientos son: N, S, E, W, NE, NW, SE, SW
    *   Que corresponden diretamente con las celdas adyacentes a Kitt:
    *   6 --> NW        7 --> N         8 --> NE
    *   11--> W         12--> Kitt      13--> E
    *   16--> SW        17--> S         18--> SE
    * 
    * 
    * @author Alejandro
    * @FechaModificación 01/11/2018
    * @Motivo Nombre más significativo. Cambio Radanner() por actualizarRadanner()
    *   Cambiada accesibilidad a privada.
    */
    private void actualizarRadanner() {
        radanner.set(0, scanner.get(6));
        radanner.set(1, scanner.get(7));
        radanner.set(2, scanner.get(8));
        radanner.set(3, scanner.get(11));
        radanner.set(4, scanner.get(12));
        radanner.set(5, scanner.get(13));
        radanner.set(6, scanner.get(16));
        radanner.set(7, scanner.get(17));
        radanner.set(8, scanner.get(18));
    }
   
    
    /**
     * Neura decide el elemento más prometedor de las proximidades de KITT.
     * 
     * En primer lugar, se comprueba la posición 4 por si KITT se encuentra ya
     * en el destino. En caso de que no lo esté, continúa la búsqueda.
     * 
     * 
     * @author:  Germán  
     * @return Devuelve la posición del elemento más prometedor.
     * @version: 1.1
     * @note:    Creo que es más importante la posición donde se encuentra
     *  el elemento más prometedor que la información que contiene.
     * 
     * 
     * @author Alejandro
     * @FechaModificación 01/11/2018
     * @Motivo Las funciones y métodos deben ser siempre verbos ya que tal y como
     * está ahora no sé lo que hace visto desde otra clase a no ser que mire el código.
     * Con "Decision" no sé si devuelve la decision, la toma, si es una variabe o qué.
     * 
     * - Cambio el nombre del método a decidirAccion().
     * - Cambio visibilidad a privada.
     * 
     * @FechaModificacion 03/11/2018
     * - Cambiado el algoritmo de búsqueda por uno más simple y que no rompa 
     *   el bucle con varios returns.
     * 
     *  El algoritmo busca en radanner por un valor menor que cero, el destino.
     *  En caso de no encontrarlo, obtiene la casilla más ventajosa para
     *  alcanzarlo, es decir, aquella que tenga el menor valor de entre todas.
     * 
     *  Mantengo la comprobación original de si KITT ya ha llegado al destino.
     */
    private int decidirAccion(){
        float minimo = Float.POSITIVE_INFINITY;
        int posicion = -1;
       
       
        if(radanner.get(4) < 0)
            posicion = 4;
        else 
            for(int i=0; i<radanner.size(); i++) {
                if(radanner.get(i)<0)
                    posicion = i;
                else
                    if(radanner.get(i)<minimo && radanner.get(i) != 0.0) {
                        minimo = radanner.get(i);
                        posicion = i;
                    }
            }
        
        return posicion;
    }
   

    /**
     * Recibe los mensajes que envía el servidor a los sensores del agente y 
     * procesa la información, actualizando su estado interno con los datos
     * proporcionados por el servidor.
     * 
     * @author Germán, Alejandro
     * 
     * @FechaModificacion 03/11/2018
     * 
     */
    private void actualizarSensores(){
        
        JsonArray datos = new JsonArray();
        // Ver el contenido de los sensores antes de iniciar la actualización
        // getSensores();
       
        for(int j=0; j<NUM_SENSORES; j++){
            recibirMensaje();
            System.out.println("[NEURA] Mensaje recibido del servidor: " + mensaje_respuesta.getContent());


            if(mensaje.toString().contains("scanner")) {
                datos = mensaje.get("scanner").asArray();
                for(int i=0; i<datos.size(); i++)
                    scanner.set(i, datos.get(i).asFloat());
            }
            else if(mensaje.toString().contains("radar")) {
                datos = mensaje.get("radar").asArray();
                for(int i=0; i<datos.size(); i++)
                    radar.set(i, datos.get(i).asInt());
            }
            else if(mensaje.toString().contains("gps")) {
                int x = mensaje.get("gps").asObject().get("x").asInt(); 
                int y = mensaje.get("gps").asObject().get("y").asInt();

                Casilla actual = comprobarCasillaExiste(x,y);
                actual.aumentarContador();
                memoria.add(actual);
            }
            else {
                System.out.println("ERROR: " + mensaje.asString());
            }
       }
       
       // Ver el contenido de los sensores despues de la actualización
       System.out.println("Sensores Actualizados: ");
       //getSensores();    
   }

   
    
    /**
     * Este método comprueba si ya se ha pasado por la posición X,Y, buscando en
     * la memoria del agente. En caso de encontrar que las coordenadas dadas ya
     * han sido registradas, delvuelve la casilla en cuestión para que se pueda
     * trabajar con ella. Sin embargo, si la casilla no se encuentra, significa
     * que es la primera vez que se pasa por esa posición, luego se guarda en la
     * memoria creando y devolviendo una nueva casilla para que se pueda trabajar
     * con ella.
     * 
     * @author Alejandro
     * 
     * @return el objeto de tipo casilla que corresponde con la posición X,Y 
     * dada o, en caso de no encontrarse, una casilla recién creada.
     */
    private Casilla comprobarCasillaExiste(int x, int y) {
        
        for(Casilla i : memoria) {
            if(i.X==x && i.Y==y)
                return i;
        }
        
        return new Casilla(x,y);
    }
    
     
    /**
     * Neura indica la acción más prometedora. 
     * 
     * @author Juan Germán Gómez Gómez
     * @return Devuelve el movimiento a realizar 
     * @note   De momento Neura no tiene memoria, de tenerla debería ordenar 
     * los movimientos más prometedores para poder cotejarlos con los realizados.
     * 
     * 
     * @author Alejandro
     * @FechaModificación 01/11/2018
     * @Motivo Las funciones y métodos deben ser siempre verbos ya que tal y como
     * está ahora no sé lo que hace visto desde otra clase a no ser que mire el código.
     * Con "Acción" no sé si devuelve la acción, la toma, si es una variabe o qué.
     * Cambio el nombre del método a getAccion()
     */
    public String getAccion(){
        return movimientos.get(decidirAccion());
    }
    
    
    /**
     * Muestra el contenido de cada sensor
     * 
     * @author  Germán
     * @note    Útil para apreciar los cambios que se realizan
     * 
     * @author Alejandro
     * @FechaModificación 01/11/2018
     * @Motivo  Cambio PrintSensores() por getSensores()
     * 
     * @FechaModificacion 03/11/2018
     * @Motivo reemplazo de bucles for por funciones propias del lenguaje
     */
    public void getSensores(){
        
        System.out.print("\n Radar: ");
        radar.toString();
  
        System.out.print("\n Scanner: ");
        scanner.toString();
        
        System.out.println("\n Radanner: ");
        radanner.toString();
        
        System.out.println("\n Posición (GPS): ");
        memoria.toString();       
    }
}
