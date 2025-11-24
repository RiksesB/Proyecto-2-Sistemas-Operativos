package model.planificacion;

import model.procesos.SolicitudIO;
import util.estructuras.Cola;
import util.estructuras.ListaEnlazada;
import util.estructuras.Nodo;

public class PlanificadorCLOOK implements PlanificadorDisco {
    
    @Override
    public ListaEnlazada<SolicitudIO> planificar(Cola<SolicitudIO> solicitudes, int posicionCabezal) {
        ListaEnlazada<SolicitudIO> resultado = new ListaEnlazada<>();
        ListaEnlazada<SolicitudIO> mayores = new ListaEnlazada<>();
        ListaEnlazada<SolicitudIO> menores = new ListaEnlazada<>();
        
        // Separar solicitudes
        Nodo<SolicitudIO> actual = solicitudes.getFrente();
        while (actual != null) {
            SolicitudIO solicitud = actual.getDato();
            if (solicitud.getBloqueDestino() >= posicionCabezal) {
                mayores.agregarAlFinal(solicitud);
            } else {
                menores.agregarAlFinal(solicitud);
            }
            actual = actual.getSiguiente();
        }
        
        // Ordenar ascendentemente
        ordenarPorBloque(mayores, true);
        ordenarPorBloque(menores, true);
        
        // Primero mayores, luego menores (circular)
        copiarLista(mayores, resultado);
        copiarLista(menores, resultado);
        
        return resultado;
    }
    
    private void ordenarPorBloque(ListaEnlazada<SolicitudIO> lista, boolean ascendente) {
        int n = lista.getTamanio();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                SolicitudIO actual = lista.obtener(j);
                SolicitudIO siguiente = lista.obtener(j + 1);
                
                if (actual != null && siguiente != null) {
                    boolean debeIntercambiar = ascendente ? 
                        actual.getBloqueDestino() > siguiente.getBloqueDestino() :
                        actual.getBloqueDestino() < siguiente.getBloqueDestino();
                    
                    if (debeIntercambiar) {
                        lista.eliminarEnPosicion(j);
                        lista.agregarEnPosicion(siguiente, j);
                        lista.eliminarEnPosicion(j + 1);
                        lista.agregarEnPosicion(actual, j + 1);
                    }
                }
            }
        }
    }
    
    private void copiarLista(ListaEnlazada<SolicitudIO> origen, ListaEnlazada<SolicitudIO> destino) {
        for (int i = 0; i < origen.getTamanio(); i++) {
            SolicitudIO solicitud = origen.obtener(i);
            if (solicitud != null) {
                destino.agregarAlFinal(solicitud);
            }
        }
    }
    
    @Override
    public String getNombre() {
        return "C-LOOK";
    }
    
    @Override
    public String getDescripcion() {
        return "Circular LOOK - Combina C-SCAN y LOOK, " +
               "solo va hasta las últimas solicitudes en modo circular.";
    }
    
    @Override
    public String toString() {
        return getNombre();
    }
}