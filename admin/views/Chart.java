package admin.views;
import javax.swing.*;
import java.awt.*;

public class Chart extends JPanel{
    private int[] data;
    private String[] columnName;
    private String chartTitle = "";
    private String yName = "";
    private String xName = "";
    private int topMargin;
    private int leftMargin;
    private int bottomMargin;
    private int rightMargin;

    public Chart(int topMargin, int leftMargin, int bottomMargin, int rightMargin){
        setBackground(Color.WHITE);
        this.topMargin = topMargin;
        this.leftMargin = leftMargin;
        this.bottomMargin = bottomMargin;
        this.rightMargin = rightMargin;
    }

    public void setTitle(String chartTitle, String yName, String xName){
        this.chartTitle = chartTitle;
        this.yName = yName;
        this.xName = xName;
    }

    public void setData(int[] other){
        data = new int[other.length];
        for(int i = 0; i < other.length; ++i)
            data[i] = other[i];
    }

    public void setColumnName(String[] other){
        columnName = new String[other.length];
        for(int i = 0; i < other.length; ++i)
            columnName[i] = other[i];
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawChart(g2, topMargin, leftMargin, bottomMargin, rightMargin);
    }

    public void drawChart(Graphics2D g2, int topMargin, int leftMargin, int bottomMargin, int rightMargin){
        int width = getWidth();
        int height = getHeight();
        
        int chartWidth = width - leftMargin - rightMargin;
        int chartHeight = height - topMargin - bottomMargin;

        int maxValue = 100;
        for(int i  = 0; i < data.length; ++i){
            if(maxValue < data[i])
                maxValue = data[i];
        }
        maxValue = ((maxValue / 100) + 1) * 100;

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(new Color(60, 60, 60));

        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(chartTitle);
        g2.drawString(chartTitle, (width - titleWidth) / 2, 25);

        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(leftMargin, topMargin, leftMargin, height - bottomMargin);
        g2.drawLine(leftMargin, height - bottomMargin, width - rightMargin, height - bottomMargin);

        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        int ySteps = 5;
        for(int i = 0; i <= ySteps; i++){
            int value = (maxValue / ySteps) * i;
            int y = height - bottomMargin - (chartHeight * i / ySteps);

            g2.setColor(Color.GRAY);
            String label = String.valueOf(value);
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, leftMargin - labelWidth - 10, y + 5);
        }
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(new Color(60, 60, 60));
        g2.rotate(-Math.PI / 2);
        g2.drawString(yName, -(height / 2 + 50), 20);
        g2.rotate(Math.PI / 2);
        
        // Draw bars
        int barWidth = chartWidth / (columnName.length + 1);
        int actualBarWidth = (int) (barWidth * 0.6);
        
        for(int i = 0; i < columnName.length; i++){
            int barHeight = (int) ((double) data[i] / maxValue * chartHeight);
            int x = leftMargin + (i + 1) * barWidth - actualBarWidth / 2;
            int y = height - bottomMargin - barHeight;
            g2.setPaint(new Color(66, 165, 245));
            g2.fillRoundRect(x, y, actualBarWidth, barHeight, 5, 5);
            
            // Draw bar border
            g2.setColor(new Color(33, 150, 243));
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(x, y, actualBarWidth, barHeight, 5, 5);
            
            // Draw value on top of bar
            if(data[i] > 0){
                g2.setColor(new Color(60, 60, 60));
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                String valueStr = String.valueOf(data[i]);
                int valueWidth = g2.getFontMetrics().stringWidth(valueStr);
                g2.drawString(valueStr, x + (actualBarWidth - valueWidth) / 2, y - 5);
            }
        }
        //Draw X-axis labels 
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(Color.GRAY);
        for(int i = 0; i < columnName.length; i++){
            int x = leftMargin + (i + 1) * barWidth;
            String monthLabel = "T" + (i + 1);
            int labelWidth = g2.getFontMetrics().stringWidth(monthLabel);
            g2.drawString(monthLabel, x - labelWidth / 2, height - bottomMargin + 20);
        }
        // Draw X-axis title
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(new Color(60, 60, 60));
        int xTitleWidth = g2.getFontMetrics().stringWidth(xName);
        g2.drawString(xName, (width - xTitleWidth) / 2, height - 20);
    }

}
