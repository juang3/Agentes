/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test_Neura;

import java.util.ArrayList;

/**
 *
 * @author juan
 */
public class Test_Neura {
    // Entorno que percibe, cuadrado de 5x5
    int TAMANIOENTORNO = 25;

// Destinados a moverse en el mapa.    
    float scanner[]  = new float[TAMANIOENTORNO];    // Tamanio fijo
    int tamanioScanner = TAMANIOENTORNO;
   
    int radar[]      = new int[TAMANIOENTORNO];      // Tamanio fijo
    int tamanioRadar = TAMANIOENTORNO;
   
// Destinados a memoria    
    int gps[]          = new int[2];     // Tamanio fijo (x,y)
    ArrayList caminado = new ArrayList();// Tamanio variable
    
// Movimientos posibles.
    String movimientos_string[] = {
       "MoveNw","MoveN" ,"MoveNE",
       "MoveW" ,"logout","MoveE" ,
       "MoveSW","MoveS" ,"MoveSE"
   };
   
    float radanner[] = new float[10];
    int TAMANIORADANNER = 9;
   
   /**
    * Constructor, rellena los sensores para realizar test de los métodos.
    */
   public Test_Neura(){
       for(int i=0; i< TAMANIOENTORNO; i++){
           scanner[i] = (float) Math.random()*70;
           radar[i] = (int) Math.floor(Math.random()*3);
       }
       gps[0]=25;
       gps[1]=125; 
       
       for(int i=0; i<TAMANIORADANNER; i++) radanner[i] =-1;
   }
    /**
     * @author: Juan Germán Gómez Gómez 
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
   public void ProcesarRadarYScanner(){
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
       
       Radanner();
       
   }
   
   /**
    * Reflexión:
    *   Los movimientos son: N, S, E, W, NE, NW, SE, SW
    * Que corresponden diretamente con las celdas adyacentes a Kitt:
    *   6 --> NW        7 --> N         8 --> NE
    *   11--> W         12--> Kitt      13--> E
    *   16--> SW        17--> S         18--> SE
    */
   void Radanner(){
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
 * Motivo de procesar la información del scanner y del radar.
 *  Un Scanner que no discrimina entre obstáculos es de poca utilidad
 *  Un Radar que no indica la distancia al objetivo es de poca utilidad
 *  Un Objeto que informe simultaneamente de obstaculos, distancia y destino
 * es de más utilidad (lo llamo radanner)
 * 
 * Opinión.
 *  Decidir los movimientos posibles en un radanner aporta más información y 
 * evita gestionar varios objetos simultaneamente.
 */
   
   int Decision(){
       float minimo;
       minimo = Float.POSITIVE_INFINITY;
       int posicion = 0;
       
       if(radanner[4]<0){
           return 4;
       }
       else{
           for(int i=0; i< TAMANIORADANNER; i++){
               if(radanner[i]<0){
                   return i;
               }
               else if(radanner[i]<minimo && radanner[i] != 0.0){
                   minimo = radanner[i];
                   posicion = i;
               }
           }
       }
       return posicion;
   }
   
  public  String Accion(){
       return movimientos_string[Decision()];
   }
   
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
}
