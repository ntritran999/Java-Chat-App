package user.views;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatLightLaf;

public class UserFrame extends JFrame {
    private UpdateInfoDialog updateInfoDialog;
    private SearchDialog msgSearchDialog, groupSearchDialog;
    private ChatSuggestDialog chatSuggestDialog;
    private SearchDialog addFriendSearchDialog;
    public UserFrame() {
        FlatLightLaf.setup();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1366, 768);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        setTitle("Ứng dụng chat");
    }

    public void setUpUserFrame() {
        updateInfoDialog = new UpdateInfoDialog(this);
        msgSearchDialog = new SearchDialog(this);
        groupSearchDialog = new SearchDialog(this);
        chatSuggestDialog = new ChatSuggestDialog(this);
        addFriendSearchDialog = new SearchDialog(this);
    }

    public void updateUserFrame(JPanel p) {
        setContentPane(p);
        revalidate();
        repaint();
    }

    public UpdateInfoDialog getUpdateInfoDialog() {
        return updateInfoDialog;
    }

    public SearchDialog getMsgSearchDialog() {
        return msgSearchDialog;
    }

    public SearchDialog getGroupSearchDialog() {
        return groupSearchDialog;
    }

    public ChatSuggestDialog getChatSuggestDialog() {
        return chatSuggestDialog;
    }

    public SearchDialog getAddFriendSearchDialog() {
        return addFriendSearchDialog;
    }
}
