package model.procesos;

/**
 * Representa una solicitud de entrada/salida al disco
 */
public class SolicitudIO {
    private int pid; 
    private TipoOperacion operacion;
    private int bloqueDestino;
    private String nombreArchivo;
    private long tiempoSolicitud;
    
    public SolicitudIO(int pid, TipoOperacion operacion, int bloqueDestino, String nombreArchivo) {
        this.pid = pid;
        this.operacion = operacion;
        this.bloqueDestino = bloqueDestino;
        this.nombreArchivo = nombreArchivo;
        this.tiempoSolicitud = System.currentTimeMillis();
    }
    

    public long getTiempoEspera() {
        return System.currentTimeMillis() - tiempoSolicitud;
    }
    
    // Getters y Setters
    public int getPid() {
        return pid;
    }
    
    public void setPid(int pid) {
        this.pid = pid;
    }
    
    public TipoOperacion getOperacion() {
        return operacion;
    }
    
    public void setOperacion(TipoOperacion operacion) {
        this.operacion = operacion;
    }
    
    public int getBloqueDestino() {
        return bloqueDestino;
    }
    
    public void setBloqueDestino(int bloqueDestino) {
        this.bloqueDestino = bloqueDestino;
    }
    
    public String getNombreArchivo() {
        return nombreArchivo;
    }
    
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    
    public long getTiempoSolicitud() {
        return tiempoSolicitud;
    }
    
    @Override
    public String toString() {
        return "Solicitud{PID=" + pid + ", " + operacion + 
               ", bloque=" + bloqueDestino + ", archivo='" + nombreArchivo + "'}";
    }
}
