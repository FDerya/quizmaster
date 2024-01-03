package controller;
// Bianca Duijvesteijn, studentnummer 500940421
// Deze controller is verantwoordelijk voor het beheer van groepen in een JavaFX-applicatie.
// Het biedt functionaliteiten zoals het maken, bijwerken en verwijderen van groepen, evenals het weergeven
// relevante informatie voor de gebruiker.

import database.mysql.GroupDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Group;
import view.Main;

import java.util.List;
import java.util.Optional;

public class ManageGroupsController {
    @FXML
    private ListView<Group> groupList;
    @FXML
    private GroupDAO groupDAO;
    @FXML
    private final TextField warningTextField = new TextField();
    @FXML
    private Label courseNameLabel;
    @FXML
    private Label groupCountLabel;

    // Initialiseert de controller en creëert een GroupDAO met databasetoegang
    public ManageGroupsController() {
        this.groupDAO = new GroupDAO(Main.getDBaccess());
    }

    // Initialiseert UI-componenten en roept configuratie methoden aan
    @FXML
    public void initialize() {
        setup();
        configureGroupListCellFactory();
        configureLabels();
        configureGroupListListener();
    }

    // Haalt de lijst met groepen op en configureert de ListView
    @FXML
    public void setup() {
        List<Group> groups = groupDAO.getAll();
        groupList.getSelectionModel().clearSelection();
        groupList.getItems().setAll(groups);
        courseNameLabel.setVisible(false);
        groupCountLabel.setVisible(false);
    }

    // Configureert de weergave van cellen in de ListView voor het weergeven van groepen
    private void configureGroupListCellFactory() {
        groupList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Group item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getGroupName());
                }
            }
        });
    }

    // Initialiseert tekstlabels met standaardtekst
    private void configureLabels() {
        courseNameLabel.setText("Cursus: ");
        groupCountLabel.setText("Aantal groepen dat deze cursus volgt: ");
    }

    // Voegt een luisteraar toe aan de ListView voor het verwerken van geselecteerde groepen
    private void configureGroupListListener() {
        groupList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleGroupSelection();
            }
        });
    }

    // Verwerkt de selectie van een groep en werkt de labels bij
    private void handleGroupSelection() {
        Group selectedGroup = groupList.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            updateLabels(selectedGroup);
        } else {
            handleNoGroupSelected();
        }
        groupList.refresh();
    }

    // Werkt de labels bij op basis van de geselecteerde groep
    private void updateLabels(Group selectedGroup) {
        int idGroup = selectedGroup.getIdGroup();
        String courseName = groupDAO.getCourseNameForGroup(idGroup);
        if (courseName != null) {
            int groupCount = groupDAO.getGroupCountForSameCourse(selectedGroup);
            if (groupCount != -1) {
                showGroupInfo(courseName, groupCount);
            } else {
                showErrorMessage("Er zijn geen groepen gevonden die dezelfde cursus volgen.");
            }
        } else {
            showNoCourseInfo();
        }
    }

    // Toont informatie over de geselecteerde groep
    private void showGroupInfo(String courseName, int groupCount) {
        courseNameLabel.setText("Cursus: " + courseName);
        groupCountLabel.setText("Aantal groepen dat dezelfde cursus volgt: " + groupCount);
        showLabels();
    }

    // Toont een foutmelding wanneer cursusinformatie ontbreekt voor de geselecteerde groep
    private void showNoCourseInfo() {
        courseNameLabel.setText("Er is geen cursusinformatie beschikbaar voor deze groep.");
        groupCountLabel.setText("");
        clearErrorMessage();
        showLabels();
    }

    // Behandelt de situatie waarin geen groep is geselecteerd
    private void handleNoGroupSelected() {
        showErrorMessage("Geen groep geselecteerd.");
        courseNameLabel.setText("");
        groupCountLabel.setText("");
        showLabels();
    }

    // Maakt de labels zichtbaar
    private void showLabels() {
        courseNameLabel.setVisible(true);
        groupCountLabel.setVisible(true);
    }

    // Geeft een foutmelding weer in een tekstveld
    private void showErrorMessage(String message) {
        warningTextField.setText(message);
        warningTextField.setVisible(true);
    }

    // Wist de foutmelding in het tekstveld
    private void clearErrorMessage() {
        warningTextField.setText("");
        warningTextField.setVisible(false);
    }

    // Verwerkt de klik gebeurtenis op de menuknop en navigeert terug naar de welkomsscène
    @FXML
    private void doMenu() {
        try {
            Main.getSceneManager().showWelcomeScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Verwerkt de creatie van een nieuwe groep en opent de bijbehorende gebruikersinterface
    @FXML
    private void doCreateGroup() {
        Main.getSceneManager().showCreateUpdateGroupScene(null);
    }

    // Verwerkt de update van een bestaande groep en opent de gebruikersinterface om studenten
    // aan groepen toe te wijzen
    @FXML
    private void doUpdateGroup() {
        Main.getSceneManager().showCreateUpdateGroupScene(null);
    }

    // Verwerkt de verwijdering van een bestaande groep, waarbij de gebruikersinterface
    // en de database worden bijgewerkt
    @FXML
    private void doDeleteGroup() {
        Group selectedGroup = groupList.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            showWarning();
            return;
        }
        if (confirmDeletion(selectedGroup)) {
            deleteGroup(selectedGroup);
            removeFromGroupList(selectedGroup);
        }
    }

    // Geeft een waarschuwing weer in een dialoogvenster
    private void showWarning() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Waarschuwing");
            alert.setHeaderText(null);
            alert.setContentText("Selecteer een groep.");
            alert.showAndWait();
        });
    }

    // Vraagt om bevestiging van de gebruiker voor het verwijderen van een groep
    private boolean confirmDeletion(Group selectedGroup) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Verwijder groep");
        alert.setHeaderText("Groep " + selectedGroup.getGroupName() + " wordt verwijderd.");
        alert.setContentText("Weet je het zeker?");
        ButtonType buttonTypeYes = new ButtonType("Ja");
        ButtonType buttonTypeNo = new ButtonType("Nee");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    // Verwijdert een groep uit de database
    private void deleteGroup(Group selectedGroup) {
        groupDAO.deleteGroup(selectedGroup);
    }

    // Verwijdert een groep uit de lijst
    private void removeFromGroupList(Group selectedGroup) {
        groupList.getItems().remove(selectedGroup);
    }
}