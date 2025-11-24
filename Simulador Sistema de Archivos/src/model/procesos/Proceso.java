package model.procesos;

import model.sistema.Usuario;

public class Proceso {
    private int pid;
    private String nombre;
    private TipoOperacion operacion;
    private EstadoProceso estado;
    private String archivoObjetivo;
    private Usuario propietario;
    private int programCounter;
    private int numeroInstrucciones;
    private int[] bloquesAsignados;
    private SolicitudIO solicitudIO;
    private String bloquesIO;
    private int movimientoCabezal;
    private static int contadorPID = 1;
    public Proceso(String nombre, TipoOperacion operacion, Usuario propietario, 
                   String archivoObjetivo, int numeroInstrucciones) {
        this.pid = contadorPID++;
        this.nombre = nombre;
        this.operacion = operacion;
        this.propietario = propietario;
        this.archivoObjetivo = archivoObjetivo;
        this.numeroInstrucciones = numeroInstrucciones;
        this.programCounter = 0;
        this.estado = EstadoProceso.NUEVO;
        this.bloquesAsignados = new int[numeroInstrucciones];
        this.bloquesIO = "";
        this.movimientoCabezal = 0;
    }
    
    public void ejecutarInstruccion() {
        if (programCounter < numeroInstrucciones) {
            programCounter++;
        }
    }
    
    public boolean haTerminado() {
        return programCounter >= numeroInstrucciones;
    }
    
    public double getPorcentajeCompletado() {
        if (numeroInstrucciones == 0) return 0;
        return (programCounter * 100.0) / numeroInstrucciones;
    }
    
    public void asignarBloqueEnInstruccionActual(int numeroBloque) {
        if (programCounter > 0 && programCounter <= numeroInstrucciones) {
            bloquesAsignados[programCounter - 1] = numeroBloque;
        }
    }
    
    public int[] getBloquesAsignados() {
        int[] resultado = new int[programCounter];
        System.arraycopy(bloquesAsignados, 0, resultado, 0, programCounter);
        return resultado;
    }
    

    public int getPid() {
        return pid;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public TipoOperacion getOperacion() {
        return operacion;
    }
    
    public EstadoProceso getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoProceso estado) {
        this.estado = estado;
    }
    
    public String getArchivoObjetivo() {
        return archivoObjetivo;
    }
    
    public Usuario getPropietario() {
        return propietario;
    }
    
    public int getProgramCounter() {
        return programCounter;
    }
    
    public int getNumeroInstrucciones() {
        return numeroInstrucciones;
    }

    public void crearSolicitudIO(int bloqueDestino) {
        this.solicitudIO = new SolicitudIO(this.pid, this.operacion, bloqueDestino, this.archivoObjetivo);
    }

    public SolicitudIO getSolicitudIO() {
        return solicitudIO;
    }
    
    public String getBloquesIO() {
        return bloquesIO;
    }
    
    public void setBloquesIO(String bloquesIO) {
        this.bloquesIO = bloquesIO;
    }
    
    public int getMovimientoCabezal() {
        return movimientoCabezal;
    }
    
    public void setMovimientoCabezal(int movimientoCabezal) {
        this.movimientoCabezal = movimientoCabezal;
    }
    
    public void agregarMovimientoCabezal(int movimiento) {
        this.movimientoCabezal += movimiento;
    }

    public String obtenerInformacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("===============================\n");
        sb.append("   INFORMACIÓN DEL PROCESO\n");
        sb.append("===============================\n\n");
        sb.append("PID: ").append(pid).append("\n");
        sb.append("Nombre: ").append(nombre).append("\n");
        sb.append("Estado: ").append(estado).append("\n");
        sb.append("Operación: ").append(operacion).append("\n");
        sb.append("Archivo Objetivo: ").append(archivoObjetivo).append("\n");
        sb.append("Propietario: ").append(propietario.getNombre()).append("\n");
        sb.append("Program Counter: ").append(programCounter).append("/").append(numeroInstrucciones).append("\n");
        sb.append("Progreso: ").append(String.format("%.1f%%", getPorcentajeCompletado())).append("\n");
        
        if (bloquesIO != null && !bloquesIO.isEmpty()) {
            sb.append("\nOperaciones I/O:\n");
            sb.append("  Bloques accedidos: ").append(bloquesIO).append("\n");
            sb.append("  Movimiento del cabezal: ").append(movimientoCabezal).append(" bloques\n");
        }

        if (solicitudIO != null) {
            sb.append("\nSolicitud I/O Activa:\n");
            sb.append("  - Bloque Destino: ").append(solicitudIO.getBloqueDestino()).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("[PID:%d] %s - %s | PC:%d/%d | Estado:%s", 
                           pid, nombre, operacion, programCounter, numeroInstrucciones, estado);
    }
    
    public static void reiniciarContadorPID() {
        contadorPID = 1;
    }
}