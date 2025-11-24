package model.disco;


public enum EstadoBloque {
    LIBRE("Libre"),
    OCUPADO("Ocupado"),
    DEFECTUOSO("Defectuoso");
    
    private final String descripcion;
    
    EstadoBloque(String descripcion) {
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