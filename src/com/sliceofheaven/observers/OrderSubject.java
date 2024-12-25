package com.sliceofheaven.observers;

//Interface defining the Subject role in the Observer design pattern
public interface OrderSubject {
    void attach(OrderObserver observer);
    void detach(OrderObserver observer);
    void notifyObservers(String message);
}