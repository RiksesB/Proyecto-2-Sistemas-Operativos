package model.planificacion;

public class GestorPlanificacion {
    private PlanificadorDisco planificadorActual;
    private TipoPlanificacion tipoActual;

    public GestorPlanificacion() {
        this.tipoActual = TipoPlanificacion.FIFO;
    }

    public void setPlanificador(PlanificadorDisco planificador) {
        this.planificadorActual = planificador;
    }

    public PlanificadorDisco getPlanificadorActual() {
        return planificadorActual;
    }

    public void setTipoActual(TipoPlanificacion tipo) {
        this.tipoActual = tipo;
    }

    public TipoPlanificacion getTipoActual() {
        return tipoActual;
    }
}