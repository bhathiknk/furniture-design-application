package com.myfurniture.designapp.Main;

import com.myfurniture.designapp.Core.DesignManager;
import com.myfurniture.designapp.UI.RoomDesigner2D;
import com.myfurniture.designapp.UI.RoomRenderer3D;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class Dashboard extends BorderPane {
    private DesignManager designManager;
    private RoomDesigner2D roomDesigner2D;
    private RoomRenderer3D roomRenderer3D;

    public Dashboard(DesignManager designManager) {
        this.designManager = designManager;
        initComponents();
    }

    private void initComponents() {
        TabPane tabPane = new TabPane();

        Tab tab2D = new Tab("2D Room Designer");
        roomDesigner2D = new RoomDesigner2D(designManager, () -> {
            if (roomRenderer3D != null) {
                roomRenderer3D.updateScene();
            }
        });
        tab2D.setContent(roomDesigner2D);
        tab2D.setClosable(false);

        Tab tab3D = new Tab("3D Room View");
        roomRenderer3D = new RoomRenderer3D(designManager);
        tab3D.setContent(roomRenderer3D);
        tab3D.setClosable(false);

        tabPane.getTabs().addAll(tab2D, tab3D);
        setCenter(tabPane);
    }
}
