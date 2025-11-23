package model.archivos;

//¿qué permiso hay en cada archivo?
public enum TipoPermiso {
    LECTURA("Lectura"),
    ESCRITURA("Escritura"),
    LECTURA_ESCRITURA("Lectura y Escritura"),
    SOLO_PROPIETARIO("Solo Propietario"),
    PUBLICO("Público");
    
    private final String descripcion;
    
    TipoPermiso(String descripcion) {
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