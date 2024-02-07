package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.Optional;


public class WarningAlertController {
    public final int MAXLENGTH = 45;
    @FXML
    Label warningLabel;
    @FXML
    Label comboLabel;
    // Warning to call if not every field is completed
    public void showWarningLabel(boolean trueorfalse) {
        String labelNoFields = "Je hebt niet alles ingevuld";
        warningLabel.setText(labelNoFields);
        if (trueorfalse) {
            warningLabel.setVisible(true);
        } else {
            warningLabel.setVisible(false);
        }
    }
    // Warning to show an object is saved
    public void showSaved(String insert) {
        String newAlert = "Nieuw: " + insert + "  is toegevoegd";
        warningLabel.setText(newAlert);
        warningLabel.setVisible(true);
    }
    // Warning to show an object is changed
    public void showUpdated(String insert) {
        String updateAlert = insert + " is gewijzigd";
        warningLabel.setText(updateAlert);
        warningLabel.setVisible(true);

    }
    // Warning to show an object is emtpy
    public void showEmpty(String type, String insert) {
        String updateAlert = "Deze "+ type + " heeft geen " + insert;
        warningLabel.setText(updateAlert);
        warningLabel.setVisible(true);

    }
    // Method to color text red if fields are not filled

    public void checkAndChangeLabelColor(boolean emptyfields, Label label) {
        if (emptyfields) {
            label.setTextFill(Color.RED);
        } else {
            label.setTextFill(Color.BLACK);
        }
    }

    // Method to color text red when a comboBox is not chosen
    public void isCorrectInputComboBox(String insert) {
        if (insert == null) {
            comboLabel.setTextFill(Color.RED);
        } else {
            comboLabel.setTextFill(Color.BLACK);
        }
    }
    // Warning to show if no choice is made but button is activated
    public void setEmptyChoice(String insert, boolean trueorfalse) {
        String choice = "Je moet eerst een " + insert + " kiezen";
        warningLabel.setText(choice);
        if (trueorfalse) {
            warningLabel.setVisible(true);
        } else warningLabel.setVisible(false);
    }
    //Alert to show an object is being deleted

    public boolean confirmDeletion(String insert, String object) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Verwijder " + object);
        alert.setHeaderText(object + " " + insert + " wordt verwijderd");
        alert.setContentText("Weet je het zeker?");
        ButtonType buttonCancel = new ButtonType("Annuleer");
        ButtonType buttonContinue = new ButtonType("Verwijder");
        alert.getButtonTypes().setAll(buttonCancel, buttonContinue);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonContinue;
    }
    // Warning to show a name is chosen which already exist in the database
    public void showSame(boolean trueorfalse, String insert) {
        String same = "Deze "+insert+"naam bestaat al";
        warningLabel.setText(same);
        if (trueorfalse) {
            warningLabel.setVisible(true);
        } else warningLabel.setVisible(false);
    }
}

