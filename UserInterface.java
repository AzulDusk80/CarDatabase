import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInterface {
    private static JFrame frame = new JFrame("The Car Database");
    private static JPanel panel = new JPanel();
    private static JTable table;
    private static Database carDatabase;
    private static String[][] data;
    private static String searchString;
    private static boolean permission = false;
    private static String[] logins = new String[2];
    private static JTextArea resultArea = new JTextArea(10, 40);
    

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
                simpleSearchPageOptions();
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

    public void simpleSearchPageOptions(){
        clear();

        JButton sellerCategoryButton = new JButton("Search Seller");
        JButton carCategoryButton = new JButton("Search Car");
        JButton manufactureCategoryButton = new JButton("Search Manufacture");
        sellerCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String category = "Seller";
                simpleSearchPage(category);
            }
        });

        carCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String category = "Car";
                simpleSearchPage(category);
            }
        });

        manufactureCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String category = "Manufacture";
                simpleSearchPage(category);
            }
        });
        panel.add(sellerCategoryButton);
        panel.add(carCategoryButton);
        panel.add(manufactureCategoryButton);
    }

    public void simpleSearchPage(String category){
        clear();
        //GUI components
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        //Add components to frame
        panel.add(new JLabel("Enter Search Keyword:"));
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(new JScrollPane(resultArea));

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText().trim();
                simpleSearch(category, keyword, logins[0], logins[1], Database.databaseURL);
            }
        });
        
    }

    //searches the information based on one element
    public void simpleSearch(String category, String keyword, String user, String pass, String url){
        //NOAH
        System.out.println("a");
        String prefix;
        String column;
        if ("Seller".equals(category)) {
            prefix = "s.";
            column = "Seller s";

        } else if ("Car".equals(category)) {
            prefix = "c.";
            column = "Car c";

        } else if ("Manufacture".equals(category)) {
            prefix = "m.";
            column = "Manufacture m";
        } else {
            return;
        }
        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            //SQL query
            String query = "SELECT * FROM " + column + " WHERE " + prefix + "column_name LIKE ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + keyword + "%");
                ResultSet resultSet = statement.executeQuery();

                // Display the results in the text area
                resultArea.setText("");
                while (resultSet.next()) {
                    // Customize this part based on your table structure
                    String result = resultSet.getString("column_name1") + "\t"
                            + resultSet.getString("column_name2") + "\t"
                            + resultSet.getString("column_name3") + "\n";
                    resultArea.append(result);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            resultArea.setText("Error occurred while searching the database.");
        }
    
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

