package com.sliceofheaven.states;

import com.sliceofheaven.models.Order;

public class CancelledState implements OrderState {
    @Override
    public void handle(Order order) {
        // Final state
    }

    @Override
    public String getStatusMessage() {
        return "Order has been cancelled";
    }
}