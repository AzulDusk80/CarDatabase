import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.text.ParseException;
import javax.swing.JOptionPane;

public class Database {

    static String databasePrefix ="";
    static String netID =""; 
    static final String hostName ="washington.uww.edu"; 
    static String databaseURL ="jdbc:mariadb://"+hostName+"/"+databasePrefix;
    static String password=""; 

    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    public Database(){
        getLogin();
        connectData();
    }

	//gets login for the database used in class
    public void getLogin(){
        Scanner input = new Scanner(System.in);
        System.out.println("NetID:");
        netID = input.nextLine();
        System.out.println("password:");
        password = input.nextLine();
        
        databasePrefix = "cs366-2237_" + netID;
        input.close();
        databaseURL ="jdbc:mariadb://"+hostName+"/"+databasePrefix;
    }

	//connects to our database using logins
    public void connectData(){
	  
	    try {
	    	    Class.forName("org.mariadb.jdbc.Driver");
	    	  	System.out.println("databaseURL"+ databaseURL);
	            connection = DriverManager.getConnection(databaseURL, netID, password);
	            System.out.println("Successfully connected to the database");
	    }
	    catch (ClassNotFoundException e) {
	            e.printStackTrace();
	    }
	    catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	//gives back a table made with the sql command given to it
    public String[][] sqlCommand(String sqlQuery) {
		ArrayList<String[]> list = new ArrayList<String[]>();
		int columns = 0;
	    try {
	    	statement = connection.createStatement();
	    	resultSet = statement.executeQuery(sqlQuery);

	    	ResultSetMetaData metaData = resultSet.getMetaData();
	    	columns = metaData.getColumnCount();

			String[] temp = new String[columns];
	    	for (int i=1; i<= columns; i++) {
	    		temp[i-1] = metaData.getColumnName(i);
	    	}
			list.add(temp);

	    	while (resultSet.next()) {
				String[] temp2 = new String[columns];
	    		for (int i=1; i<= columns; i++) {
	    				temp2[i-1] = resultSet.getObject(i)+"";
	    		}
	    			list.add(temp2);
	    	}
	    }
	    catch (SQLException e) {
	    		e.printStackTrace();
	    }
		String[][] setList = new String[list.size()][columns];
		for(int x = 0; x < list.size(); x++){
			for(int i = 0; i < columns; i++){
				setList[x][i] = list.get(x)[i];
			}
		}
		return setList;
	}

	//no return statement, it will just edit the table
	public void tableEdit(String sqlQuery){
		try {
	    	statement = connection.createStatement();
	    	resultSet = statement.executeQuery(sqlQuery);
		}
	    catch (SQLException e) {
	    		e.printStackTrace();
	    }
	}

	//for distinct elements
	public String[][] distinctData(String table, String want){
		return sqlCommand("select distinct " + want + " from " + table);
	}

	//used to check if valid user account
	public boolean isUser(String[] account){
		String[][] s = sqlCommand("call GetPasswordFromUsername(\"" + account[0] + "\")");
		if(s.length <= 1){
			return false;
		}
		if(s[1][0].equals(account[1])){
			return true;
		}
		return false;
	}

	//check is username is being used
	public boolean isUsed(String name){
		if(name == null){
			return true;
		}
		String[][] s = sqlCommand("select username from User");
		for(int i = 0; i < s.length; i++){
			for(int k = 0; k < s[0].length; k++){
				if(name.equals(s[i][k]))
					return true;
			}
		}
		return false;
	}

	//check permission of user
	public boolean hasPermission(String user){
		String[][] s = sqlCommand("call GetPermissionsFromUsername(\"" + user + "\")");
		if(s[1][0].equals("true"))
			return true;
		else
			return false;

	}

	//add user to database
	public void registerUser(String[] account){
		tableEdit("INSERT INTO User VALUES (\"" + account[0] + "\", \"" + account[1] + "\", \"" + account[2] + "\", \"" + account[3] + "\", 0);");
	}

	//removes car from table
    public void deleteRow(String vin){
		String[][] table = sqlCommand("select c.vin, s.idenetification from Car c, Seller s where c.idenetification = s.idenetification AND c.vin = \"" + vin+ "\" ");
		tableEdit("DELETE FROM Car WHERE vin = \"" + table[1][0]+ "\" ");
		tableEdit("DELETE FROM Seller WHERE idenetification = \"" + table[1][1]+ "\" ");
    }

	public boolean newCar(String vin){
		if(vin.trim().isEmpty())
			return false;
		String[][] s = sqlCommand("select vin from Car Where vin = \"" + vin + "\"");
		if(s.length <= 1)
			return true;
		return false;
	}


	public boolean validVin(String vin){
		if(vin.trim().isEmpty())
			return false;
		String[][] s = sqlCommand("select vin from Car Where vin = \"" + vin + "\"");
		if(s.length > 1)
			return true;
		return false;
	}

	//edit car from table
	public void editCar(String[] car, String[] titles){
		String s = "UPDATE Car SET ";
		for (int i = 1; i < car.length; i++) {
			s += titles[i] + " = \"" +car[i] + "\", "; 
		}
		s = s.substring(0, s.length() - 2);
		s+= " WHERE vin = \"" +car[0] +"\"";
		tableEdit(s);

	}

	//add car to table
	public void addCar(String[] car, String[] titles){
		String s = "INSERT INTO Car Values ( ";
		for (int i = 0; i < car.length; i++) {
			s += "\"" +car[i] + "\", "; 
		}
		s = s.substring(0, s.length() - 2);
		s += ")";
		//System.out.println(s);
		tableEdit(s);
	}

	//validates car
	public boolean checkCar(String[] car, String[] titles){

		//int only
		int[] onlyInt = {7, 11, 21, 22, 37, 39, 41, 43, 44, 57, 59};
		//boolean only
		int[] onlyBoo = {14, 15, 19, 24, 25, 26, 27, 28, 42, 46};

		for (int i : onlyInt) {
			try {
				float intValue = Float.parseFloat(car[i]);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, car[i] + " is not a valid number in " + titles[i], "Error in entered data", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		for (int i : onlyBoo) {
			if(car[i].equals("true") || car[i].equals("false")){
			}
			else{
				JOptionPane.showMessageDialog(null, car[i] + " is not a valid boolean in " + titles[i], "Error in entered data", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {
            sdf.parse(car[31]);
        } catch (ParseException e) {
			JOptionPane.showMessageDialog(null, car[31] + " is not a valid date in " + titles[31], "Error in entered data", JOptionPane.ERROR_MESSAGE);
            return false;
        }

		//check valid identification or make

		return true;
	}

	public float priceCheck(String s[][], int type){
		if(type == 0){
			int size = s.length - 1;
			float price = 0;
			for(int i = 1; i < s.length; i++){
				try {
				price += Float.parseFloat(s[i][4].trim());
				} catch (NumberFormatException e) {
					System.out.println(s[i][4]);
				}
			}
			return price/size;
		}
		else if(type == 1){
			float price = 0;
			for(int i = 1; i < s.length; i++){
				try {
					if (price < Float.parseFloat(s[i][4])) {
						price = Float.parseFloat(s[i][4]);
					}
				} catch (NumberFormatException e) {
					System.out.println(s[i][4]);
				}
			}
			return price;
		}
		else if(type == 2){
			float price = Float.MAX_VALUE;
			for(int i = 1; i < s.length; i++){
				try {
					if (price > Float.parseFloat(s[i][4])) {
						price = Float.parseFloat(s[i][4]);
					}
				} catch (NumberFormatException e) {
					System.out.println(s[i][4]);
				}
			}
			return price;
		}
		return -1;

	}

}