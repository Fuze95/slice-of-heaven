package com.sliceofheaven.payment;

//Strategy Pattern - Payment
public interface PaymentStrategy {
    boolean pay(double amount);
}