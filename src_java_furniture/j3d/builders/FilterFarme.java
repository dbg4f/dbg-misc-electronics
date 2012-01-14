package j3d.builders;

import j3d.primitives.Detail;
import j3d.samples.RedrawListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class FilterFarme extends JFrame {

    private final Detail[] details;

    private final RedrawListener redrawListener;

    public FilterFarme(Collection<Detail> detailList, final RedrawListener redrawListener) {

        super("AKCheckList");
        this.redrawListener = redrawListener;


        details = new ArrayList<Detail>(detailList).toArray(new Detail[detailList.size()]);

        //String[] listData = {"Apple", "Orange", "Cherry", "Blue Berry", "Banana", "Red Plum", "Watermelon"};



        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Unable to find System Look and Feel");
        }

        //This listbox holds only the checkboxes
        final JList listCheckBox = new JList(buildCheckBoxItems());

        //This listbox holds the actual descriptions of list items.
        final JList listDescription = new JList(details);

        listDescription.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listDescription.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() != 2)
                    return;
                int selectedIndex = listDescription.locationToIndex(me.getPoint());
                if (selectedIndex < 0)
                    return;
                CheckBoxItem item = (CheckBoxItem) listCheckBox.getModel().getElementAt(selectedIndex);
                item.setChecked(!item.isChecked());
                //item.detail.setVisible(item.isChecked());
                listCheckBox.repaint();
                if (redrawListener!= null) {
                    redrawListener.onRedraw();
                }

            }
        });


        listCheckBox.setCellRenderer(new CheckBoxRenderer());

        listCheckBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listCheckBox.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                int selectedIndex = listCheckBox.locationToIndex(me.getPoint());
                if (selectedIndex < 0)
                    return;
                CheckBoxItem item = (CheckBoxItem) listCheckBox.getModel().getElementAt(selectedIndex);
                item.setChecked(!item.isChecked());
                listDescription.setSelectedIndex(selectedIndex);
                listCheckBox.repaint();
                if (redrawListener!= null) {
                    redrawListener.onRedraw();
                }

            }
        });


        // Now create a scrollpane;

        JScrollPane scrollPane = new JScrollPane();

        //Make the listBox with Checkboxes look like a rowheader.
        //This will place the component on the left corner of the scrollpane
        scrollPane.setRowHeaderView(listCheckBox);

        //Now, make the listbox with actual descriptions as the main view
        scrollPane.setViewportView(listDescription);

        // Align both the checkbox height and widths
        listDescription.setFixedCellHeight(20);
        listCheckBox.setFixedCellHeight(listDescription.getFixedCellHeight());
        listCheckBox.setFixedCellWidth(20);

        getContentPane().add(scrollPane); //, BorderLayout.CENTER);

        setSize(650, 400);
        setVisible(true);

    }

    private CheckBoxItem[] buildCheckBoxItems() {
        CheckBoxItem[] checkboxItems = new CheckBoxItem[details.length];
        for (int counter = 0; counter < details.length; counter++) {
            checkboxItems[counter] = new CheckBoxItem(details[counter]);
        }
        return checkboxItems;
    }

    public static void main(String args[]) {
        launch(Collections.EMPTY_LIST, null);
    }

    public static void launch(Collection<Detail> details, RedrawListener redrawListener) {
        FilterFarme checkList = new FilterFarme(details, redrawListener);
        checkList.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    /* Inner class to hold data for JList with checkboxes */
    class CheckBoxItem {
        //private boolean isChecked;

        final Detail detail;

        public CheckBoxItem(Detail detail) {
            this.detail = detail;
            //isChecked = false;
        }

        public boolean isChecked() {
            return detail.isVisible();
        }

        public void setChecked(boolean value) {
            detail.setVisible(value);
        }
    }

/* Inner class that renders JCheckBox to JList*/

    class CheckBoxRenderer extends JCheckBox implements ListCellRenderer {

        public CheckBoxRenderer() {
            setBackground(UIManager.getColor("List.textBackground"));
            setForeground(UIManager.getColor("List.textForeground"));
        }

        public Component getListCellRendererComponent(JList listBox, Object obj, int currentindex, boolean isChecked, boolean hasFocus) {
            CheckBoxItem checkBoxItem = (CheckBoxItem) obj;
            setSelected(checkBoxItem.isChecked());
            return this;
        }

    }


}