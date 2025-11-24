package model.planificacion;

import model.procesos.SolicitudIO;
import util.estructuras.Cola;
import util.estructuras.ListaEnlazada;
import util.estructuras.Nodo;

public class PlanificadorSCAN implements PlanificadorDisco {
    
    private boolean direccionCreciente; // true = hacia arriba, false = hacia abajo
    
    public PlanificadorSCAN() {
        this.direccionCreciente = true; // Empieza hacia arriba por defecto
    }
    
    public PlanificadorSCAN(boolean direccionCreciente) {
        this.direccionCreciente = direccionCreciente;
    }
    
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
        
        // Ordenar ambos grupos
        ordenarPorBloque(mayores, true);  // Ascendente
        ordenarPorBloque(menores, false); // Descendente
        
        // Combinar según la dirección
        if (direccionCreciente) {
            // Primero las mayores (ascendente), luego las menores (descendente)
            copiarLista(mayores, resultado);
            copiarLista(menores, resultado);
        } else {
            // Primero las menores (descendente), luego las mayores (ascendente)
            copiarLista(menores, resultado);
            copiarLista(mayores, resultado);
        }
        
        // Cambiar dirección para la próxima vez
        direccionCreciente = !direccionCreciente;
        
        return resultado;
    }
    
 
    private void ordenarPorBloque(ListaEnlazada<SolicitudIO> lista, boolean ascendente) {
        // Bubble sort simple
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
                        // Intercambiar
                        lista.eliminarEnPosicion(j);
                        lista.agregarEnPosicion(siguiente, j);
                        lista.eliminarEnPosicion(j + 1);
                        lista.agregarEnPosicion(actual, j + 1);
                    }
                }
            }
        }
    }
    
    /**
     * Copia elementos de una lista a otra
     */
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
        return "SCAN";
    }
    
    @Override
    public String getDescripcion() {
        return " El cabezal se mueve en una dirección atendiendo " +
               "todas las solicitudes, luego invierte y hace lo mismo en la otra dirección (Alg ascensor).";
    }
    
    public void setDireccionCreciente(boolean direccionCreciente) {
        this.direccionCreciente = direccionCreciente;
    }
    
    public boolean isDireccionCreciente() {
        return direccionCreciente;
    }
    
    @Override
    public String toString() {
        return getNombre();
    }
}