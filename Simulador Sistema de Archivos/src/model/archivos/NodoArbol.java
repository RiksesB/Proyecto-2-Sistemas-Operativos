package model.archivos;

import model.sistema.Usuario;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//clase para un archivo y directorio
public abstract class NodoArbol {
    protected String nombre;
    protected Directorio padre;
    protected Usuario propietario;
    protected TipoPermiso permiso;
    protected LocalDateTime fechaCreacion;
    protected LocalDateTime fechaModificacion;
    
 
    public NodoArbol(String nombre, Directorio padre) {
        this.nombre = nombre;
        this.padre = padre;
        this.permiso = TipoPermiso.PUBLICO;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
    }
    
//es directorio?
    public abstract boolean esDirectorio();
    
//tamaño del nodo
    public abstract int obtenerTamanio();
    
    public String obtenerRutaCompleta() {
        if (padre == null) {
            return "/" + nombre;
        }
        String rutaPadre = padre.obtenerRutaCompleta();
        if (rutaPadre.equals("/root")) {
            return "/" + nombre;
        }
        return rutaPadre + "/" + nombre;
    }
    

    public void actualizarFechaModificacion() {
        this.fechaModificacion = LocalDateTime.now();
    }
    
    protected String formatearFecha(LocalDateTime fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fecha.format(formatter);
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
        actualizarFechaModificacion();
    }
    
    public Directorio getPadre() {
        return padre;
    }
    
    public void setPadre(Directorio padre) {
        this.padre = padre;
    }
    
    public Usuario getPropietario() {
        return propietario;
    }
    
    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }
    
    public TipoPermiso getPermiso() {
        return permiso;
    }
    
    public void setPermiso(TipoPermiso permiso) {
        this.permiso = permiso;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }
    
    @Override
    public String toString() {
        return nombre;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NodoArbol nodo = (NodoArbol) obj;
        return nombre.equals(nodo.nombre) && 
               (padre == null ? nodo.padre == null : padre.equals(nodo.padre));
    }
}