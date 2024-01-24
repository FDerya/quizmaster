package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.Optional;


public class WarningAlertController {
    public final int MAXLENGTH = 45;
    public final int QUESTIONMAXLENGTH = 1000;
    public final int USERNAMEMAXLENGTH = 10;
    @FXML
    Label warningLabelNoFields;
    @FXML
    Label saveNewLabel;
    @FXML
    Label saveUpdateLabel;
    @FXML
    Label levelLabel;
    @FXML
    Label choiceLabel;
    @FXML
    Label sameNameLabel;

    public void showWarningLabel(boolean trueorfalse) {
        String labelNoFields = "Je hebt niet alles ingevuld";
        warningLabelNoFields.setText(labelNoFields);
        if (trueorfalse) {
            warningLabelNoFields.setVisible(true);
        } else {
            warningLabelNoFields.setVisible(false);
        }
    }

    public void showSaved(String insert) {
        String newAlert = "Nieuw: " + insert + "  is toegevoegd";
        warningLabelNoFields.setText(newAlert);
        warningLabelNoFields.setVisible(true);
    }

    public void showUpdated(String insert) {
        String updateAlert = insert + " is gewijzigd";
        warningLabelNoFields.setText(updateAlert);
        warningLabelNoFields.setVisible(true);

    }

    public void checkAndChangeLabelColor(boolean emptyfields, Label label) {
        if (emptyfields) {
            label.setTextFill(Color.RED);
        } else {
            label.setTextFill(Color.BLACK);
        }
    }

    // Melding tonen en tekst rood kleuren wanneer geen level is gekozen
    public void isCorrectInputLevel(String level) {
        if (level == null) {
            levelLabel.setTextFill(Color.RED);
        } else {
            levelLabel.setTextFill(Color.BLACK);
        }
    }

    public void setEmptyChoice(String insert, boolean trueorfalse) {
        String choice = "Je moet eerst een " + insert + " kiezen.";
        warningLabelNoFields.setText(choice);
        if (trueorfalse) {
            warningLabelNoFields.setVisible(true);
        } else warningLabelNoFields.setVisible(false);
    }
    //Waarschuwing in pop-up scherm weergeven voor verwijderen object

    public boolean confirmDeletion(String insert, String object) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Verwijder " + object);
        alert.setHeaderText(object + " " + insert + " wordt verwijderd.");
        alert.setContentText("Weet je het zeker?");
        ButtonType buttonCancel = new ButtonType("Annuleer");
        ButtonType buttonContinue = new ButtonType("Verwijder");
        alert.getButtonTypes().setAll(buttonCancel, buttonContinue);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonContinue;
    }

    public void showSame(boolean trueorfalse) {
        String same = "Deze naam bestaat al.";
        warningLabelNoFields.setText(same);
        if (trueorfalse) {
            warningLabelNoFields.setVisible(true);
        } else warningLabelNoFields.setVisible(false);
    }
}
