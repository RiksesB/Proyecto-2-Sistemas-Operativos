package util.excepciones;

// se intenta acceder al sistema y no existe el archivo
public class ArchivoNoEncontradoException extends Exception {
    
 
    public ArchivoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    //mensaje: descripcion error, causa: excepcion que causa el error

    public ArchivoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    
    public ArchivoNoEncontradoException() {
        super("El archivo o directorio especificado no existe");
    }
}