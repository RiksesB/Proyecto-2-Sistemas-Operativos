package model.planificacion;

import model.procesos.SolicitudIO;
import util.estructuras.Cola;
import util.estructuras.ListaEnlazada;

/**
 * Clase que gestiona los diferentes algoritmos de planificación
 * y permite cambiar entre ellos dinámicamente
 */
public class GestorPlanificacion {
    
    private PlanificadorDisco planificadorActual;
    private TipoPlanificacion tipoActual;
    
    /**
     * Constructor por defecto con FIFO
     */
    public GestorPlanificacion() {
        this.tipoActual = TipoPlanificacion.FIFO;
        this.planificadorActual = new PlanificadorFIFO();
    }
    
    /**
     * Constructor con tipo específico
     * @param tipo Tipo de planificación inicial
     */
    public GestorPlanificacion(TipoPlanificacion tipo) {
        cambiarPlanificador(tipo);
    }
    
    /**
     * Cambia el algoritmo de planificación
     * @param tipo Nuevo tipo de planificación
     */
    public void cambiarPlanificador(TipoPlanificacion tipo) {
        this.tipoActual = tipo;
        
        switch (tipo) {
            case FIFO:
                this.planificadorActual = new PlanificadorFIFO();
                break;
            case SSTF:
                this.planificadorActual = new PlanificadorSSTF();
                break;
            case SCAN:
                this.planificadorActual = new PlanificadorSCAN();
                break;
            case CSCAN:
                this.planificadorActual = new PlanificadorCSCAN();
                break;
            case LOOK:
                this.planificadorActual = new PlanificadorLOOK();
                break;
            case CLOOK:
                this.planificadorActual = new PlanificadorCLOOK();
                break;
            default:
                this.planificadorActual = new PlanificadorFIFO();
        }
    }
    
    /**
     * Planifica las solicitudes usando el algoritmo actual
     * @param solicitudes Cola de solicitudes
     * @param posicionCabezal Posición actual del cabezal
     * @return Lista ordenada de solicitudes
     */
    public ListaEnlazada<SolicitudIO> planificar(Cola<SolicitudIO> solicitudes, int posicionCabezal) {
        return planificadorActual.planificar(solicitudes, posicionCabezal);
    }
    
    /**
     * Obtiene todos los tipos de planificación disponibles
     * @return array con todos los tipos
     */
    public static TipoPlanificacion[] getTiposDisponibles() {
        return TipoPlanificacion.values();
    }
    
    /**
     * Calcula el movimiento total para las solicitudes planificadas
     * @param solicitudes Lista de solicitudes ordenadas
     * @param posicionInicial Posición inicial del cabezal
     * @return movimiento total
     */
    public int calcularMovimientoTotal(ListaEnlazada<SolicitudIO> solicitudes, int posicionInicial) {
        return planificadorActual.calcularMovimientoTotal(solicitudes, posicionInicial);
    }
    
    /**
     * Compara el rendimiento de todos los algoritmos
     * @param solicitudes Cola de solicitudes
     * @param posicionCabezal Posición del cabezal
     * @return String con la comparación
     */
    public String compararAlgoritmos(Cola<SolicitudIO> solicitudes, int posicionCabezal) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== COMPARACIÓN DE ALGORITMOS ===\n\n");
        
        TipoPlanificacion tipoOriginal = tipoActual;
        
        for (TipoPlanificacion tipo : TipoPlanificacion.values()) {
            cambiarPlanificador(tipo);
            ListaEnlazada<SolicitudIO> resultado = planificar(solicitudes, posicionCabezal);
            int movimiento = calcularMovimientoTotal(resultado, posicionCabezal);
            
            sb.append(tipo.getDescripcion()).append("\n");
            sb.append("Movimiento total: ").append(movimiento).append(" bloques\n");
            sb.append("Orden de atención: ");
            for (int i = 0; i < resultado.getTamanio(); i++) {
                SolicitudIO s = resultado.obtener(i);
                if (s != null) {
                    sb.append(s.getBloqueDestino());
                    if (i < resultado.getTamanio() - 1) {
                        sb.append(" → ");
                    }
                }
            }
            sb.append("\n\n");
        }
        
        // Restaurar el planificador original
        cambiarPlanificador(tipoOriginal);
        
        return sb.toString();
    }
    
    // Getters
    public PlanificadorDisco getPlanificadorActual() {
        return planificadorActual;
    }
    
    public TipoPlanificacion getTipoActual() {
        return tipoActual;
    }
    
    public String getNombreAlgoritmoActual() {
        return planificadorActual.getNombre();
    }
    
    public String getDescripcionAlgoritmoActual() {
        return planificadorActual.getDescripcion();
    }
    
    @Override
    public String toString() {
        return "GestorPlanificacion{algoritmo=" + tipoActual + "}";
    }
}