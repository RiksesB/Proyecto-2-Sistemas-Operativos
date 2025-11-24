package util.excepciones;


public class PermisosDenegadosException extends Exception {
    

    public PermisosDenegadosException(String mensaje) {
        super(mensaje);
    }
    
 
    public PermisosDenegadosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
   
    public PermisosDenegadosException() {
        super("No tiene permisos suficientes para realizar esta operación");
    }
}