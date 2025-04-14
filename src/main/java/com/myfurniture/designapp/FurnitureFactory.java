package com.myfurniture.designapp;

import javafx.scene.paint.Color;

public class FurnitureFactory {
    public static FurnitureItem createFurniture(String type) {
        switch (type.toLowerCase()) {
            case "chair":
                return new FurnitureItem("Chair", 50, 50, 80, 80, Color.ORANGE, Color.DARKGRAY, "fabric");
            case "table":
                return new FurnitureItem("Table", 50, 50, 100, 60, Color.CYAN, Color.GRAY, "wood");
            case "bed":
                return new FurnitureItem("Bed", 50, 50, 160, 80, Color.rgb(200, 150, 120), Color.WHITE, "fabric");
            case "sofa":
                return new FurnitureItem("Sofa", 50, 50, 120, 70, Color.rgb(150, 80, 80), Color.rgb(120, 60, 60), "leather");
            case "bookshelf":
                return new FurnitureItem("Bookshelf", 50, 50, 60, 120, Color.rgb(100, 70, 40), Color.BLACK, "wood");
            case "wardrobe":
                return new FurnitureItem("Wardrobe", 50, 50, 70, 140, Color.BEIGE, Color.SADDLEBROWN, "wood");
            case "dining table":
                return new FurnitureItem("Dining Table", 50, 50, 130, 70, Color.LIGHTBLUE, Color.DARKBLUE, "wood");
            case "lamp":
                return new FurnitureItem("Lamp", 50, 50, 20, 60, Color.YELLOW, Color.GRAY, "metal");
            case "tv stand":
                return new FurnitureItem("TV Stand", 50, 50, 100, 40, Color.DARKGRAY, Color.BLACK, "wood");
            case "coffee table":
                return new FurnitureItem("Coffee Table", 50, 50, 80, 50, Color.BURLYWOOD, Color.CHOCOLATE, "wood");
            default:
                return null;
        }
    }
}
