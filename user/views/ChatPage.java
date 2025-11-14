package user.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import static user.views.Style.*;

class IconButton extends JButton {
    public IconButton(String iconPath) {
        setIcon(new ImageIcon(getClass().getResource(iconPath)));
        setContentAreaFilled(false);
        setBorder(BorderFactory.createEmptyBorder());
        setPreferredSize(new Dimension(32, 32));
    }
}

public class ChatPage extends JPanel {
    public class ChatLinePanel extends JPanel {
        public ChatLinePanel(String text, boolean sender) {
            int align = (sender) ? FlowLayout.TRAILING : FlowLayout.LEADING;
            setLayout(new FlowLayout(align, 10, 0));

            JTextArea msg = new JTextArea(text);
            msg.setFont(msg.getFont().deriveFont(16f));
            msg.setLineWrap(true);
            msg.setWrapStyleWord(true);
            msg.setEditable(false);
            msg.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.black),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            msg.setBackground(new Color(bgColorDark));

            int maxWidth = 400;
            msg.setSize(new Dimension(maxWidth, Integer.MAX_VALUE));
            Dimension d = msg.getPreferredSize();
            msg.setPreferredSize(d);
            msg.setMaximumSize(new Dimension(maxWidth, d.height));

            add(msg);
            msg.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent me) {
                    if (SwingUtilities.isRightMouseButton(me)) {
                        Object[] options = { "Có", "Không" };
                        JOptionPane.showOptionDialog(null, 
                                                "Bạn có muốn xoá đoạn chat này không?", 
                                                "Xoá chat",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE,
                                                null,
                                                options,
                                                options[0]);
                    }
                }
            });
        }
    }

    public class SearchResultPanel extends JPanel {
        private JLabel fullName;

        public SearchResultPanel(String fullName, MouseListener l) {
            addMouseListener(l);
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setPreferredSize(new Dimension(250, 30));
            setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            
            this.fullName = new JLabel(fullName);
            this.fullName.setFont(new Font("SansSerif", Font.BOLD, 16));

            add(this.fullName);
        }

        public JLabel getfullName() {
            return fullName;
        }
    }

    public class GroupPanel extends JPanel {
        private JButton groupSettingButton;
        private JLabel fullName;

        public GroupPanel(String fullName, MouseListener l) {
            addMouseListener(l);
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setPreferredSize(new Dimension(300, 50));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
            this.fullName = new JLabel(fullName);
            this.fullName.setFont(new Font("SansSerif", Font.BOLD, 24));
            textPanel.add(this.fullName);

            groupSettingButton = new IconButton("/assets/icons/more-icon.png");

            add(textPanel);
            add(Box.createHorizontalGlue());
            add(groupSettingButton);
        }

        public JLabel getfullName() {
            return fullName;
        }

        public void addGroupSettingButtonEvent(ActionListener l) {
            groupSettingButton.addActionListener(l);
        }
    }

    public class PersonPanel extends JPanel {
        private JButton createMsgButton, reportButton;
        private JLabel fullName, status;

        public PersonPanel(String fullName, String status, MouseListener l) {
            addMouseListener(l);
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setPreferredSize(new Dimension(300, 50));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
            this.fullName = new JLabel(fullName);
            this.fullName.setFont(new Font("SansSerif", Font.BOLD, 24));
            this.status = new JLabel(status);
            textPanel.add(this.fullName);
            textPanel.add(this.status);

            createMsgButton = new IconButton("/assets/icons/add-friend-icon.png");
            reportButton = new IconButton("/assets/icons/spam-icon.png");

            add(textPanel);
            add(Box.createHorizontalGlue());
            add(createMsgButton);
            add(Box.createRigidArea(new Dimension(10, 0)));
            add(reportButton);
        }

        public JLabel getfullName() {
            return fullName;
        }

        public JLabel getStatus() {
            return status;
        }

        public void addcreateMsgButtonEvent(ActionListener l) {
            createMsgButton.addActionListener(l);
        }

        public void addReportButtonEvent(ActionListener l) {
            reportButton.addActionListener(l);
        }

    }

    public class FriendPanel extends JPanel {
        private JButton removeFriendButton, blockButton, reportButton;
        private JLabel fullName, status;

        public FriendPanel(String fullName, String status, MouseListener l) {
            addMouseListener(l);
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setPreferredSize(new Dimension(300, 50));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
            this.fullName = new JLabel(fullName);
            this.fullName.setFont(new Font("SansSerif", Font.BOLD, 24));
            this.status = new JLabel(status);
            textPanel.add(this.fullName);
            textPanel.add(this.status);

            removeFriendButton = new JButton();
            blockButton = new JButton();
            reportButton = new JButton();
            JButton moreButton = new IconButton("/assets/icons/more-icon.png");
            moreButton.addActionListener(_ -> {
                removeFriendButton.setText("Huỷ kết bạn");
                removeFriendButton.setIcon(new ImageIcon(getClass().getResource("/assets/icons/trashcan-icon.png")));
                blockButton.setText("Chặn");
                blockButton.setIcon(new ImageIcon(getClass().getResource("/assets/icons/block-icon.png")));
                reportButton.setText("Báo cáo spam");
                reportButton.setIcon(new ImageIcon(getClass().getResource("/assets/icons/spam-icon.png")));
                JButton[] btns = { removeFriendButton, blockButton, reportButton };
                JOptionPane.showOptionDialog(null,
                        "Bạn có muốn hủy kết bạn, chặn hay báo cáo spam người dùng này?",
                        "Cài đặt bạn bè",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        btns,
                        null);
            });

            add(textPanel);
            add(Box.createHorizontalGlue());
            add(moreButton);
        }

        public void addRemoveFriendButtonEvent(ActionListener l) {
            removeFriendButton.addActionListener(l);
        }

        public void addBlockButtonEvent(ActionListener l) {
            blockButton.addActionListener(l);
        }

        public void addReportButtonEvent(ActionListener l) {
            reportButton.addActionListener(l);
        }

        public JLabel getfullName() {
            return fullName;
        }

        public JLabel getStatus() {
            return status;
        }
    }

    public class FriendRequestPanel extends JPanel {
        private IconButton addButton, declineButton;
        private JLabel fullName;

        public FriendRequestPanel(String fullName) {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setPreferredSize(new Dimension(300, 50));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
            this.fullName = new JLabel(fullName);
            this.fullName.setFont(new Font("SansSerif", Font.BOLD, 24));
            textPanel.add(this.fullName);

            addButton = new IconButton("/assets/icons/yes-icon.png");
            declineButton = new IconButton("/assets/icons/no-icon.png");

            add(textPanel);
            add(Box.createHorizontalGlue());
            add(addButton);
            add(Box.createRigidArea(new Dimension(5, 0)));
            add(declineButton);
        }

        public void addAddButtonEvent(ActionListener l) {
            addButton.addActionListener(l);
        }

        public void addDeclineButtonEvent(ActionListener l) {
            declineButton.addActionListener(l);
        }

        public JLabel getFullName() {
            return fullName;
        }
    }

    private JTextField searchField, chatSearchField;
    private JTextArea messageTextArea;
    private IconButton settingButton, exitButton, inboxButton, listButton, onlineButton, createMsgButton, createGroupButton,
            sendButton, deleteAllHistoryButton, chatSuggestionButtton;
    private JButton findButton, findAllButton;
    private JPanel listPanel, centerChatPanel, topChatPanel;

    public ChatPage() {
        setLayout(new BorderLayout());
        setBackground(new Color(bgColorDark));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.PAGE_AXIS));
        sideBar.setPreferredSize(new Dimension(80, 100));

        JLabel logo = new JLabel();
        logo.setIcon(new ImageIcon(getClass().getResource("/assets/icons/logo-icon.png")));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        onlineButton = new IconButton("/assets/icons/online-icon.png");
        onlineButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        onlineButton.setPreferredSize(new Dimension(40, 40));

        listButton = new IconButton("/assets/icons/person-icon.png");
        listButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        listButton.setPreferredSize(new Dimension(40, 40));

        inboxButton = new IconButton("/assets/icons/invite-icon.png");
        inboxButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        inboxButton.setPreferredSize(new Dimension(40, 40));

        exitButton = new IconButton("/assets/icons/exit-icon.png");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setPreferredSize(new Dimension(40, 40));

        settingButton = new IconButton("/assets/icons/setting-icon.png");
        settingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingButton.setPreferredSize(new Dimension(40, 40));

        sideBar.add(logo);
        sideBar.add(Box.createVerticalGlue());
        sideBar.add(onlineButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 10)));
        sideBar.add(listButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 10)));
        sideBar.add(inboxButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 10)));
        sideBar.add(exitButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 10)));
        sideBar.add(settingButton);

        JPanel main = new JPanel();
        main.setLayout(new BorderLayout(10, 0));
        main.setBackground(new Color(bgColorDark));
        main.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JPanel friendListPanel = new JPanel();
        friendListPanel.setLayout(new BorderLayout());
        friendListPanel.setPreferredSize(new Dimension(350, 350));
        JPanel searchBar = new JPanel();
        searchBar.setLayout(new FlowLayout(FlowLayout.TRAILING, 10, 10));
        searchField = new JTextField("Tìm Kiếm");
        searchField.setPreferredSize(new Dimension(200, 30));
        createMsgButton = new IconButton("/assets/icons/new-msg-icon.png");
        createGroupButton = new IconButton("/assets/icons/add-group-icon.png");

        searchBar.add(searchField);
        searchBar.add(Box.createRigidArea(new Dimension(20, 0)));
        searchBar.add(createMsgButton);
        searchBar.add(createGroupButton);

        listPanel = new JPanel();
        listPanel.setLayout(new WrapLayout());
        JScrollPane friendScrollPane = new JScrollPane(listPanel);

        friendListPanel.add(searchBar, BorderLayout.NORTH);
        friendListPanel.add(friendScrollPane, BorderLayout.CENTER);

        JPanel chatPanel = new JPanel(new BorderLayout());

        centerChatPanel = new JPanel();
        centerChatPanel.setLayout(new BoxLayout(centerChatPanel, BoxLayout.PAGE_AXIS));
        JScrollPane centerScrollPane = new JScrollPane(centerChatPanel);
        JPanel bottomChatPanel = new JPanel();
        JPanel topChatPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        chatSearchField = new JTextField();
        chatSearchField.setPreferredSize(new Dimension(250, 30));
        JButton searchBtn = new IconButton("/assets/icons/search-icon.png");
        deleteAllHistoryButton = new IconButton("/assets/icons/delete-history-icon.png");
        findButton = new JButton("Ở cuộc hội thoại này");
        findAllButton = new JButton("Ở tất cả cuộc hội thoại");
        searchBtn.addActionListener(e -> {
                JButton[] btns = { findButton, findAllButton };
                JOptionPane.showOptionDialog(this,
                        "Bạn muốn tìm kiếm ở cuộc hội thoại nào?",
                        "Tìm kiếm hội thoại",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        btns,
                        null);
        });
        topChatPanel.add(chatSearchField);
        topChatPanel.add(searchBtn);
        topChatPanel.add(deleteAllHistoryButton);

        messageTextArea = new JTextArea();
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);
        messageTextArea.setFont(messageTextArea.getFont().deriveFont(16f));
        JScrollPane msgScrollPane = new JScrollPane(messageTextArea);
        msgScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        msgScrollPane.setPreferredSize(new Dimension(700, 30));
        sendButton = new IconButton("/assets/icons/send-icon.png");
        chatSuggestionButtton = new IconButton("/assets/icons/llm-icon.png");

        bottomChatPanel.add(msgScrollPane);
        bottomChatPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        bottomChatPanel.add(chatSuggestionButtton);
        bottomChatPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        bottomChatPanel.add(sendButton);
        chatPanel.add(centerScrollPane, BorderLayout.CENTER);
        chatPanel.add(bottomChatPanel, BorderLayout.SOUTH);
        chatPanel.add(topChatPanel, BorderLayout.NORTH);

        main.add(friendListPanel, BorderLayout.WEST);
        main.add(chatPanel, BorderLayout.CENTER);
        add(sideBar, BorderLayout.WEST);
        add(main, BorderLayout.CENTER);

    }

    public FriendPanel createFriendPanel(String fullName, String status, MouseListener l) {
        return new FriendPanel(fullName, status, l);
    }

    public FriendRequestPanel createFriendRequestPanel(String fullName) {
        return new FriendRequestPanel(fullName);
    }

    public PersonPanel createPersonPanel(String fullName, String status, MouseListener l) {
        return new PersonPanel(fullName, status, l);
    }

    public GroupPanel createGroupPanel(String fullName, MouseListener l) {
        return new GroupPanel(fullName, l);
    }

    public SearchResultPanel createSearchResultPanel(String fullName, MouseListener l) {
        return new SearchResultPanel(fullName, l);
    }

    public ChatLinePanel createChatLinePanel(String text, boolean sender) {
        return new ChatLinePanel(text, sender);
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JPanel getListPanel() {
        return listPanel;
    }

    public JTextArea getMessageArea() {
        return messageTextArea;
    }

    public void emptyMessage() {
        messageTextArea.setText("");
    }

    public void addSettingButtonEvent(ActionListener l) {
        settingButton.addActionListener(l);
    }

    public void addExitButtonEvent(ActionListener l) {
        exitButton.addActionListener(l);
    }

    public void addCreateMsgButtonEvent(ActionListener l) {
        createMsgButton.addActionListener(l);
    }

    public void addCreateGroupButtonEvent(ActionListener l) {
        createGroupButton.addActionListener(l);
    }

    public void addSendButtonEvent(ActionListener l) {
        sendButton.addActionListener(l);
    }

    public void addInboxButtonEvent(ActionListener l) {
        inboxButton.addActionListener(l);
    }

    public void addOnlineButtonEvent(ActionListener l) {
        onlineButton.addActionListener(l);
    }

    public void addListButtonEvent(ActionListener l) {
        listButton.addActionListener(l);
    }

    public void addFindButtonEvent(ActionListener l) {
        findButton.addActionListener(l);
    }

    public void addFindAllButtonEvent(ActionListener l) {
        findAllButton.addActionListener(l);
    }

    public void addDeleteAllHistoryEvent(ActionListener l) {
        deleteAllHistoryButton.addActionListener(l);
    }

    public void addChatSuggestionEvent(ActionListener l) {
        chatSuggestionButtton.addActionListener(l);
    }

    public JPanel getCenterChatPanel() {
        return centerChatPanel;
    }

    public JPanel getTopChatPanel() {
        return topChatPanel;
    }

    public void addToListPanel(Component c) {
        listPanel.add(c);
        listPanel.revalidate();
        listPanel.repaint();
    }

    public void clearListPanel() {
        listPanel.removeAll();
    }

    public void updateListPanel() {
        listPanel.revalidate();
        listPanel.repaint();
    }

    public void addToChatPanel(Component c) {
        centerChatPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerChatPanel.add(c);
        centerChatPanel.revalidate();
        centerChatPanel.repaint();
    }
}
