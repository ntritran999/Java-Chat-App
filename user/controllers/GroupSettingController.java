package user.controllers;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import user.models.GroupSettingModel;
import user.views.GroupSettingDialog;

public class GroupSettingController {
    public static void handleGroupSetting(Connection conn, GroupSettingDialog gsd, int groupId, String username) {
        gsd.addChangeGroupNameEvent(e -> {
            String newGroupName = gsd.getNewGroupName();
            if (!newGroupName.isEmpty()) {
                new SwingWorker<Void, Void> () {
                    @Override
                    protected Void doInBackground() throws Exception {
                        GroupSettingModel.changeGroupName(conn, groupId, newGroupName);
                        return null;
                    }
                    @Override
                    protected void done() {
                        try {
                            JOptionPane.showMessageDialog(gsd, "Đổi tên nhóm thành công");
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }.execute();
            }
        });

        gsd.addAddMemEvent(e -> {
            String newMem = gsd.getMemberToAdd();
            if (!newMem.isEmpty()) {
                new SwingWorker<Void, Void> () {
                    @Override
                    protected Void doInBackground() throws Exception {
                        GroupSettingModel.addMember(conn, groupId, newMem);
                        return null;
                    }
                    @Override
                    protected void done() {
                        try {
                            JOptionPane.showMessageDialog(gsd, "Thêm thành viên thành công");
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }.execute();
            }
        });

        gsd.addAddAdminEvent(e -> {
            String newAdmin = gsd.getAdminToAdd();
            if (!newAdmin.isEmpty()) {
                new SwingWorker<Void, Void> () {
                    @Override
                    protected Void doInBackground() throws Exception {
                        GroupSettingModel.setAdmin(conn, groupId, newAdmin);
                        return null;
                    }
                    @Override
                    protected void done() {
                        try {
                            JOptionPane.showMessageDialog(gsd, "Gán admin thành công");
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }.execute();
            }
        });

        gsd.addRemoveMemEvent(e -> {
            String mem = gsd.getMemberToRemove();
            if (!mem.isEmpty()) {
                new SwingWorker<Void, Void> () {
                    @Override
                    protected Void doInBackground() throws Exception {
                        GroupSettingModel.removeMember(conn, groupId, mem, username);
                        return null;
                    }
                    @Override
                    protected void done() {
                        try {
                            JOptionPane.showMessageDialog(gsd, "Xoá thành viên thành công");
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }.execute();
            }
        });
        gsd.showGroupSettingDialog();
    }
}
