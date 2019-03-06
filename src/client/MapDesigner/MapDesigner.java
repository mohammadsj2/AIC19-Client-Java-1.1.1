package client.MapDesigner;


import client.model.Cell;
import client.model.Map;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.util.Date;
import java.util.Formatter;
import java.util.Random;


public class MapDesigner extends Application {
    private int choice = 2;//0 sabze  1: My respawnZone  2:opp respawnZone 3: objectiveZone  4:Wall

    private Color[] colors = {Color.rgb(99, 106, 62),
            Color.rgb(189, 222, 253),
            Color.rgb(25, 85, 167),
            Color.rgb(200, 109, 24),
            Color.rgb(214, 176, 126)};

    private Group root = new Group();
    private Scene scene = new Scene(root, 1000, 900);
    private Map map;

    private void createMap() {
        Pane pane = new Pane();
        addNode(pane);
        int rowNum = 31;
        int columnNum = 31;
        Cell[][] cells = new Cell[rowNum][columnNum];
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < columnNum; j++) {
                cells[i][j] = new Cell(i, j);
                Rectangle rectangle = new Rectangle(25 * j, 25 * i, 23, 23);
                setRectanglesColor(rectangle, cells[i][j]);

                int finalI = i;
                int finalJ = j;
                rectangle.setOnMouseEntered(event -> {
                    if (event.isShiftDown() || event.isControlDown()) {
                        clickOnCell(cells[finalI][finalJ], rectangle, choice);
                    } else if (event.isAltDown()) {
                        clickOnCell(cells[finalI][finalJ], rectangle, 0);
                    }
                });
                rectangle.setOnMouseClicked(event -> {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        clickOnCell(cells[finalI][finalJ], rectangle, choice);
                    } else {
                        clickOnCell(cells[finalI][finalJ], rectangle, 0);
                    }
                });
                pane.getChildren().add(rectangle);
            }
        }
        pane.relocate(220, 110);
        map = new Map();
        map.setCells(cells);
        map.setRowNum(rowNum);
        map.setColumnNum(columnNum);
    }

    private void clickOnCell(Cell cell, Rectangle rectangle, int choice) {
        setCellStatus(cell, choice);
        setRectanglesColor(rectangle, cell);
    }

    private void setCellStatus(Cell cell, int choice) {
        cell.setInMyRespawnZone(false);
        cell.setInOppRespawnZone(false);
        cell.setInObjectiveZone(false);
        cell.setWall(false);
        switch (choice) {
            case 0:
                break;
            case 1:
                cell.setInMyRespawnZone(true);
                break;
            case 2:
                cell.setInOppRespawnZone(true);
                break;
            case 3:
                cell.setInObjectiveZone(true);
                break;
            case 4:
                cell.setWall(true);
                break;
        }
    }

    private void setRectanglesColor(Rectangle rectangle, Cell cell) {
        if (cell.isInMyRespawnZone()) {
            rectangle.setFill(colors[1]);
        } else if (cell.isInObjectiveZone()) {
            rectangle.setFill(colors[3]);
        } else if (cell.isInOppRespawnZone()) {
            rectangle.setFill(colors[2]);
        } else if (cell.isWall()) {
            rectangle.setFill(colors[4]);
        } else {
            rectangle.setFill(colors[0]);
        }
    }

    private void addNode(Node node) {
        if (!root.getChildren().contains(node))
            root.getChildren().add(node);
    }

    private void saveMap(String name) {
        StringBuilder stringBuilder = new StringBuilder("{\n" +
                "\t\"gameConstants\": {\n" +
                "\t\t\"killScore\": 10,\n" +
                "\t\t\"objectiveZoneScore\": 1,\n" +
                "\t\t\"timeout\": 500,\n" +
                "\t\t\"maxAP\": 100,\n" +
                "\t\t\"maxTurns\": 100,\n" +
                "\t\t\"maxScore\": 300\n" +
                "\t},\n" +
                "\t\"map\": {\n" +
                "\t\t\"cells\": [\n");
        for (int i = 0; i < map.getRowNum(); i++) {
            for (int j = 0; j < map.getColumnNum(); j++) {
                Cell cell = map.getCell(i, j);
                stringBuilder.append("\t\t\t{\n" +
                        "\t\t\t\t\"row\": " + cell.getRow() + ",\n" +
                        "\t\t\t\t\"column\": " + cell.getColumn() + ",\n" +
                        "\t\t\t\t\"isWall\": " + cell.isWall() + ",\n" +
                        "\t\t\t\t\"isInFirstRespawnZone\": " + cell.isInMyRespawnZone() + ",\n" +
                        "\t\t\t\t\"isInSecondRespawnZone\": " + cell.isInOppRespawnZone() + ",\n" +
                        "\t\t\t\t\"isInObjectiveZone\": " + cell.isInObjectiveZone() + "\n" +
                        "\t\t\t}");
                if (!(i == map.getRowNum() - 1 && j == map.getColumnNum() - 1)) {
                    stringBuilder.append(",");
                }
                stringBuilder.append("\n");
            }
        }
        stringBuilder.append("\t\t],\n" +
                "\t\t\"rowNum\": " + map.getRowNum() + ",\n" +
                "\t\t\"columnNum\": " + map.getColumnNum() + "\n" +
                "\t},\n" +
                "\t\"heroConstants\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"SENTRY\",\n" +
                "\t\t\t\"abilityNames\": [\n" +
                "\t\t\t\t\"SENTRY_ATTACK\",\n" +
                "\t\t\t\t\"SENTRY_DODGE\",\n" +
                "\t\t\t\t\"SENTRY_RAY\"\n" +
                "\t\t\t],\n" +
                "\t\t\t\"maxHP\": 120,\n" +
                "\t\t\t\"moveAPCost\": 6,\n" +
                "\t\t\t\"respawnTime\": 5\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"BLASTER\",\n" +
                "\t\t\t\"abilityNames\": [\n" +
                "\t\t\t\t\"BLASTER_ATTACK\",\n" +
                "\t\t\t\t\"BLASTER_DODGE\",\n" +
                "\t\t\t\t\"BLASTER_BOMB\"\n" +
                "\t\t\t],\n" +
                "\t\t\t\"maxHP\": 250,\n" +
                "\t\t\t\"moveAPCost\": 4,\n" +
                "\t\t\t\"respawnTime\": 5\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"HEALER\",\n" +
                "\t\t\t\"abilityNames\": [\n" +
                "\t\t\t\t\"HEALER_ATTACK\",\n" +
                "\t\t\t\t\"HEALER_DODGE\",\n" +
                "\t\t\t\t\"HEALER_HEAL\"\n" +
                "\t\t\t],\n" +
                "\t\t\t\"maxHP\": 200,\n" +
                "\t\t\t\"moveAPCost\": 4,\n" +
                "\t\t\t\"respawnTime\": 5\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"GUARDIAN\",\n" +
                "\t\t\t\"abilityNames\": [\n" +
                "\t\t\t\t\"GUARDIAN_ATTACK\",\n" +
                "\t\t\t\t\"GUARDIAN_DODGE\",\n" +
                "\t\t\t\t\"GUARDIAN_FORTIFY\"\n" +
                "\t\t\t],\n" +
                "\t\t\t\"maxHP\": 400,\n" +
                "\t\t\t\"moveAPCost\": 8,\n" +
                "\t\t\t\"respawnTime\": 5\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"abilityConstants\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"SENTRY_ATTACK\",\n" +
                "\t\t\t\"type\": \"OFFENSIVE\",\n" +
                "\t\t\t\"range\": 7,\n" +
                "\t\t\t\"APCost\": 15,\n" +
                "\t\t\t\"cooldown\": 0,\n" +
                "\t\t\t\"areaOfEffect\": 0,\n" +
                "\t\t\t\"power\": 30,\n" +
                "\t\t\t\"isLobbing\": false\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"SENTRY_DODGE\",\n" +
                "\t\t\t\"type\": \"DODGE\",\n" +
                "\t\t\t\"range\": 3,\n" +
                "\t\t\t\"APCost\": 25,\n" +
                "\t\t\t\"cooldown\": 6,\n" +
                "\t\t\t\"areaOfEffect\": 0,\n" +
                "\t\t\t\"power\": 0,\n" +
                "\t\t\t\"isLobbing\": true\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"HEALER_ATTACK\",\n" +
                "\t\t\t\"type\": \"OFFENSIVE\",\n" +
                "\t\t\t\"range\": 4,\n" +
                "\t\t\t\"APCost\": 15,\n" +
                "\t\t\t\"cooldown\": 0,\n" +
                "\t\t\t\"areaOfEffect\": 0,\n" +
                "\t\t\t\"power\": 25,\n" +
                "\t\t\t\"isLobbing\": true\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"BLASTER_ATTACK\",\n" +
                "\t\t\t\"type\": \"OFFENSIVE\",\n" +
                "\t\t\t\"range\": 3,\n" +
                "\t\t\t\"APCost\": 15,\n" +
                "\t\t\t\"cooldown\": 0,\n" +
                "\t\t\t\"areaOfEffect\": 1,\n" +
                "\t\t\t\"power\": 20,\n" +
                "\t\t\t\"isLobbing\": false\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"GUARDIAN_DODGE\",\n" +
                "\t\t\t\"type\": \"DODGE\",\n" +
                "\t\t\t\"range\": 2,\n" +
                "\t\t\t\"APCost\": 25,\n" +
                "\t\t\t\"cooldown\": 8,\n" +
                "\t\t\t\"areaOfEffect\": 0,\n" +
                "\t\t\t\"power\": 0,\n" +
                "\t\t\t\"isLobbing\": true\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"GUARDIAN_FORTIFY\",\n" +
                "\t\t\t\"type\": \"FORTIFY\",\n" +
                "\t\t\t\"range\": 4,\n" +
                "\t\t\t\"APCost\": 25,\n" +
                "\t\t\t\"cooldown\": 7,\n" +
                "\t\t\t\"areaOfEffect\": 0,\n" +
                "\t\t\t\"power\": 0,\n" +
                "\t\t\t\"isLobbing\": true\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"HEALER_DODGE\",\n" +
                "\t\t\t\"type\": \"DODGE\",\n" +
                "\t\t\t\"range\": 4,\n" +
                "\t\t\t\"APCost\": 25,\n" +
                "\t\t\t\"cooldown\": 5,\n" +
                "\t\t\t\"areaOfEffect\": 0,\n" +
                "\t\t\t\"power\": 0,\n" +
                "\t\t\t\"isLobbing\": true\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"BLASTER_DODGE\",\n" +
                "\t\t\t\"type\": \"DODGE\",\n" +
                "\t\t\t\"range\": 4,\n" +
                "\t\t\t\"APCost\": 25,\n" +
                "\t\t\t\"cooldown\": 5,\n" +
                "\t\t\t\"areaOfEffect\": 0,\n" +
                "\t\t\t\"power\": 0,\n" +
                "\t\t\t\"isLobbing\": true\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"BLASTER_BOMB\",\n" +
                "\t\t\t\"type\": \"OFFENSIVE\",\n" +
                "\t\t\t\"range\": 5,\n" +
                "\t\t\t\"APCost\": 25,\n" +
                "\t\t\t\"cooldown\": 4,\n" +
                "\t\t\t\"areaOfEffect\": 2,\n" +
                "\t\t\t\"power\": 35,\n" +
                "\t\t\t\"isLobbing\": true\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"GUARDIAN_ATTACK\",\n" +
                "\t\t\t\"type\": \"OFFENSIVE\",\n" +
                "\t\t\t\"range\": 1,\n" +
                "\t\t\t\"APCost\": 15,\n" +
                "\t\t\t\"cooldown\": 0,\n" +
                "\t\t\t\"areaOfEffect\": 1,\n" +
                "\t\t\t\"power\": 40,\n" +
                "\t\t\t\"isLobbing\": true\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"SENTRY_RAY\",\n" +
                "\t\t\t\"type\": \"OFFENSIVE\",\n" +
                "\t\t\t\"range\": 1000,\n" +
                "\t\t\t\"APCost\": 25,\n" +
                "\t\t\t\"cooldown\": 5,\n" +
                "\t\t\t\"areaOfEffect\": 0,\n" +
                "\t\t\t\"power\": 50,\n" +
                "\t\t\t\"isLobbing\": false\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"name\": \"HEALER_HEAL\",\n" +
                "\t\t\t\"type\": \"DEFENSIVE\",\n" +
                "\t\t\t\"range\": 4,\n" +
                "\t\t\t\"APCost\": 15,\n" +
                "\t\t\t\"cooldown\": 2,\n" +
                "\t\t\t\"areaOfEffect\": 0,\n" +
                "\t\t\t\"power\": 30,\n" +
                "\t\t\t\"isLobbing\": true\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}");
        try {
            OutputStream outputStream = new FileOutputStream("./Maps/" + name + ".map");
            Formatter formatter = new Formatter(outputStream);
            formatter.format(stringBuilder.toString());
            formatter.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        createMap();
        initScene();
        primaryStage.setTitle("Map Designer");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }

    private void initScene() {

        Label header = new Label("Use alt, shift, ctrl or mouse to design map!");
        header.setTextFill(Color.DARKRED);
        header.setStyle("-fx-font-size: 50;");
        header.relocate(50, 20);
        addNode(header);
        String[] names = {"Sabze", "My respawnZone", "Opp respawnZone", "ObjectiveZone", "Wall"};
        for (int i = 0; i < 5; i++) {
            Rectangle rectangle = new Rectangle(10, 150 + i * 70, 50, 50);
            Label label = new Label(names[i]);
            label.relocate(70, 170 + i * 70);
            rectangle.setFill(colors[i]);
            int finalI = i;
            rectangle.setOnMouseClicked(event -> choice = finalI);
            addNode(rectangle);
            addNode(label);
        }
        Random random = new Random(new Date().getTime());
        TextField textField = new TextField("themap" + random.nextInt(1000));
        textField.relocate(10, 700);

        Label saveLabel = new Label("Save");
        saveLabel.relocate(10, 750);
        saveLabel.setStyle("-fx-font-size: 50;-fx-background-color: black");
        saveLabel.setOnMouseClicked(event -> saveMap(textField.getText()));
        addNode(saveLabel);
        addNode(textField);
    }
}
