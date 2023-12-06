import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInterface {
    private static JFrame frame = new JFrame("The Car Database");
    private static JPanel panel = new JPanel();
    private static JTable table;
    private static Database carDatabase;
    private static String[][] data;
    private static String searchString;
    private static boolean permission = false;
    private static String[] logins = new String[2];
    private JTextField searchField;
    private JComboBox<String> tableComboBox;
    private JList<String> columnList;
    private JComboBox<String> columnComboBox;
    private JTextArea resultArea;
    private String databaseURL = Database.databaseURL;
    private String username = Database.netID;
    private String password = Database.password;
    private String user;
    

    public UserInterface(Database data) {
        carDatabase = data;
        JLabel label = new JLabel("Welcome");
        panel.add(label);
        // Add the panel to the frame
        frame.add(panel);

        // Set default close operation (exit when the window is closed)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the size of the window
        frame.setSize(1500, 1000);

        // Set the window to be visible
        frame.setVisible(true);
    }

    //start from login page
    public void start(){
        clear();
        frame.setTitle("The Car Database");

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
                    user = logins[0];
                    System.out.println(logins[0]);
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
        JButton specified = new JButton("Specified Search");
        JButton column = new JButton("Column Search");
        JButton button = new JButton("All Search");

        simple.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displaySimpleSearch();
            }
        });

        specified.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displaySpecificSearch();
            }
        });

        column.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayColumnSearch();
            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fullSearch();
            }
        });
        
        panel.add(simple);
        panel.add(specified);
        panel.add(column);
        panel.add(button);

        JLabel vinLabel = new JLabel("Vin:");
        JTextField vinTextField = new JTextField(20);
        JButton findCar = new JButton("Car Search");
        findCar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (carDatabase.validVin(vinTextField.getText())) {
                    details(vinTextField.getText(), 1);
                }
                else
                    JOptionPane.showMessageDialog(null, "Not a valid Vin!", "Invalid", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(vinLabel);
        panel.add(vinTextField);
        panel.add(findCar);


        if(permission){
            frame.setTitle("Employee: " + user);
            JButton regCar = new JButton("Register a New Car");
            regCar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    if (carDatabase.newCar(vinTextField.getText())) {
                        fileCar(vinTextField.getText(), 0);
                    }
                    else
                        JOptionPane.showMessageDialog(null, "Not a valid Vin!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            panel.add(regCar);
        }
        else{
            JButton userHistory = new JButton("Bookmaked Purchase");
            userHistory.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    purchaseList();
                }
            });
            panel.add(userHistory);
        }

        JButton logout = new JButton("Logout");
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                start();
            }
        });
        panel.add(logout);

    }

    public void purchaseList(){
        clear();

        JLabel title = new JLabel("Bookmarks: ");
        JButton bckButton = new JButton("Back");
        bckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                getSubset();
            }
        });
        
        panel.add(title);
        String[][] s = carDatabase.sellsToList(user);
        for (int i = 1; i < s.length; i++) {
                String vin = carDatabase.vinFromId(s[i][0]);
                JLabel book = new JLabel("Seller ID: " + s[i][0]);
                panel.add(book);
                JButton details = new JButton("Details");
                details.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e){
                        details(vin, 1);
                    }
                });
                panel.add(details);
        }

        panel.add(bckButton);
    }

    public void displaySimpleSearch() {
        clear();
    
        //GUI components
        searchField = new JTextField(20);
        tableComboBox = new JComboBox<>(getTables());
        columnComboBox = new JComboBox<>(getColumns(tableComboBox.getSelectedItem().toString()));  
        JButton searchButton = new JButton("Search");
        resultArea = new JTextArea(25, 80);
        JButton goBack = new JButton("Back");
        
        panel.add(goBack);
        panel.add(new JLabel("Select Table:"));
        panel.add(tableComboBox);
        panel.add(new JLabel("Select Column:"));
        panel.add(new JScrollPane(columnComboBox));  
        panel.add(new JLabel("Enter Search Keyword:"));
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(new JScrollPane(resultArea));
    
        //Action listeners
        tableComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateColumnComboBox();  
            }
        });
    
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = tableComboBox.getSelectedItem().toString();
                String columnName = columnComboBox.getSelectedItem().toString();  
                String keyword = searchField.getText().trim();
                simpleSearch(tableName, columnName, keyword);
            }
        });

        goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                getSubset();
            }
        });
    }
    
    
    private void updateColumnComboBox() {
        String selectedTable = tableComboBox.getSelectedItem().toString();
        columnComboBox.setModel(new DefaultComboBoxModel<>(getColumns(selectedTable)));
    }

    //searches the information based on one element
    public void simpleSearch(String tableName, String columnName, String keyword){
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password)) {
            String query = "SELECT * FROM " + tableName + " WHERE " + columnName + " LIKE ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + keyword + "%");
    
                ResultSet resultSet = statement.executeQuery();
    
                //Process the result set and display in resultArea
                resultArea.setText("");
    
                //Display column names above the result area
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                StringBuilder columnHeader = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    columnHeader.append(metaData.getColumnName(i)).append("\t");
                }
                resultArea.append(columnHeader.toString().trim() + "\n");
    
                //Display the actual results
                while (resultSet.next()) {
                    StringBuilder result = new StringBuilder();
                    for (int i = 1; i <= columnCount; i++) {
                        result.append(resultSet.getString(i)).append("\t");
                    }
                    resultArea.append(result.toString().trim() + "\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resultArea.setText("Error occurred while searching the database.");
        }
            
    }
           
        public void displaySpecificSearch(){
        clear();

        //GUI components
        searchField = new JTextField(20);
        tableComboBox = new JComboBox<>(getTables());
        columnList = new JList<>(getColumns(tableComboBox.getSelectedItem().toString()));
        JButton searchButton = new JButton("Search");
        resultArea = new JTextArea(25, 80);
        JButton goBack = new JButton("Back");
        
        panel.add(goBack);
        panel.add(new JLabel("Select Table:"));
        panel.add(tableComboBox);
        panel.add(new JLabel("Select Column(s) (Ctrl+Click to select multiple):"));
        panel.add(new JScrollPane(columnList));
        panel.add(new JLabel("Enter Search Keyword:"));
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(new JScrollPane(resultArea));

        //Action listeners
        tableComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateColumnList();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = tableComboBox.getSelectedItem().toString();
                String[] columns = columnList.getSelectedValuesList().toArray(new String[0]);
                String keyword = searchField.getText().trim();
                specificSearch(tableName, columns, keyword);
            }
        });
        
        goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                getSubset();
            }
        });
    }

    private void updateColumnList() {
        String selectedTable = tableComboBox.getSelectedItem().toString();
        columnList.setListData(getColumns(selectedTable));
    }

    private String[] getTables() {
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password)) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", null);

            List<String> tableNames = new ArrayList<>();
            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME"));
            }
            tableNames.remove(tableNames.size() - 1);
            tableNames.remove(tableNames.size() - 1);

            return tableNames.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    private String[] getColumns(String tableName) {
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password)) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);

            List<String> columnNames = new ArrayList<>();
            while (columns.next()) {
                columnNames.add(columns.getString("COLUMN_NAME"));
            }

            return columnNames.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    private void specificSearch(String tableName, String[] columns, String keyword) {
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password)) {
            StringBuilder queryBuilder = new StringBuilder("SELECT ");
            if (columns.length == 0) {
                queryBuilder.append("*");
            } else {
                for (int i = 0; i < columns.length; i++) {
                    queryBuilder.append(columns[i]);
                    if (i < columns.length - 1) {
                        queryBuilder.append(", ");
                    }
                }
            }
            queryBuilder.append(" FROM ").append(tableName).append(" WHERE ");

            for (int i = 0; i < columns.length; i++) {
                queryBuilder.append(columns[i]).append(" LIKE ?");

                if (i < columns.length - 1) {
                    queryBuilder.append(" OR ");
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
                for (int i = 0; i < columns.length; i++) {
                    statement.setString(i + 1, "%" + keyword + "%");
                }

                ResultSet resultSet = statement.executeQuery();
                resultArea.setText("");

                //Display column names above the data
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                StringBuilder columnHeader = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    columnHeader.append(metaData.getColumnName(i)).append("\t");
                }
                resultArea.append(columnHeader.toString().trim() + "\n");

                //Process the result and display in resultArea
                while (resultSet.next()) {
                    StringBuilder result = new StringBuilder();
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        result.append(resultSet.getString(i)).append("\t");
                    }
                    resultArea.append(result.toString().trim() + "\n");
                }

                
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resultArea.setText("Error occurred while searching the database.");
        }
    }

     public void displayColumnSearch(){
        clear();

        //GUI components
        searchField = new JTextField(20);
        columnList = new JList<>(getAllColumns());
        JButton searchButton = new JButton("Search");
        resultArea = new JTextArea(25, 80);
        JButton goBack = new JButton("Back");
            
        panel.add(goBack);
        panel.add(new JLabel("Select Column(s) (Ctrl+Click to select multiple):"));
        panel.add(new JScrollPane(columnList));
        panel.add(new JLabel("Enter Search Keyword:"));
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(new JScrollPane(resultArea));

        //Action listeners
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] tableNames = {"Car", "Seller", "Manufacture"};
                String[] columns = columnList.getSelectedValuesList().toArray(new String[0]);
                String keyword = searchField.getText().trim();
                columnSearch(tableNames, columns, keyword);
            }
        });
        
        goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                getSubset();
            }
        });
    }

    public String[] getAllColumns() {
        // Fetch columns from all tables
        List<String> allColumns = new ArrayList<>();
    
        String[] tableNames = {"Car", "Seller", "Manufacture"};
    
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password)) {
            for (String tableName : tableNames) {
                try (Statement statement = connection.createStatement()) {
                    try (ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " LIMIT 1")) {
                        ResultSetMetaData metaData = resultSet.getMetaData();
                        int columnCount = metaData.getColumnCount();
    
                        for (int i = 1; i <= columnCount; i++) {
                            allColumns.add(metaData.getColumnName(i));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return allColumns.toArray(new String[0]);
    }

    private void columnSearch(String[] tableNames, String[] columns, String keyword) {
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password)) {
            resultArea.setText(""); 
    
            StringBuilder queryBuilder = new StringBuilder();
            boolean firstTable = true;
    
            for (String tableName : tableNames) {
                if (!firstTable) {
                    queryBuilder.append(" UNION ");
                }
    
                queryBuilder.append("SELECT ");
                if (columns.length == 0) {
                    queryBuilder.append("*");
                } else {
                    for (int i = 0; i < columns.length; i++) {
                        queryBuilder.append(columns[i]);
                        if (i < columns.length - 1) {
                            queryBuilder.append(", ");
                        }
                    }
                }
    
                queryBuilder.append(" FROM ").append(tableName).append(" WHERE ");
    
                for (int i = 0; i < columns.length; i++) {
                    queryBuilder.append(columns[i]).append(" LIKE ?");
    
                    if (i < columns.length - 1) {
                        queryBuilder.append(" OR ");
                    }
                }
    
                firstTable = false;
            }
    
            try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
                for (int i = 0; i < tableNames.length * columns.length; i++) {
                    statement.setString(i + 1, "%" + keyword + "%");
                }
    
                ResultSet resultSet = statement.executeQuery();
    
                // Display results
                displayColumnResults(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resultArea.setText("Error occurred during column search.");
        }
    }

    private void displayColumnResults(ResultSet resultSet) throws SQLException {
        //Labels
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        StringBuilder columnHeader = new StringBuilder();
        for (int i = 1; i <= columnCount; i++) {
            columnHeader.append(metaData.getColumnName(i)).append("\t");
        }
        resultArea.append(columnHeader.toString().trim() + "\n");
    
        //Process and display in resultArea
        while (resultSet.next()) {
            StringBuilder result = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                result.append(resultSet.getString(i)).append("\t");
            }
            resultArea.append(result.toString().trim() + "\n");
        }
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

        Object[][] dataTable = new Object[data.length-1][data[0].length + 1];
            for(int i = 1; i < data.length; i++){
                for(int j = 0; j < data[0].length; j++){
                    dataTable[i-1][j] = data[i][j];
                }
                dataTable[i-1][data[0].length] = "details";
            }

        String[] title = {"Vin", "Model Name", "Dealer Zip","City", "Price", "Make Name", "Year", "Accidents", "Details"};
        table = new JTable(dataTable, title);
        table.getColumnModel().getColumn(data[0].length).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(data[0].length).setCellEditor(new ButtonEditor());

        JButton averBut = new JButton("Average");
        JButton HighBut = new JButton("Cheapest");
        JButton LowBut = new JButton("Costly");
        JLabel priceLabel = new JLabel("");

        averBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                float price = carDatabase.priceCheck(data, 0);
                priceLabel.setText("Average: " + price);

            }
        });
        LowBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                float price = carDatabase.priceCheck(data, 1);
                priceLabel.setText("Highest: " + price);

            }
        });
        HighBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                float price = carDatabase.priceCheck(data, 2);
                priceLabel.setText("Lowest: " + price);
            }
        });

        panel.add(seachLabel);
        panel.add(averBut);
        panel.add(HighBut);
        panel.add(LowBut);
        panel.add(priceLabel);
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
                        details(value.toString(), 0);
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
    public void details(String vin, int type){
        clear();
        //back button
        JButton goBack = new JButton("Back");
        goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if(type == 0){
                    mainPage(data, searchString);
                }
                else if (type == 1){
                    getSubset();
                }
            }
        });
        
        //gives people with the permission to edit or delete
        if(permission){
        //edit data
            JButton editBut = new JButton("Edit");
        //delete
            JButton deleteBut = new JButton("Remove Car");

            editBut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    fileCar(vin, 1);
                }
             });

             deleteBut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    int reply = JOptionPane.showConfirmDialog(null, "Are You Sure?", "Delete Car Entry", JOptionPane.YES_NO_OPTION);
                    if(reply == JOptionPane.YES_OPTION){
                        carDatabase.deleteRow(vin);
                        getSubset();
                    }
                }
             });

            panel.add(editBut);
            panel.add(deleteBut);
        }
        else if(carDatabase.notCopy(vin, user)){
            JButton buyButton = new JButton("Bookmark Car");
            buyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    carDatabase.purchaseCar(vin, user);
                    getSubset();
                }
             });
             panel.add(buyButton);
        }

        //Query and arrays
        String[][] carData = carDatabase.sqlCommand("CALL GetCarInfo(\"" + vin + "\")");
        String[] columnNames = carData[0];
        String[] rowData = carData.length > 1 ? carData[1] : new String[columnNames.length];

        //Loop to add all the data and labels
        for (int i = 0; i < columnNames.length; i++) {
            JLabel label = new JLabel(columnNames[i] + ": ");
            JLabel textField = new JLabel(rowData[i] + ". ");
        
            panel.add(label);
            panel.add(textField);
        }
    
        panel.add(goBack);
    }

    //open ups a file for car information
	public void fileCar(String vin, int type){
        clear();
        JLabel vinlabel = new JLabel(vin);
        panel.add(vinlabel);
        String[][] s = carDatabase.sqlCommand("select * from Car where vin = \"" + vin + "\"");
        String[] titles = new String[60];
        for(int i = 0; i < s[0].length; i++){
            titles[i] = s[0][i];
        }
        if (s.length <= 1) {
            s = new String[2][60];
        }

        JLabel back_legroomlabel = new JLabel("back_legroom");
        JTextField back_legroomtextField = new JTextField(s[1][1], 20);
        panel.add(back_legroomlabel);
        panel.add(back_legroomtextField);
        JLabel bedlabel = new JLabel("bed");
        JTextField bedtextField = new JTextField(s[1][2], 20);
        panel.add(bedlabel);
        panel.add(bedtextField);
        JLabel bed_heightlabel = new JLabel("bed_height");
        JTextField bed_heighttextField = new JTextField(s[1][3], 20);
        panel.add(bed_heightlabel);
        panel.add(bed_heighttextField);
        JLabel bed_lengthlabel = new JLabel("bed_length");
        JTextField bed_lengthtextField = new JTextField(s[1][4], 20);
        panel.add(bed_lengthlabel);
        panel.add(bed_lengthtextField);
        JLabel body_typelabel = new JLabel("body_type");
        JTextField body_typetextField = new JTextField(s[1][5], 20);
        panel.add(body_typelabel);
        panel.add(body_typetextField);
        JLabel cabinlabel = new JLabel("cabin");
        JTextField cabintextField = new JTextField(s[1][6], 20);
        panel.add(cabinlabel);
        panel.add(cabintextField);
        JLabel city_fuel_economylabel = new JLabel("city_fuel_economy");
        JTextField city_fuel_economytextField = new JTextField(s[1][7], 20);
        panel.add(city_fuel_economylabel);
        panel.add(city_fuel_economytextField);
        JLabel combine_fuel_economylabel = new JLabel("combine_fuel_economy");
        JTextField combine_fuel_economytextField = new JTextField(s[1][8], 20);
        panel.add(combine_fuel_economylabel);
        panel.add(combine_fuel_economytextField);
        JLabel descriptionlabel = new JLabel("description");
        JTextField descriptiontextField = new JTextField(s[1][9], 20);
        panel.add(descriptionlabel);
        panel.add(descriptiontextField);
        JLabel engine_cylinderslabel = new JLabel("engine_cylinders");
        JTextField engine_cylinderstextField = new JTextField(s[1][10], 20);
        panel.add(engine_cylinderslabel);
        panel.add(engine_cylinderstextField);
        JLabel engine_displacementlabel = new JLabel("engine_displacement");
        JTextField engine_displacementtextField = new JTextField(s[1][11], 20);
        panel.add(engine_displacementlabel);
        panel.add(engine_displacementtextField);
        JLabel engine_typelabel = new JLabel("engine_type");
        JTextField engine_typetextField = new JTextField(s[1][12], 20);
        panel.add(engine_typelabel);
        panel.add(engine_typetextField);
        JLabel exterior_colorlabel = new JLabel("exterior_color");
        JTextField exterior_colortextField = new JTextField(s[1][13], 20);
        panel.add(exterior_colorlabel);
        panel.add(exterior_colortextField);
        JLabel fleetlabel = new JLabel("fleet");
        JTextField fleettextField = new JTextField(s[1][14], 20);
        panel.add(fleetlabel);
        panel.add(fleettextField);
        JLabel frame_damagedlabel = new JLabel("frame_damaged");
        JTextField frame_damagedtextField = new JTextField(s[1][15], 20);
        panel.add(frame_damagedlabel);
        panel.add(frame_damagedtextField);
        JLabel front_legroomlabel = new JLabel("front_legroom");
        JTextField front_legroomtextField = new JTextField(s[1][16], 20);
        panel.add(front_legroomlabel);
        panel.add(front_legroomtextField);
        JLabel fuel_tank_volumelabel = new JLabel("fuel_tank_volume");
        JTextField fuel_tank_volumetextField = new JTextField(s[1][17], 20);
        panel.add(fuel_tank_volumelabel);
        panel.add(fuel_tank_volumetextField);
        JLabel fuel_typelabel = new JLabel("fuel_type");
        JTextField fuel_typetextField = new JTextField(s[1][18], 20);
        panel.add(fuel_typelabel);
        panel.add(fuel_typetextField);
        JLabel has_accidentslabel = new JLabel("has_accidents");
        JTextField has_accidentstextField = new JTextField(s[1][19], 20);
        panel.add(has_accidentslabel);
        panel.add(has_accidentstextField);
        JLabel heightlabel = new JLabel("height");
        JTextField heighttextField = new JTextField(s[1][20], 20);
        panel.add(heightlabel);
        panel.add(heighttextField);
        JLabel highway_fuel_economylabel = new JLabel("highway_fuel_economy");
        JTextField highway_fuel_economytextField = new JTextField(s[1][21], 20);
        panel.add(highway_fuel_economylabel);
        panel.add(highway_fuel_economytextField);
        JLabel horsepowerlabel = new JLabel("horsepower");
        JTextField horsepowertextField = new JTextField(s[1][22], 20);
        panel.add(horsepowerlabel);
        panel.add(horsepowertextField);
        JLabel interior_colorlabel = new JLabel("interior_color");
        JTextField interior_colortextField = new JTextField(s[1][23], 20);
        panel.add(interior_colorlabel);
        panel.add(interior_colortextField);
        JLabel isCablabel = new JLabel("isCab");
        JTextField isCabtextField = new JTextField(s[1][24], 20);
        panel.add(isCablabel);
        panel.add(isCabtextField);
        JLabel is_certifiedlabel = new JLabel("is_certified");
        JTextField is_certifiedtextField = new JTextField(s[1][25], 20);
        panel.add(is_certifiedlabel);
        panel.add(is_certifiedtextField);
        JLabel is_cpolabel = new JLabel("is_cpo");
        JTextField is_cpotextField = new JTextField(s[1][26], 20);
        panel.add(is_cpolabel);
        panel.add(is_cpotextField);
        JLabel is_newlabel = new JLabel("is_new");
        JTextField is_newtextField = new JTextField(s[1][27], 20);
        panel.add(is_newlabel);
        panel.add(is_newtextField);
        JLabel is_oemcpolabel = new JLabel("is_oemcpo");
        JTextField is_oemcpotextField = new JTextField(s[1][28], 20);
        panel.add(is_oemcpolabel);
        panel.add(is_oemcpotextField);
        JLabel latitudelabel = new JLabel("latitude");
        JTextField latitudetextField = new JTextField(s[1][29], 20);
        panel.add(latitudelabel);
        panel.add(latitudetextField);
        JLabel lengthlabel = new JLabel("length");
        JTextField lengthtextField = new JTextField(s[1][30], 20);
        panel.add(lengthlabel);
        panel.add(lengthtextField);
        JLabel listed_datelabel = new JLabel("listed_date");
        JTextField listed_datetextField = new JTextField(s[1][31], 20);
        panel.add(listed_datelabel);
        panel.add(listed_datetextField);
        JLabel listing_colorlabel = new JLabel("listing_color");
        JTextField listing_colortextField = new JTextField(s[1][32], 20);
        panel.add(listing_colorlabel);
        panel.add(listing_colortextField);
        JLabel longitudelabel = new JLabel("longitude");
        JTextField longitudetextField = new JTextField(s[1][33], 20);
        panel.add(longitudelabel);
        panel.add(longitudetextField);
        JLabel main_picture_urllabel = new JLabel("main_picture_url");
        JTextField main_picture_urltextField = new JTextField(s[1][34], 20);
        panel.add(main_picture_urllabel);
        panel.add(main_picture_urltextField);
        JLabel major_optionslabel = new JLabel("major_options");
        JTextField major_optionstextField = new JTextField(s[1][35], 20);
        panel.add(major_optionslabel);
        panel.add(major_optionstextField);
        JLabel maximum_seatinglabel = new JLabel("maximum_seating");
        JTextField maximum_seatingtextField = new JTextField(s[1][36], 20);
        panel.add(maximum_seatinglabel);
        panel.add(maximum_seatingtextField);
        JLabel mileagelabel = new JLabel("mileage");
        JTextField mileagetextField = new JTextField(s[1][37], 20);
        panel.add(mileagelabel);
        panel.add(mileagetextField);
        JLabel model_namelabel = new JLabel("model_name");
        JTextField model_nametextField = new JTextField(s[1][38], 20);
        panel.add(model_namelabel);
        panel.add(model_nametextField);
        JLabel owner_countlabel = new JLabel("owner_count");
        JTextField owner_counttextField = new JTextField(s[1][39], 20);
        panel.add(owner_countlabel);
        panel.add(owner_counttextField);
        JLabel powerlabel = new JLabel("power");
        JTextField powertextField = new JTextField(s[1][40], 20);
        panel.add(powerlabel);
        panel.add(powertextField);
        JLabel pricelabel = new JLabel("price");
        JTextField pricetextField = new JTextField(s[1][41], 20);
        panel.add(pricelabel);
        panel.add(pricetextField);
        JLabel salvagelabel = new JLabel("salvage");
        JTextField salvagetextField = new JTextField(s[1][42], 20);
        panel.add(salvagelabel);
        panel.add(salvagetextField);
        JLabel savings_amountlabel = new JLabel("savings_amount");
        JTextField savings_amounttextField = new JTextField(s[1][43], 20);
        panel.add(savings_amountlabel);
        panel.add(savings_amounttextField);
        JLabel sp_idlabel = new JLabel("sp_id");
        JTextField sp_idtextField = new JTextField(s[1][44], 20);
        panel.add(sp_idlabel);
        panel.add(sp_idtextField);
        JLabel sp_namelabel = new JLabel("sp_name");
        JTextField sp_nametextField = new JTextField(s[1][45], 20);
        panel.add(sp_namelabel);
        panel.add(sp_nametextField);
        JLabel theft_titlelabel = new JLabel("theft_title");
        JTextField theft_titletextField = new JTextField(s[1][46], 20);
        panel.add(theft_titlelabel);
        panel.add(theft_titletextField);
        JLabel torquelabel = new JLabel("torque");
        JTextField torquetextField = new JTextField(s[1][47], 20);
        panel.add(torquelabel);
        panel.add(torquetextField);
        JLabel transmissionlabel = new JLabel("transmission");
        JTextField transmissiontextField = new JTextField(s[1][48], 20);
        panel.add(transmissionlabel);
        panel.add(transmissiontextField);
        JLabel transmission_displaylabel = new JLabel("transmission_display");
        JTextField transmission_displaytextField = new JTextField(s[1][49], 20);
        panel.add(transmission_displaylabel);
        panel.add(transmission_displaytextField);
        JLabel trimIdlabel = new JLabel("trimId");
        JTextField trimIdtextField = new JTextField(s[1][50], 20);
        panel.add(trimIdlabel);
        panel.add(trimIdtextField);
        JLabel trim_namelabel = new JLabel("trim_name");
        JTextField trim_nametextField = new JTextField(s[1][51], 20);
        panel.add(trim_namelabel);
        panel.add(trim_nametextField);
        JLabel vehicle_damage_categorylabel = new JLabel("vehicle_damage_category");
        JTextField vehicle_damage_categorytextField = new JTextField(s[1][52], 20);
        panel.add(vehicle_damage_categorylabel);
        panel.add(vehicle_damage_categorytextField);
        JLabel wheel_systemlabel = new JLabel("wheel_system");
        JTextField wheel_systemtextField = new JTextField(s[1][53], 20);
        panel.add(wheel_systemlabel);
        panel.add(wheel_systemtextField);
        JLabel wheel_system_displaylabel = new JLabel("wheel_system_display");
        JTextField wheel_system_displaytextField = new JTextField(s[1][54], 20);
        panel.add(wheel_system_displaylabel);
        panel.add(wheel_system_displaytextField);
        JLabel wheelbaselabel = new JLabel("wheelbase");
        JTextField wheelbasetextField = new JTextField(s[1][55], 20);
        panel.add(wheelbaselabel);
        panel.add(wheelbasetextField);
        JLabel widthlabel = new JLabel("width");
        JTextField widthtextField = new JTextField(s[1][56], 20);
        panel.add(widthlabel);
        panel.add(widthtextField);
        JLabel yearlabel = new JLabel("year");
        JTextField yeartextField = new JTextField(s[1][57], 20);
        panel.add(yearlabel);
        panel.add(yeartextField);
        JLabel make_namelabel = new JLabel("make_name");
        JTextField make_nametextField = new JTextField(s[1][58], 20);
        panel.add(make_namelabel);
        panel.add(make_nametextField);
        JLabel idenetificationlabel = new JLabel("idenetification");
        JTextField idenetificationtextField = new JTextField(s[1][59], 20);
        panel.add(idenetificationlabel);
        panel.add(idenetificationtextField);

        // for(int i = 0; i < s[0].length; i++){
            
        //     System.out.println("JLabel " + s[0][i] + "label = new JLabel(\"" + s[0][i] + "\");");
        //     System.out.println(s[0][i] + "textField.getText() ,");
        //     System.out.println("panel.add("+ s[0][i] +"label);");
        //      System.out.println("panel.add("+ s[0][i] +"textField);");
       // }
        //type 0 is for adding
        if(type == 0){
            JButton reButton = new JButton("Register");
            reButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    String[] data = {vin, back_legroomtextField.getText() ,bedtextField.getText() ,bed_heighttextField.getText() ,bed_lengthtextField.getText() ,body_typetextField.getText() ,cabintextField.getText() ,city_fuel_economytextField.getText() ,combine_fuel_economytextField.getText() ,descriptiontextField.getText() ,engine_cylinderstextField.getText() ,engine_displacementtextField.getText() ,engine_typetextField.getText() ,exterior_colortextField.getText() ,fleettextField.getText() ,frame_damagedtextField.getText() ,front_legroomtextField.getText() ,fuel_tank_volumetextField.getText() ,fuel_typetextField.getText() ,has_accidentstextField.getText() ,heighttextField.getText() ,highway_fuel_economytextField.getText() ,horsepowertextField.getText() ,interior_colortextField.getText() ,isCabtextField.getText() ,is_certifiedtextField.getText() ,is_cpotextField.getText() ,is_newtextField.getText() ,is_oemcpotextField.getText() ,latitudetextField.getText() ,lengthtextField.getText() ,listed_datetextField.getText() ,listing_colortextField.getText() ,longitudetextField.getText() ,main_picture_urltextField.getText() ,major_optionstextField.getText() ,maximum_seatingtextField.getText() ,mileagetextField.getText() ,model_nametextField.getText() ,owner_counttextField.getText() ,powertextField.getText() ,pricetextField.getText() ,salvagetextField.getText() ,savings_amounttextField.getText() ,sp_idtextField.getText() ,sp_nametextField.getText() ,theft_titletextField.getText() ,torquetextField.getText() ,transmissiontextField.getText() ,transmission_displaytextField.getText() ,trimIdtextField.getText() ,trim_nametextField.getText() ,vehicle_damage_categorytextField.getText() ,wheel_systemtextField.getText() ,wheel_system_displaytextField.getText() ,wheelbasetextField.getText() ,widthtextField.getText() ,yeartextField.getText() ,make_nametextField.getText() ,idenetificationtextField.getText()};
                    if(carDatabase.checkCar(data, titles)){
                        for (int i = 0; i < data.length; i++) {
                            if(data[i].equals("false"))
                                data[i] = "0";
                            else if(data[i].equals("true"))
                                data[i] = "1";
                        }
                        carDatabase.addCar(data, titles);
                        getSubset();
                    }
                }
            });
            panel.add(reButton);

            JButton goBack = new JButton("Back");
            goBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    getSubset();
                }
            });
            panel.add(goBack);
        }
        //type 1 is for editing
        if(type == 1){
            JButton editBut = new JButton("Edit");

            editBut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    String[] data = {vin, back_legroomtextField.getText() ,bedtextField.getText() ,bed_heighttextField.getText() ,bed_lengthtextField.getText() ,body_typetextField.getText() ,cabintextField.getText() ,city_fuel_economytextField.getText() ,combine_fuel_economytextField.getText() ,descriptiontextField.getText() ,engine_cylinderstextField.getText() ,engine_displacementtextField.getText() ,engine_typetextField.getText() ,exterior_colortextField.getText() ,fleettextField.getText() ,frame_damagedtextField.getText() ,front_legroomtextField.getText() ,fuel_tank_volumetextField.getText() ,fuel_typetextField.getText() ,has_accidentstextField.getText() ,heighttextField.getText() ,highway_fuel_economytextField.getText() ,horsepowertextField.getText() ,interior_colortextField.getText() ,isCabtextField.getText() ,is_certifiedtextField.getText() ,is_cpotextField.getText() ,is_newtextField.getText() ,is_oemcpotextField.getText() ,latitudetextField.getText() ,lengthtextField.getText() ,listed_datetextField.getText() ,listing_colortextField.getText() ,longitudetextField.getText() ,main_picture_urltextField.getText() ,major_optionstextField.getText() ,maximum_seatingtextField.getText() ,mileagetextField.getText() ,model_nametextField.getText() ,owner_counttextField.getText() ,powertextField.getText() ,pricetextField.getText() ,salvagetextField.getText() ,savings_amounttextField.getText() ,sp_idtextField.getText() ,sp_nametextField.getText() ,theft_titletextField.getText() ,torquetextField.getText() ,transmissiontextField.getText() ,transmission_displaytextField.getText() ,trimIdtextField.getText() ,trim_nametextField.getText() ,vehicle_damage_categorytextField.getText() ,wheel_systemtextField.getText() ,wheel_system_displaytextField.getText() ,wheelbasetextField.getText() ,widthtextField.getText() ,yeartextField.getText() ,make_nametextField.getText() ,idenetificationtextField.getText()};
                    if(carDatabase.checkCar(data, titles)){
                        for (int i = 0; i < data.length; i++) {
                            if(data[i].equals("false"))
                                data[i] = "0";
                            else if(data[i].equals("true"))
                                data[i] = "1";
                        }
                        carDatabase.editCar(data, titles);
                        details(vin, 0);
                    }
                }
            });

            panel.add(editBut);

            JButton goBack = new JButton("Back");
            goBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    details(vin, 0);
                }
            });
            panel.add(goBack);

            

        }
    }

    //clears the panel, allows for new objects
    public void clear(){
        panel.removeAll();
        panel.updateUI();
    }
}

