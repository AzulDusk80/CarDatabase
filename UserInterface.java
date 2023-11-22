import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInterface {
    private static JFrame frame = new JFrame("The Car Database");
    private static JPanel panel = new JPanel();
    private static String[][] dataset;
    int count = 0;

    public UserInterface(Database data) {
        String[] logins = new String[2];
        // Create a JLabel (text label)
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");

        JTextField userTextField = new JTextField(20);
        JPasswordField passPasswordField = new JPasswordField(20);
        // Create a JButton (clickable button)
        JButton button = new JButton("Enter Username");

        // Add an ActionListener to the button so we can get the password and username
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userTextField.getText();
                String pass = new String(passPasswordField.getPassword()); // Note: Use getPassword() for security
                logins[0] = username;
                logins[1] = pass;
                //check if valid password and username
                if(data.isUser(logins)){
                    getSubset();
                }
            }
        });

        panel.add(userLabel);
        panel.add(userTextField);
        panel.add(passLabel);
        panel.add(passPasswordField);
        panel.add(button);

        // Add the panel to the frame
        frame.add(panel);

        // Set default close operation (exit when the window is closed)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the size of the window
        frame.setSize(750, 500);

        // Set the window to be visible
        frame.setVisible(true);

    }

    public void getSubset(){
        clear();

        JButton simple = new JButton("Simple Search");
        JButton complex = new JButton("Specified Search");
        JButton button = new JButton("All Search");

        simple.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpleSearch();
            }
        });

        complex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                complexSearch();
            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fullSearch();
            }
        });
        
        panel.add(simple);
        panel.add(complex);
        panel.add(button);
    }

    public void simpleSearch(){
        String[][] data = {{"a", "b", "c", "d", "e"}, {"f", "g", "h", "i", "j"}, {"k", "l", "m", "n", "o"}};
        mainPage(data);
    }

    public void complexSearch(){
        System.out.println("b");
    }

    public void fullSearch(){
        System.out.println("c");
    }

    //will be the main page to display all information
    public void mainPage(String[][] data){
        clear();

        dataset = data;
        count = 0;

        Object[][] dataTable = new Object[data.length][data[0].length + 1];
            for(int i = 0; i < data.length; i++){
                for(int j = 0; j < data[0].length; j++){
                    dataTable[i][j] = data[i][j];
                }
                dataTable[i][data[0].length] = "details";
            }

        String[] title = {"a!S", "b", "c","d", "e", "details"};
        JTable table = new JTable(dataTable, title);
        table.getColumnModel().getColumn(data[0].length).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(data[0].length).setCellEditor(new ButtonEditor());
        panel.add(new JScrollPane(table));
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;

        public ButtonEditor() {
            super(new JTextField());
            button = new JButton("Details");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    details(dataset[count][0]);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText((value == null) ? "" : value.toString());
            return button;
        }
    }


    //gets the details on a certain car
    public void details(String vin){
        clear();

        JLabel label = new JLabel(vin);
        panel.add(label);
    }

    //clears the panel
    public void clear(){
        panel.removeAll();
        panel.updateUI();
    }
}

