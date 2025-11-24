package model.sistema;


public class Usuario {
    private String nombre;
    private TipoUsuario tipo;
    private int id;
    private static int contadorId = 1;
    

    public Usuario(String nombre, TipoUsuario tipo) {
        this.id = contadorId++;
        this.nombre = nombre;
        this.tipo = tipo;
    }
    

    public Usuario(String nombre) {
        this(nombre, TipoUsuario.USUARIO);
    }
    

    public boolean esAdministrador() {
        return tipo == TipoUsuario.ADMINISTRADOR;
    }
    
  
    public boolean tienePermisosEscritura() {
        return esAdministrador();
    }
    
    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public TipoUsuario getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }
    
    public int getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return nombre + " (" + tipo + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return id == usuario.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}