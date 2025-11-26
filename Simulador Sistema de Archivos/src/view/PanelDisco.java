package view;

import controller.ControladorPrincipal;
import controller.SimuladorIO;
import model.archivos.Archivo;
import model.disco.Bloque;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class PanelDisco extends JPanel implements SimuladorIO.ObservadorSimulacion {
    
    private ControladorPrincipal controlador;
    private SimuladorIO simulador;
    private JPanel panelBloques;
    private JLabel lblInfo;
    private static final int BLOQUES_POR_FILA = 20;
    private static final int TAMANIO_BLOQUE = 25;
    
    // Mapa para acceder rápidamente a los paneles de bloques
    private Map<Integer, JPanel> mapaBloques;
    private Integer bloqueResaltado = null;
    private Timer timerParpadeo;
    
  
    public PanelDisco(ControladorPrincipal controlador) {
        this.controlador = controlador;
        this.simulador = controlador.getSimuladorIO();
        this.mapaBloques = new HashMap<>();
        
        // Registrarse como observador
        this.simulador.agregarObservador(this);
        
        inicializarComponentes();
        dibujarDisco();
        inicializarTimerParpadeo();
    }
    
   
    private void inicializarComponentes() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(43, 43, 43));
        
        // Panel de información superior
        lblInfo = new JLabel();
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 12));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(lblInfo, BorderLayout.NORTH);
        
        // Panel para los bloques
        panelBloques = new JPanel();
        panelBloques.setBackground(new Color(43, 43, 43));
        
        JScrollPane scroll = new JScrollPane(panelBloques);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        
        actualizarInfo();
    }
    

    private void dibujarDisco() {
        panelBloques.removeAll();
        mapaBloques.clear();
        
        int totalBloques = controlador.getGestorDisco().getTamanioTotal();
        Bloque[] bloques = controlador.getGestorDisco().getDisco().getBloques();
        
        // Calcular filas necesarias
        int filas = (int) Math.ceil((double) totalBloques / BLOQUES_POR_FILA);
        
        panelBloques.setLayout(new GridLayout(filas, BLOQUES_POR_FILA, 2, 2));
        
        // Crear un panel para cada bloque
        for (int i = 0; i < totalBloques; i++) {
            Bloque bloque = bloques[i];
            JPanel panelBloque = crearPanelBloque(bloque);
            panelBloques.add(panelBloque);
            mapaBloques.put(bloque.getNumeroBloque(), panelBloque);
        }
        
        panelBloques.revalidate();
        panelBloques.repaint();
    }
    

    private JPanel crearPanelBloque(Bloque bloque) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(TAMANIO_BLOQUE, TAMANIO_BLOQUE));
        panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        
        if (bloque.estaLibre()) {
            panel.setBackground(new Color(60, 60, 60)); // Gris oscuro para libre
        } else {
            Archivo archivo = bloque.getArchivoPropietario();
            if (archivo != null) {
                panel.setBackground(archivo.getColor());
            } else {
                panel.setBackground(Color.BLUE); // Por si acaso
            }
        }
        
        StringBuilder tooltip = new StringBuilder("<html>");
        tooltip.append("<b>Bloque #").append(bloque.getNumeroBloque()).append("</b><br>");
        tooltip.append("Estado: ").append(bloque.getEstado()).append("<br>");
        
        if (bloque.estaOcupado() && bloque.getArchivoPropietario() != null) {
            Archivo archivo = bloque.getArchivoPropietario();
            tooltip.append("Archivo: ").append(archivo.getNombre()).append("<br>");
            tooltip.append("Propietario: ");
            tooltip.append(archivo.getPropietario() != null ? 
                          archivo.getPropietario().getNombre() : "Sistema");
            tooltip.append("<br>");
            tooltip.append("Proceso Creador: ");
            tooltip.append(archivo.getProcesoCreador() != null ? 
                          archivo.getProcesoCreador() : "N/A");
        }
        
        tooltip.append("</html>");
        panel.setToolTipText(tooltip.toString());
        
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mostrarInfoBloque(bloque);
            }
        });
        
        return panel;
    }
    
    
    private void mostrarInfoBloque(Bloque bloque) {
        StringBuilder info = new StringBuilder();
        info.append("Bloque #").append(bloque.getNumeroBloque()).append("\n");
        info.append("Estado: ").append(bloque.getEstado()).append("\n\n");
        
        if (bloque.estaOcupado()) {
            Archivo archivo = bloque.getArchivoPropietario();
            if (archivo != null) {
                info.append("Archivo: ").append(archivo.getNombre()).append("\n");
                info.append("Tamaño: ").append(archivo.getTamanioEnBloques()).append(" bloques\n");
                info.append("Propietario: ");
                info.append(archivo.getPropietario() != null ? 
                           archivo.getPropietario().getNombre() : "Sistema");
                info.append("\n");
                info.append("Proceso Creador: ");
                info.append(archivo.getProcesoCreador() != null ? 
                           archivo.getProcesoCreador() : "N/A");
                info.append("\n");
                
                if (bloque.getSiguiente() != null) {
                    info.append("Siguiente bloque: #").append(bloque.getSiguiente().getNumeroBloque());
                } else {
                    info.append("Último bloque del archivo");
                }
            }
        } else {
            info.append("Bloque libre");
        }
        
        JOptionPane.showMessageDialog(
            this,
            info.toString(),
            "Información del Bloque",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
 
    private void actualizarInfo() {
        int total = controlador.getGestorDisco().getTamanioTotal();
        int ocupados = controlador.getGestorDisco().getBloquesOcupados();
        int libres = controlador.getGestorDisco().getBloquesLibres();
        double porcentaje = controlador.getGestorDisco().getPorcentajeUso();
        
        lblInfo.setText(String.format(
            "Bloques: %d/%d  |  Libres: %d  |  Uso: %.1f%%",
            ocupados, total, libres, porcentaje
        ));
    }
    
   
    public void actualizar() {
        actualizarInfo();
        dibujarDisco();
    }
    
    private void inicializarTimerParpadeo() {
        timerParpadeo = new Timer(300, e -> {
            if (bloqueResaltado != null) {
                JPanel panel = mapaBloques.get(bloqueResaltado);
                if (panel != null) {
                    Color colorActual = panel.getBackground();
                    if (colorActual.equals(Color.YELLOW) || colorActual.equals(Color.ORANGE)) {
                        Bloque[] bloques = controlador.getGestorDisco().getDisco().getBloques();
                        if (bloqueResaltado < bloques.length) {
                            Bloque bloque = bloques[bloqueResaltado];
                            if (bloque.estaLibre()) {
                                panel.setBackground(new Color(60, 60, 60));
                            } else {
                                Archivo archivo = bloque.getArchivoPropietario();
                                if (archivo != null) {
                                    panel.setBackground(archivo.getColor());
                                }
                            }
                        }
                    } else {
                        panel.setBackground(Color.YELLOW);
                    }
                    panel.repaint();
                }
            }
        });
    }
    

    private void resaltarBloque(int numeroBloque) {
        if (bloqueResaltado != null && bloqueResaltado != numeroBloque) {
            JPanel panelAnterior = mapaBloques.get(bloqueResaltado);
            if (panelAnterior != null) {
                Bloque[] bloques = controlador.getGestorDisco().getDisco().getBloques();
                if (bloqueResaltado < bloques.length) {
                    Bloque bloque = bloques[bloqueResaltado];
                    if (bloque.estaLibre()) {
                        panelAnterior.setBackground(new Color(60, 60, 60));
                    } else {
                        Archivo archivo = bloque.getArchivoPropietario();
                        if (archivo != null) {
                            panelAnterior.setBackground(archivo.getColor());
                        }
                    }
                    panelAnterior.repaint();
                }
            }
        }
        
        bloqueResaltado = numeroBloque;
        
        // Iniciar parpadeo
        if (!timerParpadeo.isRunning()) {
            timerParpadeo.start();
        }
    }
    
 
    private void detenerResaltado() {
        if (timerParpadeo.isRunning()) {
            timerParpadeo.stop();
        }
        
        if (bloqueResaltado != null) {
            JPanel panel = mapaBloques.get(bloqueResaltado);
            if (panel != null) {
                Bloque[] bloques = controlador.getGestorDisco().getDisco().getBloques();
                if (bloqueResaltado < bloques.length) {
                    Bloque bloque = bloques[bloqueResaltado];
                    if (bloque.estaLibre()) {
                        panel.setBackground(new Color(60, 60, 60));
                    } else {
                        Archivo archivo = bloque.getArchivoPropietario();
                        if (archivo != null) {
                            panel.setBackground(archivo.getColor());
                        }
                    }
                    panel.repaint();
                }
            }
        }
        
        bloqueResaltado = null;
    }
    

    @Override
    public void onSimulacionActualizada() {
        SwingUtilities.invokeLater(() -> {
            SimuladorIO.EstadoSimulacion estado = simulador.getEstadoActual();
            
            // Resaltar bloque SOLO cuando está asignando (procesando el bloque)
            if (estado == SimuladorIO.EstadoSimulacion.ASIGNANDO_BLOQUE) {
                int posicionCabezal = simulador.getPosicionCabezalActual();
                resaltarBloqueSuave(posicionCabezal);
                
            } else if (estado == SimuladorIO.EstadoSimulacion.MOVIENDO_CABEZAL) {
                // Durante movimiento, limpiar resaltado anterior sin marcar nada
                if (bloqueResaltado != null) {
                    JPanel panelAnterior = mapaBloques.get(bloqueResaltado);
                    if (panelAnterior != null) {
                        Bloque[] bloques = controlador.getGestorDisco().getDisco().getBloques();
                        if (bloqueResaltado < bloques.length) {
                            Bloque bloque = bloques[bloqueResaltado];
                            if (bloque.estaLibre()) {
                                panelAnterior.setBackground(new Color(60, 60, 60));
                            } else {
                                Archivo archivo = bloque.getArchivoPropietario();
                                if (archivo != null) {
                                    panelAnterior.setBackground(archivo.getColor());
                                }
                            }
                            panelAnterior.repaint();
                        }
                    }
                    bloqueResaltado = null;
                }
                
            } else if (estado == SimuladorIO.EstadoSimulacion.COMPLETADO ||
                       estado == SimuladorIO.EstadoSimulacion.ESPERANDO) {
                
                detenerResaltado();
                // Actualizar todo el disco para mostrar bloques asignados
                actualizarInfo();
                dibujarDisco();
            } else if (estado == SimuladorIO.EstadoSimulacion.PLANIFICANDO_IO) {
                // Actualizar info durante planificación
                actualizarInfo();
            }
        });
    }
    
    private void resaltarBloqueSuave(int numeroBloque) {
        // Detener parpadeo durante movimiento para visualización más suave
        if (timerParpadeo.isRunning()) {
            timerParpadeo.stop();
        }
        
        // Restaurar bloque anterior
        if (bloqueResaltado != null && bloqueResaltado != numeroBloque) {
            JPanel panelAnterior = mapaBloques.get(bloqueResaltado);
            if (panelAnterior != null) {
                Bloque[] bloques = controlador.getGestorDisco().getDisco().getBloques();
                if (bloqueResaltado < bloques.length) {
                    Bloque bloque = bloques[bloqueResaltado];
                    if (bloque.estaLibre()) {
                        panelAnterior.setBackground(new Color(60, 60, 60));
                    } else {
                        Archivo archivo = bloque.getArchivoPropietario();
                        if (archivo != null) {
                            panelAnterior.setBackground(archivo.getColor());
                        }
                    }
                    panelAnterior.repaint();
                }
            }
        }
        
        // Resaltar nuevo bloque
        bloqueResaltado = numeroBloque;
        JPanel panel = mapaBloques.get(numeroBloque);
        if (panel != null) {
            panel.setBackground(Color.YELLOW);
            panel.repaint();
        }
    }
}