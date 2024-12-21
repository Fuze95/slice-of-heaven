package com.sliceofheaven.states;

import com.sliceofheaven.models.Order;

public class CompletedState implements OrderState {
    @Override
    public void handle(Order order) {
        // Final state
    }

    @Override
    public String getStatusMessage() {
        return "Enjoy your meal!";
    }
}