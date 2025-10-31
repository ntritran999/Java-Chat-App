package user.controllers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import user.models.*;
import user.views.*;

public class UserController {
    private User user;
    private UserFrame userFrame;
    
    public UserController(User user, UserFrame userFrame) {
        this.user = user;
        this.userFrame = userFrame;
    }

    public void useLoginPage() {
        LoginPage lp = new LoginPage();
        lp.addLoginButtonEvent(e -> {
            useChatPage();
        });
        lp.addCreateAccButtonEvent(e -> {
            useSignUpPage();
        });
        lp.addForgetPasswordButtonEvent(e -> {
            JOptionPane.showMessageDialog(null, "Just some message");
        });

        updateUserFrame(lp);
    }

    public void useSignUpPage() {
        SignupPage sp = new SignupPage();
        sp.addCreateAccButtonEvent(e -> {
            JOptionPane.showMessageDialog(null, "Just some message");
        });
        sp.addReturnLoginButtonEvent(e -> {
            useLoginPage();
        });
        updateUserFrame(sp);
    }

    public void useChatPage() {
        ChatPage cp = new ChatPage();
        cp.addToListPanel(cp.createFriendPanel("Some friend", "Online"));

        cp.addToListPanel(cp.createPersonPanel("Not a friend", "Online"));
        cp.addToListPanel(cp.createGroupPanel("Just a group"));

        cp.addToChatPanel(cp.createChatLinePanel("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.", true));
        cp.addToChatPanel(cp.createChatLinePanel("Wow, that's a lot of text.", false));
        cp.addToChatPanel(cp.createChatLinePanel("I know right", true));

        cp.addSettingButtonEvent(e -> {
            new UpdateInfoDialog(null , true);
        });

        cp.addExitButtonEvent(e -> {
            useLoginPage();
        });

        cp.addInboxButtonEvent(e -> {
            cp.clearListPanel();
            cp.addToListPanel(cp.createFriendRequestPanel("Some stranger"));
            cp.updateListPanel();
        });

        cp.addListButtonEvent(e -> {
            cp.clearListPanel();
            cp.addToListPanel(cp.createFriendPanel("Some friend", "Online"));
            cp.updateListPanel();
        });

        cp.addOnlineButtonEvent(e -> {
            cp.clearListPanel();
            cp.updateListPanel();
        });

        cp.addSendButtonEvent(e -> {
            String msg = cp.getMessageArea().getText();
            if (!msg.isEmpty()) {
                cp.emptyMessage();
                cp.addToChatPanel(cp.createChatLinePanel(msg, true));
            }
        });

        cp.addCreateMsgButtonEvent(e -> {
            SearchDialog sd = new SearchDialog(userFrame);
            sd.showSearchDialog();
            sd.getSearchField().addActionListener(ee -> {
                sd.hideSearchDialog();
                sd.clearListPanel();
                sd.addToListPanel(cp.createSearchResultPanel("Some name"));
                sd.addToListPanel(cp.createSearchResultPanel("Some name"));
                sd.updateListPanel();
                sd.showSearchDialog();
            });
        });

        cp.addCreateGroupButtonEvent(e -> {
            SearchDialog sd = new SearchDialog(userFrame);
            sd.showSearchDialog();
        });

        updateUserFrame(cp);
    }

    private void updateUserFrame(JPanel p) {
        userFrame.setContentPane(p);
        userFrame.revalidate();
        userFrame.repaint();
    }
}
