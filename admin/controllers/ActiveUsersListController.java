package admin.controllers;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import admin.models.ActiveUsersListModel;
import admin.views.ActiveUsersList;

import java.sql.SQLException;
import java.util.List;

public class ActiveUsersListController {
    private ActiveUsersList view;
    private DefaultTableModel tableModel;

    // filter
    private String startDate = null;
    private String endDate = null;
    private String usernameSearch = null;
    private String comboSort = null;
    private String filterOp = null;
    private int filterNum = -1;

    public ActiveUsersListController(ActiveUsersList view){
        this.view = view;
        this.tableModel = view.getDefaultTableModel();

        initController();
        loadData();

    }

    private void initController(){
        view.getButtonFilterDate().addActionListener(e ->{
            startDate = view.getStartDatePicker().getDate().toString();
            endDate = view.getEnDatePicker().getDate().toString();

            loadData();
        });

        view.getButtonSearchName().addActionListener(e ->{
            String username = view.getSearchField().getText().trim();
            if(!username.isEmpty() && username != null){
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
        
        view.getButtonClearFilter().addActionListener(e ->{
            filterOp = null;
            filterNum = -1;
            view.getActionCounTextField().setText("");
            view.getFilterOperator().setSelectedIndex(0);
            loadData();
        });

        view.getButtonFilter().addActionListener(e ->{
            int selectedIndex = view.getFilterOperator().getSelectedIndex();
            switch (selectedIndex){
                case 0: 
                    filterOp = "=";
                    break;
                case 1: 
                    filterOp = ">";
                    break;
                case 2: 
                    filterOp = "<";
                    break;
                case 3: 
                    filterOp = ">=";
                    break;
                case 4:
                    filterOp = "<=";
                    break;
                default:
                    filterOp = "=";
            }

            String numTmp = view.getActionCounTextField().getText().trim();
            if(numTmp != null && !numTmp.isEmpty()){
                filterNum = Integer.parseInt(numTmp);
                loadData();
            }
        });
    }

    private void loadData(){
        view.getUserTable().setEnabled(false);
        SwingWorker<List<ActiveUsersListModel>, Void> worker = new SwingWorker<List<ActiveUsersListModel>,Void>() {
            @Override
            protected List<ActiveUsersListModel> doInBackground() throws SQLException{
                ActiveUsersListModel model = new ActiveUsersListModel();
                return model.getAllUsers(startDate, endDate, usernameSearch, comboSort, filterOp, filterNum);
            }

            @Override
            protected void done(){
                try{
                    List<ActiveUsersListModel> users = get();
                    tableModel.setRowCount(0);
                    for(ActiveUsersListModel user : users){
                        Object[] row = new Object[]{
                            user.getId(),
                            user.getUsername(),
                            user.getNumOpen(),
                            user.getNumChat(),
                            user.getNumGroup()
                        };
                        tableModel.addRow(row);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        view.getUserTable().setEnabled(true);
        worker.execute();
    }

}
