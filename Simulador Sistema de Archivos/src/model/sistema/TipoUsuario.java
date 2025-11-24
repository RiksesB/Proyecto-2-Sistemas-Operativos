package model.sistema;


public enum TipoUsuario {
    ADMINISTRADOR("Administrador"),
    USUARIO("Usuario");
    
    private final String descripcion;
    
    TipoUsuario(String descripcion) {
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