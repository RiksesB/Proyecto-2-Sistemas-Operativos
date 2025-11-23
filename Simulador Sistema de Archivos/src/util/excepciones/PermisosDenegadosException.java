package util.excepciones;

//usuario intenta realizar accion que no le corresponde (crear archivos porejempl)
public class PermisosDenegadosException extends Exception {
    

    public PermisosDenegadosException(String mensaje) {
        super(mensaje);
    }

    public PermisosDenegadosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
 
        //mensaje: descripcion error, causa: excepcion que causa el error
    
    public PermisosDenegadosException() {
        super("No tiene permisos suficientes para realizar esta operación");
    }
}