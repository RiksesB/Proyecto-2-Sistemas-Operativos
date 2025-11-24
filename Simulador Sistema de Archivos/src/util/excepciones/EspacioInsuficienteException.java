package util.excepciones;


public class EspacioInsuficienteException extends Exception {
    
 
    public EspacioInsuficienteException(String mensaje) {
        super(mensaje);
    }
    
  
    public EspacioInsuficienteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    

    public EspacioInsuficienteException() {
        super("No hay suficiente espacio disponible en el disco");
    }
}