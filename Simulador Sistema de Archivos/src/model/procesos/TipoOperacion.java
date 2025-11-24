package model.procesos;

public enum TipoOperacion {
    CREAR("Crear"),
    LEER("Leer"),
    ACTUALIZAR("Actualizar"),
    ELIMINAR("Eliminar"),
    RENOMBRAR("Renombrar");
    
    private final String descripcion;
    
    TipoOperacion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
}