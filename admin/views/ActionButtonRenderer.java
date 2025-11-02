package admin.views;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.DefaultCellEditor;
import java.awt.*;
import java.util.List;
//https://docs.oracle.com/javase/tutorial/uiswing/components/table.html#editrender

public class ActionButtonRenderer extends JPanel implements TableCellRenderer{

    private static List<JButton> actionButtonsRef;
    
    public static void setActionButtons(List<JButton> buttons){
        actionButtonsRef = buttons;
    }

    public static List<JButton> getActionButtons(){
        return actionButtonsRef;
    }

    public ActionButtonRenderer(){
        setLayout(new GridBagLayout());
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column){
        removeAll(); 

        if(actionButtonsRef != null && row < actionButtonsRef.size()){
            add(actionButtonsRef.get(row));
        }

        return this;
    }
}


// ================== CLASS EDITOR ==================
class ActionButtonEditor extends DefaultCellEditor{

    public ActionButtonEditor(JCheckBox checkBox){
        super(checkBox);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column){
        JPanel panel = new JPanel(new GridBagLayout());
        List<JButton> buttons = ActionButtonRenderer.getActionButtons();

        if(buttons != null && row < buttons.size()){
            panel.add(buttons.get(row));
        }

        return panel;
    }
}
