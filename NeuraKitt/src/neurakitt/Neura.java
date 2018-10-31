/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neurakitt;

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
     * @author Alvaro, Germán
     * @param aid       identificador de Neura
     * @param idKitt    identificador de receptor de los mensajes de Neura
     * @throws Exception 
     */
    public Neura(AgentID aid, AgentID idKitt) throws Exception {
        super(aid);
        
       for(int i=0; i< TAMANIOENTORNO; i++){
        //   scanner[i] = (float) Math.random()*70;
           scanner[i] = 0;
           
        //   radar[i] = (int) Math.floor(Math.random()*3);
           radar[i] = -1;
       }
       gps[0]=-1;
       gps[1]=-1;
       gps[2]= 0;
       
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
}
