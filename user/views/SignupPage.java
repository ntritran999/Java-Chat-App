package user.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout;

import com.github.lgooddatepicker.components.DatePicker;

import static user.views.Style.*;

public class SignupPage extends JPanel{
    private JTextField fullnameField, emailField, usernameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JRadioButton maleRadioButton, femaleRadioButton;
    private JTextArea addressField;
    private JButton createAccButton, returnLoginButton;
    private DatePicker dateOfBirthPicker;

    public SignupPage() {
        setLayout(new BorderLayout());
        setBackground(new Color(bgColor));
        setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
        main.setBackground(new Color(bgColorDark));
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("ĐĂNG KÝ");
        title.setForeground(new Color(textColor));
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel fullnameLabel = new JLabel("Họ tên:"), 
               genderLabel = new JLabel("Giới tính:"), 
               dateOfBirthLabel = new JLabel("Ngày sinh:"), 
               addressLabel = new JLabel("Địa chỉ:"), 
               emailLabel = new JLabel("Email:"),
               usernameLabel = new JLabel("Tên đăng nhập:"), 
               passwordLabel = new JLabel("Mật khẩu:"), 
               confirmPasswordLabel = new JLabel("Xác nhận mật khẩu:");
        fullnameLabel.setForeground(new Color(textColor));
        genderLabel.setForeground(new Color(textColor));
        dateOfBirthLabel.setForeground(new Color(textColor));
        addressLabel.setForeground(new Color(textColor));
        emailLabel.setForeground(new Color(textColor));
        usernameLabel.setForeground(new Color(textColor));
        passwordLabel.setForeground(new Color(textColor));
        confirmPasswordLabel.setForeground(new Color(textColor));

        fullnameField = new JTextField();
        emailField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        maleRadioButton = new JRadioButton("Nam");
        femaleRadioButton = new JRadioButton("Nữ");
        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(maleRadioButton);
        radioGroup.add(femaleRadioButton);

        createAccButton = new JButton("Tạo tài khoản");
        returnLoginButton = new JButton("Trở lại đăng nhập");
        
        addressField = new JTextArea(5, 20);
        JScrollPane addressScrollPane = new JScrollPane(addressField);

        dateOfBirthPicker = new DatePicker();
        
        JPanel form = new JPanel();
        form.setMaximumSize(new Dimension(400, 500));
        form.setOpaque(false);
        GroupLayout formLayout = new GroupLayout(form);
        form.setLayout(formLayout);
        formLayout.setHorizontalGroup(
            formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(formLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, formLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(returnLoginButton)
                        .addGap(18, 18, 18)
                        .addComponent(createAccButton))
                    .addGroup(formLayout.createSequentialGroup()
                        .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addGroup(formLayout.createSequentialGroup()
                                .addComponent(usernameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(usernameField))
                            .addGroup(formLayout.createSequentialGroup()
                                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(formLayout.createSequentialGroup()
                                        .addComponent(dateOfBirthLabel)
                                        .addGap(18, 18, 18)
                                        .addComponent(dateOfBirthPicker, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(formLayout.createSequentialGroup()
                                        .addComponent(genderLabel)
                                        .addGap(18, 18, 18)
                                        .addComponent(maleRadioButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(femaleRadioButton))
                                    .addGroup(formLayout.createSequentialGroup()
                                        .addComponent(passwordLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(formLayout.createSequentialGroup()
                                        .addComponent(confirmPasswordLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(confirmPasswordField, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(formLayout.createSequentialGroup()
                                        .addComponent(emailLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(emailField, GroupLayout.PREFERRED_SIZE, 258, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(formLayout.createSequentialGroup()
                                        .addComponent(addressLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(addressScrollPane, GroupLayout.PREFERRED_SIZE, 258, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(formLayout.createSequentialGroup()
                                        .addComponent(fullnameLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fullnameField, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)))
                                .addGap(12, 12, 12)))
                        .addGap(0, 73, Short.MAX_VALUE)))
                .addContainerGap())
        );
        formLayout.setVerticalGroup(
            formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(formLayout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(fullnameLabel)
                    .addComponent(fullnameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(genderLabel)
                    .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(maleRadioButton)
                        .addComponent(femaleRadioButton)))
                .addGap(18, 18, 18)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(dateOfBirthLabel)
                    .addComponent(dateOfBirthPicker, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(addressLabel)
                    .addComponent(addressScrollPane, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(emailLabel)
                    .addComponent(emailField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(confirmPasswordLabel)
                    .addComponent(confirmPasswordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(createAccButton)
                    .addComponent(returnLoginButton))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        main.add(title);
        main.add(form);
        add(main, BorderLayout.CENTER);
    }

    public JTextField getFullnameField() {
        return fullnameField;
    }

    public JTextField getEmailField() {
        return emailField;
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JPasswordField getConfirmPasswordField() {
        return confirmPasswordField;
    }

    public JRadioButton getMaleRadioButton() {
        return maleRadioButton;
    }

    public JRadioButton getFemaleRadioButton() {
        return femaleRadioButton;
    }

    public JTextArea getAddressField() {
        return addressField;
    }

    public void addCreateAccButtonEvent(ActionListener l) {
        createAccButton.addActionListener(l);
    }

    public void addReturnLoginButtonEvent(ActionListener l) {
        returnLoginButton.addActionListener(l);
    }

    public DatePicker getDateOfBirthPicker() {
        return dateOfBirthPicker;
    }
}
