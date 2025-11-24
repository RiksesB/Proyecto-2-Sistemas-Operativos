package model.planificacion;

import model.procesos.SolicitudIO;
import util.estructuras.Cola;
import util.estructuras.ListaEnlazada;


public interface PlanificadorDisco {
    

    ListaEnlazada<SolicitudIO> planificar(Cola<SolicitudIO> solicitudes, int posicionCabezal);
    

    String getNombre();
    

    String getDescripcion();
    

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