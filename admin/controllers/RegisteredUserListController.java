package admin.controllers;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import admin.models.RegisteredUserListModel;
import admin.views.RegisteredUserList;

import java.sql.SQLException;
import java.util.List;

public class RegisteredUserListController {
    private RegisteredUserList view;
    private DefaultTableModel tableModel;

    // trang thai filter
    private String startDate = null;
    private String endDate = null;
    private String usernameSearch = null;
    private String comboSort = null;

    public RegisteredUserListController(RegisteredUserList view){
        this.view = view;
        this.tableModel = view.getTableModel();

        initController();
        loadData();
    }

    private void initController(){
        view.getButtonFilterDate().addActionListener(e ->{
            startDate = view.getStartDatePicker().getDate().toString();
            endDate = view.getEndDatePicker().getDate().toString();
            loadData();
        });

        view.getBtnSearch().addActionListener(e->{
            String username = view.getSearchField().getText().trim();
            if(username != null && !username.isEmpty()){
                usernameSearch = username;
                loadData();
            }
        });

        view.getSortCombo().addActionListener(e ->{
            int selectedIndex = view.getSortCombo().getSelectedIndex();
            switch (selectedIndex) {
                case 0:
                    comboSort = "NAME_ASC";
                    break;
                case 1:
                    comboSort = "NAME_DESC";
                    break;
                case 2:
                    comboSort = "DATE_DESC";
                    break;
                case 3:
                    comboSort = "DATE_ASC";
                    break;
                default:
                    break;
            }
            loadData();
        });

    }

    private void loadData(){
        view.getUserTable().setEnabled(false);
        SwingWorker<List<RegisteredUserListModel>, Void> worker = new SwingWorker<List<RegisteredUserListModel>,Void>() {
            @Override
            protected List<RegisteredUserListModel> doInBackground() throws SQLException{
                RegisteredUserListModel model = new RegisteredUserListModel();
                return model.getAllUsers(
                    startDate,
                    endDate,
                    usernameSearch,
                    comboSort
                );
            }
            @Override
            protected void done(){
                List<RegisteredUserListModel> users = null;
                try{    
                    users = get();
                }catch(Exception e){
                    e.printStackTrace();
                }
                tableModel.setRowCount(0);
                for(RegisteredUserListModel user : users){
                    Object[] row = new Object[]{
                        user.getUserName(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getDateCreated(),
                        user.getStatus(),
                    };
                    tableModel.addRow(row);
                }

            }
        };
        view.getUserTable().setEnabled(true);
        worker.execute();
    }
}
