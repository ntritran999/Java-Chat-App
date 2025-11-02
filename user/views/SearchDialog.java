package user.views;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class SearchDialog extends JDialog {
    private JTextField searchField;
    private JPanel listPanel;
    public SearchDialog(JFrame parent) {
        super(parent, "Tìm kiếm", true);
        setIconImage(new ImageIcon(getClass().getResource("/assets/icons/search-icon.png")).getImage());
        setPreferredSize(new Dimension(300, 500));
        
        Container cp = getContentPane();
        cp.setLayout(new BoxLayout(cp, BoxLayout.PAGE_AXIS));

        JLabel title = new JLabel("Nhập tên hoặc tên đăng nhập");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(250, 30));
        
        listPanel = new JPanel();
        listPanel.setLayout(new WrapLayout(FlowLayout.CENTER, 0, 10));
        JScrollPane sp = new JScrollPane(listPanel);

        cp.add(title);
        cp.add(Box.createRigidArea(new Dimension(0, 5)));
        cp.add(searchField);
        cp.add(sp);

        pack();
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                searchField.setText("");
                clearListPanel();
            }
        });
    }
    
    public JTextField getSearchField() {
        return searchField;
    }

    public JPanel getListPanel() {
        return listPanel;
    }

    public void addToListPanel(Component c) {
        listPanel.add(c);
        listPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    public void clearListPanel() {
        listPanel.removeAll();
    }

    public void updateListPanel() {
        listPanel.revalidate();
        listPanel.repaint();
    }

    public void showSearchDialog() {
        setVisible(true);
    }
}
