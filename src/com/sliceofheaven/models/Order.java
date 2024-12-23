package com.sliceofheaven.models;

import com.sliceofheaven.payment.PaymentStrategy;
import com.sliceofheaven.states.OrderState;
import com.sliceofheaven.states.PlacedState;
import com.sliceofheaven.states.CancelledState;
import com.sliceofheaven.Admin;
import com.sliceofheaven.observers.OrderObserver;
import com.sliceofheaven.observers.OrderSubject;
import java.util.*;

public class Order implements OrderSubject {
    private String orderId;
    private Customer customer;
    private List<Pizza> pizzas;
    private String deliveryTown;
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
        this.deliveryTown = null;
        this.currentState = new PlacedState();
        this.observers = new ArrayList<>();
        attach(customer);
        this.currentState = new PlacedState();
    }

    public void addPizza(Pizza pizza) {
        pizzas.add(pizza);
        calculateTotal();
    }

    private void calculateTotal() {
        totalAmount = pizzas.stream().mapToDouble(Pizza::getPrice).sum();
        if (isDelivery && deliveryTown != null) {
            Admin admin = Admin.getInstance();
            totalAmount += Admin.getInstance().getDeliveryFee(deliveryTown);
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
            int points = (int) (totalAmount / 1000.0 * 10);
            customer.addLoyaltyPoints(points);
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

    public void setDeliveryTown(String town) {
        this.deliveryTown = town;
    }

    public String getOrderId() { return orderId; }
    public double getTotalAmount() { return totalAmount; }
    public OrderState getCurrentState() { return currentState; }
    public Customer getCustomer() { return customer; }
    public List<Pizza> getPizzas() { return pizzas; }
    public boolean isDelivery() { return isDelivery; }
    public String getDeliveryTown() { return deliveryTown; }
}