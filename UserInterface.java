import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInterface {
    private static JFrame frame = new JFrame("The Car Database");
    private static JPanel panel = new JPanel();

    public UserInterface(Database data) {
        String[] logins = new String[2];
        // Create a JLabel (text label)
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");

        JTextField userTextField = new JTextField(20);
        JPasswordField passPasswordField = new JPasswordField(20);
        // Create a JButton (clickable button)
        JButton button = new JButton("Enter Username");

        // Add an ActionListener to the button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userTextField.getText();
                String pass = new String(passPasswordField.getPassword()); // Note: Use getPassword() for security
                logins[0] = username;
                logins[1] = pass;
                if(data.isUser(logins)){
                    getSubset();
                }
            }
        });

        // Add components to the panel
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
        //clear container
    }
}

