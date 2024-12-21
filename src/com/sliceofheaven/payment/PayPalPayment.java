package com.sliceofheaven.payment;

public class PayPalPayment implements PaymentStrategy {
    private String email;
    private String password;

    public PayPalPayment(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Paid " + amount + " LKR using PayPal account: " + email);
        return true;
    }
}