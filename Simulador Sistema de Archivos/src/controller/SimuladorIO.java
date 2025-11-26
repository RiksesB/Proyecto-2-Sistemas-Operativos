package controller;

import model.procesos.Proceso;
import model.procesos.SolicitudIO;
import model.procesos.EstadoProceso;
import model.archivos.Archivo;
import util.estructuras.ListaEnlazada;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;


public class SimuladorIO {

    public interface ObservadorSimulacion {
        void onSimulacionActualizada();
    }

    public enum EstadoSimulacion {
        ESPERANDO("Esperando próxima operación"),
        CREANDO_PROCESO("Creando proceso"),
        EJECUTANDO_PROCESO("Ejecutando proceso"),
        GENERANDO_SOLICITUDES("Generando solicitudes de I/O"),
        PLANIFICANDO_IO("Planificando solicitudes de I/O"),
        MOVIENDO_CABEZAL("Moviendo cabezal del disco"),
        ASIGNANDO_BLOQUE("Asignando bloque"),
        LIBERANDO_BLOQUE("Liberando bloque"),
        TERMINANDO_PROCESO("Terminando proceso"),
        COMPLETADO("Operación completada");

        private final String descripcion;

        EstadoSimulacion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    private EstadoSimulacion estadoActual;
    private GestorDisco gestorDisco;

    // Variables 
    private Proceso procesoActual;
    private Archivo archivoActual;
    private model.archivos.Directorio directorioActual;
    private ListaEnlazada<SolicitudIO> solicitudesPendientes;
    private int indiceSolicitudActual;
    private int posicionCabezalActual;
    private int posicionCabezalDestino;
    private int cicloPausa;

    // Configuracion
    private int velocidadSimulacion; 
    private boolean pausado;
    
    // Observadores para sincronización con UI
    private List<ObservadorSimulacion> observadores;
    private Timer timerAuto;

    
    public SimuladorIO(GestorProcesos gestorProcesos, GestorDisco gestorDisco) {
        this.gestorDisco = gestorDisco;
        this.estadoActual = EstadoSimulacion.ESPERANDO;
        this.velocidadSimulacion = 300; // 300ms por defecto para mejor visualización
        this.pausado = false;
        this.solicitudesPendientes = new ListaEnlazada<>();
        this.indiceSolicitudActual = 0;
        this.cicloPausa = 0;
        this.observadores = new ArrayList<>();
    }
    
   
    public void agregarObservador(ObservadorSimulacion observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }
    
    
    public void eliminarObservador(ObservadorSimulacion observador) {
        observadores.remove(observador);
    }
    
    private void notificarCambio() {
        for (ObservadorSimulacion obs : observadores) {
            obs.onSimulacionActualizada();
        }
    }

 
    public void iniciarCreacionArchivo(Archivo archivo, model.archivos.Directorio directorio, int numeroBloquesNecesarios) {
        this.archivoActual = archivo;
        this.directorioActual = directorio;
        this.estadoActual = EstadoSimulacion.CREANDO_PROCESO;
        this.indiceSolicitudActual = 0;
        this.solicitudesPendientes = new ListaEnlazada<>();
        this.posicionCabezalActual = gestorDisco.getDisco().getPosicionCabezal();
        notificarCambio();
    }

   
    public void iniciarEliminacionArchivo(Archivo archivo) {
        this.archivoActual = archivo;
        this.estadoActual = EstadoSimulacion.CREANDO_PROCESO;
        this.indiceSolicitudActual = 0;
        this.solicitudesPendientes = new ListaEnlazada<>();
        this.posicionCabezalActual = gestorDisco.getDisco().getPosicionCabezal();
        notificarCambio();
    }


    public void iniciarActualizacionArchivo(Archivo archivo, String nuevoContenido) {
        this.archivoActual = archivo;
        this.estadoActual = EstadoSimulacion.CREANDO_PROCESO;
        this.indiceSolicitudActual = 0;
        this.solicitudesPendientes = new ListaEnlazada<>();
        this.posicionCabezalActual = gestorDisco.getDisco().getPosicionCabezal();
        notificarCambio();
    }

