package model.procesos;

public enum EstadoProceso {
    NUEVO("Nuevo"),
    LISTO("Listo"),
    EJECUTANDO("Ejecutando"),
    BLOQUEADO("Bloqueado"),
    TERMINADO("Terminado");
    
    private final String descripcion;
    
    EstadoProceso(String descripcion) {
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