package admin.controllers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingWorker;

import admin.models.LoginHistoryModel;
import admin.models.dbConnection;
import admin.views.LoginHistory;

public class LoginHistoryController {
    private LoginHistoryModel lhModel;
    private LoginHistory lh;
    private boolean isLoading;

    public LoginHistoryController(){
        lh = new LoginHistory();
        lhModel = null;
        isLoading = false;
    }
    public LoginHistory createLoginHistory() {
        initLoginHistory();
        lh.addRefreshEvent(e -> {
            if (lh != null && !isLoading) {
                isLoading = true;
                refreshLoginHistory();
            }
        });
        return lh;
    }

    public LoginHistory createLoginHistoryId(String idString){
        initLoginHistoryId(idString);
        return lh;
    }

    public LoginHistoryModel getHistoryModel(){
        return lhModel;
    }

    private void initLoginHistory() {
        new SwingWorker<LoginHistoryModel, Void> () {
            @Override
            protected LoginHistoryModel doInBackground() throws Exception {
                Connection conn = dbConnection.getConnection(); 
                return new LoginHistoryModel(conn);
            }
            @Override
            protected void done() {
                try {
                    lhModel = get();
                    refreshLoginHistory();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.execute();
    }
    public void initLoginHistoryId(String idString) {
        new SwingWorker<LoginHistoryModel, Void> () {
            @Override
            protected LoginHistoryModel doInBackground() throws Exception {
                Connection conn = dbConnection.getConnection(); 
                return new LoginHistoryModel(conn);
            }
            @Override
            protected void done() {
                try {
                    lhModel = get();
                    loadLoginHistoryById(idString);
                } catch (Exception e) {
                    System.out.println(e);
                } finally{
                    isLoading = false;
                }
            }
        }.execute();
    }

    private void refreshLoginHistory() {
        new SwingWorker<ArrayList<HashMap<String, String>>, Void>() {
            @Override
            protected ArrayList<HashMap<String, String>> doInBackground() throws Exception {
                lhModel.loadLoginHistory();
                return lhModel.getHistory();
            }
            @Override
            protected void done() {
                try {
                    lh.reloadTableModel(get());
                } catch (Exception e) {
                    System.out.println(e);
                }
                finally {
                    isLoading = false;
                }
            }
        }.execute();
    }

    public boolean queryHistoryById(String id){
        if(id == null || id.trim().isEmpty())
            return false;  
        loadLoginHistoryById(id);
        return true;
    }

    private void loadLoginHistoryById(String id){
        new SwingWorker<ArrayList<HashMap<String, String>>, Void>() {
            @Override
            protected ArrayList<HashMap<String, String>> doInBackground() throws Exception {
                lhModel.loadHistoryById(id);
                return lhModel.getHistory();
            }
            @Override
            protected void done() {
                try {
                    lh.reloadTableModel(get());
                } catch (Exception e) {
                    System.out.println(e);
                }
                finally {
                    isLoading = false;
                }
            }
        }.execute();
    }
}
