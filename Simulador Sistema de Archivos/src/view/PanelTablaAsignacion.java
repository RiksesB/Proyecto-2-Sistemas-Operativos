package view;

import controller.ControladorPrincipal;
import model.disco.TablaAsignacion;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * muestra la tabla de asignación de archivos
 */
public class PanelTablaAsignacion extends JPanel {
    
    private ControladorPrincipal controlador;
    private JTable table;
    private DefaultTableModel tableModel;
    private String[] columnas = {"Archivo", "Bloques", "Primer Bloque", "Propietario", "Color"};
    
 
    public PanelTablaAsignacion(ControladorPrincipal controlador) {
        this.controlador = controlador;
        inicializarComponentes();
        actualizarTabla();
    }
    
 
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBackground(new Color(43, 43, 43));
        
   
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable
            }
        };
        
        table = new JTable(tableModel);
        table.setBackground(new Color(50, 50, 50));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setRowHeight(25);
        table.setGridColor(new Color(80, 80, 80));
        table.setSelectionBackground(new Color(75, 110, 175));
    
        table.getColumnModel().getColumn(4).setCellRenderer(new ColorCellRenderer());
        table.getColumnModel().getColumn(4).setPreferredWidth(50);
       
        table.getTableHeader().setBackground(new Color(60, 63, 65));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
    }
    
 
    private void actualizarTabla() {
        tableModel.setRowCount(0); // Limpiar
        
        TablaAsignacion tabla = controlador.getGestorDisco().getTablaAsignacion();
        
        for (int i = 0; i < tabla.getEntradas().getTamanio(); i++) {
            TablaAsignacion.EntradaTabla entrada = tabla.getEntradas().obtener(i);
            if (entrada != null) {
                Object[] fila = {
                    entrada.getNombreArchivo(),
                    entrada.getCantidadBloques(),
                    "#" + entrada.getPrimerBloque(),
                    entrada.getPropietario(),
                    entrada.getColorHex()
                };
                tableModel.addRow(fila);
            }
        }
    }
    

    private class ColorCellRenderer extends JLabel implements TableCellRenderer {
        
        public ColorCellRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (value != null && value instanceof String) {
                try {
                    Color color = Color.decode((String) value);
                    setBackground(color);
                    setText("");
                } catch (NumberFormatException e) {
                    setBackground(Color.GRAY);
                    setText("N/A");
                }
            }
            
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            return this;
        }
    }
    
 
    public void actualizar() {
        actualizarTabla();
    }
}
