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

    public void showWarningLabel(boolean trueorfalse) {
        String labelNoFields = "Je hebt niet alles ingevuld";
        warningLabel.setText(labelNoFields);
        if (trueorfalse) {
            warningLabel.setVisible(true);
        } else {
            warningLabel.setVisible(false);
        }
    }

    public void showSaved(String insert) {
        String newAlert = "Nieuw: " + insert + "  is toegevoegd";
        warningLabel.setText(newAlert);
        warningLabel.setVisible(true);
    }

    public void showUpdated(String insert) {
        String updateAlert = insert + " is gewijzigd";
        warningLabel.setText(updateAlert);
        warningLabel.setVisible(true);

    }
    public void showEmpty(String type, String insert) {
        String updateAlert = "Deze "+ type + " heeft geen " + insert;
        warningLabel.setText(updateAlert);
        warningLabel.setVisible(true);

    }
    // Melding tonen en tekst rood kleuren wanneer veld niet is ingevuld

    public void checkAndChangeLabelColor(boolean emptyfields, Label label) {
        if (emptyfields) {
            label.setTextFill(Color.RED);
        } else {
            label.setTextFill(Color.BLACK);
        }
    }

    // Melding tonen en tekst rood kleuren wanneer geen level is gekozen
    public void isCorrectInputComboBox(String insert) {
        if (insert == null) {
            comboLabel.setTextFill(Color.RED);
        } else {
            comboLabel.setTextFill(Color.BLACK);
        }
    }

    public void setEmptyChoice(String insert, boolean trueorfalse) {
        String choice = "Je moet eerst een " + insert + " kiezen";
        warningLabel.setText(choice);
        if (trueorfalse) {
            warningLabel.setVisible(true);
        } else warningLabel.setVisible(false);
    }
    //Waarschuwing in pop-up scherm weergeven voor verwijderen object

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

    public void showSame(boolean trueorfalse, String insert) {
        String same = "Deze "+insert+"naam bestaat al";
        warningLabel.setText(same);
        if (trueorfalse) {
            warningLabel.setVisible(true);
        } else warningLabel.setVisible(false);
    }
}

