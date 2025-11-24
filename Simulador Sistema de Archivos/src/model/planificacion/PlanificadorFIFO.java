package model.planificacion;

import model.procesos.SolicitudIO;
import util.estructuras.Cola;
import util.estructuras.ListaEnlazada;
import util.estructuras.Nodo;

/**
 * Implementación del algoritmo FIFO (First In First Out)
 * Procesa las solicitudes en el orden en que llegan
 */
public class PlanificadorFIFO implements PlanificadorDisco {
    
    @Override
    public ListaEnlazada<SolicitudIO> planificar(Cola<SolicitudIO> solicitudes, int posicionCabezal) {
        ListaEnlazada<SolicitudIO> resultado = new ListaEnlazada<>();
        
        // Copiar todas las solicitudes de la cola a la lista en el mismo orden
        Nodo<SolicitudIO> actual = solicitudes.getFrente();
        while (actual != null) {
            resultado.agregarAlFinal(actual.getDato());
            actual = actual.getSiguiente();
        }
        
        return resultado;
    }
    
    @Override
    public String getNombre() {
        return "FIFO";
    }
    
    @Override
    public String getDescripcion() {
        return "First In First Out - Atiende las solicitudes en el orden de llegada. " +
               "Es el más simple pero puede tener mucho movimiento del cabezal.";
    }
    
    @Override
    public String toString() {
        return getNombre();
    }
}