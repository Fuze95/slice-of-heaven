package com.sliceofheaven.states;
import com.sliceofheaven.models.Order;

public class DeliveryState implements OrderState {
    @Override
    public void handle(Order order) {
        order.setState(new CompletedState());
    }

    @Override
    public String getStatusMessage() {
        return "Your order is on the way";
    }

    @Override
    public OrderState previewNextState() {
        return new CompletedState();
    }
}