import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;


public class MapaDebug {
    private JFrame frame;
    private JPanel panel;
    private JTable table;
    private JScrollPane tableContainer;
    private DefaultTableModel model_table;
    private String[][] data;
    private Integer CELL_SIZE = 20;



    public MapaDebug(ArrayList<InfoPosition> pos_jogadoresA, ArrayList<InfoPosition> pos_jogadoresB) {

        // set frame site
        this.frame = new JFrame("Agentes e Sistemas MultiAgente");

        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout());
        this.data = new String[35][35];


        this.model_table = new DefaultTableModel(35,35);

        for(int i = 0; i < 35; i++){
            for(int j = 0; j < 35; j++) {
                this.data[i][j] = "#f9ebea";
            }
        }

        update(pos_jogadoresA,pos_jogadoresB);


        this.table = new JTable(this.model_table);
        CellColorRenderer renderer = new CellColorRenderer(this.data);
        this.table.setRowHeight(CELL_SIZE);
        TableColumnModel columnModel = this.table.getColumnModel();
        for(int i = 0; i < 35; i++){
            columnModel.getColumn(i).setCellRenderer(renderer);
            columnModel.getColumn(i).setPreferredWidth(CELL_SIZE);
        }

        this.table.setPreferredScrollableViewportSize(table.getPreferredSize());

        this.table.setDefaultEditor(Object.class, null);
        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        this.tableContainer = new JScrollPane(table);
        this.table.setTableHeader(null);

        this.panel.add(tableContainer, BorderLayout.CENTER);
        this.frame.getContentPane().add(panel);

        this.frame.pack();
        this.frame.setVisible(true);
        this.frame.setAlwaysOnTop(true);

    }


    public void update(ArrayList<InfoPosition> pos_jogadoresA, ArrayList<InfoPosition> pos_jogadoresB) {
        for(int i = 0; i < 35; i++){
            for(int j = 0; j < 35; j++){
                this.data[i][j] = "#f9ebea";
            }
        }

        for(InfoPosition i : pos_jogadoresA){
            int x = i.getPosition().getX();
            int y = i.getPosition().getY();

            data[y-1][x-1] = "#FF0000";
        }

        for(InfoPosition i : pos_jogadoresB){
            int x = i.getPosition().getX();
            int y = i.getPosition().getY();
            data[y-1][x-1] = "#0000FF";
        }

    }


    public void updateGUI(){

        DefaultTableModel tableModel = (DefaultTableModel) this.table.getModel();
        tableModel.fireTableDataChanged();
    }

}


class CellColorRenderer extends DefaultTableCellRenderer {
    private String[][] data;
    CellColorRenderer(String[][] data) {
        super();
        this.data = data;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,   boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        cell.setBackground(Color.decode(this.data[row][column]));
        return cell;
    }
}