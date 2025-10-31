package user.views;

import javax.swing.JFrame;

import com.formdev.flatlaf.FlatLightLaf;

public class UserFrame extends JFrame {
    public UserFrame() {
        FlatLightLaf.setup();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1366, 768);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
