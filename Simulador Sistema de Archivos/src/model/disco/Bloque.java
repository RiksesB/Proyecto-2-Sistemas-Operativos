package model.disco;

import model.archivos.Archivo;

public class Bloque {
    private int numeroBloque;
    private EstadoBloque estado;
    private String datos;
    private Bloque siguiente; 
    private Archivo archivoPropietario;
    
    public Bloque(int numeroBloque) {
        this.numeroBloque = numeroBloque;
        this.estado = EstadoBloque.LIBRE;
        this.datos = "";
        this.siguiente = null;
        this.archivoPropietario = null;
    }
    

    public void ocupar(Archivo archivo) {
        this.estado = EstadoBloque.OCUPADO;
        this.archivoPropietario = archivo;
    }
    
  
    public void liberar() {
        this.estado = EstadoBloque.LIBRE;
        this.datos = "";
        this.siguiente = null;
        this.archivoPropietario = null;
    }
    

    public boolean estaLibre() {
        return estado == EstadoBloque.LIBRE;
    }

    public boolean estaOcupado() {
        return estado == EstadoBloque.OCUPADO;
    }
    
    // Getters y Setters
    public int getNumeroBloque() {
        return numeroBloque;
    }
    
    public void setNumeroBloque(int numeroBloque) {
        this.numeroBloque = numeroBloque;
    }
    
    public EstadoBloque getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoBloque estado) {
        this.estado = estado;
    }
    
    public String getDatos() {
        return datos;
    }
    
    public void setDatos(String datos) {
        this.datos = datos;
    }
    
    public Bloque getSiguiente() {
        return siguiente;
    }
    
    public void setSiguiente(Bloque siguiente) {
        this.siguiente = siguiente;
    }
    
    public Archivo getArchivoPropietario() {
        return archivoPropietario;
    }
    
    public void setArchivoPropietario(Archivo archivoPropietario) {
        this.archivoPropietario = archivoPropietario;
    }
    
    @Override
    public String toString() {
        return "Bloque #" + numeroBloque + " [" + estado + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Bloque bloque = (Bloque) obj;
        return numeroBloque == bloque.numeroBloque;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(numeroBloque);
    }
}