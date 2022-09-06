import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Mapa {
    private JFrame frame;
    private JPanel panel;
    private JTable table;
    private JScrollPane tableContainer;
    private DefaultTableModel model_table;
    private String[][] data;
    private ImageIcon[][] data_images;
    private HashMap<String, ImageIcon> objects;
    private Integer CELL_SIZE = 20;
    private Integer turn = 0;



    public Mapa(ArrayList<InfoPosition> pos_jogadoresA, ArrayList<InfoPosition> pos_jogadoresB) {
        objects = new HashMap<>();
        ImageIcon playerA = new ImageIcon(getClass().getResource("assets/playerA.png")); // load the image to a imageIcon
        Image image = playerA.getImage(); // transform it
        Image newimg = image.getScaledInstance(CELL_SIZE, CELL_SIZE,  Image.SCALE_SMOOTH); // scale it the smooth way
        playerA = new ImageIcon(newimg);  // transform it back
        objects.put("playerA", playerA);


        ImageIcon playerB = new ImageIcon(getClass().getResource("assets/playerB.png")); // load the image to a imageIcon
        image = playerB.getImage(); // transform it
        newimg = image.getScaledInstance(CELL_SIZE, CELL_SIZE,  Image.SCALE_SMOOTH); // scale it the smooth way
        playerB = new ImageIcon(newimg);  // transform it back
        objects.put("playerB", playerB);

        ImageIcon background1 = new ImageIcon(getClass().getResource("assets/background1.png")); // load the image to a imageIcon
        image = background1.getImage(); // transform it
        newimg = image.getScaledInstance(CELL_SIZE, CELL_SIZE,  Image.SCALE_SMOOTH); // scale it the smooth way
        background1 = new ImageIcon(newimg);  // transform it back
        objects.put("background1", background1);

        ImageIcon background2 = new ImageIcon(getClass().getResource("assets/background2.png")); // load the image to a imageIcon
        image = background2.getImage(); // transform it
        newimg = image.getScaledInstance(CELL_SIZE, CELL_SIZE,  Image.SCALE_SMOOTH); // scale it the smooth way
        background2 = new ImageIcon(newimg);  // transform it back
        objects.put("background2", background2);

        // set frame site
        this.frame = new JFrame("Agentes e Sistemas MultiAgente");

        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout());
        this.data = new String[35][35];
        this.data_images = new ImageIcon[35][35];

        this.model_table = new DefaultTableModel(35,35);


        update(pos_jogadoresA,pos_jogadoresB);


        this.table = new JTable(this.model_table);
        CellColorRenderer2 renderer = new CellColorRenderer2(this.data);
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
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
        this.frame.setAlwaysOnTop(true);

    }


    public void update(ArrayList<InfoPosition> pos_jogadoresA, ArrayList<InfoPosition> pos_jogadoresB) {
        for(int i = 0; i < 35; i++){
            for(int j = 0; j < 35; j++){
                this.data[i][j] = "#ddd1a7";
                if ((i + j) % 2 == 0) {
                    this.data_images[i][j] = this.objects.get("background1");
                }
                else{
                    this.data_images[i][j] = this.objects.get("background2");
                }
            }
        }

        for(InfoPosition i : pos_jogadoresA){
            int x = i.getPosition().getX();
            int y = i.getPosition().getY();

            this.data_images[x-1][y-1] = this.objects.get("playerA");
        }

        for(InfoPosition i : pos_jogadoresB){
            int x = i.getPosition().getX();
            int y = i.getPosition().getY();

            this.data_images[x-1][y-1] = this.objects.get("playerB");
        }

    }


    public void updateGUI(int a, int b, int score){
        for(int i = 0; i < 35; i++){
            for(int j = 0; j < 35; j++){
                this.table.setValueAt(this.data_images[j][i],j,i);
            }
        }
        DefaultTableModel tableModel = (DefaultTableModel) this.table.getModel();
        tableModel.fireTableDataChanged();

        if(turn > 1){
            if(a == 0) {
                System.out.println("***** EQUIPA B GANHOU *****");
                System.out.println("Nr Jogadores restantes: " + b);

                new pop(1, score, this.frame);
            }
            if(b == 0) {
                System.out.println("***** EQUIPA A GANHOU *****");
                System.out.println("Nr Jogadores restantes: " + a);
                new pop(0, score, this.frame);
            }
        }
        turn++;
    }

}


class CellColorRenderer2 extends DefaultTableCellRenderer {
    private String[][] data;
    CellColorRenderer2(String[][] data) {
        super();
        this.data = data;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,   boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        ((JLabel) cell).setIcon( (ImageIcon) value );
        cell.setBackground(Color.decode(this.data[row][column]));
        return cell;
    }
}

class pop extends JFrame {
    pop(int winner, int score, JFrame parent) {
        // create a frame
        JFrame f = new JFrame("Fim de Jogo");


        if(winner == 0)
            JOptionPane.showMessageDialog(parent,
                    "Equipa A ganhou!\nScore: " + score,"Fim de Jogo",
                    JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(parent,
                    "Equipa B ganhou!\nScore: " + score,"Fim de Jogo",
                    JOptionPane.INFORMATION_MESSAGE);
    }
}
