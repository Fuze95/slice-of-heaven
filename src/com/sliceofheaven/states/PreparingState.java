package com.sliceofheaven.states;
import com.sliceofheaven.models.Order;

//Represents the preparation state of an order in the kitchen
public class PreparingState implements OrderState {
    @Override
    public void handle(Order order) {
        order.setState(new CookingState());
    }
    
    @Override
    public String getStatusMessage() {
        return "Preparing your order";
    }
    
    @Override
    public OrderState previewNextState() {
        return new CookingState();
    }
}