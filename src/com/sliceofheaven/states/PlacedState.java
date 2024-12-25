package com.sliceofheaven.states;
import com.sliceofheaven.models.Order;

//Represents the initial state of an order after it has been placed
public class PlacedState implements OrderState {
    @Override
    public void handle(Order order) {
        order.setState(new PreparingState());
    }
    
    @Override
    public String getStatusMessage() {
        return "Order has been placed";
    }
    
    @Override
    public OrderState previewNextState() {
        return new PreparingState();
    }
}