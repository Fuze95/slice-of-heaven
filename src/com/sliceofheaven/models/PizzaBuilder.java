package com.sliceofheaven.models;
import java.util.*;

public class PizzaBuilder {
    private String size;
    private String crustType;
    private List<String> toppings;
    private String sauce;
    private boolean extraCheese;
    private String specialName;

    public PizzaBuilder() {
        this.toppings = new ArrayList<>();
    }

    public PizzaBuilder setSize(String size) {
        this.size = size;
        return this;
    }

    public PizzaBuilder setCrust(String type) {
        this.crustType = type;
        return this;
    }

    public PizzaBuilder addTopping(String topping) {
        this.toppings.add(topping);
        return this;
    }

    public PizzaBuilder setSauce(String sauce) {
        this.sauce = sauce;
        return this;
    }

    public PizzaBuilder setExtraCheese(boolean extra) {
        this.extraCheese = extra;
        return this;
    }

    public PizzaBuilder setSpecialName(String specialName) {
        this.specialName = specialName;
        return this;
    }

    public Pizza build() {
        return new Pizza(this);
    }

    String getSize() { return size; }
    String getCrust() { return crustType; }
    List<String> getToppings() { return new ArrayList<>(toppings); }
    String getSauce() { return sauce; }
    boolean hasExtraCheese() { return extraCheese; }
    String getSpecialName() { return specialName; }
}