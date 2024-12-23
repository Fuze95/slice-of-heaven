package com.sliceofheaven.payment;

//Koko payment
public class KOKOPayment implements PaymentStrategy {
    private String phoneNumber;
    private String password;

    public KOKOPayment(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Paid " + amount + " LKR using KOKO Pay: " + phoneNumber);
        return true;
    }
}