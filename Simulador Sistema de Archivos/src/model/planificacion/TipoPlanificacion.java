package model.planificacion;

public enum TipoPlanificacion {
    FIFO("FIFO - First In First Out"),
    SSTF("SSTF - Shortest Seek Time First"),
    SCAN("SCAN - Elevador"),
    CSCAN("C-SCAN - Circular SCAN"),
    LOOK("LOOK"),
    CLOOK("C-LOOK - Circular LOOK");

    private final String descripcion;

    TipoPlanificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}