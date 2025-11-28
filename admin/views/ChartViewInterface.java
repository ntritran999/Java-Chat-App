package admin.views;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public interface ChartViewInterface {
    JButton getbuttonView();
    JComboBox<String> getYearComboBox();
    void refreshStatistics();
    int getCurrentYear();
    Chart getChartPanelActive();
    void setUserData(int[] data);
    void setTotalValue(String data);
    void setAvgValue(String data);
    void setPeakValue(String data);
    JPanel createStatisticsPanel();
}
