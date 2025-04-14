package com.myfurniture.designapp.Main;

import com.myfurniture.designapp.Core.DesignManager;
import com.myfurniture.designapp.Factory.FurnitureFactory;
import com.myfurniture.designapp.Core.FurnitureItem;
import com.myfurniture.designapp.Core.RoomDesign;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private DesignManager designManager = new DesignManager();

    @Override
    public void start(Stage primaryStage) {
        // Create a sample room design.
        RoomDesign room = new RoomDesign(800, 600, javafx.scene.paint.Color.LIGHTGRAY);
        // Add sample furniture.
        FurnitureItem chair = FurnitureFactory.createFurniture("Chair");
        chair.setX(100);
        chair.setY(150);
        room.addFurniture(chair);

        FurnitureItem table = FurnitureFactory.createFurniture("Table");
        table.setX(300);
        table.setY(200);
        room.addFurniture(table);

        designManager.setCurrentDesign(room);

        Dashboard dashboard = new Dashboard(designManager);
        Scene scene = new Scene(dashboard, 1200, 800);
        primaryStage.setTitle("Furniture Design Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
