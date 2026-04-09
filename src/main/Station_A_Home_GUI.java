import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Station_A_Home_GUI {

    static JFrame currentModeFrame = null; // Holds the currently running mode frame

    public static void main(String[] args) {
        JFrame frame = new JFrame("Station A - Home GUI");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4,1,10,10));

        JLabel title = new JLabel("Select Interlocking Mode for Station A", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(title);

        JButton piButton = new JButton("PI Mode");
        JButton rriButton = new JButton("RRI Mode");
        JButton eiButton = new JButton("EI Mode");

        // Action to launch PI mode
        piButton.addActionListener(e -> launchMode("PI"));

        // Action to launch RRI mode
        rriButton.addActionListener(e -> launchMode("RRI"));

        // Action to launch EI mode
        eiButton.addActionListener(e -> launchMode("EI"));

        frame.add(piButton);
        frame.add(rriButton);
        frame.add(eiButton);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static void launchMode(String mode) {
        // Close any previously running mode
        if(currentModeFrame != null){
            currentModeFrame.dispose();
            currentModeFrame = null;
        }

        try {
            switch(mode){
                case "PI":
                    // Assuming StationA_PI_Interlocking has a main() method that creates JFrame
                    StationA_PI_Interlocking.main(new String[]{});
                    currentModeFrame = getLastFrame("Station A PI Interlocking");
                    break;
                case "RRI":
                    StationA_RRI_Interlocking.main(new String[]{});
                    currentModeFrame = getLastFrame("Station A RRI Interlocking");
                    break;
                case "EI":
                    StationA_EI_Interlocking.main(new String[]{});
                    currentModeFrame = getLastFrame("Station A EI Interlocking");
                    break;
            }
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Error launching mode: " + ex.getMessage());
        }
    }

    // Utility method to get the last created JFrame with the specified title
    static JFrame getLastFrame(String title){
        Frame[] frames = Frame.getFrames();
        for(int i=frames.length-1; i>=0; i--){
            if(frames[i] instanceof JFrame && frames[i].getTitle().equals(title)){
                return (JFrame)frames[i];
            }
        }
        return null;
    }
}
