/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neurakitt;

import com.eclipsesource.json.JsonArray;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;

/**
 *
 * @author Alvaro
 */
public class Neura extends Agente {
    /**
     * Abributos iniciales
     */
    
    // Entorno que percibe, cuadrado de 5x5
    int TAMANIOENTORNO = 25;

    // Destinados a moverse en el mapa.    
    float scanner[]  = new float[TAMANIOENTORNO];    // Tamanio fijo
    int tamanioScanner = TAMANIOENTORNO;
   
    int radar[]      = new int[TAMANIOENTORNO];      // Tamanio fijo
    int tamanioRadar = TAMANIOENTORNO;
    

   
    // Destinados a memoria    
    int gps[]          = new int[3];     // Tamanio fijo (x,y, veces que ha pasado)
    ArrayList caminado = new ArrayList();// Tamanio variable// Tamanio variable
    
    // Movimientos posibles.
    String movimientos[] = {
        "moveNw","moveN" ,"moveNE",
        "moveW" ,"logout","moveE" ,
        "moveSW","moveS" ,"moveSE"
    };
    
    /**
     * Contiene la información del scanner y del radar,
     * reducida a los 8 movimientos.
     */
    int TAMANIORADANNER = 9;
    float radanner[] = new float[TAMANIORADANNER];
    
    // Array necesario para actualizar los sensores.
    JsonArray entorno = new JsonArray();
    JsonArray coordenadas = new JsonArray();
    
    /**
     * @author Alvaro, Germán
     * @param aid       identificador de Neura
     * @param idKitt    identificador de receptor de los mensajes de Neura
     * @throws Exception 
     */
    public Neura(AgentID aid, AgentID idKitt) throws Exception {
        super(aid);
       
        System.out.println("Inicializando atributos ");
       for(int i=0; i< TAMANIOENTORNO; i++){
        //   scanner[i] = (float) Math.random()*70;
           scanner[i] = 0;
           
        //   radar[i] = (int) Math.floor(Math.random()*3);
           radar[i] = -1;
       }
       gps[0]=-1;
       gps[1]=-1;
       gps[2]= 0;
       
       for(int i=0; i< TAMANIORADANNER; i++)
           radanner[i] = Float.POSITIVE_INFINITY;

        System.out.println("Fin de la construcción de Neura ");
    }
    
    @Override
    /**
     * @author Alvaro
     */
    public void execute(){
        System.out.println("Hola, soy Neura");
        
        
    }
    
/** Funciones Auxiliares *******************************************************/ 
    
/**
     * @author: Germán
     * Reflexión.
     * Previos:
     *  El radar representa la información del entorno con 0,1,2 (libre, obstáculo, destino)
     *  El scanner representa la información mediante un número real que simboliza la distancia al destino.
     * 
     *  Si quiero extraer la información de ambos, debo mirar a ambos,
     * ello me implica trabajar con ambos vectores simultaneamente,
     * lo que es un foco de posibles errores.
     *  
     *  Por ello he considerado unificar la información en un solo vector,
     * multiplicando ambos vectores componente a componente.
     * Tal cual están ahora mismo el producto resultante es un vector que muestra
     * los obstáculos con información irrelevante, además de perderse la ubicación del destino.
     * 
     *  Por tanto, considero cambiar los valores:
     *      0, libre,       a  1
     *      1, obstáculo,   a  0
     *      2, destino,     a -1
     * 
     *  Multiplicando ambos vectores, el resultado sería información de la distancia al destino
     * y de simbolizar el destino con un valor negativo.
     * Que a afectos de buscar la celda de menor valor ésta sería negativa.
     * 
     *  Considerada la tranformación del radar, ahora toca procesar la del scanner,
     * pues la distancia al destino cuando se está sobre él es 0.
     * y con este cambio no sería perceptible. Por este motivo debería 
     * revisar todo el scanner hasta encontrar 0 e intercambiarlo por un valor positivo.
     * Ejemplo: 1, motivo: radar*scanner = (-1)*(1) es negativo.
     */
   
