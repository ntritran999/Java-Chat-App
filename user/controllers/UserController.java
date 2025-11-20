package user.controllers;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import java.util.*;
import java.util.concurrent.ExecutionException;

import admin.views.AdminDashboard;
import user.models.*;
import user.views.*;

public class UserController {
    private UserModel userModel;
    private UserFrame userFrame;
    
    public UserController(UserModel userModel, UserFrame userFrame) {
        this.userModel = userModel;
        this.userFrame = userFrame;
    }

    public void useLoginPage() {
        LoginPage lp = new LoginPage();
        LoginPageModel lpModel = new LoginPageModel(null, null);
        lp.addLoginButtonEvent(e -> {
            SwingWorker<Integer, Void> worker = new SwingWorker<Integer,Void>(){
                @Override
                protected Integer doInBackground() throws SQLException{
                    lpModel.setUserName(lp.getUsernameField().getText().trim());
                    String password = new String(lp.getPasswordField().getPassword());
                    lpModel.setPassword(password);

                    return lpModel.authenticateAccount();
                }

                @Override
                protected void done(){
                    try {
                        lpModel.setAuthen(get());
                        final int isAuthen = lpModel.getAuthen();
                        if (isAuthen == 2) {
                            userFrame.setVisible(false);
                            AdminDashboard ad = new AdminDashboard();
                            ad.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosing(WindowEvent e) {
                                    userFrame.setVisible(true);
                                }
                            });
                        }       
                        else if(isAuthen == 1) {
                            useChatPage(lpModel.getUsername());
                        } else{
                            lp.showLoginFail();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        });
        lp.addCreateAccButtonEvent(e -> {
            useSignUpPage();
        });
        lp.addResetPasswordButtonEvent(e -> {
            // lp.showLogin();
            // lp.showResetFail("Sai tên đăng nhập.");
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean,Void>() {
                LoginPageModel model = new LoginPageModel();
                @Override
                protected Boolean doInBackground() throws SQLException{
                    String username = lp.getUsernameForReset().getText().trim();
                    if(username != null && !username.isEmpty()){
                        model.setUserName(username);
                        return model.checkingExistsResetAccountPassword();
                    }
                    return false;
                }

                @Override
                protected void done(){
                    try {
                        boolean success = get();
                        if(!success)
                            lp.showResetFail("Không tồn tại username trên hệ thống");
                        else{
                            String emailTo = model.getEmail();
                            String newPass = model.getPassword();
                            if(emailTo != null && !emailTo.trim().isEmpty()){
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("Mật khẩu mới: ");
                                stringBuilder.append(newPass);
                                GmailSender.sendMail(emailTo, stringBuilder.toString());
                                lp.showResetSuccess();
                                lp.getUsernameForReset().setText("");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        });
        userFrame.updateUserFrame(lp);
    }

    public void useSignUpPage() {
        SignupPage sp = new SignupPage();
        sp.addCreateAccButtonEvent(e -> {
            SwingWorker<Integer, Void> worker = new SwingWorker<Integer,Void>() {
                @Override
                protected Integer doInBackground() throws SQLException{
                    String fullName = sp.getFullnameField().getText().trim();
                    String email = sp.getEmailField().getText().trim();
                    String gender = null;
                    if(sp.getMaleRadioButton().isSelected())
                        gender = "M";
                    else if(sp.getFemaleRadioButton().isSelected())
                        gender = "F";
                    String dob = sp.getDateOfBirthPicker().getDate().toString();
                    String address = sp.getAddressField().getText().trim();
                    String username = sp.getUsernameField().getText().trim();
                    String password = new String(sp.getPasswordField().getPassword());
                    String confirmPassword = new String(sp.getConfirmPasswordField().getPassword());
                    if(!confirmPassword.equals(password))
                        return -3;
                    SignupModel model = new SignupModel(fullName, email, gender, dob, address, username, password);
                    return model.signUpAccount();
                }

                @Override 
                protected void done(){
                    try {
                        int status = get();
                        switch (status) {
                            case -3:
                                sp.showSignUpFail("Mật khẩu xác nhận lại không khớp");
                                break;
                            case -2:
                                sp.showSignUpFail("Lỗi hệ thống");
                                break;
                            case -1:
                                sp.showSignUpFail("Username hoặc password không hợp lệ");
                                break;
                            case 0:
                                sp.showSignUpFail("Username đã tồn tại");
                                break;
                            case 1:
                                sp.showSignUpSuccess();
                                useLoginPage();
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        });
        sp.addReturnLoginButtonEvent(e -> {
            useLoginPage();
        });

        userFrame.updateUserFrame(sp);
    }

    public void useChatPage(String username) {
        ChatPage cp = new ChatPage();
        UpdateInfoDialog updateInfoDialog = userFrame.getUpdateInfoDialog();
        SearchDialog msgSearchDialog = userFrame.getMsgSearchDialog();
        SearchDialog groupSearchDialog = userFrame.getGroupSearchDialog();
        ChatSuggestDialog chatSuggestDialog = userFrame.getChatSuggestDialog();
        SearchDialog addFriendSearchDialog = userFrame.getAddFriendSearchDialog();

        loadConversations(cp, username);

        cp.addToChatPanel(cp.createChatLinePanel("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.", true));
        cp.addToChatPanel(cp.createChatLinePanel("Wow, that's a lot of text.", false));
        cp.addToChatPanel(cp.createChatLinePanel("I know right", true));

        cp.addSettingButtonEvent(e -> {
            updateInfoDialog.showUpdateInfoDialog();
        });

        cp.addExitButtonEvent(e -> {
            if (cp.showLogoutWarning() == 0) {
                useLoginPage();
            }
        });

        cp.addInboxButtonEvent(e -> {
            cp.clearListPanel();
            cp.addToListPanel(cp.createFriendRequestPanel("Some stranger"));
            cp.updateListPanel();
        });

        cp.addListButtonEvent(e -> {

        });

        cp.addOnlineButtonEvent(e -> {
            loadOnlineFriends(cp, username);
        });

        cp.addSendButtonEvent(e -> {
            String msg = cp.getMessageArea().getText();
            if (!msg.isEmpty()) {
                cp.emptyMessage();
                cp.addToChatPanel(cp.createChatLinePanel(msg, true));
            }
        });

        cp.addCreateMsgButtonEvent(e -> {
            msgSearchDialog.showSearchDialog();
        });

        cp.addCreateGroupButtonEvent(e -> {
            groupSearchDialog.showSearchDialog();
        });

        cp.addDeleteAllHistoryEvent(e -> {
            int op = JOptionPane.showConfirmDialog(null, 
                                                    "Bạn có muốn xoá tất cả lịch sử chat?",
                                                    "Xoá lịch sử chat",
                                                    JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.WARNING_MESSAGE);
        });

        cp.addChatSuggestionEvent(e -> {
            chatSuggestDialog.showSuggestDialog();
        });

        msgSearchDialog.getSearchField().addActionListener(e -> {
            loadSearchResult(cp, msgSearchDialog, username, "person");
        });

        groupSearchDialog.getSearchField().addActionListener(e -> {
            loadSearchResult(cp, groupSearchDialog, username, "group");
        });

        cp.addMsgListButonEvent(e -> {
            loadConversations(cp, username);
        });

        cp.addAddFriendButtonEvent(e -> {
            addFriendSearchDialog.showSearchDialog();
        });
        
        addFriendSearchDialog.getSearchField().addActionListener(e -> {
            loadAddFriendSearchResult(cp, addFriendSearchDialog, username);
        });

        userFrame.updateUserFrame(cp);
    }

    private void openChat() {
        JOptionPane.showMessageDialog(null, "Hello");
    }

    private void loadOnlineFriends(ChatPage cp, String username) {
        new SwingWorker<OnlineListModel, Void> () {
            @Override
            protected OnlineListModel doInBackground() throws Exception {
                return new OnlineListModel(userModel.getConn(), username);
            }
            @Override
            protected void done() {
                try {
                    cp.clearListPanel();
                    OnlineListModel olModel = get();
                    ArrayList<HashMap<String, String>> list = olModel.getOnlines();
                    for (var map : list) {
                        ChatPage.FriendPanel fp = cp.createFriendPanel(map.get("fullname"), "Online", new MouseAdapter() {
                            @Override
                            public void mousePressed(MouseEvent me) {
                                openChat();
                            }
                        });
                        fp.addReportButtonEvent(ev -> {
                            SwingUtilities.getWindowAncestor((Component)ev.getSource()).dispose();
                            new SwingWorker<Void, Void>() {
                                @Override
                                protected Void doInBackground() {
                                    SpamModel.sendReport(userModel.getConn(), map.get("id"), username);
                                    return null;
                                }
                            }.execute();
                        });
                        cp.addToListPanel(fp);
                    }
                    cp.updateListPanel();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.execute();
    }

    private void loadSearchResult(ChatPage cp, SearchDialog sd, String username, String type) {
        String search = sd.getSearchField().getText();
        if (!search.isEmpty()) {
            new SwingWorker<PersonSearchModel, Void> () {
                @Override
                protected PersonSearchModel doInBackground() throws Exception {
                    return new PersonSearchModel(userModel.getConn(), search, username);
                }
                @Override
                protected void done() {
                    try {
                        PersonSearchModel psModel = get();
                        ArrayList<HashMap<String, String>> list = psModel.getResults();
                        sd.clearListPanel();
                        for (var map : list) {
                            MouseAdapter ma = null;
                            if (type == "group") {
                                ma = new MouseAdapter() {
                                    @Override
                                    public void mousePressed(MouseEvent me) {
                                        CreateGroupDialog createGroupDialog = userFrame.getCreateGroupDialog();
                                        CreateGroupController.handleCreateGroup(userModel.getConn(), 
                                                                                createGroupDialog, username, 
                                                                                Integer.valueOf(map.get("id")));
                                        loadConversations(cp, username);
                                    }
                                };
                            }
                            else if (type == "person") {
                                ma = new MouseAdapter() {
                                    @Override
                                    public void mousePressed(MouseEvent me) {
                                        JOptionPane.showMessageDialog(null, "Hello");
                                    }
                                };
                            }
                            sd.addToListPanel(cp.createSearchResultPanel(map.get("name"), ma));
                        }
                        sd.updateListPanel();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }.execute();
        }
    }

    private void loadConversations(ChatPage cp, String username) {
        new SwingWorker<ConversationModel, Void> () {
                @Override
                protected ConversationModel doInBackground() throws Exception {
                    return new ConversationModel(userModel.getConn(), username);
                }
                @Override
                protected void done() {
                    try {
                        cp.clearListPanel();
                        ConversationModel cm = get();
                        ArrayList<HashMap<String, String>> list = cm.getConversations();
                        for (var map : list) {
                            int id = Integer.valueOf(map.get("id"));
                            String name = map.get("name");
                            String status = map.get("status");
                            switch (map.get("type")) {
                                case "friend":
                                    ChatPage.FriendPanel fp = cp.createFriendPanel(name, status, new MouseAdapter() {
                                        @Override
                                        public void mousePressed(MouseEvent me) {
                                            openChat();
                                        }
                                    });
                                    cp.addToListPanel(fp);
                                    break;
                                case "stranger":
                                    ChatPage.PersonPanel pp = cp.createPersonPanel(name, status, new MouseAdapter() {
                                        @Override
                                        public void mousePressed(MouseEvent me) {
                                            openChat();
                                        }
                                    });
                                    cp.addToListPanel(pp);
                                    break;
                                case "group":
                                    ChatPage.GroupPanel gp = cp.createGroupPanel(name, new MouseAdapter() {
                                        @Override
                                        public void mousePressed(MouseEvent me) {
                                            openChat();
                                        }
                                    });
                                    gp.addGroupSettingButtonEvent(e -> {
                                        GroupSettingDialog groupSettingDialog = userFrame.getGroupSettingDialog();
                                        GroupSettingController.handleGroupSetting(userModel.getConn(), groupSettingDialog, id, username);
                                        loadConversations(cp, username);
                                    });
                                    cp.addToListPanel(gp);
                                    break;
                                default:
                                    break;
                            }
                        }
                        cp.updateListPanel();

                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
        }.execute();
    }

    private void loadAddFriendSearchResult(ChatPage cp, SearchDialog sd, String username) {
        System.out.println("Results: ...");
    }
}
