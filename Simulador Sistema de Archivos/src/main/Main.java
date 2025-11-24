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
                
   
                mostrarInformacionConsola(TAMANIO_DISCO);
                
  
                VentanaPrincipal ventana = new VentanaPrincipal(TAMANIO_DISCO);
                
   
                ventana.setVisible(true);
                
                System.out.println("\n✓ Sistema iniciado correctamente");
                System.out.println("✓ Ventana principal visible");
                System.out.println("=================================================\n");
            }
        });
    }
    

    private static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("✓ Look and Feel configurado: " + 
                             UIManager.getLookAndFeel().getName());
        } catch (ClassNotFoundException e) {
            System.err.println("⚠ Clase de Look and Feel no encontrada: " + e.getMessage());
        } catch (InstantiationException e) {
            System.err.println("⚠ No se pudo instanciar el Look and Feel: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("⚠ Acceso ilegal al Look and Feel: " + e.getMessage());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("⚠ Look and Feel no soportado: " + e.getMessage());
        }
    }
    
  
    private static void mostrarInformacionConsola(int tamanioDiscoEnBloques) {
        System.out.println("\n=================================================");
        System.out.println("  SIMULADOR DE SISTEMA DE ARCHIVOS");
        System.out.println("  Universidad Metropolitana");
        System.out.println("  Sistemas Operativos - Proyecto 2");
        System.out.println("=================================================");
        System.out.println();
        System.out.println("CONFIGURACIÓN DEL SISTEMA:");
        System.out.println("  • Tamaño del disco: " + tamanioDiscoEnBloques + " bloques");
        System.out.println("  • Algoritmo de planificación inicial: FIFO");
        System.out.println("  • Usuario inicial: admin (Administrador)");
        System.out.println("  • Directorios por defecto: documentos, programas, sistema");
        System.out.println();
        System.out.println("CARACTERÍSTICAS IMPLEMENTADAS:");
        System.out.println("  ✓ Gestión completa de archivos y directorios (CRUD)");
        System.out.println("  ✓ Asignación encadenada de bloques");
        System.out.println("  ✓ Sistema de permisos (Admin/Usuario)");
        System.out.println("  ✓ Procesos con operaciones de E/S reales");
        System.out.println("  ✓ 6 algoritmos de planificación de disco");
        System.out.println("  ✓ Visualización en tiempo real");
        System.out.println("  ✓ Persistencia de datos");
        System.out.println();
        System.out.println("INICIANDO INTERFAZ GRÁFICA...");
        System.out.println("=================================================");
    }
}
