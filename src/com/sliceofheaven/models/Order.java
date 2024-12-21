package com.sliceofheaven.models;

import com.sliceofheaven.payment.PaymentStrategy;
import com.sliceofheaven.states.OrderState;
import com.sliceofheaven.states.PlacedState;
import com.sliceofheaven.states.CancelledState;
import java.util.*;

public class Order implements OrderSubject {
    private String orderId;
    private Customer customer;
    private List<Pizza> pizzas;
    private boolean isDelivery;
    private OrderState currentState;
    private List<OrderObserver> observers;
    private PaymentStrategy paymentStrategy;
    private double totalAmount;

    public Order(Customer customer, boolean isDelivery) {
        this.orderId = UUID.randomUUID().toString().substring(0, 8);
        this.customer = customer;
        this.pizzas = new ArrayList<>();
        this.isDelivery = isDelivery;
        this.currentState = new PlacedState();
        this.observers = new ArrayList<>();
        attach(customer);
    }

    public void addPizza(Pizza pizza) {
        pizzas.add(pizza);
        calculateTotal();
    }

    private void calculateTotal() {
        totalAmount = pizzas.stream().mapToDouble(Pizza::getPrice).sum();
        if (isDelivery) {
            totalAmount += 200.0; // Delivery fee
        }
    }

    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }

    public boolean processPayment() {
        if (paymentStrategy == null) {
            return false;
        }
        
        boolean paymentSuccess = paymentStrategy.pay(totalAmount);
        if (paymentSuccess && totalAmount >= 1000) {
            customer.addLoyaltyPoints(10);
        }
        return paymentSuccess;
    }

    public void nextState() {
        currentState.handle(this);
        notifyObservers(currentState.getStatusMessage());
    }

    public void setState(OrderState state) {
        this.currentState = state;
    }

    public void cancelOrder() {
        this.currentState = new CancelledState();
        notifyObservers("Your order has been cancelled");
    }

    @Override
    public void attach(OrderObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(OrderObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (OrderObserver observer : observers) {
            observer.update(message);
        }
    }

    public void setDelivery(boolean isDelivery) {
        this.isDelivery = isDelivery;
    }

    public String getOrderId() { return orderId; }
    public double getTotalAmount() { return totalAmount; }
    public OrderState getCurrentState() { return currentState; }
}