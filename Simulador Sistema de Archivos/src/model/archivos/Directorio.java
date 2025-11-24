package model.archivos;

import util.estructuras.ListaEnlazada;

public class Directorio extends NodoArbol {
    private ListaEnlazada<NodoArbol> hijos;
    
    public Directorio(String nombre, Directorio padre) {
        super(nombre, padre);
        this.hijos = new ListaEnlazada<>();
    }
    
    @Override
    public boolean esDirectorio() {
        return true;
    }
    
    @Override
    public int obtenerTamanio() {
        int tamanioTotal = 0;
        for (int i = 0; i < hijos.getTamanio(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo != null) {
                tamanioTotal += hijo.obtenerTamanio();
            }
        }
        return tamanioTotal;
    }
 
    public void agregarHijo(NodoArbol nodo) {
        hijos.agregarAlFinal(nodo);
        nodo.setPadre(this);
        actualizarFechaModificacion();
    }
    
 
    public boolean eliminarHijo(NodoArbol nodo) {
        boolean eliminado = hijos.eliminar(nodo);
        if (eliminado) {
            actualizarFechaModificacion();
        }
        return eliminado;
    }
    
    
    public NodoArbol buscarHijo(String nombre) {
        for (int i = 0; i < hijos.getTamanio(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo != null && hijo.getNombre().equals(nombre)) {
                return hijo;
            }
        }
        return null;
    }
    
  
    public boolean existeHijo(String nombre) {
        return buscarHijo(nombre) != null;
    }
    
 
    public int contarArchivos() {
        int contador = 0;
        for (int i = 0; i < hijos.getTamanio(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo != null && !hijo.esDirectorio()) {
                contador++;
            }
        }
        return contador;
    }
    
 
    public int contarDirectorios() {
        int contador = 0;
        for (int i = 0; i < hijos.getTamanio(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo != null && hijo.esDirectorio()) {
                contador++;
            }
        }
        return contador;
    }
    
 
    public ListaEnlazada<Archivo> obtenerArchivos() {
        ListaEnlazada<Archivo> archivos = new ListaEnlazada<>();
        for (int i = 0; i < hijos.getTamanio(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo != null && !hijo.esDirectorio()) {
                archivos.agregarAlFinal((Archivo) hijo);
            }
        }
        return archivos;
    }
    
 
    public ListaEnlazada<Directorio> obtenerDirectorios() {
        ListaEnlazada<Directorio> directorios = new ListaEnlazada<>();
        for (int i = 0; i < hijos.getTamanio(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo != null && hijo.esDirectorio()) {
                directorios.agregarAlFinal((Directorio) hijo);
            }
        }
        return directorios;
    }
    
 
    public boolean estaVacio() {
        return hijos.estaVacia();
    }
    
    // Getters
    public ListaEnlazada<NodoArbol> getHijos() {
        return hijos;
    }
    
   
    public String obtenerInformacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre: ").append(nombre).append("\n");
        sb.append("Tipo: Directorio\n");
        sb.append("Archivos: ").append(contarArchivos()).append("\n");
        sb.append("Subdirectorios: ").append(contarDirectorios()).append("\n");
        sb.append("Tamaño total: ").append(obtenerTamanio()).append(" bloques\n");
        sb.append("Propietario: ").append(propietario != null ? propietario.getNombre() : "Sistema").append("\n");
        sb.append("Permisos: ").append(permiso).append("\n");
        sb.append("Creación: ").append(formatearFecha(fechaCreacion)).append("\n");
        sb.append("Modificación: ").append(formatearFecha(fechaModificacion)).append("\n");
        sb.append("Ruta: ").append(obtenerRutaCompleta()).append("\n");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return nombre + "/ (" + hijos.getTamanio() + " elementos)";
    }
}