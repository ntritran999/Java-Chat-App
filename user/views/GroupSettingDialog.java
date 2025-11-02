package user.views;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class GroupSettingDialog extends JDialog {
    private JTextField groupNameField, addMemberField, addAdminField, removeMemberField;
    private JButton changeGroupNameButton, addMemberButton, addAdminButton, removeMemberButton;
    public GroupSettingDialog(JFrame parent) {
        super(parent, "Cài đặt nhóm chat", true);
        setPreferredSize(new Dimension(500, 200));
        
        Container cp = getContentPane();
        cp.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

        JLabel groupNameLabel = new JLabel("Tên nhóm mới:"), 
               addMemLabel = new JLabel("Thêm thành viên:"), 
               addAdminLabel = new JLabel("Gán quyền admin:"), 
               removeMemLabel = new JLabel("Xoá thành viên:");

        groupNameField = new JTextField();
        groupNameField.setPreferredSize(new Dimension(250, 30));
        addMemberField = new JTextField();
        addMemberField.setPreferredSize(new Dimension(250, 30)); 
        addAdminField = new JTextField();
        addAdminField.setPreferredSize(new Dimension(250, 30));
        removeMemberField = new JTextField();
        removeMemberField.setPreferredSize(new Dimension(250, 30));

        changeGroupNameButton = new JButton("Đặt lại");
        addMemberButton = new JButton("Thêm");
        addAdminButton = new JButton("Gán");
        removeMemberButton = new JButton("Xoá");

        cp.add(groupNameLabel);
        cp.add(groupNameField);
        cp.add(changeGroupNameButton);

        cp.add(addMemLabel);
        cp.add(addMemberField);
        cp.add(addMemberButton);

        cp.add(addAdminLabel);
        cp.add(addAdminField);
        cp.add(addAdminButton);

        cp.add(removeMemLabel);
        cp.add(removeMemberField);
        cp.add(removeMemberButton);

        pack();
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clearGroupSettingDialog();
            }
        });
    }
    
    public void showGroupSettingDialog() {
        setVisible(true);
    }

    public void clearGroupSettingDialog() {
        groupNameField.setText(""); 
        addMemberField.setText(""); 
        addAdminField.setText(""); 
        removeMemberField.setText("");
    }

    public void addChangeGroupNameEvent(ActionListener l) {
        changeGroupNameButton.addActionListener(l);
    }

    public void addAddMemEvent(ActionListener l) {
        addMemberButton.addActionListener(l);
    }

    public void addAddAdminEvent(ActionListener l) {
        addAdminButton.addActionListener(l);
    }

    public void addRemoveMemEvent(ActionListener l) {
        removeMemberButton.addActionListener(l);
    }
}