    public boolean avanzarCiclo() {
        if (pausado || estadoActual == EstadoSimulacion.COMPLETADO) {
            return false;
        }

        if (cicloPausa > 0) {
            cicloPausa--;
            return true;
        }

        boolean huboCambio = false;

        switch (estadoActual) {
            case ESPERANDO:
                return false;

            case CREANDO_PROCESO:
                estadoActual = EstadoSimulacion.EJECUTANDO_PROCESO;
                cicloPausa = 1;
                huboCambio = true;
                break;

            case EJECUTANDO_PROCESO:
                if (procesoActual != null) {
                    procesoActual.setEstado(EstadoProceso.EJECUTANDO);
                }
                estadoActual = EstadoSimulacion.GENERANDO_SOLICITUDES;
                cicloPausa = 1;
                huboCambio = true;
                break;

            case GENERANDO_SOLICITUDES:
                estadoActual = EstadoSimulacion.PLANIFICANDO_IO;
                cicloPausa = 1;
                huboCambio = true;
                break;

            case PLANIFICANDO_IO:
                if (indiceSolicitudActual < solicitudesPendientes.getTamanio()) {
                    SolicitudIO solicitud = solicitudesPendientes.obtener(indiceSolicitudActual);
                    if (solicitud != null) {
                        posicionCabezalDestino = solicitud.getBloqueDestino();
                        estadoActual = EstadoSimulacion.MOVIENDO_CABEZAL;
                    }
                } else {
                    estadoActual = EstadoSimulacion.TERMINANDO_PROCESO;
                }
                cicloPausa = 1;
                huboCambio = true;
                break;

            case MOVIENDO_CABEZAL:
                // Mover el cabezal gradualmente
                if (posicionCabezalActual < posicionCabezalDestino) {
                    posicionCabezalActual++;
                    gestorDisco.getDisco().setPosicionCabezal(posicionCabezalActual);
                    huboCambio = true;
                    cicloPausa = 0; // Sin pausa para movimiento suave
                } else if (posicionCabezalActual > posicionCabezalDestino) {
                    posicionCabezalActual--;
                    gestorDisco.getDisco().setPosicionCabezal(posicionCabezalActual);
                    huboCambio = true;
                    cicloPausa = 0; // Sin pausa para movimiento suave
                } else {
                    // Llegó al destino
                    estadoActual = EstadoSimulacion.ASIGNANDO_BLOQUE;
                    huboCambio = true;
                    cicloPausa = 1; // Pequeña pausa al asignar
                }
                break;

            case ASIGNANDO_BLOQUE:
                // Asignar o liberar el bloque
                indiceSolicitudActual++;
                
                if (indiceSolicitudActual < solicitudesPendientes.getTamanio()) {
                    estadoActual = EstadoSimulacion.PLANIFICANDO_IO;
                } else {
                    estadoActual = EstadoSimulacion.TERMINANDO_PROCESO;
                }
                cicloPausa = 2;
                huboCambio = true;
                break;

            case TERMINANDO_PROCESO:
                if (procesoActual != null) {
                    procesoActual.setEstado(EstadoProceso.TERMINADO);
                }
                estadoActual = EstadoSimulacion.COMPLETADO;
                cicloPausa = 1;
                huboCambio = true;
                break;

            case COMPLETADO:
                return false;

            default:
                return false;
        }
        
        if (huboCambio) {
            notificarCambio();
        }
        
        return true;
    }
    
    
    public void iniciarAuto() {
        detenerAuto();
        
        timerAuto = new Timer(velocidadSimulacion, e -> {
            boolean continuar = avanzarCiclo();
            if (!continuar) {
                detenerAuto();
            }
        });
        timerAuto.start();
    }
    
   
    public void detenerAuto() {
        if (timerAuto != null && timerAuto.isRunning()) {
            timerAuto.stop();
        }
    }
    

    public boolean isAutoActivo() {
        return timerAuto != null && timerAuto.isRunning();
    }


    public void ejecutarCompleto() {
        while (avanzarCiclo()) {
            try {
                Thread.sleep(velocidadSimulacion);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    

    public void completar() {
        detenerAuto();
        while (avanzarCiclo()) {
        }
        notificarCambio();
    }

  
    public void reiniciar() {
        detenerAuto();
        this.estadoActual = EstadoSimulacion.ESPERANDO;
        this.procesoActual = null;
        this.archivoActual = null;
        this.solicitudesPendientes.limpiar();
        this.indiceSolicitudActual = 0;
        this.cicloPausa = 0;
        notificarCambio();
    }

 
    public String obtenerInformacionCiclo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADO DE LA SIMULACIÓN ===\n");
        sb.append("Estado: ").append(estadoActual.getDescripcion()).append("\n");

        if (procesoActual != null) {
            sb.append("Proceso: ").append(procesoActual.getNombre()).append(" [PID: ").append(procesoActual.getPid()).append("]\n");
            sb.append("Estado Proceso: ").append(procesoActual.getEstado()).append("\n");
        }

        if (archivoActual != null) {
            sb.append("Archivo: ").append(archivoActual.getNombre()).append("\n");
        }

        sb.append("Posición Cabezal: ").append(posicionCabezalActual);
        if (posicionCabezalDestino != posicionCabezalActual && estadoActual == EstadoSimulacion.MOVIENDO_CABEZAL) {
            sb.append(" → ").append(posicionCabezalDestino);
        }
        sb.append("\n");

        if (solicitudesPendientes.getTamanio() > 0) {
            sb.append("Solicitudes I/O: ").append(indiceSolicitudActual).append("/").append(solicitudesPendientes.getTamanio()).append("\n");
        }

        return sb.toString();
    }

    public double obtenerProgreso() {
        if (estadoActual == EstadoSimulacion.ESPERANDO) {
            return 0.0;
        }
        if (estadoActual == EstadoSimulacion.COMPLETADO) {
            return 1.0;
        }

        if (solicitudesPendientes.getTamanio() > 0) {
            return (double) indiceSolicitudActual / solicitudesPendientes.getTamanio();
        }

        return 0.5; // En proceso
    }

    // Getters y Setters
    public EstadoSimulacion getEstadoActual() {
        return estadoActual;
    }

    public void setPausado(boolean pausado) {
        this.pausado = pausado;
    }

    public boolean isPausado() {
        return pausado;
    }

    public void setVelocidadSimulacion(int milisegundos) {
        this.velocidadSimulacion = milisegundos;
        if (timerAuto != null && timerAuto.isRunning()) {
            timerAuto.setDelay(milisegundos);
        }
    }

    public int getVelocidadSimulacion() {
        return velocidadSimulacion;
    }

    public Proceso getProcesoActual() {
        return procesoActual;
    }

    public void setProcesoActual(Proceso proceso) {
        this.procesoActual = proceso;
    }

    public void setSolicitudesPendientes(ListaEnlazada<SolicitudIO> solicitudes) {
        this.solicitudesPendientes = solicitudes;
    }

    public int getPosicionCabezalActual() {
        return posicionCabezalActual;
    }

    public int getPosicionCabezalDestino() {
        return posicionCabezalDestino;
    }

    public int getIndiceSolicitudActual() {
        return indiceSolicitudActual;
    }

    public Archivo getArchivoActual() {
        return archivoActual;
    }

    public model.archivos.Directorio getDirectorioActual() {
        return directorioActual;
    }
}
