package models;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private List<String> ingredients = new ArrayList<>();

    public Order() {

    }

    public Order withIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

}
