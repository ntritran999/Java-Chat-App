package user.controllers;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import java.util.*;

import admin.views.AdminDashboard;
import server.ChatClient;
import user.models.*;
import user.views.*;
import user.views.ChatPage.FriendRequestPanel;

public class UserController {
    private UserModel userModel;
    private UserFrame userFrame;
    private ChatClient client;
    
    public UserController(UserModel userModel, UserFrame userFrame) {
        this.userModel = userModel;
        this.userFrame = userFrame;
    }

    public void useLoginPage() {
        LoginPage lp = new LoginPage();
        LoginPageModel lpModel = new LoginPageModel(userModel.getConn(), null, null);
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
                            String username = lpModel.getUsername();
                            try {
                                ActivitiesModel.setOnline(userModel.getConn(), username);
                            }
                            catch (Exception e) {
                                System.out.println(e);
                            }
                            useChatPage(username);
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
                LoginPageModel model = new LoginPageModel(userModel.getConn());
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
                    SignupModel model = new SignupModel(userModel.getConn(), fullName, email, gender, dob, address, username, password);
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

        cp.addSettingButtonEvent(e -> {
            updateInfoDialog.showUpdateInfoDialog();
        });

        updateInfoDialog.addUpdateButtonEvent(action1 ->{
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean,Void>() {
                @Override
                protected Boolean doInBackground() throws SQLException{
                    String fullName = null; 
                    String gender = null; 
                    String dob = null; 
                    String address = null; 
                    String email = null;
                    fullName = updateInfoDialog.getFullNameField().getText().trim();
                    gender = null;
                    if(updateInfoDialog.getMaleRadioButton().isSelected())
                        gender = "M";
                    else if(updateInfoDialog.getFemaleRadioButton().isSelected())
                        gender = "F";
                    LocalDate dateDOB = updateInfoDialog.getDobPicker().getDate();
                    if(dateDOB != null)
                        dob = dateDOB.toString();
                    address = updateInfoDialog.getAddressTextArea().getText().trim();
                    email = updateInfoDialog.getEmailField().getText().trim();
                    UpdateInfoDialogModel model = new UpdateInfoDialogModel(userModel.getConn(), fullName, gender, dob, address, email, null, username);
                    return model.updateInfoToDb();
                }

                @Override
                protected void done(){
                    try {
                        boolean success = get();
                        if(success)
                            cp.showUpdateInfoSuccess();
                        else    
                            cp.showUpdateInfoFail();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        });

        updateInfoDialog.addUpdatePasswordEvent(action2 ->{
            SwingWorker<Integer, Void> worker = new SwingWorker<Integer,Void>() {
                // Integer -1(confirm pass ko khop), 0(update fail), 1(update success)
                @Override
                protected Integer doInBackground() throws SQLException{ 
                    String newPassword = new String(updateInfoDialog.getNewPasswordField().getPassword());
                    String newPasswordConfirm = new String(updateInfoDialog.getConfirmPasswordField().getPassword());
                    if(!newPasswordConfirm.equals(newPassword))
                        return -1;
                    UpdateInfoDialogModel model = new UpdateInfoDialogModel(userModel.getConn(), newPassword, username);
                    return model.updatePasswordToDb();
                }

                @Override
                protected void done(){
                    try{
                        int success = get();
                        if(success == 1){
                            cp.showUpdatePassSuccess();
                            updateInfoDialog.getNewPasswordField().setText("");
                            updateInfoDialog.getConfirmPasswordField().setText("");
                        }
                        else if(success == -1)
                            cp.showUpdatePassFail("Mật khẩu xác nhận không khớp");
                        else
                            cp.showUpdatePassFail("Cập nhật mật khẩu không thành công");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

            };
            worker.execute();
        });

        cp.addExitButtonEvent(e -> {
            if (cp.showLogoutWarning() == 0) {
                try {
                    ActivitiesModel.setOffline(userModel.getConn(), username);
                } catch (Exception excep) {
                    System.out.println(excep);
                }
                if (client != null)
                    client.disconnectClient();
                useLoginPage();
            }
        });
        
        cp.addInboxButtonEvent(e -> {
            cp.clearListPanel();
            SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>,Void>() {
                @Override
                protected List<String> doInBackground() throws SQLException{
                    FriendRequestModel model = new FriendRequestModel(username);
                    return model.fetchingFriendRequest();
                }

                @Override 
                protected void done(){
                    try{
                        List<String> arr = get();
                        for(var ob : arr){
                            JPanel friend = cp.createFriendRequestPanel(ob);
                            ((FriendRequestPanel) friend).addAddButtonEvent(e-> {
                                SwingWorker<Boolean, Void> worker1 = new SwingWorker<Boolean, Void>(){
                                    @Override 
                                    protected Boolean doInBackground() throws SQLException{
                                        FriendRequestModel model = new FriendRequestModel(username, ob);
                                        return model.updateAcceptedToDb();
                                    }

                                    @Override 
                                    protected void done(){
                                        boolean success;
                                        try {
                                            success = get();
                                            if(success){
                                                cp.removePanel(friend);
                                                cp.updateListPanel();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                    worker1.execute();
                            });
                                
                            ((FriendRequestPanel) friend).addDeclineButtonEvent(e-> {
                            SwingWorker<Boolean, Void> worker2 = new SwingWorker<Boolean, Void>(){
                                @Override 
                                protected Boolean doInBackground() throws SQLException{
                                    FriendRequestModel model = new FriendRequestModel(username, ob);
                                    return model.updateDeclineToDb();
                                }

                                @Override 
                                protected void done(){
                                    boolean success;
                                    try {
                                        success = get();
                                        if(success){
                                            cp.removePanel(friend);
                                            cp.updateListPanel();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            worker2.execute();
                        });
                            //friend.addDeclineButtonEvent(declineReq());
                            cp.addToListPanel(friend);
                        }
                        cp.updateListPanel();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        });

        cp.addListButtonEvent(e -> {

        });

        cp.addOnlineButtonEvent(e -> {
            loadOnlineFriends(cp, username);
        });

        cp.addSendButtonEvent(e -> {
            String msg = cp.getMessageArea().getText();
            if (!msg.isEmpty() && client != null && client.isInitialized()) {
                cp.emptyMessage();
                cp.addToChatPanel(cp.createChatLinePanel(msg, true));
                client.sendMessage(msg);
                new SwingWorker<Void,Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        int receiver = client.getReceiver();
                        ChatModel cm = new ChatModel(userModel.getConn(), username, receiver, client.getMsgType());
                        cm.saveChat(msg);
                        return null;
                    }
                }.execute();
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

        userFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            ActivitiesModel.setOffline(userModel.getConn(), username);
                        } catch (Exception excep) {
                            System.out.println(excep);
                        }
                        return null;
                    }
                }.execute();
                if (client != null)
                    client.disconnectClient();
            }
        });

        userFrame.updateUserFrame(cp);
    }

    private void openChat(ChatPage cp, String username, int otherId, String type) {
        new SwingWorker<ChatModel, Void>() {
            @Override
            protected ChatModel doInBackground() throws Exception {
                return new ChatModel(userModel.getConn(), username, otherId, type);
            }
            @Override
            protected void done() {
                try {
                    cp.clearChatPanel();
                    ChatModel chatModel = get();
                    ArrayList<HashMap<String, String>> list = chatModel.getChatHistory();
                    int userId = chatModel.getUserId();
                    for (var map: list) {
                        boolean isSender = Integer.valueOf(map.get("sender")) == userId;
                        cp.addToChatPanel(cp.createChatLinePanel(map.get("content"), isSender));
                    }
    
                    if (client != null) 
                        client.disconnectClient();
    
                    client = new ChatClient();
                    client.initClient(chatModel.findUserId(username), otherId, "single");
                    client.addMsgHandler(msg -> {
                        if (msg.getInt("sender_id") == otherId) {
                            ChatPage.ChatLinePanel chatLine = cp.createChatLinePanel(msg.getString("content"), false);
                            cp.addToChatPanel(chatLine);
                        }
                    });
                    client.listen();
    
                    cp.updateChatPanel();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.execute();
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
                                // openChat();
                            }
                        });
                        fp.addReportButtonEvent(ev -> {
                            if(cp.showReportWarning() == 0) {
                                new SwingWorker<Void, Void>() {
                                    @Override
                                    protected Void doInBackground() {
                                        SpamModel.sendReport(userModel.getConn(), map.get("id"), username);
                                        return null;
                                    }
                                }.execute();
                            }
                            SwingUtilities.getWindowAncestor((Component)ev.getSource()).dispose();
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
                            int id = Integer.valueOf(map.get("id"));
                            if (type == "group") {
                                ma = new MouseAdapter() {
                                    @Override
                                    public void mousePressed(MouseEvent me) {
                                        CreateGroupDialog createGroupDialog = userFrame.getCreateGroupDialog();
                                        boolean successful = CreateGroupController.handleCreateGroup(userModel.getConn(), 
                                                                                createGroupDialog, username, id);
                                        
                                        if (successful) {
                                            loadConversations(cp, username);
                                        }
                                    }
                                };
                            }
                            else if (type == "person") {
                                ma = new MouseAdapter() {
                                    @Override
                                    public void mousePressed(MouseEvent me) {
                                        sd.dispose();
                                        cp.setChatName(map.get("name"));
                                        openChat(cp, username, id, "single");
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
                                            // openChat();
                                        }
                                    });
                                    cp.addToListPanel(fp);
                                    break;
                                case "stranger":
                                    ChatPage.PersonPanel pp = cp.createPersonPanel(name, status, new MouseAdapter() {
                                        @Override
                                        public void mousePressed(MouseEvent me) {
                                            cp.setChatName(name);
                                            openChat(cp, username, id, "single");
                                        }
                                    });
                                    pp.addReportButtonEvent(ev -> {
                                        if (cp.showReportWarning() == 0) {
                                            new SwingWorker<Void, Void>() {
                                                @Override
                                                protected Void doInBackground() {
                                                    SpamModel.sendReport(userModel.getConn(), map.get("id"), username);
                                                    return null;
                                                }
                                            }.execute();
                                        }
                                    });
                                    cp.addToListPanel(pp);
                                    break;
                                case "group":
                                    ChatPage.GroupPanel gp = cp.createGroupPanel(name, new MouseAdapter() {
                                        @Override
                                        public void mousePressed(MouseEvent me) {
                                            // openChat();
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
                        if (!list.isEmpty()) {
                            HashMap<String, String> firstEntry = list.get(0);
                            String name = firstEntry.get("name");
                            int otherId = Integer.valueOf(firstEntry.get("id"));
                            String msgType = (firstEntry.get("type").equals("group")) ? "multiple" : "single";
                            cp.setChatName(name);
                            openChat(cp, username, otherId, msgType);
                        }

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
