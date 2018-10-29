/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test_Neura;

/**
 *
 * @author juan
 */
public class MAIN_TestNeura {
    public static void main(String[] args) {
        Test_Neura prueba = new Test_Neura();
        
    // Test para verificar la correcta transformación    
        prueba.PrintSensores();
        prueba.ProcesarRadarYScanner();
        prueba.PrintSensores();
        System.out.println(prueba.Accion());
    }
}
