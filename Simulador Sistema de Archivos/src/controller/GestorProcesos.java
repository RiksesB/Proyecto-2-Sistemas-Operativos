package controller;

import model.procesos.EstadoProceso;
import model.procesos.Proceso;
import model.procesos.SolicitudIO;
import model.procesos.TipoOperacion;
import model.sistema.Usuario;
import model.planificacion.GestorPlanificacion;
import model.planificacion.TipoPlanificacion;
import util.estructuras.Cola;
import util.estructuras.ListaEnlazada;


public class GestorProcesos {
    
    private ListaEnlazada<Proceso> listaProcesos;
    private Cola<Proceso> colaListos;
    private Cola<SolicitudIO> colaSolicitudesIO;
    private Proceso procesoEnEjecucion;
    private GestorPlanificacion gestorPlanificacion;
    

    public GestorProcesos() {
        this.listaProcesos = new ListaEnlazada<>();
        this.colaListos = new Cola<>();
        this.colaSolicitudesIO = new Cola<>();
        this.procesoEnEjecucion = null;
        this.gestorPlanificacion = new GestorPlanificacion(TipoPlanificacion.FIFO);
    }
    

    public Proceso crearProceso(String nombre, TipoOperacion operacion,
                               Usuario usuario, String archivoObjetivo) {

        Proceso nuevoProceso = new Proceso(nombre, operacion, usuario, archivoObjetivo, 10);
        nuevoProceso.setEstado(EstadoProceso.NUEVO);
        
        listaProcesos.agregarAlFinal(nuevoProceso);
        

        nuevoProceso.setEstado(EstadoProceso.LISTO);
        colaListos.encolar(nuevoProceso);
        
        return nuevoProceso;
    }
    

    public Proceso ejecutarSiguienteProceso() {
        // Si no hay proceso en ejecución, ejecutar el siguiente de la cola
        if (procesoEnEjecucion == null && !colaListos.estaVacia()) {
            procesoEnEjecucion = colaListos.desencolar();
            if (procesoEnEjecucion != null) {
                procesoEnEjecucion.setEstado(EstadoProceso.EJECUTANDO);
            }
        }
        
        return procesoEnEjecucion;
    }
    

    public void generarSolicitudIO(int bloqueDestino) {
        if (procesoEnEjecucion != null) {
            procesoEnEjecucion.crearSolicitudIO(bloqueDestino);
            SolicitudIO solicitud = procesoEnEjecucion.getSolicitudIO();
            

            colaSolicitudesIO.encolar(solicitud);


            procesoEnEjecucion.setEstado(EstadoProceso.BLOQUEADO);
            procesoEnEjecucion = null;
        }
    }
    

    public ListaEnlazada<SolicitudIO> procesarSolicitudesIO(int posicionCabezal) {
        if (colaSolicitudesIO.estaVacia()) {
            return new ListaEnlazada<>();
        }
        

        Cola<SolicitudIO> copiaSOlicitudes = new Cola<>();
        util.estructuras.Nodo<SolicitudIO> actual = colaSolicitudesIO.getFrente();
        while (actual != null) {
            copiaSOlicitudes.encolar(actual.getDato());
            actual = actual.getSiguiente();
        }
        

        ListaEnlazada<SolicitudIO> solicitudesOrdenadas = 
            gestorPlanificacion.planificar(copiaSOlicitudes, posicionCabezal);
        

        colaSolicitudesIO.limpiar();
        
        return solicitudesOrdenadas;
    }
    

    public void completarSolicitudIO(SolicitudIO solicitud) {
        // Buscar el proceso asociado a esta solicitud
        Proceso proceso = buscarProcesoPorPID(solicitud.getPid());
        
        if (proceso != null && proceso.getEstado() == EstadoProceso.BLOQUEADO) {
            // Retornar el proceso a la cola de listos
            proceso.setEstado(EstadoProceso.LISTO);
            colaListos.encolar(proceso);
        }
    }
    

    public void terminarProcesoActual() {
        if (procesoEnEjecucion != null) {
            procesoEnEjecucion.setEstado(EstadoProceso.TERMINADO);
            procesoEnEjecucion = null;
        }
    }
    

