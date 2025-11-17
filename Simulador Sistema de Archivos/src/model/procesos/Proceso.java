package model.procesos;

public class Proceso {
    private static int contadorPID = 1;
    
    private int pid;
    private String nombre;
    private EstadoProceso estado;
    private TipoOperacion operacion;
    private String usuario;
    private String archivoObjetivo;
    private int programCounter;
    private double progreso;
    private String bloquesIO;
    private int movimientoCabezal;

    public Proceso(String nombre, TipoOperacion operacion, String usuario, String archivoObjetivo) {
        this.pid = contadorPID++;
        this.nombre = nombre;
        this.estado = EstadoProceso.NUEVO;
        this.operacion = operacion;
        this.usuario = usuario;
        this.archivoObjetivo = archivoObjetivo;
        this.programCounter = 0;
        this.progreso = 0.0;
        this.bloquesIO = "";
        this.movimientoCabezal = 0;
    }

    public int getPid() {
        return pid;
    }

    public String getNombre() {
        return nombre;
    }

    public EstadoProceso getEstado() {
        return estado;
    }

    public TipoOperacion getOperacion() {
        return operacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getArchivoObjetivo() {
        return archivoObjetivo;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public double getProgreso() {
        return progreso;
    }

    public String getBloquesIO() {
        return bloquesIO;
    }

    public int getMovimientoCabezal() {
        return movimientoCabezal;
    }

    public void setEstado(EstadoProceso estado) {
        this.estado = estado;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public void setProgreso(double progreso) {
        this.progreso = progreso;
    }

    public void setBloquesIO(String bloquesIO) {
        this.bloquesIO = bloquesIO;
    }

    public void setMovimientoCabezal(int movimientoCabezal) {
        this.movimientoCabezal = movimientoCabezal;
    }

    public String obtenerInformacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("       INFORMACIÓN DEL PROCESO\n");
        sb.append("==========================================\n\n");
        sb.append("PID: ").append(pid).append("\n");
        sb.append("Nombre: ").append(nombre).append("\n");
        sb.append("Estado: ").append(estado).append("\n");
        sb.append("Operación: ").append(operacion).append("\n");
        sb.append("Archivo Objetivo: ").append(archivoObjetivo).append("\n");
        sb.append("Propietario: ").append(usuario).append("\n");
        sb.append("Program Counter: ").append(programCounter).append("/10\n");
        sb.append("Progreso: ").append(String.format("%.1f", progreso)).append("%\n");
        
        if (!bloquesIO.isEmpty()) {
            sb.append("\n------------------------------------------\n");
            sb.append("SOLICITUDES DE I/O:\n");
            sb.append("------------------------------------------\n");
            sb.append("Bloques accedidos: ").append(bloquesIO).append("\n");
            sb.append("Movimiento del cabezal: ").append(movimientoCabezal).append(" bloques\n");
        }
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Proceso{" +
                "PID=" + pid +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                ", operacion=" + operacion +
                '}';
    }
}