package admin.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatModel {
    private Connection conn;
    private ArrayList<HashMap<String, String>> groups, groupMembers;
    private String currentFilteredGroupName;
    public GroupChatModel(Connection conn) {
        groups = new ArrayList<>();
        groupMembers = new ArrayList<>();
        currentFilteredGroupName = "";
        this.conn = conn;
    }
    
    private void fetchGroupData(String query) {
        try {
            groups.clear();
            Statement st = conn.createStatement();
            
            ResultSet rs = st.executeQuery(query);
            String id, groupName;
            Date createDate;
            while (rs.next()) {
                id = rs.getString("group_id");
                groupName = rs.getString("group_name");
                createDate = rs.getDate("create_date");

                HashMap<String, String> row = new HashMap<>();
                row.put("groupId", id);
                row.put("groupName", groupName);
                row.put("createDate", createDate.toString());
                groups.add(row);
            }
            rs.close();
            st.close();
            
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void loadGroups() {
        String query = """
                    SELECT *
                    FROM message_group
                    """;
        fetchGroupData(query);
    }

    public void sortGroupNameAsc() {
        String query = """
                    SELECT *
                    FROM message_group
                    """ 
                +  "WHERE group_name LIKE '" + currentFilteredGroupName + "%'"
                +  "ORDER BY group_name ASC";
        fetchGroupData(query);
    }

    public void sortGroupNameDesc() {
        String query = """
                    SELECT *
                    FROM message_group
                    """
                +  "WHERE group_name LIKE '" + currentFilteredGroupName + "%'"
                +  "ORDER BY group_name DESC";
        fetchGroupData(query);
    }

    public void sortCreateDateAsc() {
        String query = """
                    SELECT *
                    FROM message_group
                    """
                +  "WHERE group_name LIKE '" + currentFilteredGroupName + "%'"
                +  "ORDER BY create_date ASC";
        fetchGroupData(query);
    }

    public void sortCreateDateDesc() {
        String query = """
                    SELECT *
                    FROM message_group
                    """
                +  "WHERE group_name LIKE '" + currentFilteredGroupName + "%'"
                +  "ORDER BY create_date DESC";
        fetchGroupData(query);
    }

    public void filterGroupName(String groupName) {
        currentFilteredGroupName = groupName;
        String query = "SELECT * FROM message_group WHERE group_name LIKE '" + groupName + "%'";
        fetchGroupData(query);
    }

    public void fetchGroupMember(int groupId, boolean adminSearch) {
        try {
            groupMembers.clear();
            String query = """
                    SELECT user_info.fullname, account.status
                        FROM message_group JOIN group_member ON message_group.group_id=group_member.group_id
                        JOIN user_info ON user_info.id=group_member.user_id
                        JOIN account_info ON user_info.id=account_info.user_id
                        JOIN account ON account.username=account_info.username
                        WHERE message_group.group_id=?
                    """;
            if (adminSearch) {
                query += " AND group_member.admin=true";
            }

            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, groupId);
            
            ResultSet rs = st.executeQuery();
            String memberName, status;
            while (rs.next()) {
                memberName = rs.getString("fullname");
                status = rs.getString("status");

                HashMap<String, String> row = new HashMap<>();
                row.put("memberName", memberName);
                row.put("status", status);
                groupMembers.add(row);
            }
            rs.close();
            st.close();
            
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    public ArrayList<HashMap<String, String>> getGroups() {
        return groups;
    }

    public ArrayList<HashMap<String, String>> getGroupMembers() {
        return groupMembers;
    }
}
