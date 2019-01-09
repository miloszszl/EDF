/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edf_projekt;

import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author milosz
 */
public class GenerateDialogController {

    private Stage dialogStage;
    private Task[] tasks = null;

    public Task[] getTasks() {
        return tasks;
    }

    @FXML
    private TextField numOfTasksField;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            tasksGenerator(Integer.parseInt(numOfTasksField.getText()));
            dialogStage.close();
        }
    }

    private void tasksGenerator(int numOfTasks) {
        this.tasks = new Task[numOfTasks];
        int executionTime, period;
        Random randomGenerator = new Random();
        for (int i = 0; i < numOfTasks; i++) {
            executionTime = randomGenerator.nextInt(4) + 1;
            period = -1;
            while (period < executionTime + executionTime/1.8) {
                period = randomGenerator.nextInt(12) + 1;
            }

            tasks[i] = new Task(executionTime, period);
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        int numOfTasks;

        try {
            numOfTasks = Integer.parseInt(numOfTasksField.getText());
            if (numOfTasks < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException | NullPointerException e) {
            errorMessage += "Niepoprawna liczba zadań!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Niepoprawne dane");
            alert.setHeaderText("Prosze wprowadzić poprawne dane.");
            alert.setContentText(errorMessage);

            alert.showAndWait();
            return false;
        }
    }
}
