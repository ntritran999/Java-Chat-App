package admin.models;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChartManagementModel {
    private int year;
    private int totalValue = 0;
    private double avgValue = 0;
    private int peakValue = 0;
    private Connection conn = dbConnection.getConnection();
    
    public ChartManagementModel(int year){
        this.year = year;
    }

    public int getTotalValue(){
        return totalValue;
    }

    public int getPeakValue(){
        return peakValue;
    }

    public double getAvgValue(){
        return avgValue;
    }

    // fetching data
    public List<Integer> fetchingActiveDataChart() throws SQLException{
        List<Integer> data = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        String sql = """
                    select count(case when extract(month from last_login) = 1 then 1 end) as January,
                           count(case when extract(month from last_login) = 2 then 1 end) as February,
                           count(case when extract(month from last_login) = 3 then 1 end) as March,
                           count(case when extract(month from last_login) = 4 then 1 end) as April,
                           count(case when extract(month from last_login) = 5 then 1 end) as May,
                           count(case when extract(month from last_login) = 6 then 1 end) as June,
                           count(case when extract(month from last_login) = 7 then 1 end) as July,
                           count(case when extract(month from last_login) = 8 then 1 end) as August,
                           count(case when extract(month from last_login) = 9 then 1 end) as September,
                           count(case when extract(month from last_login) = 10 then 1 end) as October,
                           count(case when extract(month from last_login) = 11 then 1 end) as November,
                           count(case when extract(month from last_login) = 12 then 1 end) as December
                    from activities
                    where extract(year from last_login) = ?
                """;
        st = conn.prepareStatement(sql);
        st.setInt(1, year);
        rs = st.executeQuery();
        if (rs.next()) {
            int tmpPeak = 0;
            for(int i = 0; i < 12; ++i){
                int tmp = rs.getInt(i + 1);
                data.add(tmp);
                totalValue += tmp;
                if(tmpPeak < tmp){
                    tmpPeak = tmp;
                    peakValue = i + 1;
                }
            }
            avgValue = ((totalValue*1.0) / (12*1.0));
            avgValue = (double) (Math.round(avgValue*10.0)/10.0); // lam tron 1 so thap phan

        }

        if(st != null)
            st.close();
        if(rs != null)
            rs.close();
        return data;
    }

    public List<Integer> fetchingRegisteredDataChart() throws SQLException{
        List<Integer> data = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        String sql = """
                    select count(case when extract(month from a.create_date) = 1 then 1 end) as January,
                           count(case when extract(month from a.create_date) = 2 then 1 end) as February,
                           count(case when extract(month from a.create_date) = 3 then 1 end) as March,
                           count(case when extract(month from a.create_date) = 4 then 1 end) as April,
                           count(case when extract(month from a.create_date) = 5 then 1 end) as May,
                           count(case when extract(month from a.create_date) = 6 then 1 end) as June,
                           count(case when extract(month from a.create_date) = 7 then 1 end) as July,
                           count(case when extract(month from a.create_date) = 8 then 1 end) as August,
                           count(case when extract(month from a.create_date) = 9 then 1 end) as September,
                           count(case when extract(month from a.create_date) = 10 then 1 end) as October,
                           count(case when extract(month from a.create_date) = 11 then 1 end) as November,
                           count(case when extract(month from a.create_date) = 12 then 1 end) as December
                    from account_info ai
                    left join account a on a.username = ai.username
                    where extract(year from a.create_date) = ?
                """;
        st = conn.prepareStatement(sql);
        st.setInt(1, year);
        rs = st.executeQuery();
        if (rs.next()) {
            int tmpPeak = 0;
            for(int i = 0; i < 12; ++i){
                int tmp = rs.getInt(i + 1);
                data.add(tmp);
                totalValue += tmp;
                if(tmpPeak < tmp){
                    tmpPeak = tmp;
                    peakValue = i + 1;
                }
            }
            avgValue = ((totalValue*1.0) / (12*1.0));
            avgValue = (double) (Math.round(avgValue*10.0)/10.0); // lam tron 1 so thap phan
        }

        if(st != null)
            st.close();
        if(rs != null)
            rs.close();
        return data;
    }

}
