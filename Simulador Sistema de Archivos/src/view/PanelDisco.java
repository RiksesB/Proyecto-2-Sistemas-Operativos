package view;

import controller.ControladorPrincipal;
import model.archivos.Archivo;
import model.disco.Bloque;
import javax.swing.*;
import java.awt.*;

/**
 *  visualización del disco
 */
public class PanelDisco extends JPanel {
    
    private ControladorPrincipal controlador;
    private JPanel panelBloques;
    private JLabel lblInfo;
    private static final int BLOQUES_POR_FILA = 20;
    private static final int TAMANIO_BLOQUE = 25;
    

    public PanelDisco(ControladorPrincipal controlador) {
        this.controlador = controlador;
        inicializarComponentes();
        dibujarDisco();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(43, 43, 43));
        
   
        lblInfo = new JLabel();
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 12));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(lblInfo, BorderLayout.NORTH);
        

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
        
        int totalBloques = controlador.getGestorDisco().getTamanioTotal();
        Bloque[] bloques = controlador.getGestorDisco().getDisco().getBloques();
        

        int filas = (int) Math.ceil((double) totalBloques / BLOQUES_POR_FILA);
        
        panelBloques.setLayout(new GridLayout(filas, BLOQUES_POR_FILA, 2, 2));

        for (int i = 0; i < totalBloques; i++) {
            Bloque bloque = bloques[i];
            JPanel panelBloque = crearPanelBloque(bloque);
            panelBloques.add(panelBloque);
        }
        
        panelBloques.revalidate();
        panelBloques.repaint();
    }
    

    private JPanel crearPanelBloque(Bloque bloque) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(TAMANIO_BLOQUE, TAMANIO_BLOQUE));
        panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        

        if (bloque.estaLibre()) {
            panel.setBackground(new Color(60, 60, 60)); 
        } else {

            Archivo archivo = bloque.getArchivoPropietario();
            if (archivo != null) {
                panel.setBackground(archivo.getColor());
            } else {
                panel.setBackground(Color.BLUE); 
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
}