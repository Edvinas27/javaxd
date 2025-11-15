package com.example.courseprifs.fxControllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class CuisineTableParameters {
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleDoubleProperty price = new SimpleDoubleProperty();
    private final SimpleStringProperty ingredients = new SimpleStringProperty();
    private final SimpleBooleanProperty spicy = new SimpleBooleanProperty();
    private final SimpleBooleanProperty vegan = new SimpleBooleanProperty();

    public String getIngredients() {
        return ingredients.get();
    }

    public SimpleStringProperty ingredientsProperty() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients.set(ingredients);
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public boolean isSpicy() {
        return spicy.get();
    }

    public SimpleBooleanProperty spicyProperty() {
        return spicy;
    }

    public void setSpicy(boolean spicy) {
        this.spicy.set(spicy);
    }

    public boolean isVegan() {
        return vegan.get();
    }

    public SimpleBooleanProperty veganProperty() {
        return vegan;
    }

    public void setVegan(boolean vegan) {
        this.vegan.set(vegan);
    }
}
