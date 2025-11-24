package controller;

import model.sistema.SistemaArchivos;
import model.sistema.Usuario;
import model.sistema.TipoUsuario;
import model.archivos.Directorio;
import model.archivos.Archivo;
import model.archivos.NodoArbol;
import util.estructuras.ListaEnlazada;
import java.io.*;

public class GestorPersistencia {
    
    private static final String ARCHIVO_DATOS = "sistema_archivos.txt";
    private static final String SEPARADOR = "|";

    public boolean guardarSistema(SistemaArchivos sistema) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_DATOS));

            writer.println("DISCO");
            writer.println(sistema.getDisco().getTamanioTotal());
            writer.println(sistema.getDisco().getBloquesOcupados());
    
            writer.println("USUARIOS");
            ListaEnlazada<Usuario> usuarios = sistema.getUsuarios();
            writer.println(usuarios.getTamanio());
            
            for (int i = 0; i < usuarios.getTamanio(); i++) {
                Usuario usuario = usuarios.obtener(i);
                if (usuario != null) {
                    writer.println(usuario.getId() + SEPARADOR +
                                 usuario.getNombre() + SEPARADOR +
                                 usuario.getTipo().name());
                }
            }

            writer.println("USUARIO_ACTUAL");
            if (sistema.getUsuarioActual() != null) {
                writer.println(sistema.getUsuarioActual().getId());
            } else {
                writer.println("1");
            }
            
            writer.println("ESTRUCTURA");
            guardarDirectorioRecursivo(writer, sistema.getRaiz(), "");
            
            writer.close();
            System.out.println("Sistema guardado exitosamente en " + ARCHIVO_DATOS);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al guardar el sistema: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    private void guardarDirectorioRecursivo(PrintWriter writer, Directorio directorio, String ruta) {
        String rutaActual = ruta + "/" + directorio.getNombre();
        
        writer.println("DIR" + SEPARADOR + 
                      directorio.getNombre() + SEPARADOR + 
                      rutaActual + SEPARADOR + 
                      (directorio.getPropietario() != null ? directorio.getPropietario().getId() : "0") + SEPARADOR +
                      directorio.getPermiso().toString());
        
        ListaEnlazada<NodoArbol> hijos = directorio.getHijos();
        
        for (int i = 0; i < hijos.getTamanio(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo != null) {
                if (hijo.esDirectorio()) {
                    guardarDirectorioRecursivo(writer, (Directorio) hijo, rutaActual);
                } else {
                    Archivo archivo = (Archivo) hijo;
                    writer.println("FILE" + SEPARADOR + 
                                 archivo.getNombre() + SEPARADOR + 
                                 rutaActual + SEPARADOR + 
                                 archivo.getTamanioEnBloques() + SEPARADOR +
                                 (archivo.getPropietario() != null ? archivo.getPropietario().getId() : "0") + SEPARADOR +
                                 archivo.getPermiso().toString() + SEPARADOR +
                                 archivo.getContenido().replace("\n", "\\n"));
                }
            }
        }
    }
    
    public SistemaArchivos cargarSistema(int tamanioDiscoPorDefecto) {
        File archivo = new File(ARCHIVO_DATOS);

        if (!archivo.exists()) {
            System.out.println("No se encontró archivo guardado, creando sistema nuevo...");
            return new SistemaArchivos(tamanioDiscoPorDefecto);
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            String linea;

            SistemaArchivos sistema = null;
            GestorDisco gestorDisco = null;
            int usuarioActualId = 1;

            while ((linea = reader.readLine()) != null) {
                if (linea.equals("DISCO")) {
                    int tamanio = Integer.parseInt(reader.readLine());
                    sistema = new SistemaArchivos(tamanio);
                    gestorDisco = new GestorDisco(sistema.getDisco());
                    reader.readLine(); 

                } else if (linea.equals("USUARIOS")) {
                    if (sistema == null) continue;

                    int cantidadUsuarios = Integer.parseInt(reader.readLine());

                    for (int i = 0; i < cantidadUsuarios; i++) {
                        String[] datos = reader.readLine().split("\\" + SEPARADOR);
                        if (datos.length >= 3) {
                            String nombre = datos[1];
                            TipoUsuario tipo = TipoUsuario.valueOf(datos[2]);

                            if (!nombre.equals("admin")) {
                                Usuario usuario = new Usuario(nombre, tipo);
                                sistema.agregarUsuario(usuario);
                            }
                        }
                    }

                } else if (linea.equals("USUARIO_ACTUAL")) {
                    String idStr = reader.readLine();
                    if (idStr != null && !idStr.isEmpty()) {
                        usuarioActualId = Integer.parseInt(idStr);
                    }

                } else if (linea.equals("ESTRUCTURA")) {
                    if (sistema == null || gestorDisco == null) continue;

                    cargarEstructura(reader, sistema, gestorDisco);
                }
            }

            reader.close();

            if (sistema == null) {
                sistema = new SistemaArchivos(tamanioDiscoPorDefecto);
            } else {
                Usuario usuarioActual = buscarUsuarioPorId(sistema, usuarioActualId);
                if (usuarioActual != null) {
                    sistema.cambiarUsuario(usuarioActual);
                }
            }

            System.out.println("Sistema cargado exitosamente desde " + ARCHIVO_DATOS);
            return sistema;

        } catch (Exception e) {
            System.err.println("Error al cargar el sistema: " + e.getMessage());
            e.printStackTrace();
            return new SistemaArchivos(tamanioDiscoPorDefecto);
        }
    }

    private void cargarEstructura(BufferedReader reader, SistemaArchivos sistema, GestorDisco gestorDisco) throws IOException {
        String linea;
        GestorArchivos gestorArchivos = new GestorArchivos(sistema.getRaiz(), gestorDisco);

        sistema.getRaiz().getHijos().limpiar();

        while ((linea = reader.readLine()) != null) {
            if (linea.startsWith("DIR" + SEPARADOR)) {
                String[] datos = linea.split("\\" + SEPARADOR);
                if (datos.length >= 3) {
                    String nombre = datos[1];
                    String ruta = datos[2];

                    if (!nombre.equals("raiz")) {
                        try {
                            Directorio padre = obtenerDirectorioPorRuta(sistema.getRaiz(), ruta);
                            if (padre != null) {
                                gestorArchivos.crearDirectorio(nombre, padre, sistema.getUsuarioActual());
                            }
                        } catch (Exception e) {
                            System.err.println("Error al crear directorio: " + nombre);
                        }
                    }
                }

            } else if (linea.startsWith("FILE" + SEPARADOR)) {
                String[] datos = linea.split("\\" + SEPARADOR);
                if (datos.length >= 6) {
                    String nombre = datos[1];
                    String ruta = datos[2];
                    int tamanio = Integer.parseInt(datos[3]);
                    String contenido = datos.length > 6 ? datos[6].replace("\\n", "\n") : "";

                    try {
                        Directorio padre = obtenerDirectorioPorRuta(sistema.getRaiz(), ruta);
                        if (padre != null) {
                            Archivo archivo = new Archivo(nombre, padre, tamanio);
                            archivo.setPropietario(sistema.getUsuarioActual());
                            archivo.setContenido(contenido);

                            gestorDisco.asignarBloquesAArchivo(archivo);
                            padre.agregarHijo(archivo);
                        }
                    } catch (Exception e) {
                        System.err.println("Error al crear archivo: " + nombre);
                    }
                }
            }
        }
    }

    private Directorio obtenerDirectorioPorRuta(Directorio raiz, String ruta) {
        if (ruta.equals("/raiz") || ruta.equals("/")) {
            return raiz;
        }

        String[] partes = ruta.split("/");
        Directorio actual = raiz;

        for (String parte : partes) {
            if (parte.isEmpty() || parte.equals("raiz")) continue;

            NodoArbol hijo = actual.buscarHijo(parte);
            if (hijo != null && hijo.esDirectorio()) {
                actual = (Directorio) hijo;
            } else {
                return actual; 
            }
        }

        return actual;
    }

    private Usuario buscarUsuarioPorId(SistemaArchivos sistema, int id) {
        ListaEnlazada<Usuario> usuarios = sistema.getUsuarios();
        for (int i = 0; i < usuarios.getTamanio(); i++) {
            Usuario usuario = usuarios.obtener(i);
            if (usuario != null && usuario.getId() == id) {
                return usuario;
            }
        }
        return null;
    }
    
    public boolean exportarATexto(SistemaArchivos sistema, String nombreArchivo) {
        try {
            FileWriter writer = new FileWriter(nombreArchivo);
            
            writer.write("=== SISTEMA DE ARCHIVOS ===\n\n");
            writer.write(sistema.obtenerEstadisticas());
            writer.write("\n");
            writer.write(sistema.getDisco().obtenerEstadisticas());
            writer.write("\n=== ESTRUCTURA DE DIRECTORIOS ===\n");
            writer.write(exportarDirectorioTexto(sistema.getRaiz(), 0));
            
            writer.close();
            System.out.println("Sistema exportado a " + nombreArchivo);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al exportar: " + e.getMessage());
            return false;
        }
    }
    
    private String exportarDirectorioTexto(Directorio directorio, int nivel) {
        StringBuilder sb = new StringBuilder();
        String indent = "  ".repeat(nivel);
        
        sb.append(indent).append("  ").append(directorio.getNombre()).append("/\n");
        
        ListaEnlazada<NodoArbol> hijos = directorio.getHijos();
        for (int i = 0; i < hijos.getTamanio(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo != null) {
                if (hijo.esDirectorio()) {
                    sb.append(exportarDirectorioTexto((Directorio) hijo, nivel + 1));
                } else {
                    Archivo archivo = (Archivo) hijo;
                    sb.append(indent).append(" ").append(archivo.getNombre())
                      .append(" (").append(archivo.getTamanioEnBloques()).append(" bloques)\n");
                }
            }
        }
        
        return sb.toString();
    }
}