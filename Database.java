import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
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

    public String[][] query(String sqlQuery) {
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

	public void simpleStoreProcedure(String spName) {

		try {
			statement = connection.createStatement();
			int total =0;
			CallableStatement myCallStmt = connection.prepareCall("{call "+spName+"(?)}");
			myCallStmt.registerOutParameter(1,Types.BIGINT);
			myCallStmt.execute();
			total = myCallStmt.getInt(1);
			System.out.println("Average pricing "+ total);

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

}