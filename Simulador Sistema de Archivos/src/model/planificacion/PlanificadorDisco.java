package model.planificacion;

import model.procesos.SolicitudIO;
import util.estructuras.Cola;
import util.estructuras.ListaEnlazada;

/**
 * Interfaz para los algoritmos de planificación de disco
 */
public interface PlanificadorDisco {
    
    /**
     * Procesa una cola de solicitudes de I/O según el algoritmo específico
     * @param solicitudes Cola de solicitudes a procesar
     * @param posicionCabezal Posición actual del cabezal del disco
     * @return Lista ordenada de solicitudes según el algoritmo
     */
    ListaEnlazada<SolicitudIO> planificar(Cola<SolicitudIO> solicitudes, int posicionCabezal);
    
    /**
     * Obtiene el nombre del algoritmo
     * @return nombre del algoritmo
     */
    String getNombre();
    
    /**
     * Obtiene una descripción del algoritmo
     * @return descripción del algoritmo
     */
    String getDescripcion();
    
    /**
     * Calcula el movimiento total del cabezal para un conjunto de solicitudes
     * @param solicitudes Lista de solicitudes ordenadas
     * @param posicionInicial Posición inicial del cabezal
     * @return movimiento total en número de pistas
     */
    default int calcularMovimientoTotal(ListaEnlazada<SolicitudIO> solicitudes, int posicionInicial) {
        if (solicitudes.estaVacia()) {
            return 0;
        }
        
        int movimientoTotal = 0;
        int posicionActual = posicionInicial;
        
        for (int i = 0; i < solicitudes.getTamanio(); i++) {
            SolicitudIO solicitud = solicitudes.obtener(i);
            if (solicitud != null) {
                movimientoTotal += Math.abs(solicitud.getBloqueDestino() - posicionActual);
                posicionActual = solicitud.getBloqueDestino();
            }
        }
        
        return movimientoTotal;
    }
}