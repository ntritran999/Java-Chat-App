package admin.controllers;
import admin.models.UserManagementModel;
import admin.views.AdminDashboard;
import admin.views.UserManagement;
import admin.views.UsersFriendsList;

import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.SQLException;

public class UserManagementController{
    private UserManagement view;
    private DefaultTableModel tableModel;

    // trang thai cua filter
    private String currentComboType = null;
    private String currentSearchName = null;
    private String currentComboStatus = null;
    private String currentSort = null;

    private UserManagementController self;
    private AdminDashboard dashBoard;

    public UserManagementController(UserManagement view, AdminDashboard dashBoard){
        this.view = view;
        this.tableModel = view.getTableModel();
        self = this;
        this.dashBoard = dashBoard;

        initController();
        loadData(); // load data ban dau
    }

    // Event listener
    private void initController(){
        // Nut Search
        view.getBtnSearch().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                handleSearch();
            }
        });

        view.getSortCombo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                handleSort();
            }
        });

        view.getBtnUpdate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                view.showUpdateUI(self);
            }
        });

        view.getBtnAdd().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                view.showCreateAccountUI(self);
            }
        });
    }

    // Xu ly logic event
    private void handleSearch(){
        int selectedIndexType = view.getFilterTypeCombo().getSelectedIndex();
        if(selectedIndexType == 1)
            currentComboType = "FULLNAME";
        else
            currentComboType = "USERNAME";

        String searchText = view.getSearchField().getText().trim();
        if(searchText.isEmpty()){
            currentSearchName = null;
        }else
            currentSearchName = searchText;

        int selectedIndexStatus = view.getStatusFilterCombo().getSelectedIndex();
        switch (selectedIndexStatus) {
            case 0:
                currentComboStatus = "ALL";
                break;
            case 1:
                currentComboStatus = "ONLINE";
                break;
            case 2:
                currentComboStatus = "BLOCK";
                break;
            default:
                break;
        }
        loadData();
    }

    private void handleSort(){
        int selectedIndexSort = view.getSortCombo().getSelectedIndex();
        switch (selectedIndexSort) {
            case 0:
                currentSort = "NAME_ASC";
                break;
            case 1:
                currentSort = "NAME_DESC";
                break;
            case 2:
                currentSort = "DATE_DESC";
                break;
            case 3:
                currentSort = "DATE_ASC";
                break;
            default:
                break;
        }
        loadData();
    }

    private void loadData(){
        view.getUserTable().setEnabled(false);

        // background thread
        SwingWorker<List<UserManagementModel>, Void> worker = 
            new SwingWorker<List<UserManagementModel>, Void>() {
                @Override
                protected List<UserManagementModel> doInBackground() throws Exception{
                    return UserManagementModel.getAllUsers(
                        currentComboType,
                        currentSearchName,
                        currentComboStatus,
                        currentSort
                    );
                }

                @Override
                protected void done(){
                    List<UserManagementModel> users = null;
                    try {
                        users = get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                    tableModel.setRowCount(0);
                    List<JButton> actionButtons = view.getActionButtonManageView();
                    actionButtons.clear();
                    int rowIndex = 0;
                    for(UserManagementModel user : users){
                        Object[] row = new Object[]{
                            user.getId(),
                            user.getUserName(),
                            user.getFullName(),
                            user.getAddress(),
                            user.getDob(),
                            user.getGender(),
                            user.getEmail(),
                            user.getCreatedDate(),
                            user.getStatus(),
                            ""
                        };
                        tableModel.addRow(row);
                        JButton actionBtn = view.createActionButton(self, user.getUserName(), rowIndex);
                        actionButtons.add(actionBtn);
                        rowIndex++;
                    }
                    view.updateActionButtonRenderer();
                    view.getUserTable().setEnabled(true);
                    view.getUserTable().repaint();
                }
        };
        view.getUserTable().setEnabled(true);
        view.getUserTable().repaint();
        worker.execute();
    }

    public void handleUpdatePassword(String id, String username, String newPassword, JDialog dialog) { 
        if(id == null || newPassword == null || newPassword.isEmpty())
            return;
        // backend background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean,Void>() {
            @Override
            protected Boolean doInBackground() throws SQLException{
                return UserManagementModel.updatePassword(id, newPassword);
            }

            @Override
            protected void done(){
                try{
                    boolean success = get();
                    if(success)
                        JOptionPane.showMessageDialog(dialog, "Đã cập nhật mật khẩu mới cho " + username);
                    else
                        JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mật khẩu mới!");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void handleDeleteUser(String id, String username, JDialog dialog) { 
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean,Void>() {
            @Override
            protected Boolean doInBackground() throws SQLException{
                return UserManagementModel.deleteAccount(id, username);
            }

            @Override
            protected void done(){
                try{
                    boolean success = get();
                    if(success){
                        JOptionPane.showMessageDialog(dialog, "Đã xóa tài khoản " + username);
                        loadData();
                        dialog.dispose();
                    }else
                        JOptionPane.showMessageDialog(dialog, "Xóa tài khoản " + username + " không thành công");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    public void handleLockUser(String id, String username, JDialog dialog) { 
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean,Void>() {
            @Override
            protected Boolean doInBackground() throws SQLException{
                return UserManagementModel.updateLock(id, username);
            }

            @Override
            protected void done(){
                boolean success;
                try {
                    success = get();
                    if(success){
                        JOptionPane.showMessageDialog(dialog, "Đã khóa tài khoản " + username);
                        loadData();
                    }
                    else
                        JOptionPane.showMessageDialog(dialog, "Không thành công khóa tài khoản " + username);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    public void handleUnlockUser(String username, JDialog dialog) { }
    public void handleShowFriendsList(String username, JDialog dialog) { 
        UsersFriendsList viewUsersFriendsList = new UsersFriendsList();
        UsersFriendsListController uListController = new UsersFriendsListController(viewUsersFriendsList);
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean,Void>() {
            @Override
            protected Boolean doInBackground(){
                return uListController.queryListFriend(username);
            }

            @Override
            protected void done(){
                try {
                    boolean success = get();
                    if(!success)
                        JOptionPane.showMessageDialog(dialog, "Không thành công hiển thị danh sách bạn bè của " + username);
                    else{
                        System.out.println("success");
                        dialog.dispose();
                        dashBoard.showUsersFriendsList(username);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
        worker.execute();
    }
    public void handleShowLoginHistory(String username, JDialog dialog) { }

    public void handleUpdateInfoUser(String id, String fullName, String address, String email, JDialog dialog){
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean,Void>() {
            @Override
            protected Boolean doInBackground() throws SQLException{
                return UserManagementModel.updateInfoUser(id, fullName, address, email);
            }

            @Override
            protected void done(){
                boolean success;
                try {
                    success = get();
                    if(!success)
                        JOptionPane.showMessageDialog(dialog, "Không thành công cập nhật thông tin tài khoản " + id);
                    else{
                        JOptionPane.showMessageDialog(dialog, "Thành công hiển thị danh sách bạn bè của " + id);
                        loadData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void handleCreateAccount(String username, String password, String fullname, String email, String dob, String role, JDialog dialog){
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean,Void>() {
            @Override
            protected Boolean doInBackground() throws SQLException{
                return UserManagementModel.addUserAccount(username, password, fullname, email, dob, role);
            }

            @Override
            protected void done(){
                try {
                    boolean success = get();
                    if(success){
                        JOptionPane.showMessageDialog(dialog, "Thêm thành công tài khoản");
                        loadData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        worker.execute();
    }
}
