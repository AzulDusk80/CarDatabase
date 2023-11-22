import javax.swing.JOptionPane;

public final class Menu {
        private static Database data = new Database();
        private static UserInterface ui = new UserInterface(data);

    	public static void main(String[] args){
        }

        //simple testing
        public static void simpleUI(){
                String input = JOptionPane.showInputDialog("Enter command for data");
                System.out.println("Starting commands");
                String[][] temp = data.distinctData("Manufacture", "make_name");
                String s = "";
                for(String[] i : temp){
                        for(String x : i){
                                s += x + "\t\t";
                        }
                        s+= "\n";
                }
                JOptionPane.showMessageDialog(null, s);
                System.out.println("Ending commands");        
        }
}