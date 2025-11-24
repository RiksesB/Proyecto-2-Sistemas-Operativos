package util.excepciones;


public class ArchivoNoEncontradoException extends Exception {
    
 
    public ArchivoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
 
    public ArchivoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    

    public ArchivoNoEncontradoException() {
        super("El archivo o directorio especificado no existe");
    }
}