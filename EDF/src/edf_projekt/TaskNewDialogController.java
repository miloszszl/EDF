/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edf_projekt;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author milosz
 */
public class TaskNewDialogController {

    private Stage dialogStage;
    private Task newTask;
    private boolean isOkClicked = false;

    @FXML
    private Label idLabel;
    @FXML
    private TextField executionTimeField;
    @FXML
    private TextField periodField;

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
        isOkClicked = false;
        dialogStage.close();
    }

    /**
     * Sets the person to be edited in the dialog.
     *
     * @param newTask
     */
    public void setTask(Task newTask) {
        this.newTask = newTask;

        idLabel.setText(newTask.getId());
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            newTask.setExecutionTime(Integer.parseInt(executionTimeField.getText()));
            newTask.setPeriod(Integer.parseInt(periodField.getText()));
            isOkClicked = true;
            dialogStage.close();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        int et = -1, p = -1;
        boolean flag = true;
        
        try {
            et = Integer.parseInt(executionTimeField.getText());
        } catch (NumberFormatException | NullPointerException e) {
            errorMessage += "Niepoprawny czas wykonania zadania!\n";
            flag = false;
        }

        try {
            p = Integer.parseInt(periodField.getText());
        } catch (NumberFormatException | NullPointerException e) {
            errorMessage += "Niepoprawny okres wykonania zadania!\n";
            flag = false;
        }
        
        if (flag && (et < 0 || p < 0 || et > p)) {
            errorMessage += "Niepoprawny dane!\n";
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

    public boolean isOkClicked() {
        return isOkClicked;
    }
}
