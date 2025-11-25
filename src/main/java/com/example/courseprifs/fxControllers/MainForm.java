package com.example.courseprifs.fxControllers;

import com.example.courseprifs.HelloApplication;
import com.example.courseprifs.model.*;
import com.example.courseprifs.utils.FxUtils;
import com.example.courseprifs.hibernateControl.CustomHibernate;
import com.example.courseprifs.hibernateControl.GenericHibernate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.query.Order;

import java.io.Console;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainForm implements Initializable {
    @FXML Button deleteUserButton;
    @FXML Button updateUserButton;
    @FXML Button createUserButton;
    @FXML Button deleteCuisineButton;
    @FXML Button createOrderButton;
    @FXML ComboBox<User> ordersClientsCombo;
    @FXML RadioButton spicyRadioFood;
    @FXML RadioButton veganRadioFood;
    @FXML
    Button addCuisineButton;
    @FXML
    TextField addCuisinePrice;
    @FXML
    TextField addCuisineIngredients;
    @FXML
    TextField addCuisineName;
    @FXML
    TableView<CuisineTableParameters> cuisineListTable;
    @FXML
    TableColumn<CuisineTableParameters, String> foodNameCol;
    @FXML
    TableColumn<CuisineTableParameters, Double> foodPriceCol;
    @FXML
    TableColumn<CuisineTableParameters, String> foodIngredientCol;
    @FXML TableColumn<CuisineTableParameters, Boolean> foodVeganCol;
    @FXML TableColumn<CuisineTableParameters, Boolean> foodSpicyCol;
    @FXML
    Tab userTab;
    @FXML
    Tab managementTab;
    @FXML
    Tab foodTab;
    @FXML
    TabPane tabsPane;
    @FXML
    TableColumn<UserTableParameters, String> userTypeCol;
    @FXML
    TableColumn<UserTableParameters, String> idCol;
    @FXML
    TableColumn<UserTableParameters, String> loginCol;
    @FXML
    TableColumn<UserTableParameters, String> passwordCol;
    @FXML
    TableColumn<UserTableParameters, String> nameCol;
    @FXML
    TableColumn<UserTableParameters, String> surnameCol;
    @FXML
    TableColumn<UserTableParameters, String> addressCol;
    @FXML
    TableColumn<UserTableParameters, Void> dummyCol;
    @FXML
    TableView<UserTableParameters> userTable;

    @FXML TableColumn<OrderTableParameters, String> ordersNameCol;
    @FXML TableColumn<OrderTableParameters, Double> ordersPriceCol;
    @FXML TableColumn<OrderTableParameters, OrderStatus> ordersStatusCol;
    @FXML ComboBox<OrderStatus> ordersComboBox;
    @FXML ListView<Cuisine> ordersListView;
    @FXML TableView<OrderTableParameters> orderTableView;


    private final ObservableList<UserTableParameters> data = FXCollections.observableArrayList();
    private final ObservableList<OrderTableParameters> ordersData = FXCollections.observableArrayList();
    private final ObservableList<CuisineTableParameters> foodData = FXCollections.observableArrayList();

    private User currentUser;

    public void setData(User user) {
        this.currentUser = user;
        setUserFormVisibility();
        tabsPane.getSelectionModel().select(userTab);
        reloadTableData();
    }

    private void setUserFormVisibility() {
        if (currentUser instanceof User) {
            tabsPane.getTabs().remove(foodTab);
        }
    }

    public void reloadTableData() {
        data.clear();
        foodData.clear();
        ordersData.clear();
        Tab selected = tabsPane.getSelectionModel().getSelectedItem();
        if (selected == userTab) {
            List<User> userList = GenericHibernate.getAll(User.class);
            for (User u : userList) {
                UserTableParameters utp = new UserTableParameters();
                utp.setId(u.getId());
                utp.setUserType(u.getClass().getSimpleName());
                utp.setSurname(u.getSurname());
                utp.setLogin(u.getLogin());
                utp.setName(u.getName());
                utp.setPassword(u.getPassword());
                if (u instanceof BasicUser) {
                    utp.setAddress(((BasicUser) u).getAddress());
                }
                data.add(utp);
            }
            userTable.setItems(data);
        } else if (selected == foodTab) {
            List<Cuisine> cuisines = CustomHibernate.getRestaurantCuisine((Restaurant) currentUser);
            List<FoodOrder> orders = CustomHibernate.getRestaurantOrders((Restaurant) currentUser);
            if (orders.isEmpty()) {
                return;
            }
            if (cuisines.isEmpty()) {
                return;
            }
            for (FoodOrder o : orders) {
               OrderTableParameters otp = new OrderTableParameters();
                otp.setName(o.getName());
                otp.setPrice(o.getPrice());
                otp.setStatus(o.getOrderStatus().toString());
                ordersData.add(otp);
            }

            for (Cuisine fo : cuisines) {
                CuisineTableParameters ctp = new CuisineTableParameters();
                ctp.setName(fo.getName());
                ctp.setPrice(fo.getPrice());
                ctp.setIngredients(fo.getIngredients());
                ctp.setSpicy(fo.isSpicy());
                ctp.setVegan(fo.isVegan());
                foodData.add(ctp);
            }
            cuisineListTable.setItems(foodData);
            orderTableView.setItems(ordersData);
        } else if (selected == managementTab) {
            List<FoodOrder> orders = CustomHibernate.getRestaurantOrders((Restaurant) currentUser);
            if (orders.isEmpty()) {
                return;
            }
            for (FoodOrder fo : orders) {
                OrderTableParameters otp = new OrderTableParameters();
                otp.setName(fo.getName());
                otp.setPrice(fo.getPrice());
                otp.setStatus(fo.getOrderStatus().toString());
                ordersData.add(otp);
            }
        }
        orderTableView.setItems(ordersData);
    }

    private List<FoodOrder> getFoodOrders() {
        if (currentUser instanceof Restaurant) {
            return CustomHibernate.getRestaurantOrders((Restaurant) currentUser);
        } else {
            return GenericHibernate.getAll(FoodOrder.class);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userTable.setEditable(true);
        setCellValueFactory();
        setCellFactory();
        setupOnEditCommit();

        ordersComboBox.getItems().addAll(OrderStatus.values());

        addCuisineButton.setOnAction(event -> {
            createCuisine();
        });

        ordersComboBox.setOnAction(event -> {
            OrderStatus selectedStatus = ordersComboBox.getSelectionModel().getSelectedItem();
            List<FoodOrder> filteredOrders = CustomHibernate.getOrdersByStatus(selectedStatus);
            ordersData.clear();
            for (FoodOrder fo : filteredOrders) {
                OrderTableParameters otp = new OrderTableParameters();
                otp.setName(fo.getName());
                otp.setPrice(fo.getPrice());
                otp.setStatus(fo.getOrderStatus().toString());
                ordersData.add(otp);
            }
            orderTableView.setItems(ordersData);
        });

        orderTableView.setOnMouseClicked(event -> {
            OrderTableParameters selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
            if (selectedOrder != null) {
                List<Cuisine> cuisines = CustomHibernate.getRestaurantCuisine((Restaurant) currentUser);
                System.out.println("Selected Order: " + selectedOrder.getName() + ", Price: " + selectedOrder.getPrice());
                ordersListView.getItems().clear();
                for (Cuisine c : cuisines) {
                    if (c.getName().equals(selectedOrder.getName())
                            && c.getPrice() == selectedOrder.getPrice()) {
                        System.out.println(c.getName());
                        ordersListView.getItems().add(c);
                    }
                }
            }
        });
    }

    private void setCellValueFactory() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userTypeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        foodNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        foodPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        foodIngredientCol.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
        foodSpicyCol.setCellValueFactory(new PropertyValueFactory<>("spicy"));
        foodVeganCol.setCellValueFactory(new PropertyValueFactory<>("vegan"));

        ordersNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        ordersPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        ordersStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setCellFactory() {
        passwordCol.setCellFactory(TextFieldTableCell.forTableColumn());
        loginCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        surnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        addressCol.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    private void setupOnEditCommit() {
        passwordCol.onEditCommitProperty().set(e -> {
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setPassword(e.getNewValue());
            User user = GenericHibernate.getById(User.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
            user.setPassword(e.getNewValue());
            GenericHibernate.update(user);
        });
        loginCol.onEditCommitProperty().set(e -> {
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setLogin(e.getNewValue());
            User user = GenericHibernate.getById(User.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
            user.setLogin(e.getNewValue());
            GenericHibernate.update(user);
        });
        nameCol.onEditCommitProperty().set(e -> {
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setName(e.getNewValue());
            User user = GenericHibernate.getById(User.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
            user.setName(e.getNewValue());
            GenericHibernate.update(user);
        });
        surnameCol.onEditCommitProperty().set(e -> {
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setSurname(e.getNewValue());
            User user = GenericHibernate.getById(User.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
            user.setSurname(e.getNewValue());
            GenericHibernate.update(user);
        });
        addressCol.onEditCommitProperty().set(e -> {
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setAddress(e.getNewValue());
            BasicUser user = GenericHibernate.getById(BasicUser.class, e.getTableView().getItems().get(e.getTablePosition().getRow()).getId());
            user.setAddress(e.getNewValue());
            GenericHibernate.update(user);
        });
    }

    public void createCuisine() {
        String name = addCuisineName.getText();
        String ingredients = addCuisineIngredients.getText();
        String priceStr = addCuisinePrice.getText();
        boolean isSpicy = spicyRadioFood.isSelected();
        boolean isVegan = veganRadioFood.isSelected();
        if (name.isEmpty() || ingredients.isEmpty() || priceStr.isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Input Error", "All fields must be filled.", "Input Error");
            return;
        }
        if (!isNumeric(priceStr)) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Input Error", "Price must be a numeric value.", "Input Error");
        } else {
            double price = Double.parseDouble(priceStr);
            Cuisine newCuisine = new Cuisine();
            newCuisine.setName(name);
            newCuisine.setIngredients(ingredients);
            newCuisine.setPrice(price);
            newCuisine.setRestaurant((Restaurant) currentUser);
            newCuisine.setSpicy(isSpicy);
            newCuisine.setVegan(isVegan);
            GenericHibernate.create(newCuisine);
            FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Cuisine added successfully.", "Success");
            addCuisineName.clear();
            addCuisineIngredients.clear();
            addCuisinePrice.clear();
            spicyRadioFood.setSelected(false);
            veganRadioFood.setSelected(false);
            reloadTableData();
        }
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void addUser(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-form.fxml"));
        Parent parent = fxmlLoader.load();

        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void updateUser(ActionEvent actionEvent) {
    }

    public void deleteUser() {
        UserTableParameters selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Selection Error", "No user selected.", "Please select a user to delete.");
            return;
        }


        GenericHibernate.delete(User.class, selectedUser.getId());
        FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.", "Success");
        reloadTableData();
    }

    public void deleteCuisine(ActionEvent actionEvent) {
        CuisineTableParameters selectedCuisine = cuisineListTable.getSelectionModel().getSelectedItem();
        if (selectedCuisine == null) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Selection Error", "No cuisine selected.", "Please select a cuisine to delete.");
            return;
        }

        List<Cuisine> cuisines = CustomHibernate.getRestaurantCuisine((Restaurant) currentUser);
        for (Cuisine c : cuisines) {
            if (c.getName().equals(selectedCuisine.getName())
                    && c.getPrice() == selectedCuisine.getPrice()
                    && c.getIngredients().equals(selectedCuisine.getIngredients())
                    && c.isSpicy() == selectedCuisine.isSpicy()
                    && c.isVegan() == selectedCuisine.isVegan()) {
                GenericHibernate.delete(Cuisine.class, c.getId());
                FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Cuisine deleted successfully.", "Success");
                reloadTableData();
                return;
            }
        }

        FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Cuisine not found.", "The selected cuisine could not be found.");
    }

    public void createOrder(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("order-form.fxml"));
        Parent parent = fxmlLoader.load();

        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        reloadTableData();
    }
}
