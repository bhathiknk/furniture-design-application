package com.myfurniture.designapp;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Always start the UI on the Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
