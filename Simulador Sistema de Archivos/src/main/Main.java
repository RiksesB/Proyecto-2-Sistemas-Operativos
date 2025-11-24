package main;

import view.VentanaPrincipal;
import javax.swing.*;

public class Main {
    
    public static void main(String[] args) {
        configurarLookAndFeel();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final int TAMANIO_DISCO = 200;
                
                VentanaPrincipal ventana = new VentanaPrincipal(TAMANIO_DISCO);
                
                ventana.setVisible(true);
            }
        });
    }
    
    private static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
    }
}
