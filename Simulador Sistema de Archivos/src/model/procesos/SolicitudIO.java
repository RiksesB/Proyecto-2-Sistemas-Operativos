package model.procesos;

public class SolicitudIO {
    private int procesoPID;
    private TipoOperacion operacion;
    private int bloqueDestino;
    private String archivoAsociado;

    public SolicitudIO(int procesoPID, TipoOperacion operacion, int bloqueDestino, String archivoAsociado) {
        this.procesoPID = procesoPID;
        this.operacion = operacion;
        this.bloqueDestino = bloqueDestino;
        this.archivoAsociado = archivoAsociado;
    }

    public int getProcesoPID() {
        return procesoPID;
    }

    public TipoOperacion getOperacion() {
        return operacion;
    }

    public int getBloqueDestino() {
        return bloqueDestino;
    }

    public String getArchivoAsociado() {
        return archivoAsociado;
    }

    public void setProcesoPID(int procesoPID) {
        this.procesoPID = procesoPID;
    }

    public void setOperacion(TipoOperacion operacion) {
        this.operacion = operacion;
    }

    public void setBloqueDestino(int bloqueDestino) {
        this.bloqueDestino = bloqueDestino;
    }

    public void setArchivoAsociado(String archivoAsociado) {
        this.archivoAsociado = archivoAsociado;
    }

    @Override
    public String toString() {
        return "SolicitudIO{" +
                "PID=" + procesoPID +
                ", op=" + operacion +
                ", bloque=" + bloqueDestino +
                ", archivo='" + archivoAsociado + '\'' +
                '}';
    }
}
