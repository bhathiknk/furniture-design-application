package com.myfurniture.designapp;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginStage extends Stage {
    private TextField txtUsername;
    private PasswordField txtPassword;
    private Button btnLogin;

    public LoginStage() {
        setTitle("Designer Login");
        initComponents();
    }

    private void initComponents() {
        // Using a GridPane for layout similar to the Swing GridBagLayout.
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label lblUsername = new Label("Username:");
        Label lblPassword = new Label("Password:");

        txtUsername = new TextField();
        txtUsername.setPrefWidth(150);
        txtPassword = new PasswordField();
        txtPassword.setPrefWidth(150);

        btnLogin = new Button("Login");

        // Arrange controls in the grid.
        grid.add(lblUsername, 0, 0);
        grid.add(txtUsername, 1, 0);
        grid.add(lblPassword, 0, 1);
        grid.add(txtPassword, 1, 1);
        grid.add(btnLogin, 1, 2);

        // Set action handler for login.
        btnLogin.setOnAction(e -> handleLogin());

        Scene scene = new Scene(grid, 400, 220);
        setScene(scene);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        // Use hardcoded credentials for demonstration.
        if ("designer".equals(username) && "password".equals(password)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Login Successful!");
            alert.showAndWait();
            // Close the login stage.
            close();
            // Open your JavaFX dashboard (Dashboard replaces the old DashboardFrame).
            Dashboard dashboard = new Dashboard(new DesignManager());
            Stage dashboardStage = new Stage();
            dashboardStage.setScene(new Scene(dashboard, 1200, 800));
            dashboardStage.setTitle("Furniture Design Application - JavaFX 3D");
            dashboardStage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid credentials. Please try again.");
            alert.setHeaderText("Login Error");
            alert.showAndWait();
        }
    }
}
