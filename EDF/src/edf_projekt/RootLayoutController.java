/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edf_projekt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author milosz
 */
public class RootLayoutController {

    private final ObservableList<Task> taskList
            = FXCollections.observableArrayList();

    private final Paint[] style = {Color.BLUE, Color.BLUEVIOLET, Color.BROWN, Color.DARKBLUE, Color.CADETBLUE, Color.CORAL, Color.DARKGREEN, Color.GOLD, Color.LIGHTSEAGREEN, Color.ROYALBLUE};

    private boolean generateDialogOpen = false, taskNewDialegOpen = false,
            taskEditDialogOpen = false;

    private List<Paint> styleList;

    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn<Task, String> columnId;
    @FXML
    private TableColumn<Task, Integer> columnExecutionTime;
    @FXML
    private TableColumn<Task, Integer> columnPeriod;
    @FXML
    private Canvas chartCanvas;
    @FXML
    private Label utilizationLabel;

    @FXML
    private void initialize() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnExecutionTime.setCellValueFactory(new PropertyValueFactory<>("executionTime"));
        columnPeriod.setCellValueFactory(new PropertyValueFactory<>("period"));
        taskTable.setItems(taskList);
    }

    @FXML
    private void handleDeleteTask() {
        int selectedIndex = taskTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            taskTable.getItems().remove(selectedIndex);
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            ((Stage) alert.getDialogPane().getScene().getWindow())
                    .getIcons()
                    .add(new Image("file:resources/images/icon.png"));
            alert.setTitle("Niepoprawna akcja");
            alert.setHeaderText("Nie wskazano zadania");
            alert.setContentText("Wskaż zadanie.");
            alert.showAndWait();
        }
    }

    public boolean showTaskEditDialog(Task task) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RootLayoutController.class.getResource("TaskEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Eytuj zadanie");
            dialogStage.getIcons()
                    .add(new Image("file:resources/images/icon.png"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setAlwaysOnTop(true);

            TaskEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTask(task);

            dialogStage.showAndWait();
            taskEditDialogOpen = false;
            return controller.isOkClicked();
        } catch (IOException e) {
            return false;
        }
    }

    public boolean showTaskNewDialog(Task task) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RootLayoutController.class.getResource("TaskNewDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nowe zadanie");
            dialogStage.getIcons()
                    .add(new Image("file:resources/images/icon.png"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setAlwaysOnTop(true);

            TaskNewDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTask(task);

            dialogStage.showAndWait();
            taskNewDialegOpen = false;
            return controller.isOkClicked();
        } catch (IOException e) {
            return false;
        }
    }

    public Task[] showGenerateDialog() {

        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RootLayoutController.class.getResource("GenerateDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Generuj zadania");
            dialogStage.getIcons()
                    .add(new Image("file:resources/images/icon.png"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setAlwaysOnTop(true);

            GenerateDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            taskNewDialegOpen = false;
            return controller.getTasks();
        } catch (IOException e) {
            return null;
        }
    }

    @FXML
    private void handleTasksFromFile(ActionEvent ae) {
        Node source = (Node) ae.getSource();
        Window stage = source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Plik txt", "*.txt"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            updateTaskTableFromFile(file);
        }
    }

    private void updateTaskTableFromFile(File file) {
        List<Task> localTaskList = new LinkedList<>();
        boolean readOk = true;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                String[] s = st.trim().split("\\D+");
                if (s != null && s.length >= 2) {
                    Task tmpTask = new Task();
                    tmpTask.setExecutionTime(Integer.parseInt(s[0]));
                    tmpTask.setPeriod(Integer.parseInt(s[1]));
                    localTaskList.add(tmpTask);
                } else {
                    readOk = false;
                }
            }
        } catch (FileNotFoundException ex) {
            readOk = false;
        } catch (IOException ex) {
            readOk = false;
        }

        if (readOk) {
            localTaskList.forEach((t) -> {
                taskList.add(t);
            });
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            ((Stage) alert.getDialogPane().getScene().getWindow())
                    .getIcons()
                    .add(new Image("file:resources/images/icon.png"));
            alert.setTitle("Niepoprawna zawartość pliku");
            alert.setHeaderText("Zawartość pliku posiada zły format");
            alert.setContentText("Zawartość pliku musi być poprawna");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleNewTask() {
        if (taskNewDialegOpen == false) {
            taskNewDialegOpen = true;
            Task tmpTask = new Task();
            boolean okClicked = showTaskNewDialog(tmpTask);
            if (okClicked) {
                taskList.add(tmpTask);
            }
            taskNewDialegOpen = false;
        }
    }

    @FXML
    private void handleGenerateTasks() {
        if (generateDialogOpen == false) {
            generateDialogOpen = true;
            Task[] tasks = showGenerateDialog();
            if (tasks != null) {
                taskList.addAll(tasks);
            }
            generateDialogOpen = false;
        }
    }

    @FXML
    private void handleEditTask() {
        if (taskEditDialogOpen == false) {
            taskEditDialogOpen = true;
            Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                boolean okClicked = showTaskEditDialog(selectedTask);
                if (okClicked) {
                    taskTable.refresh();
                }

            } else {
                // Nothing selected.
                Alert alert = new Alert(AlertType.WARNING);
                ((Stage) alert.getDialogPane().getScene().getWindow())
                        .getIcons()
                        .add(new Image("file:resources/images/icon.png"));
                alert.setTitle("Niepoprawna akcja");
                alert.setHeaderText("Nie wskazano zadania");
                alert.setContentText("Wskaż zadanie.");

                alert.showAndWait();
            }
            taskEditDialogOpen = false;
        }
    }

    private Paint getRandomStyle() {
        Random random = new Random();
        int randomIndex = random.nextInt(styleList.size());
        Paint randomStyle = styleList.get(randomIndex);
        styleList.remove(randomIndex);
        return randomStyle;
    }

    @FXML
    private void handleSchedule() {
        if (taskList.size() > 10) {
            Alert alert = new Alert(AlertType.WARNING);
            ((Stage) alert.getDialogPane().getScene().getWindow())
                    .getIcons()
                    .add(new Image("file:resources/images/icon.png"));
            alert.setTitle("Niepoprawna liczba zadań");
            alert.setHeaderText("Liczba zadań nie może być większa niż 10");
            alert.setContentText("Należy usunąć nadmiarowe zadania");

            alert.showAndWait();
        } else if (taskList.size() > 0) {
            styleList = new ArrayList<>(Arrays.asList(style));
            int LCM = EDFScheduler.getLeastCommonMultiple(taskList);
            List<Task> scheduledTasks = EDFScheduler.schedule(taskList, LCM);
            drawChart(taskList, scheduledTasks, LCM);
            BigDecimal U = EDFScheduler.calculateUtilizationPercentage(taskList);
            utilizationLabel.setText("U = " + U + "%");
        }
    }

    private void drawChart(List<Task> unorderedTasks, List<Task> scheduledTaskList, int LCM) {

        int maxWidth = 8000;
        int padding = 50;
        int taskHeight = 86;
        int timeUnitWidth = 20;
        int lineLength = 20;
        int width = (timeUnitWidth * LCM + 30) > maxWidth ? maxWidth : timeUnitWidth * LCM + 30;
        int height = unorderedTasks.size() * taskHeight + 30;

        chartCanvas.setHeight(height + 100);
        chartCanvas.setWidth(width + 100);
        GraphicsContext gc = chartCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, chartCanvas.getWidth(), chartCanvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        writeTextOnCanvas(gc, "ID", padding - 25, padding);
        drawArrowUp(gc, padding, padding + height, padding, padding);
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        writeTextOnCanvas(gc, "Time", padding + width + 5, padding + height + 15);
        String title = "Wykres Gantta dla algorytmu EDF (NWW=" + LCM + ")";
        writeTextOnCanvas(gc, title, width / 2 - title.length(), padding - 35);
        drawArrowRight(gc, padding, padding + height, padding + width + 15, padding + height);

        for (int i = 0; i < unorderedTasks.size(); i++) {
            gc.setStroke(Color.BLACK);
            writeTextOnCanvas(gc, unorderedTasks.get(i).getId(), padding - 40, padding + taskHeight * (i + 1) - 2 - taskHeight / 2);
            drawLine(gc, padding - lineLength / 2, padding + taskHeight * (i + 1) - taskHeight / 2, padding + lineLength / 2, padding + taskHeight * (i + 1) - taskHeight / 2);
            gc.setStroke(Color.GAINSBORO);
            drawLine(gc, padding + lineLength / 2, padding + taskHeight * (i + 1) - taskHeight / 2, padding + width - 30, padding + taskHeight * (i + 1) - taskHeight / 2);
        }

        for (int i = 0; i <= scheduledTaskList.size(); i++) {
            if (padding + i * timeUnitWidth <= padding + width - 10) {
                gc.setStroke(Color.BLACK);
                drawLine(gc, padding + i * timeUnitWidth, padding + height - lineLength / 2, padding + i * timeUnitWidth, padding + height + lineLength / 2);
                int textLen = (i + "").length();
                writeTextOnCanvas(gc, i + "", padding + i * timeUnitWidth - textLen * 3, padding + height + lineLength);
                gc.setStroke(Color.GAINSBORO);
                if (i > 0) {
                    drawLine(gc, padding + i * timeUnitWidth, padding + height - lineLength / 2, padding + i * timeUnitWidth, padding);
                }
            }
        }

        for (int i = 0; i < unorderedTasks.size(); i++) {
            Paint color = getRandomStyle();
            gc.setFill(color);
            boolean isFinishedInPeriod = false;
            boolean isStartedInPeriod = false;
            int counter = 0;
            for (int j = 0; j < scheduledTaskList.size(); j++) {
                if (padding + j * timeUnitWidth <= padding + width - 30) {
                    if (unorderedTasks.get(i).equals(scheduledTaskList.get(j))) {
                        isStartedInPeriod = true;
                        isFinishedInPeriod = false;
                        counter++;
                        if (counter >= scheduledTaskList.get(j).getExecutionTime()) {
                            isFinishedInPeriod = true;
                            counter = 0;
                            isStartedInPeriod = false;
                        }
                        drawRectangle(gc, padding + j * timeUnitWidth, (int) (padding + taskHeight * i + taskHeight / 4), timeUnitWidth, (int) (taskHeight / 2));
                    } else if (isStartedInPeriod && !isFinishedInPeriod) {
                        drawRectangle(gc, padding + j * timeUnitWidth, (int) (padding + taskHeight * i + taskHeight / 2.7), timeUnitWidth, (int) (taskHeight / 4));
                    }

                    if (j != 0 && j % unorderedTasks.get(i).getPeriod() == 0) {
                        gc.setStroke(Color.BLACK);
                        drawLine(gc, padding + j * timeUnitWidth, (int) (padding + taskHeight * i), padding + j * timeUnitWidth, (int) (padding + taskHeight * i + taskHeight));
                    }
                }
            }
        }
    }

    void drawArrowRight(GraphicsContext gc, int x1, int y1, int x2, int y2) {
        drawLine(gc, x1, y1, x2, y2);
        gc.setFill(Color.BLACK);
        gc.fillPolygon(new double[]{x2, x2 + 12, x2, x2}, new double[]{y2 - 8, y2, y2 + 8, y2 - 8}, 4);
    }

    void drawArrowUp(GraphicsContext gc, int x1, int y1, int x2, int y2) {
        drawLine(gc, x1, y1, x2, y2);
        gc.setFill(Color.BLACK);
        gc.fillPolygon(new double[]{x2 - 8, x2, x2 + 8, x2 - 8}, new double[]{y2, y2 - 12, y2, y2}, 4);
    }

    void drawRectangle(GraphicsContext gc, int x, int y, int w, int h) {
        gc.setGlobalAlpha(0.7);
        gc.fillRect(x, y, w, h);
    }

    void writeTextOnCanvas(GraphicsContext gc, String text, int x, int y) {
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(text, x, y);
    }

    void drawLine(GraphicsContext gc, int x1, int y1, int x2, int y2) {
        gc.setLineWidth(2);
        gc.strokeLine(x1, y1, x2, y2);
    }
}
