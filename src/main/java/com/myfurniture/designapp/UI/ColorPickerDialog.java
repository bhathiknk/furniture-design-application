package com.myfurniture.designapp.UI;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class ColorPickerDialog {
    public static Color showDialog(Color initialColor) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Choose Color");

        ColorPicker colorPicker = new ColorPicker(initialColor);
        Button btnOk = new Button("OK");
        Button btnCancel = new Button("Cancel");

        VBox layout = new VBox(10, colorPicker, btnOk, btnCancel);
        layout.setStyle("-fx-padding: 10;");
        Scene scene = new Scene(layout);
        dialog.setScene(scene);

        final Color[] chosenColor = new Color[1];
        btnOk.setOnAction(e -> {
            chosenColor[0] = colorPicker.getValue();
            dialog.close();
        });
        btnCancel.setOnAction(e -> dialog.close());

        dialog.showAndWait();
        return chosenColor[0];
    }
}
