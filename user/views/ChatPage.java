package user.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import static user.views.Style.*;

class IconButton extends JButton {
    public IconButton(String iconPath) {
        this.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        this.setContentAreaFilled(false);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setPreferredSize(new Dimension(32, 32));
    }
}

public class ChatPage extends JPanel {
    public class FriendPanel extends JPanel {
        private JButton moreButton, removeFriendButton, blockButton;
        private JLabel fullName, status;
        public FriendPanel(String fullName, String status) {
            this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            this.setPreferredSize(new Dimension(300, 50));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
            this.fullName = new JLabel(fullName);
            this.fullName.setFont(new Font("SansSerif", Font.BOLD, 24));
            this.status = new JLabel(status);
            textPanel.add(this.fullName);
            textPanel.add(this.status);

            removeFriendButton = new JButton();
            blockButton = new JButton();
            moreButton = new IconButton("/assets/icons/more-icon.png");
            moreButton.addActionListener( _ -> {
                removeFriendButton.setText("Huỷ kết bạn");
                removeFriendButton.setIcon(new ImageIcon(getClass().getResource("/assets/icons/trashcan-icon.png")));
                blockButton.setText("Chặn");
                blockButton.setIcon(new ImageIcon(getClass().getResource("/assets/icons/block-icon.png")));
                JButton[] btns = {removeFriendButton, blockButton};
                JOptionPane.showOptionDialog(null, 
                                             "Bạn có muốn hủy kết bạn hoặc chặn người dùng này?", 
                                             "Cài đặt bạn bè", 
                                             JOptionPane.YES_NO_OPTION, 
                                             JOptionPane.WARNING_MESSAGE, 
                                             null, 
                                             btns, 
                                             btns[0]);
            });
            
            this.add(textPanel);
            this.add(Box.createHorizontalGlue());
            this.add(moreButton);
        }

        public JButton getMoreButton() {
            return moreButton;
        }

        public JButton getRemoveFriendButton() {
            return removeFriendButton;
        }

        public JButton getBlockButton() {
            return blockButton;
        }

        public JLabel getfullName() {
            return fullName;
        }

        public JLabel getStatus() {
            return status;
        }
    }

    private JTextField searchField;
    private JTextArea messageTextArea;
    private IconButton settingButton, findButton, addFriendButton, addGroupButton, sendButton;
    private JPanel listPanel;
    public ChatPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(bgColorDark));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.PAGE_AXIS));
        sideBar.setPreferredSize(new Dimension(80, 100));

        JLabel logo = new JLabel();
        logo.setIcon(new ImageIcon(getClass().getResource("/assets/icons/logo-icon.png")));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        settingButton = new IconButton("/assets/icons/setting-icon.png");
        settingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingButton.setPreferredSize(new Dimension(40, 40));

        sideBar.add(logo);
        sideBar.add(Box.createVerticalGlue());
        sideBar.add(settingButton);

        JPanel main = new JPanel();
        main.setLayout(new BorderLayout(10, 0));
        main.setBackground(new Color(bgColorDark));
        main.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JPanel friendListPanel = new JPanel();
        friendListPanel.setLayout(new BorderLayout());
        friendListPanel.setPreferredSize(new Dimension(350,350));
        JPanel searchBar = new JPanel();
        searchBar.setLayout(new FlowLayout(FlowLayout.TRAILING, 10, 10));
        searchField = new JTextField("Tìm Kiếm");
        searchField.setPreferredSize(new Dimension(200, 30));
        findButton = new IconButton("/assets/icons/search-icon.png");
        addFriendButton = new IconButton("/assets/icons/add-friend-icon.png");
        addGroupButton = new IconButton("/assets/icons/add-group-icon.png");
        
        searchBar.add(searchField);
        searchBar.add(findButton);
        searchBar.add(addFriendButton);
        searchBar.add(addGroupButton);
        
        JScrollPane friendScrollPane = new JScrollPane();
        listPanel = new JPanel();
        listPanel.setLayout(new WrapLayout());
        friendScrollPane.setViewportView(listPanel);
        
        friendListPanel.add(searchBar, BorderLayout.NORTH);
        friendListPanel.add(friendScrollPane, BorderLayout.CENTER);

        JPanel chatPanel = new JPanel(new BorderLayout());

        JPanel centerChatPanel = new JPanel();
        centerChatPanel.setBackground(Color.red);
        JPanel bottomChatPanel = new JPanel();

        JScrollPane msgScrollPane = new JScrollPane();
        messageTextArea = new JTextArea(2, 100);
        messageTextArea.setLineWrap(true);
        msgScrollPane.setViewportView(messageTextArea);
        msgScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sendButton = new IconButton("/assets/icons/send-icon.png");

        bottomChatPanel.add(msgScrollPane);
        bottomChatPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        bottomChatPanel.add(sendButton);
        chatPanel.add(centerChatPanel, BorderLayout.CENTER);
        chatPanel.add(bottomChatPanel, BorderLayout.SOUTH);

        main.add(friendListPanel, BorderLayout.WEST);
        main.add(chatPanel, BorderLayout.CENTER);
        this.add(sideBar, BorderLayout.WEST);
        this.add(main, BorderLayout.CENTER);
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getSettingButton() {
        return settingButton;
    }

    public JButton getFindButton() {
        return findButton;
    }

    public JButton getAddFriendButton() {
        return addFriendButton;
    }

    public JButton getAddGroupButton() {
        return addGroupButton;
    }

    public JPanel getListPanel() {
        return listPanel;
    }
    
    public JTextArea getMessageArea() {
        return messageTextArea;
    }

    public JButton getSendButton() {
        return sendButton;
    }
}
