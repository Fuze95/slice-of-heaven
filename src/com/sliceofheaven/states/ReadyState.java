package com.sliceofheaven.states;
import com.sliceofheaven.models.Order;

//Represents the ready state of an order after cooking is complete
public class ReadyState implements OrderState {
    @Override
    public void handle(Order order) {
        order.setState(new DeliveryState());
    }

    @Override
    public String getStatusMessage() {
        return "Order is ready for delivery/pickup";
    }

    @Override
    public OrderState previewNextState() {
        return new DeliveryState();
    }
}