
public final class Menu {
    	public static void main(String[] args){
            System.out.println("Hello");
            Database data = new Database();
            data.simpleQuery("select distinct make_name from Car;");
            
        }
}