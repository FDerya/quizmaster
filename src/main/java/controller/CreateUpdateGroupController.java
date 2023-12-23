package controller;
// Bianca Duijvesteijn, studentnummer 500940421
//

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import model.Group;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateUpdateGroupController implements Initializable {
    @FXML
    private TextField groupNameField;

    public void setup(Group group) {}

    public void doMenu() {}

    public void doCreateUpdateGroup() {}


    public void initialize(URL location, ResourceBundle resources) {
       groupNameField.setText("DefaultGroupName");
    }
}
