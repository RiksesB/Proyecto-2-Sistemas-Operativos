package model.sistema;

//todos los tipos de usuario, o sea admin y usuario xd
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