package com.example.courseprifs.fxControllers;

import com.example.courseprifs.HelloApplication;
import com.example.courseprifs.hibernateControl.GenericHibernate;
import com.example.courseprifs.model.*;
import com.example.courseprifs.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserForm implements Initializable {

    @FXML RadioButton userRadio;
    @FXML RadioButton restaurantRadio;
    @FXML RadioButton clientRadio;
    @FXML RadioButton driverRadio;
    @FXML ToggleGroup Select;
    @FXML TextField addressField;
    @FXML TextField usernameField;
    @FXML TextField nameField;
    @FXML TextField surnameField;
    @FXML TextField phoneField;
    @FXML PasswordField passwordField;

    @FXML ComboBox<VehicleType> comboField;

    @FXML TextField licenseField;
    @FXML DatePicker birthDateField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disableFields();
        comboField.getItems().addAll(VehicleType.values());
    }

    public void disableFields() {
        if (userRadio.isSelected()) {
            addressField.setDisable(true);
            licenseField.setDisable(true);
            birthDateField.setDisable(true);
            comboField.setDisable(true);
        } else if (restaurantRadio.isSelected()) {
            addressField.setDisable(false);
            licenseField.setDisable(true);
            birthDateField.setDisable(true);
            comboField.setDisable(true);
        } else if (clientRadio.isSelected()) {
            addressField.setDisable(false);
            licenseField.setDisable(true);
            birthDateField.setDisable(true);
            comboField.setDisable(true);
        } else if (driverRadio.isSelected()) {
            licenseField.setDisable(false);
            birthDateField.setDisable(false);
            comboField.setDisable(false);
            addressField.setDisable(true);
        }
    }

    public void createNewUser() throws IOException {
        if (userRadio.isSelected()) {
            User user = new User(usernameField.getText(), passwordField.getText(), nameField.getText(), surnameField.getText(), phoneField.getText());
            create(user);
            return;
        } else if (restaurantRadio.isSelected()) {
            Restaurant restaurant = new Restaurant(usernameField.getText(), passwordField.getText(), nameField.getText(), surnameField.getText(), phoneField.getText(), addressField.getText());
            create(restaurant);
            return;
        } else if (clientRadio.isSelected()) {
            BasicUser client = new BasicUser(usernameField.getText(), passwordField.getText(), nameField.getText(), surnameField.getText(), phoneField.getText(), addressField.getText());
            create(client);
            return;
        } else if (driverRadio.isSelected()) {
            Driver driver = new Driver(usernameField.getText(), passwordField.getText(), nameField.getText(), surnameField.getText(), phoneField.getText(), addressField.getText(), licenseField.getText(), birthDateField.getValue(), comboField.getValue());
            create(driver);
            return;
        }
        FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Something went wrong during login", "Not implemented!");
    }

    private void returnToLogin() throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-form.fxml"));
            Parent parent = fxmlLoader.load();

            showStage(parent);
    }

    private void showStage(Parent parent) {
        Scene scene = new Scene(parent);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    private void create(Object object) throws IOException {
        GenericHibernate.create(object);
        FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Created", "Welcome aboard to the Wolter family!");
        returnToLogin();
    }
}
