/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pii_proyecto2_redsocial;

import javax.swing.SwingUtilities;

/**
 *
 * @author emyca
 */
public class PII_Proyecto2_RedSocial {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Thread hiloServidor = new Thread(() -> ChatServidor.main(new String[]{}));
        hiloServidor.setDaemon(true);
        hiloServidor.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }

        Logica logicaSetup = new Logica();
        logicaSetup.crearCuentasDefault();          
        logicaSetup.hacerQueSeSignanEntreSi();       
        logicaSetup.crearPublicacionesDefault();     

        SwingUtilities.invokeLater(() -> new GUI());
    }

}
