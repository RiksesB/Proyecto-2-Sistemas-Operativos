package model.sistema;

import model.archivos.Directorio;
import model.disco.Disco;
import model.procesos.Proceso;
import util.estructuras.Cola;
import util.estructuras.ListaEnlazada;


public class SistemaArchivos {
    private Directorio raiz;
    private Disco disco;
    private Usuario usuarioActual;
    private ListaEnlazada<Usuario> usuarios;
    private final Cola<Proceso> colaProcesos;;
    private int procesoIdCounter;
    

    public SistemaArchivos(int tamanioDiscoEnBloques) {
        this.disco = new Disco(tamanioDiscoEnBloques);
        this.raiz = new Directorio("root", null);
        this.usuarios = new ListaEnlazada<>();
        this.colaProcesos = new Cola<>();
        this.procesoIdCounter = 1;
     
        Usuario admin = new Usuario("admin", TipoUsuario.ADMINISTRADOR);
        usuarios.agregarAlFinal(admin);
        this.usuarioActual = admin;
    }
    

    public void cambiarUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }
    
    /**
     * Agrega un nuevo usuario al sistema
     */
    public void agregarUsuario(Usuario usuario) {
        usuarios.agregarAlFinal(usuario);
    }
    
    /**
     * nuevo ID para un proceso
     */
    public int generarProcesoId() {
        return procesoIdCounter++;
    }

    public void encolarProceso(Proceso proceso) {
        colaProcesos.encolar(proceso);
    }
    

    public Proceso desencolarProceso() {
        return colaProcesos.desencolar();
    }
    
    public Proceso verProcesoActual() {
        return colaProcesos.verFrente();
    }
    
 
    public ListaEnlazada<Usuario> getUsuarios() {
        return usuarios;
    }
    

    public Directorio getRaiz() {
        return raiz;
    }
    
    public void setRaiz(Directorio raiz) {
        this.raiz = raiz;
    }
    
    public Disco getDisco() {
        return disco;
    }
    
    public void setDisco(Disco disco) {
        this.disco = disco;
    }
    
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }
    
    public Cola<Proceso> getColaProcesos() {
        return colaProcesos;
    }
    

    public String obtenerEstadisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADÍSTICAS DEL SISTEMA ===\n");
        sb.append("Usuario actual: ").append(usuarioActual.getNombre()).append("\n");
        sb.append("Bloques totales: ").append(disco.getTamanioTotal()).append("\n");
        sb.append("Bloques ocupados: ").append(disco.getBloquesOcupados()).append("\n");
        sb.append("Bloques libres: ").append(disco.getBloquesLibres()).append("\n");
        sb.append("Procesos en cola: ").append(colaProcesos.getTamanio()).append("\n");
        sb.append("Usuarios registrados: ").append(usuarios.getTamanio()).append("\n");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "SistemaArchivos{" +
                "usuario=" + usuarioActual.getNombre() +
                ", bloques=" + disco.getBloquesOcupados() + "/" + disco.getTamanioTotal() +
                ", procesos=" + colaProcesos.getTamanio() +
                '}';
    }
}