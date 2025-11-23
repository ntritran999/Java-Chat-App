package user.controllers;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import user.models.AddGroupModel;
import user.models.PersonSearchModel;
import user.views.CreateGroupDialog;

public class CreateGroupController {
    public static boolean handleCreateGroup(Connection conn, CreateGroupDialog cgd, String admin, int firstMemId) {
        var successfulWrapper = new Object(){ boolean successful = false; };
        HashSet<String> added = new HashSet<>();
        cgd.addToMemList(admin);
        added.add(admin);

        new SwingWorker<String, Void> () {
            @Override
            protected String doInBackground() throws Exception {
                return AddGroupModel.getUsernameFromId(conn, firstMemId);
            }
            @Override
            protected void done() {
                try {
                    String username = get();
                    cgd.addToMemList(username);
                    added.add(username);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.execute();
        
        cgd.addAddMemberEvent(ev -> {
            new SwingWorker<ArrayList<HashMap<String, String>>, Void> () {
                @Override
                protected ArrayList<HashMap<String, String>> doInBackground() throws Exception {
                    PersonSearchModel psModel = new PersonSearchModel(conn, cgd.getSearch(), admin);
                    return psModel.getResults();
                }
                @Override
                protected void done() {
                    try {
                        ArrayList<HashMap<String, String>> list = get();
                        for (var map : list) {
                            int id = Integer.valueOf(map.get("id"));
                            String username = AddGroupModel.getUsernameFromId(conn, id);
                            if (!added.contains(username)) {
                                added.add(username);
                                cgd.addToMemList(username);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }.execute();
        });
        
        cgd.addCreateGroupEvent(ev -> {
            String groupName = cgd.getGroupName();
            if (!groupName.isEmpty() && added.size() >= 2) {
                new SwingWorker<Void, Void> () {
                @Override
                protected Void doInBackground(){
                    try {
                        AddGroupModel.createGroup(conn, groupName, cgd.getMemList(), admin);
                        JOptionPane.showMessageDialog(cgd, "Tạo nhóm thành công", "Thông báo tạo nhóm", JOptionPane.INFORMATION_MESSAGE);
                        successfulWrapper.successful = true;
                    } catch (Exception e) {
                        System.out.println(e);
                        JOptionPane.showMessageDialog(cgd, "Tạo nhóm thất bại", "Thông báo tạo nhóm", JOptionPane.ERROR_MESSAGE);
                    }
                    finally {
                        cgd.dispose();
                    }
                    return null;
                }
            }.execute();
                
            }
        });
        
        cgd.showCreateGroupDialog();
        return successfulWrapper.successful;
    }
}
