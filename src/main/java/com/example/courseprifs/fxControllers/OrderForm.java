package com.example.courseprifs.fxControllers;

import com.example.courseprifs.hibernateControl.CustomHibernate;
import com.example.courseprifs.hibernateControl.GenericHibernate;
import com.example.courseprifs.model.*;
import com.example.courseprifs.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderForm implements Initializable {

    @FXML
    TextField orderNameField;
    @FXML
    ComboBox<BasicUser> buyerComboBox;
    @FXML
    ComboBox<Restaurant> restaurantComboBox;
    @FXML
    ListView<Cuisine> availableCuisinesListView;
    @FXML
    ListView<Cuisine> selectedCuisinesListView;
    @FXML
    ComboBox<OrderStatus> orderStatusComboBox;
    @FXML
    TextField totalPriceField;
    @FXML
    CheckBox autoCalculateCheckBox;
    @FXML
    Button createOrderButton;
    @FXML
    Button cancelButton;
    @FXML
    Button addCuisineButton;
    @FXML
    Button removeCuisineButton;

    private List<Cuisine> selectedCuisines = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadBuyers();
        loadRestaurants();
        loadOrderStatuses();

        autoCalculateCheckBox.setOnAction(event -> {
            if (autoCalculateCheckBox.isSelected()) {
                totalPriceField.setEditable(false);
                calculateTotalPrice();
            } else {
                totalPriceField.setEditable(true);
            }
        });
    }

    private void loadBuyers() {
        List<BasicUser> buyers = GenericHibernate.getAll(BasicUser.class);
        if (buyers != null && !buyers.isEmpty()) {
            buyerComboBox.getItems().addAll(buyers);
        }
    }

    private void loadRestaurants() {
        List<Restaurant> restaurants = GenericHibernate.getAll(Restaurant.class);
        if (restaurants != null && !restaurants.isEmpty()) {
            restaurantComboBox.getItems().addAll(restaurants);
        }
    }

    private void loadOrderStatuses() {
        orderStatusComboBox.getItems().addAll(OrderStatus.values());
        orderStatusComboBox.getSelectionModel().selectFirst();
    }

    public void loadCuisines() {
        Restaurant selectedRestaurant = restaurantComboBox.getSelectionModel().getSelectedItem();
        if (selectedRestaurant != null) {
            List<Cuisine> cuisines = CustomHibernate.getRestaurantCuisine(selectedRestaurant);
            availableCuisinesListView.getItems().clear();
            if (cuisines != null && !cuisines.isEmpty()) {
                availableCuisinesListView.getItems().addAll(cuisines);
            }
        }
    }

    public void addCuisineToOrder() {
        Cuisine selectedCuisine = availableCuisinesListView.getSelectionModel().getSelectedItem();
        if (selectedCuisine != null) {
            if (!selectedCuisines.contains(selectedCuisine)) {
                selectedCuisines.add(selectedCuisine);
                selectedCuisinesListView.getItems().add(selectedCuisine);
                if (autoCalculateCheckBox.isSelected()) {
                    calculateTotalPrice();
                }
            } else {
                FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Cuisine already added", "This cuisine is already in your order.");
            }
        } else {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Selection Error", "No cuisine selected", "Please select a cuisine to add.");
        }
    }

    public void removeCuisineFromOrder() {
        Cuisine selectedCuisine = selectedCuisinesListView.getSelectionModel().getSelectedItem();
        if (selectedCuisine != null) {
            selectedCuisines.remove(selectedCuisine);
            selectedCuisinesListView.getItems().remove(selectedCuisine);
            if (autoCalculateCheckBox.isSelected()) {
                calculateTotalPrice();
            }
        } else {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Selection Error", "No cuisine selected", "Please select a cuisine to remove.");
        }
    }

    private void calculateTotalPrice() {
        double total = 0.0;
        for (Cuisine cuisine : selectedCuisines) {
            if (cuisine.getPrice() != null) {
                total += cuisine.getPrice();
            }
        }
        totalPriceField.setText(String.format("%.2f", total));
    }

    public void createOrder() {
        String orderName = orderNameField.getText();
        BasicUser buyer = buyerComboBox.getSelectionModel().getSelectedItem();
        Restaurant restaurant = restaurantComboBox.getSelectionModel().getSelectedItem();
        OrderStatus orderStatus = orderStatusComboBox.getSelectionModel().getSelectedItem();
        String priceStr = totalPriceField.getText();

        if (orderName.isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Input Error", "Order name is required.", "Please enter an order name.");
            return;
        }

        if (buyer == null) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Input Error", "Buyer is required.", "Please select a buyer.");
            return;
        }

        if (restaurant == null) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Input Error", "Restaurant is required.", "Please select a restaurant.");
            return;
        }

        if (selectedCuisines.isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Input Error", "Cuisines are required.", "Please add at least one cuisine to the order.");
            return;
        }

        if (orderStatus == null) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Input Error", "Order status is required.", "Please select an order status.");
            return;
        }

        if (priceStr.isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Input Error", "Price is required.", "Please enter a price or enable auto-calculate.");
            return;
        }

        if (!isNumeric(priceStr)) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Input Error", "Price must be a numeric value.", "Input Error");
            return;
        }

        double price = Double.parseDouble(priceStr);

        FoodOrder newOrder = new FoodOrder();
        newOrder.setName(orderName);
        newOrder.setBuyer(buyer);
        newOrder.setRestaurant(restaurant);
        newOrder.setCuisineList(new ArrayList<>(selectedCuisines));
        newOrder.setOrderStatus(orderStatus);
        newOrder.setPrice(price);

        GenericHibernate.create(newOrder);

        FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Order created successfully.", "Your order has been placed!");
        closeWindow();
    }

    public void cancelOrder() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) createOrderButton.getScene().getWindow();
        stage.close();
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

