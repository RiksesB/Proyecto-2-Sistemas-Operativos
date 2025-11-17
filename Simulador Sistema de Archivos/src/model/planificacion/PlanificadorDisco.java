package model.planificacion;

import model.procesos.SolicitudIO;
import util.estructuras.ListaEnlazada;

public interface PlanificadorDisco {
    
    ListaEnlazada<SolicitudIO> planificar(ListaEnlazada<SolicitudIO> solicitudes, int posicionActual);
    
    String getNombre();
}