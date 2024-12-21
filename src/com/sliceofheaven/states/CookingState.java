package com.sliceofheaven.states;

import com.sliceofheaven.models.Order;

public class CookingState implements OrderState {
    @Override
    public void handle(Order order) {
        order.setState(new ReadyState());
    }

    @Override
    public String getStatusMessage() {
        return "Your pizza is in the oven";
    }
}