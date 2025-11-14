package admin.controllers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingWorker;

import admin.models.AdminModel;
import admin.models.LoginHistoryModel;
import admin.views.LoginHistory;

public class LoginHistoryController {
    private LoginHistoryModel lhModel;
    private LoginHistory lh;
    private boolean isLoading;
    public LoginHistory createLoginHistory() {
        lh = new LoginHistory();
        lhModel = null;
        isLoading = false;
        initLoginHistory();
        lh.addRefreshEvent(e -> {
            if (lh != null && !isLoading) {
                isLoading = true;
                refreshLoginHistory();
            }
        });
        return lh;
    }

    private void initLoginHistory() {
        new SwingWorker<LoginHistoryModel, Void> () {
            @Override
            protected LoginHistoryModel doInBackground() throws Exception {
                Connection conn = AdminModel.createConnection(); // TODO: Thay cai AdminModel nay` bang cai class connect toi db
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
}
