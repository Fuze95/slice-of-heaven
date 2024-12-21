package com.sliceofheaven.models;

import java.util.*;

// Builder Pattern - Pizza
public class Pizza {
    private String size;
    private String crustType;
    private List<String> toppings;
    private String sauce;
    private boolean extraCheese;
    private double price;

    private Pizza(PizzaBuilder builder) {
        this.size = builder.size;
        this.crustType = builder.crustType;
        this.toppings = builder.toppings;
        this.sauce = builder.sauce;
        this.extraCheese = builder.extraCheese;
        calculatePrice();
    }

    private void calculatePrice() {
        // Base price based on size
        switch (size.toLowerCase()) {
            case "personal": price = 1000.0; break;
            case "medium": price = 1800.0; break;
            case "large": price = 2400.0; break;
            default: price = 1000.0;
        }
        
        price += toppings.size() * 150.0;
        if (extraCheese) price += 200.0;
    }

    public double getPrice() { return price; }

    public static class PizzaBuilder {
        private String size;
        private String crustType;
        private List<String> toppings;
        private String sauce;
        private boolean extraCheese;

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

        public Pizza build() {
            return new Pizza(this);
        }
    }
}