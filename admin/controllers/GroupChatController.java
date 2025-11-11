package admin.controllers;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingWorker;

import admin.models.AdminModel;
import admin.models.GroupChatModel;
import admin.views.ChatList;


public class GroupChatController {
    private GroupChatModel gcModel;
    private ChatList gc;
    public ChatList createChatList() {
        gc = new ChatList();
        gcModel = null;
        initChatList();
        
        gc.addSortEvent(e -> {
            refreshChatList(gc.getSortType());
        });
        gc.addFilterEvent(e -> {
            filterChatList(gc.getSearchValue());
        });
        return gc;
    }

    private void initChatList() {
        new SwingWorker<GroupChatModel, Void> () {
            @Override
            protected GroupChatModel doInBackground() throws Exception {
                Connection conn = AdminModel.createConnection(); // TODO: Thay cai AdminModel nay` bang cai class connect toi db
                return new GroupChatModel(conn);
            }
            @Override
            protected void done() {
                try {
                    gcModel = get();
                    refreshChatList("load groups");
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.execute();
    }

    private void filterChatList(String groupName) {
        new SwingWorker<ArrayList<HashMap<String, String>>, Void>() {
            @Override
            protected ArrayList<HashMap<String, String>> doInBackground() throws Exception {
                gcModel.filterGroupName(groupName);
                return gcModel.getGroups();
            }
            @Override
            protected void done() {
                try {
                    gc.reloadTableModel(get());
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.execute();
    }

    private void refreshChatList(String type) {
        new SwingWorker<ArrayList<HashMap<String, String>>, Void>() {
            @Override
            protected ArrayList<HashMap<String, String>> doInBackground() throws Exception {
                switch (type) {
                    case "load groups":
                        gcModel.loadGroups();
                        break;
                    case "group name asc":
                        gcModel.sortGroupNameAsc();
                        break;
                    case "group name desc":
                        gcModel.sortGroupNameDesc();
                        break;
                    case "create date asc":
                        gcModel.sortCreateDateAsc();
                        break;
                    case "create date desc":
                        gcModel.sortCreateDateDesc();
                        break;
                    default:
                        break;
                }
                return gcModel.getGroups();
            }
            @Override
            protected void done() {
                try {
                    gc.reloadTableModel(get());
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.execute();
    }
}

