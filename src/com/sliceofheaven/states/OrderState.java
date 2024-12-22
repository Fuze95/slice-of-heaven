package com.sliceofheaven.states;
import com.sliceofheaven.models.Order;

// State Pattern - Order States
public interface OrderState {
    void handle(Order order);
    String getStatusMessage();
    OrderState previewNextState();
}