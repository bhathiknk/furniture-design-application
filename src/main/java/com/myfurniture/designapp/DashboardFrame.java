package com.myfurniture.designapp;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private RoomDesignerPanel roomDesignerPanel;
    private RoomRenderer3D roomRenderer3D;
    private DesignManager designManager;

    public DashboardFrame() {
        designManager = new DesignManager();
        setTitle("Furniture Design Application");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        roomDesignerPanel = new RoomDesignerPanel(designManager);
        roomRenderer3D = new RoomRenderer3D(designManager);

        tabbedPane.addTab("2D Room Designer", roomDesignerPanel);
        tabbedPane.addTab("3D Room View", roomRenderer3D);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
