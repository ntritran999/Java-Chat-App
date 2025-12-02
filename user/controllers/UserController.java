package user.controllers;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import admin.views.AdminDashboard;
import server.ChatClient;
import user.models.*;
import user.services.E2EGroup;
import user.services.E2ESession;
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
        userFrame.setUpUserFrame();
        UpdateInfoDialog updateInfoDialog = userFrame.getUpdateInfoDialog();
        SearchDialog msgSearchDialog = userFrame.getMsgSearchDialog();
        SearchDialog groupSearchDialog = userFrame.getGroupSearchDialog();
        ChatSuggestDialog chatSuggestDialog = userFrame.getChatSuggestDialog();
        SearchDialog addFriendSearchDialog = userFrame.getAddFriendSearchDialog();

        loadConversations(cp, username, false);

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
            var encryptedMsgWrapper = new Object(){ String encryptedMsg = " "; };
            if (!msg.isEmpty() && client != null && client.isInitialized()) {
                int receiver = client.getReceiver();
                String msgType = client.getMsgType();
                ChatModel cm = new ChatModel(userModel.getConn(), username, receiver, msgType);
                new SwingWorker<Integer,Void>() {
                    @Override
                    protected Integer doInBackground() throws Exception {
                        if (msgType.equals("single"))
                            return cm.saveSingleChat(msg);
                        
                        boolean[] isFirstMsg = {false};
                        byte[] groupKey = E2EGroup.getGroupKey(username, receiver, isFirstMsg);
                        if (groupKey != null) {
                            if (isFirstMsg[0]) {
                                ArrayList<HashMap<String, String>> members = cm.findMembers();
                                for (var mem: members) {
                                    int memId = Integer.valueOf(mem.get("id"));
                                    String publicKey = mem.get("public_key");
                                    byte[] sharedKey = E2ESession.getSharedSecret(username, memId, publicKey);
                                    String egk = E2EGroup.encryptGroupKey(groupKey, sharedKey);
                                        client.sendGroupKey(egk, receiver, memId);
                                }
                            }

                            try {
                                encryptedMsgWrapper.encryptedMsg = E2ESession.encrypt(msg, E2ESession.getSecretKeySpec(groupKey));
                                return cm.saveGroupChat(encryptedMsgWrapper.encryptedMsg, receiver);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return -1;
                            }
                        }
                        return -1;
                    }
                    @Override
                    protected void done() {
                        try {
                            int msgId = get();
                            if (msgId != -1) {
                                cp.emptyMessage();
                                ChatPage.ChatLinePanel chatLine = cp.createChatLinePanel(msg, true, msgId);
                                cp.addToChatPanel(chatLine);
                                addDeleteChatLineEvent(chatLine, cm, cp, msgId, username, receiver, msgType);
                                if (msgType.equals("single")) {
                                    client.sendMessage(username, msg, msgId);
                                }
                                else {
                                    client.sendMessage(username, encryptedMsgWrapper.encryptedMsg, msgId);
                                }
                            }
                            else {
                                JOptionPane.showMessageDialog(userFrame, "Không thể gửi tin nhắn", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
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
            if (op == 0 && client != null) {
                int receiver = client.getReceiver();
                String msgType = client.getMsgType();
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        ChatModel.delAllChat(userModel.getConn(), username, receiver, msgType);
                        return null;
                    }
                    @Override
                    protected void done() {
                        openChat(cp, username, receiver, msgType);
                    }
                }.execute();
            }
        });

        cp.addChatSuggestionEvent(e -> {
            chatSuggestDialog.addSuggestEvent(evt -> {
                try {
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("model", "llama-3.3-70b-versatile");
                    JSONObject message = new JSONObject();
                    message.put("role", "user");
                    message.put("content", chatSuggestDialog.getPrompt());
                    JSONArray messages = new JSONArray();
                    messages.put(message);
                    requestBody.put("messages", messages);

                    Properties prop = new Properties();
                    FileReader fr = new FileReader("groq_api_key.properties");
                    prop.load(fr);
                    String API_KEY = prop.getProperty("API_KEY");
                    HttpRequest request = HttpRequest.newBuilder()
                                    .uri(new URI("https://api.groq.com/openai/v1/chat/completions"))
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + API_KEY)
                                    .POST(BodyPublishers.ofString(requestBody.toString()))
                                    .build();

                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
                    JSONObject responseBody = new JSONObject(response.body());
                    String answer = responseBody.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                    chatSuggestDialog.showAnswer(answer);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            });
            chatSuggestDialog.showSuggestDialog();
        });

        msgSearchDialog.getSearchField().addActionListener(e -> {
            loadSearchResult(cp, msgSearchDialog, username, "person");
        });

        groupSearchDialog.getSearchField().addActionListener(e -> {
            loadSearchResult(cp, groupSearchDialog, username, "group");
        });

        cp.addMsgListButonEvent(e -> {
            loadConversations(cp, username, false);
        });

        cp.addAddFriendButtonEvent(e -> {
            addFriendSearchDialog.showSearchDialog();
        });

        cp.addSearchThisChatEvent(e -> {
            SwingUtilities.getWindowAncestor((Component)e.getSource()).dispose();
            cp.findTextInChat(cp.getChatSearch());
        });

        cp.addSearchAllChatEvent(e -> {
            SwingUtilities.getWindowAncestor((Component)e.getSource()).dispose();
            new SwingWorker<HashMap<String, String>, Void>() {
                @Override
                protected HashMap<String, String> doInBackground() {
                    return ChatModel.findTextFromAll(userModel.getConn(), username, cp.getChatSearch());
                }
                @Override
                protected void done() {
                    try {
                        HashMap<String, String> map = get();
                        cp.setChatName(map.get("name"));
                        SwingWorker<ChatModel, Void> worker = openChat(cp, username, Integer.valueOf(map.get("receiver")), map.get("type"));
                        worker.addPropertyChangeListener(evt -> {
                            if ("state".equals(evt.getPropertyName()) && (SwingWorker.StateValue.DONE.equals(evt.getNewValue()))) {
                                cp.findTextInChat(cp.getChatSearch());
                            }
                        });
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }.execute();
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

    private SwingWorker<ChatModel, Void> openChat(ChatPage cp, String username, int otherId, String type) {
        SwingWorker<ChatModel, Void> worker = new SwingWorker<ChatModel, Void>() {
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
                        int msgId = Integer.valueOf(map.get("msgId"));
                        ChatPage.ChatLinePanel chatLine;
                        String content = map.get("content");
                        boolean isBase64;
                        try {
                            Base64.getDecoder().decode(content.split("\\?")[0]);
                            isBase64 = true;
                        } catch (Exception e) {
                            isBase64 = false;
                        }
                        if (isBase64) {
                            byte[] gk = E2EGroup.getGroupKey(username, otherId, null);
                            String message = E2ESession.decrypt(content, E2ESession.getSecretKeySpec(gk));
                            chatLine = cp.createChatLinePanel(message, isSender, msgId);
                        }
                        else {
                            chatLine = cp.createChatLinePanel(content, isSender, msgId);
                        }

                        if (isSender) {
                            addDeleteChatLineEvent(chatLine, chatModel, cp, msgId, username, otherId, type);
                        }
                        else {
                            chatLine.addSenderName(map.get("sender_name"));
                        }
                        cp.addToChatPanel(chatLine);
                    }
    
                    if (client != null) 
                    client.disconnectClient();

                    client = new ChatClient();
                    client.initClient(userId, otherId, type);
                    client.addMsgHandler(msg -> {
                        if (msg.has("status")) {
                            int id = msg.getInt("id");
                            new SwingWorker<Boolean, Void>() {
                                @Override
                                protected Boolean doInBackground() {
                                    return new ConversationModel(userModel.getConn(), username).isIdInConversations(id);
                                }
                                @Override
                                protected void done() {
                                    try {
                                        if (get()) {
                                            loadConversations(cp, username, true);
                                        }
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                }
                            }.execute();
                        }
                        else if (msg.has("gk")) {
                            if (msg.getInt("group_id") == otherId) {
                                int sender = msg.getInt("sender");
                                String publicKey = chatModel.getPublicKey(sender);
                                byte[] sharedKey = E2ESession.getSharedSecret(username, sender, publicKey);
                                try {
                                    String gk = E2ESession.decrypt(msg.getString("gk"), E2ESession.getSecretKeySpec(sharedKey));
                                    String fileName = otherId + "_group_key.txt";
                                    E2ESession.saveKeyToFile(gk, username, fileName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else if (msg.has("group_id")) {
                            if (msg.getInt("group_id") == otherId) {
                                String content = msg.getString("content");
                                byte[] gk = E2EGroup.getGroupKey(username, otherId, null);
                                try {
                                    String decryptContent = E2ESession.decrypt(content, E2ESession.getSecretKeySpec(gk));
                                    ChatPage.ChatLinePanel chatLine = cp.createChatLinePanel(decryptContent, 
                                                                                false, 
                                                                                        msg.getInt("msg_id"));
                                    chatLine.addSenderName(msg.getString("sender_name"));
                                    cp.addToChatPanel(chatLine);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else {
                            if (msg.getInt("sender_id") == otherId) {
                                ChatPage.ChatLinePanel chatLine = cp.createChatLinePanel(msg.getString("content"), 
                                                                                false, 
                                                                                        msg.getInt("msg_id"));
                                cp.addToChatPanel(chatLine);
                            }
                        }
                    });

                    client.listen();
    
                    cp.updateChatPanel();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        };
        worker.execute();
        return worker;
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
                                cp.setChatName(map.get("fullname"));
                                openChat(cp, username, Integer.valueOf(map.get("id")), "single");
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
                                        CreateGroupDialog createGroupDialog = new CreateGroupDialog(userFrame);
                                        boolean successful = CreateGroupController.handleCreateGroup(userModel.getConn(), 
                                                                                createGroupDialog, username, id);
                                        
                                        if (successful) {
                                            loadConversations(cp, username, false);
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

    private void loadConversations(ChatPage cp, String username, boolean isPassive) {
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
                                            cp.setChatName(name);
                                            openChat(cp, username, id, "single");
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
                                            cp.setChatName(name);
                                            openChat(cp, username, id, "multiple");
                                        }
                                    });
                                    gp.addGroupSettingButtonEvent(e -> {
                                        GroupSettingDialog groupSettingDialog = new GroupSettingDialog(userFrame);
                                        GroupSettingController.handleGroupSetting(userModel.getConn(), groupSettingDialog, id, username);
                                        loadConversations(cp, username, false);
                                    });
                                    cp.addToListPanel(gp);
                                    break;
                                default:
                                    break;
                            }
                        }
                        cp.updateListPanel();
                        if (!isPassive && !list.isEmpty()) {
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

    private void addDeleteChatLineEvent(ChatPage.ChatLinePanel chatLine, ChatModel chatModel, ChatPage cp, int msgId, String username, int otherId, String type) {
        chatLine.addDeleteEvent(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    Object[] options = { "Có", "Không" };
                    int option = JOptionPane.showOptionDialog(null, 
                                            "Bạn có muốn xoá đoạn chat này không?", 
                                            "Xoá chat",
                                            JOptionPane.YES_NO_OPTION,
                                            JOptionPane.WARNING_MESSAGE,
                                            null,
                                            options,
                                            options[0]);
                    if (option == 0) {
                        chatModel.delChat(msgId);
                        openChat(cp, username, otherId, type);
                    }
                }
            }
        });
    }
}
