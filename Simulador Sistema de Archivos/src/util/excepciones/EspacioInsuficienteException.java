package util.excepciones;

// excepcion: no hay suficiente espacio en disco
public class EspacioInsuficienteException extends Exception {
    

    public EspacioInsuficienteException(String mensaje) {
        super(mensaje);
    }
    
        //mensaje: descripcion error, causa: excepcion que causa el error
    
    public EspacioInsuficienteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    
    public EspacioInsuficienteException() {
        super("No hay suficiente espacio disponible en el disco");
    }
}