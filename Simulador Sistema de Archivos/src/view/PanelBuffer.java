package view;

import controller.ControladorPrincipal;
import controller.GestorBuffer;
import javax.swing.*;
import java.awt.*;


public class PanelBuffer extends JPanel {
    
    private ControladorPrincipal controlador;
    private JTextArea txtBuffer;
    private JLabel lblInfo;
    private JComboBox<GestorBuffer.PoliticaReemplazo> comboPolitica;
    private JButton btnLimpiar;
    private JButton btnReiniciarEstadisticas;
    

    public PanelBuffer(ControladorPrincipal controlador) {
        this.controlador = controlador;
        inicializarComponentes();
    }
    

    private void inicializarComponentes() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(43, 43, 43));
        
        // Panel superior con información y controles
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        panelSuperior.setBackground(new Color(43, 43, 43));
        
        // Información del buffer
        lblInfo = new JLabel("Buffer: 0/20 bloques");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 10));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        panelSuperior.add(lblInfo, BorderLayout.WEST);
        
        // Panel de controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 2));
        panelControles.setBackground(new Color(43, 43, 43));
        
        JLabel lblPolitica = new JLabel("Política:");
        lblPolitica.setForeground(Color.WHITE);
        lblPolitica.setFont(new Font("Arial", Font.PLAIN, 9));
        panelControles.add(lblPolitica);
        
        comboPolitica = new JComboBox<>(GestorBuffer.PoliticaReemplazo.values());
        comboPolitica.setFont(new Font("Arial", Font.PLAIN, 9));
        comboPolitica.setPreferredSize(new Dimension(65, 20));
        comboPolitica.addActionListener(e -> cambiarPolitica());
        panelControles.add(comboPolitica);
        
        btnReiniciarEstadisticas = new JButton("🔄 Reiniciar");
        btnReiniciarEstadisticas.setFont(new Font("Arial", Font.PLAIN, 9));
        btnReiniciarEstadisticas.setPreferredSize(new Dimension(80, 20));
        btnReiniciarEstadisticas.setFocusPainted(false);
        btnReiniciarEstadisticas.addActionListener(e -> reiniciarEstadisticas());
        panelControles.add(btnReiniciarEstadisticas);
        
        btnLimpiar = new JButton("🧹 Limpiar");
        btnLimpiar.setFont(new Font("Arial", Font.PLAIN, 9));
        btnLimpiar.setPreferredSize(new Dimension(75, 20));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(e -> limpiarBuffer());
        panelControles.add(btnLimpiar);
        
        panelSuperior.add(panelControles, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);
        
        // Área de texto para mostrar el contenido del buffer
        txtBuffer = new JTextArea();
        txtBuffer.setEditable(false);
        txtBuffer.setBackground(new Color(50, 50, 50));
        txtBuffer.setForeground(Color.WHITE);
        txtBuffer.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtBuffer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scroll = new JScrollPane(txtBuffer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
        
        // Actualizar información inicial
        actualizar();
    }
    

    private void cambiarPolitica() {
        GestorBuffer.PoliticaReemplazo politica = 
            (GestorBuffer.PoliticaReemplazo) comboPolitica.getSelectedItem();
        if (politica != null) {
            controlador.cambiarPoliticaBuffer(politica);
            actualizar();
        }
    }
    
 
    private void reiniciarEstadisticas() {
        controlador.getGestorBuffer().reiniciarEstadisticas();
        actualizar();
        JOptionPane.showMessageDialog(
            this,
            "Estadísticas reiniciadas correctamente",
            "Estadísticas",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    

    private void limpiarBuffer() {
        int confirmar = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea limpiar el buffer?",
            "Confirmar Limpieza",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmar == JOptionPane.YES_OPTION) {
            controlador.getGestorBuffer().limpiarBuffer();
            actualizar();
            JOptionPane.showMessageDialog(
                this,
                "Buffer limpiado correctamente",
                "Buffer Limpio",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
 
    public void actualizar() {
        GestorBuffer buffer = controlador.getGestorBuffer();
        
        // Actualizar label superior
        lblInfo.setText(String.format("Buffer: %d/%d bloques | Hits: %d | Misses: %d",
            buffer.getTamanioActual(),
            buffer.getTamanioMaximo(),
            buffer.getHits(),
            buffer.getMisses()
        ));
        
        StringBuilder sb = new StringBuilder();
        sb.append(buffer.obtenerEstadisticas());
        sb.append("\n\n");
        sb.append(buffer.obtenerBloquesEnBuffer());
        
        txtBuffer.setText(sb.toString());
        
        comboPolitica.setSelectedItem(buffer.getPolitica());
    }
}
