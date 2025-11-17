package user.views;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CreateGroupDialog extends JDialog {
    private JTextField groupNameField, addMemberField;
    private JTextArea memList;
    private JButton createButton;
    public CreateGroupDialog(JFrame parent) {
        super(parent, "Tạo nhóm chat", true);
        setPreferredSize(new Dimension(300, 310));
        
        Container cp = getContentPane();
        cp.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

        JLabel groupNameLabel = new JLabel("Tên nhóm:"),
               addMemberLabel = new JLabel("Thêm thành viên:"),
               memberListLabel = new JLabel("Danh sách thành viên");
        
        addMemberLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        memberListLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        groupNameField = new JTextField();
        groupNameField.setPreferredSize(new Dimension(200, 20));
        addMemberField = new JTextField();
        addMemberField.setPreferredSize(new Dimension(250, 20));

        memList = new JTextArea();
        memList.setEditable(false);
        JScrollPane sp = new JScrollPane(memList);
        sp.setPreferredSize(new Dimension(250, 100));

        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttonPane.setPreferredSize(new Dimension(280, 100));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        createButton = new JButton("Tạo");
        buttonPane.add(createButton);

        cp.add(groupNameLabel);
        cp.add(groupNameField);
        cp.add(addMemberLabel);
        cp.add(addMemberField);
        cp.add(memberListLabel);
        cp.add(sp);
        cp.add(buttonPane);

        pack();
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clearCreateGroupDialog();
            }
        });
    }
    
    public void showCreateGroupDialog() {
        setVisible(true);
    }

    public void clearCreateGroupDialog() {
        groupNameField.setText("");
        addMemberField.setText("");
        memList.setText("");
    }

    public String getGroupName() {
        return groupNameField.getText();
    }

    public void addToMemList(String name) {
        memList.setText(memList.getText() + name + "\n");
    }

    public String[] getMemList() {
        return memList.getText().split("\\n");
    }
    
    public void addAddMemberEvent(ActionListener l) {
        addMemberField.addActionListener(l);
    }

    public void addCreateGroupEvent(ActionListener l) {
        createButton.addActionListener(l);
    }

    public String getSearch() {
        return addMemberField.getText();
    }
}
