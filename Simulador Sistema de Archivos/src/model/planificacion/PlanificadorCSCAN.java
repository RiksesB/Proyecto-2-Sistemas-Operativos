package model.planificacion;

import model.procesos.SolicitudIO;
import util.estructuras.Cola;
import util.estructuras.ListaEnlazada;
import util.estructuras.Nodo;

public class PlanificadorCSCAN implements PlanificadorDisco {
    
    @Override
    public ListaEnlazada<SolicitudIO> planificar(Cola<SolicitudIO> solicitudes, int posicionCabezal) {
        ListaEnlazada<SolicitudIO> resultado = new ListaEnlazada<>();
        ListaEnlazada<SolicitudIO> mayores = new ListaEnlazada<>();
        ListaEnlazada<SolicitudIO> menores = new ListaEnlazada<>();
        
        // Separar solicitudes en dos grupos
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
        
        // Ordenar ambos grupos ascendentemente
        ordenarPorBloque(mayores, true);
        ordenarPorBloque(menores, true);
        
        // Primero las mayores (hacia el final), luego salta y hace las menores (desde el inicio)
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
        return "C-SCAN";
    }
    
    @Override
    public String getDescripcion() {
        return "Circular SCAN - Similar a SCAN pero al llegar al final vuelve al inicio " +
               "sin atender solicitudes.";
    }
    
    @Override
    public String toString() {
        return getNombre();
    }
}