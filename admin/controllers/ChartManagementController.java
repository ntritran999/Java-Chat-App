package admin.controllers;
import javax.swing.*;

import admin.models.ChartManagementModel;
import admin.views.ActiveUsersChart;
import admin.views.ChartViewInterface;
import admin.views.RegisteredUserChart;

import java.util.List;
import java.sql.SQLException;


public class ChartManagementController {
    private ChartViewInterface view;
    private int currentYear = 0;

    // trang thai fillter
    private int year = 0;

    public ChartManagementController(RegisteredUserChart view){
        this.view = view;
        currentYear = view.getCurrentYear();
        initController();
        loadData();
    }

    public ChartManagementController(ActiveUsersChart view){
        this.view = view;
        currentYear = view.getCurrentYear();
        initController();
        loadData();
    }

    private void initController(){
        view.getbuttonView().addActionListener(e ->{
            int selectedIndex = view.getYearComboBox().getSelectedIndex();
            year = currentYear - selectedIndex;
            loadData();
        });
    }

    private void loadData(){
        view.getChartPanelActive().setEnabled(false);

        ChartManagementModel model = new ChartManagementModel(year);
        SwingWorker<List<Integer>, Void> worker = new SwingWorker<List<Integer>, Void>() {
            @Override 
            protected List<Integer> doInBackground() throws SQLException{
                if(view instanceof RegisteredUserChart)
                    return model.fetchingRegisteredDataChart();
                else
                    return model.fetchingActiveDataChart();
            }

            @Override
            protected void done(){
                List<Integer> dataChart = null;
                try{
                    dataChart = get();
                    int[] arr = new int[dataChart.size()];
                    for (int i = 0; i < dataChart.size(); i++){
                        arr[i] = dataChart.get(i);
                    }
                    view.getChartPanelActive().setData(arr);
                    view.setTotalValue(String.valueOf(model.getTotalValue()));
                    view.setAvgValue(String.valueOf(model.getAvgValue()));
                    view.setPeakValue(String.valueOf(model.getPeakValue()));
                    view.getChartPanelActive().repaint();
                    view.refreshStatistics();
                    view.getChartPanelActive().setEnabled(true);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }; 
        worker.execute();
    }
    
    
}
