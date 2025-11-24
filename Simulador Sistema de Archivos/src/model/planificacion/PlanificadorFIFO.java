package model.planificacion;

import model.procesos.SolicitudIO;
import util.estructuras.Cola;
import util.estructuras.ListaEnlazada;
import util.estructuras.Nodo;

public class PlanificadorFIFO implements PlanificadorDisco {
    
    @Override
    public ListaEnlazada<SolicitudIO> planificar(Cola<SolicitudIO> solicitudes, int posicionCabezal) {
        ListaEnlazada<SolicitudIO> resultado = new ListaEnlazada<>();
        
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
               " ";
    }
    
    @Override
    public String toString() {
        return getNombre();
    }
}