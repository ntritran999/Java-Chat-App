package admin.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatModel {
private Connection conn;
    private ArrayList<HashMap<String, String>> groups;
    private String currentFilteredGroupName;
    public GroupChatModel(Connection conn) {
        groups = new ArrayList<>();
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
    
    public ArrayList<HashMap<String, String>> getGroups() {
        return groups;
    }
}
