package admin.controllers;
import admin.models.UsersFriendsListModel;
import admin.views.UsersFriendsList;

import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

public class UsersFriendsListController{
    private UsersFriendsList view;
    private DefaultTableModel tableModel;

    // trang thai cua filter
    private String currentSearchName = null;
    private String currentSortBy = "DATE_DESC";
    private String currentFilterOperator = null;
    private Integer currentFriendsCount = -1;

    public UsersFriendsListController(UsersFriendsList view){
        this.view = view;
        this.tableModel = view.getTableModel();

        initController();
        loadData(); // Load data ban dau
    }

    // Event listener
    private void initController(){
        // Nút search
        view.getBtnSearch().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearch();
            }
        });

        // ComboBox sắp xếp
        view.getSortCombo().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                handleSort();
            }
        });

        // Button filter
        view.getBtnFilter().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                handleFilter();
            }
        });

        // Button xóa filter
        view.getBtnClearFilter().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                handleClearFilter();
            }
        });
    }

    // Xu ly logic event
    private void handleSearch(){
        String searchText = view.getSearchNameField().getText().trim();
        if(searchText.isEmpty())
            currentSearchName = null;
        else
            currentSearchName = searchText;
        loadData();
    }

    private void handleSort(){
        int selectedIndex = view.getSortCombo().getSelectedIndex();
        switch (selectedIndex){
            case 0: 
                currentSortBy = "NAME_ASC";
                break;
            case 1: 
                currentSortBy = "NAME_DESC";
                break;
            case 2:
                currentSortBy = "DATE_DESC";
                break;
            case 3: 
                currentSortBy = "DATE_ASC";
                break;
            case 4: 
                currentSortBy = "FRIENDS_DESC";
                break;
            case 5: 
                currentSortBy = "FRIENDS_ASC";
                break;
            default:
                currentSortBy = "DATE_DESC";
        }
        loadData();
    }

    private void handleFilter(){
        // text so luong ban
        String countText = view.getFriendsCountField().getText().trim();
        if(countText.isEmpty()){
            JOptionPane.showMessageDialog(view,
                "Vui lòng nhập số lượng bạn bè!",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try{
            int count = Integer.parseInt(countText);
            if(count < 0){
                JOptionPane.showMessageDialog(view,
                    "Số lượng bạn bè phải là số không âm!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            currentFriendsCount = count;

            // lay ra operator
            int selectedIndex = view.getFilterOperator().getSelectedIndex();
            switch (selectedIndex){
                case 0: 
                    currentFilterOperator = "=";
                    break;
                case 1: 
                    currentFilterOperator = ">";
                    break;
                case 2: 
                    currentFilterOperator = "<";
                    break;
                case 3: 
                    currentFilterOperator = ">=";
                    break;
                case 4:
                    currentFilterOperator = "<=";
                    break;
                default:
                    currentFilterOperator = "=";
            }
            loadData();
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(view,
                "Số lượng bạn bè phải là số nguyên!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleClearFilter(){
        currentFilterOperator = null;
        currentFriendsCount = -1;
        view.getFriendsCountField().setText("");
        view.getFilterOperator().setSelectedIndex(0);
        loadData();
    }

    private void loadData(){
        view.getUserTable().setEnabled(false);

        // background thread
        SwingWorker<List<UsersFriendsListModel>, Void> worker = 
            new SwingWorker<List<UsersFriendsListModel>, Void>() {
                @Override
                protected List<UsersFriendsListModel> doInBackground() throws Exception{
                    return UsersFriendsListModel.getAllUsers(
                        currentSearchName,
                        currentSortBy,
                        currentFilterOperator,
                        currentFriendsCount
                    );
                }

                @Override
                protected void done(){
                    List<UsersFriendsListModel> users = null;
                    try {
                        users = get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                    tableModel.setRowCount(0);
                    for(UsersFriendsListModel user : users){
                        Object[] row = new Object[]{
                            user.getId(),
                            user.getUserName(),
                            user.getFullName(),
                            user.getEmail(),
                            user.getCreatedDate(),
                            user.getDirectFriends(),
                            user.getFriendOfFriends()
                        };
                        tableModel.addRow(row);
                    }
                }
        };
        view.getUserTable().setEnabled(true);
        worker.execute();
    }

    public void resetFilters(){
        currentSearchName = null;
        currentSortBy = "DATE_DESC";
        currentFilterOperator = null;
        currentFriendsCount = -1;

        view.getSearchNameField().setText("");
        view.getFriendsCountField().setText("");
        view.getSortCombo().setSelectedIndex(2);
        view.getFilterOperator().setSelectedIndex(0);

        loadData();
    }
}