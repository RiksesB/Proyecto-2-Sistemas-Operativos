package view;

import controller.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;

/**
 * panel que muestra el buffer 
 * 
 */
public class PanelBuffer extends JPanel {
    
    private ControladorPrincipal controlador;
    private JTextArea txtBuffer;
    private JLabel lblInfo;
    

    public PanelBuffer(ControladorPrincipal controlador) {
        this.controlador = controlador;
        inicializarComponentes();
    }
    

    private void inicializarComponentes() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(43, 43, 43));
        

        lblInfo = new JLabel("Buffer: 0/10 entradas");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 12));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(lblInfo, BorderLayout.NORTH);
        

        txtBuffer = new JTextArea();
        txtBuffer.setEditable(false);
        txtBuffer.setBackground(new Color(50, 50, 50));
        txtBuffer.setForeground(Color.WHITE);
        txtBuffer.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtBuffer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scroll = new JScrollPane(txtBuffer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
        

        txtBuffer.setText("Buffer no implementado\n\nEste es un componente opcional.");
    }
    

    public void actualizar() {
 
        lblInfo.setText("Buffer: N/A");
    }
}