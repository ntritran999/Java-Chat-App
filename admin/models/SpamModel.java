package admin.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SpamModel {
    private Connection conn;
    private ArrayList<HashMap<String, String>> spamList;
    private String baseQuery;
    private String curFilteredUsername;
    private Date curFilteredStartDate, curFilteredEndDate; 
    public SpamModel(Connection conn) {
        spamList = new ArrayList<>();
        baseQuery = """
                SELECT spam_report.username, report_date, user_id, status
                FROM spam_report JOIN account ON spam_report.username=account.username
                 """;
        curFilteredUsername = "";
        curFilteredStartDate = curFilteredEndDate = null;
        this.conn = conn;
    }
    
    private void fetchSpamList(String query) {
        try {
            spamList.clear();
            Statement st = conn.createStatement();
            
            ResultSet rs = st.executeQuery(query);
            String username, reportDate, userId, status, isLocked;
            while (rs.next()) {
                username = rs.getString("username");
                reportDate = rs.getString("report_date");
                userId = rs.getString("user_id");
                status = rs.getString("status");
                
                if (status.compareTo("L") == 0) {
                    isLocked = "true";
                }
                else {
                    isLocked = "";
                }

                HashMap<String, String> row = new HashMap<>();
                row.put("username", username);
                row.put("userId", userId);
                row.put("reportDate", reportDate);
                row.put("isLocked", isLocked);
                spamList.add(row);
            }
            rs.close();
            st.close();
            
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private String buildBaseSortQuery() {
        String query = baseQuery
                + "WHERE spam_report.username LIKE '" + curFilteredUsername + "%'";
        if (curFilteredStartDate != null && curFilteredEndDate != null) {
            query += "AND report_date BETWEEN '" + curFilteredStartDate.toString() + "' AND '" + curFilteredEndDate.toString() + "'";
        }
        return query;
    }

    public void loadSpams() {
        fetchSpamList(baseQuery);
    }

    public void sortDateAsc() {
        String query = buildBaseSortQuery();
        query += "ORDER BY report_date ASC";
        fetchSpamList(query);
    }

    public void sortDateDesc() {
        String query = buildBaseSortQuery();
        query += "ORDER BY report_date DESC";
        fetchSpamList(query);
    }

    public void sortUsernameAsc() {
        String query = buildBaseSortQuery();
        query += "ORDER BY username ASC";
        fetchSpamList(query);
    }

    public void sortUsernameDesc() {
        String query = buildBaseSortQuery();
        query += "ORDER BY username DESC";
        fetchSpamList(query);
    }

    public void filterDate(Date start, Date end) {
        curFilteredStartDate = start;
        curFilteredEndDate = end;
        String query = baseQuery
                + "WHERE report_date BETWEEN '" + start.toString() + "' AND '" + end.toString() + "'"
                + "AND spam_report.username LIKE '" + curFilteredUsername + "%'";;
        fetchSpamList(query);
    }

    public void filterUsername(String username) {
        curFilteredUsername = username;
        String query = baseQuery
                + "WHERE spam_report.username LIKE '" + username + "%'";
        if (curFilteredStartDate != null && curFilteredEndDate != null) {
            query += "AND report_date BETWEEN '" + curFilteredStartDate.toString() + "' AND '" + curFilteredEndDate.toString() + "'";
        }
        fetchSpamList(query);
    }

    public void lockUser(String username) {
        try {
            String query = """
                    UPDATE account
                    SET status='L'
                    WHERE username=?
                    """;
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, username);
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public ArrayList<HashMap<String, String>> getSpamList() {
        return spamList;
    }
}