   /**
    * Transforma la informacion del scanner para mejor gestión de esta.
    * @author:  Juan Germán Gómez Gómez.
    * @note:    La representación: 0 libre, 1 obstáculo, 2 destino
    * no la considero adecuada por la reflexión anterior.
    * Por ello realizaré el cambio: 1 libre, 0 obstáculo, -1 destino.
    * 
    */
   private void ProcesarRadarYScanner(){
       int traslacion;
       for(int i=0; i< tamanioScanner; i++){
//           scanner[i] = (1 - radar[i])*scanner[i];
           traslacion= 1 - radar[i];    // Transformación, el radar no se altera.
           if(traslacion < 0){          //Es celda destino
               scanner[i]= -1;
           }
           else
               scanner[i]=traslacion*scanner[i];
       } 
   }
   
   /**
    * Asigna a cada posición el contenido procesado del Radar y del Scanner.
    * @author:  Germán
    * @note: Reflexión
    *   Los movimientos son: N, S, E, W, NE, NW, SE, SW
    *   Que corresponden diretamente con las celdas adyacentes a Kitt:
    *   6 --> NW        7 --> N         8 --> NE
    *   11--> W         12--> Kitt      13--> E
    *   16--> SW        17--> S         18--> SE
    */
   public void Radanner(){
       ProcesarRadarYScanner();
       
       radanner[0] = scanner[6];
       radanner[1] = scanner[7];
       radanner[2] = scanner[8];
       radanner[3] = scanner[11];
       radanner[4] = scanner[12];
       radanner[5] = scanner[13];
       radanner[6] = scanner[16];
       radanner[7] = scanner[17];
       radanner[8] = scanner[18];
   }
   
   /**
    * Muestra el contenido de cada sensor
    * @author:  Germán
    * @note:    Útil para apreciar los cambios que se realizan
    */
   public void PrintSensores(){
       System.out.print("\n Radar: ");
       for(int i=0; i<TAMANIOENTORNO; i++){
           System.out.print(radar[i]+" ");
       }
       
       System.out.print("\n Scanner: ");
       for(int i=0; i<TAMANIOENTORNO; i++){
           System.out.print(scanner[i]+" ");
       }
       
       System.out.print("\n GPS: x="+ gps[0] + ", y="+ gps[1]);
       
       System.out.println("\n Radanner: ");
       for(int i=0; i<9; i++){
           System.out.println("i= "+i+" "+radanner[i]+" ");
       }
   }
   
   /**
    * Neura decide el elemento más prometedor de las proximidades de Kitt.
    * @author:  Germán  
    * @return Devuelve la posición del elemento más prometedor.
    * @version: 1.1
    * @note:    Creo que es más importante la posición donde se encuentra
    *  el elemento más prometedor que la información que contiene.
    */
   int Decision(){
       float minimo;
       minimo = Float.POSITIVE_INFINITY;
       int posicion = -1;
       
    /**
     *  Miro primeramente la posición 4 que es donde está Kitt,
     * para averiguar si estoy en destino.
     * En otro caso recorro el radanner.
     */   
       if(radanner[4]<0)        
           return 4;            // Kitt está en el destino
       else{                    // Kitt no está en destino
           for(int i=0; i< TAMANIORADANNER; i++){
               if(radanner[i]<0)
                   return i;    
               
               else if(radanner[i]<minimo && radanner[i] != 0.0){
                   minimo = radanner[i];
                   posicion = i;
               }
           }
       }
       return posicion;
   }
   
   /**
    * Neura indica la acción más prometedora. 
    * @author:  Juan Germán Gómez Gómez
    * @return Devuelve el movimiento a realizar 
    * @note:    De momento Neura no tiene memoria, de tenerla debería ordenar 
    * los movimientos más prometedores para poder cotejarlos con los realizados.
    * 
    */
   public  String Accion(){
       int decision = Decision();
       return movimientos[decision];
   }
   
   private void ActualizacionDeSentidos(){
       
       // Ver el contenido de los sensores antes de iniciar la actualización
       PrintSensores();
       

       ReciboYDecodificoMensaje();
       entorno = mensaje.get("scanner").asArray();
       for(int i=0; i<entorno.size(); i++){
           scanner[i] = entorno.get(i).asFloat();
       }
       
       ReciboYDecodificoMensaje();
       coordenadas = mensaje.get("gps").asArray();
       gps[0] = coordenadas.get(0).asInt();
       gps[1] = coordenadas.get(1).asInt();
       gps[2] += 1;
       
       ReciboYDecodificoMensaje();
       entorno = mensaje.get("radar").asArray();
       for(int i=0; i<entorno.size(); i++){
           radar[i] = entorno.get(i).asInt();
       }
       
       // Ver el contenido de los sensores despues de la actualización
       PrintSensores();
   }
}
