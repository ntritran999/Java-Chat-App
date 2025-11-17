package admin.controllers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingWorker;

import admin.models.GroupChatModel;
import admin.views.ChatList;
import admin.models.dbConnection;

public class GroupChatController {
    private GroupChatModel gcModel;
    private ChatList gc;
    private GroupChatController self;
    public ChatList createChatList() {
        gc = new ChatList();
        gcModel = null;
        self = this;
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
                Connection conn = dbConnection.getConnection(); 
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
                    gc.reloadTableModel(get(), self);
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
                        System.out.println("Unknown refresh type.");
                        break;
                }
                return gcModel.getGroups();
            }
            @Override
            protected void done() {
                try {
                    gc.reloadTableModel(get(), self);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.execute();
    }

    public ArrayList<HashMap<String, String>> loadGroupMembers(int groupId) {
        gcModel.fetchGroupMember(groupId, false);
        return gcModel.getGroupMembers();
            
    }

    public ArrayList<HashMap<String, String>> loadGroupAdmins(int groupId) {
        gcModel.fetchGroupMember(groupId, true);
        return gcModel.getGroupMembers();       
    }
}