    public void terminarProceso(Proceso proceso) {
        if (proceso != null) {
            proceso.setEstado(EstadoProceso.TERMINADO);

            // Si era el proceso en ejecución, limpiarlo
            if (procesoEnEjecucion != null && procesoEnEjecucion.equals(proceso)) {
                procesoEnEjecucion = null;
            }
        }
    }
    

    public Proceso buscarProcesoPorPID(int pid) {
        for (int i = 0; i < listaProcesos.getTamanio(); i++) {
            Proceso proceso = listaProcesos.obtener(i);
            if (proceso != null && proceso.getPid() == pid) {
                return proceso;
            }
        }
        return null;
    }
    

    public ListaEnlazada<Proceso> obtenerProcesosPorEstado(EstadoProceso estado) {
        ListaEnlazada<Proceso> resultado = new ListaEnlazada<>();
        
        for (int i = 0; i < listaProcesos.getTamanio(); i++) {
            Proceso proceso = listaProcesos.obtener(i);
            if (proceso != null && proceso.getEstado() == estado) {
                resultado.agregarAlFinal(proceso);
            }
        }
        
        return resultado;
    }
    

    public void cambiarAlgoritmoPlanificacion(TipoPlanificacion tipo) {
        gestorPlanificacion.cambiarPlanificador(tipo);
    }
    

    public String obtenerEstadisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADÍSTICAS DE PROCESOS ===\n");
        sb.append("Total de procesos: ").append(listaProcesos.getTamanio()).append("\n");
        sb.append("Procesos nuevos: ").append(obtenerProcesosPorEstado(EstadoProceso.NUEVO).getTamanio()).append("\n");
        sb.append("Procesos listos: ").append(colaListos.getTamanio()).append("\n");
        sb.append("Proceso en ejecución: ").append(procesoEnEjecucion != null ? "Sí (PID " + procesoEnEjecucion.getPid() + ")" : "No").append("\n");
        sb.append("Procesos bloqueados: ").append(obtenerProcesosPorEstado(EstadoProceso.BLOQUEADO).getTamanio()).append("\n");
        sb.append("Procesos terminados: ").append(obtenerProcesosPorEstado(EstadoProceso.TERMINADO).getTamanio()).append("\n");
        sb.append("Solicitudes de I/O pendientes: ").append(colaSolicitudesIO.getTamanio()).append("\n");
        sb.append("Algoritmo de planificación: ").append(gestorPlanificacion.getNombreAlgoritmoActual()).append("\n");
        return sb.toString();
    }
    

    public void limpiarProcesosTerminados() {
        ListaEnlazada<Proceso> nuevaLista = new ListaEnlazada<>();
        
        for (int i = 0; i < listaProcesos.getTamanio(); i++) {
            Proceso proceso = listaProcesos.obtener(i);
            if (proceso != null && proceso.getEstado() != EstadoProceso.TERMINADO) {
                nuevaLista.agregarAlFinal(proceso);
            }
        }
        
        listaProcesos = nuevaLista;
    }
    
    // Getters
    public ListaEnlazada<Proceso> getListaProcesos() {
        return listaProcesos;
    }
    
    public Cola<Proceso> getColaListos() {
        return colaListos;
    }
    
    public Cola<SolicitudIO> getColaSolicitudesIO() {
        return colaSolicitudesIO;
    }
    
    public Proceso getProcesoEnEjecucion() {
        return procesoEnEjecucion;
    }
    
    public GestorPlanificacion getGestorPlanificacion() {
        return gestorPlanificacion;
    }
    
    public int getCantidadProcesos() {
        return listaProcesos.getTamanio();
    }
    
    public int getCantidadSolicitudesIO() {
        return colaSolicitudesIO.getTamanio();
    }
    
    @Override
    public String toString() {
        return "GestorProcesos{" +
                "procesos=" + listaProcesos.getTamanio() +
                ", listos=" + colaListos.getTamanio() +
                ", solicitudesIO=" + colaSolicitudesIO.getTamanio() +
                '}';
    }
}
