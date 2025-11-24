package model.planificacion;

import model.procesos.SolicitudIO;
import util.estructuras.Cola;
import util.estructuras.ListaEnlazada;
import util.estructuras.Nodo;

/**
 * Implementación del algoritmo SSTF (Shortest Seek Time First)
 * Siempre atiende la solicitud más cercana a la posición actual del cabezal
 */
public class PlanificadorSSTF implements PlanificadorDisco {
    
    @Override
    public ListaEnlazada<SolicitudIO> planificar(Cola<SolicitudIO> solicitudes, int posicionCabezal) {
        ListaEnlazada<SolicitudIO> resultado = new ListaEnlazada<>();
        ListaEnlazada<SolicitudIO> pendientes = new ListaEnlazada<>();
        
        // Copiar solicitudes a lista temporal
        Nodo<SolicitudIO> actual = solicitudes.getFrente();
        while (actual != null) {
            pendientes.agregarAlFinal(actual.getDato());
            actual = actual.getSiguiente();
        }
        
        int posicionActual = posicionCabezal;
        
        // Mientras haya solicitudes pendientes
        while (!pendientes.estaVacia()) {
            SolicitudIO masCercana = null;
            int distanciaMinima = Integer.MAX_VALUE;
            int indiceMinimo = -1;
            
            // Buscar la solicitud más cercana
            for (int i = 0; i < pendientes.getTamanio(); i++) {
                SolicitudIO solicitud = pendientes.obtener(i);
                if (solicitud != null) {
                    int distancia = Math.abs(solicitud.getBloqueDestino() - posicionActual);
                    if (distancia < distanciaMinima) {
                        distanciaMinima = distancia;
                        masCercana = solicitud;
                        indiceMinimo = i;
                    }
                }
            }
            
            // Agregar la más cercana al resultado y eliminarla de pendientes
            if (masCercana != null) {
                resultado.agregarAlFinal(masCercana);
                pendientes.eliminarEnPosicion(indiceMinimo);
                posicionActual = masCercana.getBloqueDestino();
            }
        }
        
        return resultado;
    }
    
    @Override
    public String getNombre() {
        return "SSTF";
    }
    
    @Override
    public String getDescripcion() {
        return "Shortest Seek Time First - Siempre atiende la solicitud más cercana. " +
               "Minimiza el movimiento del cabezal pero puede causar inanición.";
    }
    
    @Override
    public String toString() {
        return getNombre();
    }
}
