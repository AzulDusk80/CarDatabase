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
    private static JTable table;
    private static Database carDatabase;
    private static String[][] data;
    private static String searchString;
    private static boolean permission = false;

    public UserInterface(Database data) {
        carDatabase = data;
        JLabel label = new JLabel("Welcome");
        panel.add(label);
        // Add the panel to the frame
        frame.add(panel);

        // Set default close operation (exit when the window is closed)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the size of the window
        frame.setSize(750, 500);

        // Set the window to be visible
        frame.setVisible(true);
    }

    //start from login page
    public void start(){
        clear();

        String[] logins = new String[2];
        // Create a JLabel (text label)
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");

        JTextField userTextField = new JTextField(20);
        JPasswordField passPasswordField = new JPasswordField(20);
        // Create a JButton (clickable button)
        JButton button = new JButton("Enter");
        JButton button2 = new JButton("Register");

        // Add an ActionListener to the button so we can get the password and username
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userTextField.getText();
                String pass = new String(passPasswordField.getPassword()); // Note: Use getPassword() for security
                logins[0] = username;
                logins[1] = pass;
                //check if valid password and username
                if(carDatabase.isUser(logins)){
                    permission = carDatabase.hasPermission(logins[0]);
                    getSubset();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Not a valid account!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                userRegister();
            }    
        });

        panel.add(userLabel);
        panel.add(userTextField);
        panel.add(passLabel);
        panel.add(passPasswordField);
        panel.add(button);
        panel.add(button2);
        panel.updateUI();
    }

    //adds a new user
    public void userRegister(){
        clear();
        String[] account = new String[4];
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JLabel addressLabel = new JLabel("Address:");
        JLabel phoneLabel = new JLabel("Phone Number:");

        JTextField userTextField = new JTextField(20);
        JTextField passTextField = new JTextField(20);
        JTextField addressTextField = new JTextField(20);
        JTextField phoneTextField = new JTextField(20);

        JButton addUser = new JButton("Register");
        addUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                account[0] = userTextField.getText();
                account[1] = passTextField.getText();
                account[2] = addressTextField.getText();
                account[3] = phoneTextField.getText();
                if(!carDatabase.isUsed(account[0]) && account[0] != null){
                    carDatabase.registerUser(account);
                    start();
                }
                else
                    JOptionPane.showMessageDialog(null, "Not a valid account!", "Error", JOptionPane.ERROR_MESSAGE);
            }    
        });

        JButton goBack = new JButton("Back");
        goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                start();
            }
        });

        panel.add(userLabel);
        panel.add(userTextField);
        panel.add(passLabel);
        panel.add(passTextField);
        panel.add(addressLabel);
        panel.add(addressTextField);
        panel.add(phoneLabel);
        panel.add(phoneTextField);
        panel.add(addUser);
        panel.add(goBack);
    }
    
    //get the user desiered search
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

        if(permission){
            JButton regCar = new JButton("Register a New Car");

            panel.add(regCar);
        }
    }

    //searches the information based on one element
    public void simpleSearch(){
        //NOAH
        System.out.println("a");
    }

    //searches the information based on multiple elements
    public void complexSearch(){
        //NOAH
        System.out.println("b");
    }

    //gets the whole database
    public void fullSearch(){
        System.out.println("Loading...");
        String[][] searchData = carDatabase.sqlCommand("SELECT c.vin,c.model_name,s.dealer_zip,s.city,c.price, m.make_name, c.year, c.has_accidents From Car c, Seller s, Manufacture m WHERE c.idenetification = s.idenetification AND m.make_name = c.make_name");
        System.out.println("Completed");
        mainPage(searchData, "Full Car Search");
    }

    //will be the main page to display all information in a table
    public void mainPage(String[][] searchData, String SearchString){
        clear();
        data = searchData;
        searchString = SearchString;
        JLabel seachLabel = new JLabel(SearchString);
        JButton goBack = new JButton("Back");
        goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                getSubset();
            }
        });

        Object[][] dataTable = new Object[data.length][data[0].length + 1];
            for(int i = 0; i < data.length; i++){
                for(int j = 0; j < data[0].length; j++){
                    dataTable[i][j] = data[i][j];
                }
                dataTable[i][data[0].length] = "details";
            }

        String[] title = {"Vin", "Model Name", "Dealer Zip","City", "Price", "Make Name", "Year", "Accidents", "Details"};
        table = new JTable(dataTable, title);
        table.getColumnModel().getColumn(data[0].length).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(data[0].length).setCellEditor(new ButtonEditor());

        panel.add(seachLabel);
        panel.add(goBack);
        panel.add(new JScrollPane(table));
    }
    //used to make a table button possible
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
    //used to make a table buttons work
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;

        public ButtonEditor() {
            super(new JTextField());
            button = new JButton("Details");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //makes it so the button sends out information from the first column of each row
                    Object value = table.getValueAt(table.getSelectedRow(), 0);
                    if (value != null) {
                        details(value.toString());
                    }
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
        //back button
        JButton goBack = new JButton("Back");
        goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                mainPage(data, searchString);
            }
        });
        //gives people with the permission to edit or delete
        if(permission){
        //edit data
            JButton editBut = new JButton("Edit");
        //delete
            JButton deleteBut = new JButton("Remove Car");

            panel.add(editBut);
            panel.add(deleteBut);
        }

        //NOAH add details
        JLabel label = new JLabel(vin);
        panel.add(goBack);//you can move this where ever you want, just the back button
        panel.add(label);
    }

    //clears the panel, allows for new objects
    public void clear(){
        panel.removeAll();
        panel.updateUI();
    }
}

