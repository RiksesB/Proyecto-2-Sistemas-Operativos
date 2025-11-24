package view;

import javax.swing.*;
import java.awt.*;


public class PanelLog extends JPanel {
    
    private JTextArea txtLog;
    private JScrollPane scrollPane;
    private static final int MAX_LINEAS = 1000;
    

    public PanelLog() {
        inicializarComponentes();
    }
    

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBackground(new Color(43, 43, 43));
        
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setBackground(new Color(20, 20, 20));
        txtLog.setForeground(new Color(0, 255, 0)); // Verde estilo terminal
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtLog.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        scrollPane = new JScrollPane(txtLog);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        panelBotones.setBackground(new Color(50, 50, 50));
        
        JButton btnLimpiar = new JButton("🗑️ Limpiar");
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setToolTipText("Limpiar el log");
        btnLimpiar.addActionListener(e -> limpiar());
        panelBotones.add(btnLimpiar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        agregarMensaje("=== SISTEMA DE ARCHIVOS INICIADO ===");
        agregarMensaje("Esperando operaciones...\n");
    }
    
  
    public void agregarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append(mensaje + "\n");
            
            String texto = txtLog.getText();
            String[] lineas = texto.split("\n");
            if (lineas.length > MAX_LINEAS) {
                StringBuilder nuevoTexto = new StringBuilder();
                for (int i = lineas.length - MAX_LINEAS; i < lineas.length; i++) {
                    nuevoTexto.append(lineas[i]).append("\n");
                }
                txtLog.setText(nuevoTexto.toString());
            }
            
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }
    
    public void agregarError(String mensaje) {
        agregarMensaje("[ERROR] " + mensaje);
    }

    public void agregarExito(String mensaje) {
        agregarMensaje("[✓] " + mensaje);
    }
    
  
    public void agregarAdvertencia(String mensaje) {
        agregarMensaje("[⚠] " + mensaje);
    }
    

    public void agregarSeparador() {
        agregarMensaje("─".repeat(60));
    }
    

    public void limpiar() {
        txtLog.setText("");
        agregarMensaje("=== LOG LIMPIADO ===\n");
    }
    

    public void actualizar() {
        // No necesita hacer nada
    }
}