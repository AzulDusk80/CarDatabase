import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Database {

    static String databasePrefix ="";
    static String netID =""; // Please enter your netId
    static final String hostName ="washington.uww.edu"; //140.146.23.39 or washington.uww.edu
    static String databaseURL ="jdbc:mariadb://"+hostName+"/"+databasePrefix;
    static String password=""; // please enter your own password

    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    public Database(){
        getLogin();
        connectData();
    }

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

    public void simpleQuery(String sqlQuery) {
	    try {
	    	statement = connection.createStatement();
	    	resultSet = statement.executeQuery(sqlQuery);

	    	ResultSetMetaData metaData = resultSet.getMetaData();
	    	int columns = metaData.getColumnCount();

	    	for (int i=1; i<= columns; i++) {
	    		System.out.print(metaData.getColumnName(i)+"\t");
	    	}

	    	System.out.println();

	    	while (resultSet.next()) {
	       
	    		for (int i=1; i<= columns; i++) {
	    				System.out.print(resultSet.getObject(i)+"\t\t");
	    		}
	    			System.out.println();
	    	}
	    }
	    catch (SQLException e) {
	    		e.printStackTrace();
	    }
	}

}