package com.sliceofheaven.states;

import com.sliceofheaven.models.Order;

public class PlacedState implements OrderState {
    @Override
    public void handle(Order order) {
        order.setState(new PreparingState());
    }

    @Override
    public String getStatusMessage() {
        return "Order has been placed";
    }
}