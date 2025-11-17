package admin.controllers;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingWorker;

import admin.models.AdminModel;
import admin.models.SpamModel;
import admin.views.SpamManagement;

public class SpamController {
    private SpamController self;
    private SpamModel spamModel;
    private SpamManagement spamMngmt;
    public SpamManagement createSpamManagement() {
        self = this;
        spamMngmt = new SpamManagement();
        spamModel = null;

        initSpamManagement();

        spamMngmt.addSortEvent(e -> {
            refreshSpams(spamMngmt.getSortType());
        });
        spamMngmt.addSearchEvent(e -> {
            filterSpamsByUsername(spamMngmt.getUsernameSearch());
        });
        spamMngmt.addDateFilterEvent(e -> {
            filterSpamsByDate(spamMngmt.getStartDate(), spamMngmt.getEndDate());
        });
        return spamMngmt;
    }

    private void initSpamManagement() {
        new SwingWorker<SpamModel, Void> () {
            @Override
            protected SpamModel doInBackground() throws Exception {
                Connection conn = AdminModel.createConnection(); // TODO: Thay cai AdminModel nay` bang cai class connect toi db
                return new SpamModel(conn);
            }
            @Override
            protected void done() {
                try {
                    spamModel = get();
                    refreshSpams("load spams");
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.execute();
    }

    private void filterSpamsByUsername(String username) {
        new SwingWorker<ArrayList<HashMap<String, String>>, Void> () {
            @Override
            protected ArrayList<HashMap<String, String>>doInBackground() throws Exception {
                spamModel.filterUsername(username);
                return spamModel.getSpamList();
            }
            @Override
            protected void done() {
                try {
                    spamMngmt.reloadTableModel(get(), self);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.execute();
    }

    private void filterSpamsByDate(Date start, Date end) {
        new SwingWorker<ArrayList<HashMap<String, String>>, Void> () {
            @Override
            protected ArrayList<HashMap<String, String>>doInBackground() throws Exception {
                spamModel.filterDate(start, end);
                return spamModel.getSpamList();
            }
            @Override
            protected void done() {
                try {
                    spamMngmt.reloadTableModel(get(), self);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.execute();
    }

    private void refreshSpams(String type) {
        new SwingWorker<ArrayList<HashMap<String, String>>, Void> () {
            @Override
            protected ArrayList<HashMap<String, String>> doInBackground() throws Exception {
                switch (type) {
                    case "load spams":
                        spamModel.loadSpams();
                        break;
                    case "username asc":
                        spamModel.sortUsernameAsc();
                        break;
                    case "username desc":
                        spamModel.sortUsernameDesc();
                        break;
                    case "date asc":
                        spamModel.sortDateAsc();
                        break;
                    case "date desc":
                        spamModel.sortDateDesc();
                        break;
                    default:
                        System.out.println("Unknown refresh type.");
                        break;
                }
                return spamModel.getSpamList();
            }
            @Override
            protected void done() {
                try {
                    spamMngmt.reloadTableModel(get(), self);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.execute();
    }

    public void lockSpam(String username) {
        spamModel.lockUser(username);
    }
}
