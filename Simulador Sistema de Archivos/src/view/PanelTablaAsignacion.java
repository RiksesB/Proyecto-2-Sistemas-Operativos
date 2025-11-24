package view;

import controller.ControladorPrincipal;
import model.disco.TablaAsignacion;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;


public class PanelTablaAsignacion extends JPanel {
    
    private ControladorPrincipal controlador;
    private JTable table;
    private DefaultTableModel tableModel;
    private String[] columnas = {"Archivo", "Bloques", "Primer Bloque", "Propietario", "Proceso Creador", "Color"};
    

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
        table.setBackground(new Color(60, 60, 60)); 
        table.setForeground(Color.WHITE); 
        table.setFont(new Font("Consolas", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setGridColor(new Color(80, 80, 80));
        table.setSelectionBackground(new Color(70, 130, 180)); 
        table.setSelectionForeground(Color.WHITE);
        
        
        table.getColumnModel().getColumn(5).setCellRenderer(new ColorCellRenderer());
        table.getColumnModel().getColumn(5).setPreferredWidth(50);
        
        javax.swing.table.DefaultTableCellRenderer defaultRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(new Color(60, 60, 60));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(new Color(70, 130, 180));
                    c.setForeground(Color.WHITE);
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
        
        for (int i = 0; i < 5; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }
        
        table.getTableHeader().setBackground(new Color(50, 50, 50));
        table.getTableHeader().setForeground(Color.BLACK); // Texto negro para header blanco
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(60, 60, 60));
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
                    entrada.getProcesoCreador(),
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
                    setForeground(Color.BLACK);
                    setText("");
                } catch (NumberFormatException e) {
                    setBackground(new Color(60, 60, 60));
                    setForeground(Color.WHITE);
                    setText("N/A");
                }
            } else {
                setBackground(new Color(60, 60, 60));
                setForeground(Color.WHITE);
            }
            
            if (isSelected) {
                setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
            } else {
                setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
            }
            
            return this;
        }
    }
    

    public void actualizar() {
        actualizarTabla();
    }
}