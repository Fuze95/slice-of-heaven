package com.sliceofheaven.models;

interface OrderSubject {
    void attach(OrderObserver observer);
    void detach(OrderObserver observer);
    void notifyObservers(String message);
}