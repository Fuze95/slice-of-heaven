package com.sliceofheaven;

import com.sliceofheaven.models.*;
import com.sliceofheaven.payment.*;
import com.sliceofheaven.states.*;
import java.util.*;

public class SliceOfHeaven {
    private static Scanner scanner = new Scanner(System.in);
    private static Admin admin = Admin.getInstance();
    private static Map<String, Order> activeOrders = new HashMap<>();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Slice of Heaven Pizza ===");
            System.out.println("1. Create New Customer");
            System.out.println("2. Place Order");
            System.out.println("3. Check Order Status");
            System.out.println("4. Cancel Order");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createCustomer();
                    break;
                case 2:
                    placeOrder();
                    break;
                case 3:
                    checkOrderStatus();
                    break;
                case 4:
                    cancelOrder();
                    break;
                case 5:
                    System.out.println("Thank you for using Slice of Heaven!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void createCustomer() {
        System.out.println("\n=== Create New Customer ===");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter mobile number: ");
        String mobile = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        admin.createCustomer(name, email, mobile, address);
        System.out.println("Customer created successfully!");
    }

    private static void placeOrder() {
        System.out.println("\n=== Place Order ===");
        System.out.print("Enter customer mobile number: ");
        String mobile = scanner.nextLine();

        Customer customer = admin.getCustomer(mobile);
        if (customer == null) {
            System.out.println("Customer not found!");
            return;
        }

        System.out.println("Available towns for delivery: " + admin.getTowns());
        System.out.print("Is this for delivery? (y/n): ");
        boolean isDelivery = scanner.nextLine().toLowerCase().startsWith("y");

        Order order = new Order(customer, isDelivery);
        
        while (true) {
            Pizza pizza = createPizza();
            if (pizza != null) {
                order.addPizza(pizza);
            }

            System.out.print("Add another pizza? (y/n): ");
            if (!scanner.nextLine().toLowerCase().startsWith("y")) {
                break;
            }
        }

        // Payment processing
        System.out.println("\nTotal Amount: " + order.getTotalAmount() + " LKR");
        System.out.println("Select payment method:");
        System.out.println("1. Credit Card");
        System.out.println("2. PayPal");
        System.out.println("3. KOKO Pay");
        
        int paymentChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        PaymentStrategy paymentStrategy;
        switch (paymentChoice) {
            case 1:
                System.out.print("Enter card number: ");
                String cardNumber = scanner.nextLine();
                System.out.print("Enter CVV: ");
                String cvv = scanner.nextLine();
                System.out.print("Enter expiry date: ");
                String expiry = scanner.nextLine();
                paymentStrategy = new CreditCardPayment(cardNumber, cvv, expiry);
                break;
            case 2:
                System.out.print("Enter PayPal email: ");
                String email = scanner.nextLine();
                System.out.print("Enter PayPal password: ");
                String paypalPass = scanner.nextLine();
                paymentStrategy = new PayPalPayment(email, paypalPass);
                break;
            case 3:
                System.out.print("Enter KOKO phone number: ");
                String phone = scanner.nextLine();
                System.out.print("Enter KOKO password: ");
                String kokoPass = scanner.nextLine();
                paymentStrategy = new KOKOPayment(phone, kokoPass);
                break;
            default:
                System.out.println("Invalid payment method!");
                return;
        }

        order.setPaymentStrategy(paymentStrategy);
        if (order.processPayment()) {
            activeOrders.put(order.getOrderId(), order);
            order.nextState(); // Move to first state
            System.out.println("Order placed successfully! Order ID: " + order.getOrderId());
            System.out.println("Current Loyalty Points: " + customer.getLoyaltyPoints());
        } else {
            System.out.println("Payment failed!");
        }
    }

    private static Pizza createPizza() {
        System.out.println("\n=== Create Pizza ===");
        Pizza.PizzaBuilder builder = new Pizza.PizzaBuilder();

        // Size selection
        System.out.println("Select size:");
        System.out.println("1. Personal (1000 LKR)");
        System.out.println("2. Medium (1800 LKR)");
        System.out.println("3. Large (2400 LKR)");
        int sizeChoice = scanner.nextInt();
        scanner.nextLine();
        
        switch (sizeChoice) {
            case 1: builder.setSize("personal"); break;
            case 2: builder.setSize("medium"); break;
            case 3: builder.setSize("large"); break;
            default: 
                System.out.println("Invalid size!");
                return null;
        }

        // Crust selection
        System.out.println("\nSelect crust type:");
        System.out.println("1. Pan");
        System.out.println("2. Thin");
        int crustChoice = scanner.nextInt();
        scanner.nextLine();
        
        switch (crustChoice) {
            case 1: builder.setCrust("pan"); break;
            case 2: builder.setCrust("thin"); break;
            default:
                System.out.println("Invalid crust type!");
                return null;
        }

        // Sauce selection
        System.out.println("\nSelect sauce:");
        System.out.println("1. Mayo");
        System.out.println("2. Tomato");
        System.out.println("3. Kotchchi");
        int sauceChoice = scanner.nextInt();
        scanner.nextLine();
        
        switch (sauceChoice) {
            case 1: builder.setSauce("mayo"); break;
            case 2: builder.setSauce("tomato"); break;
            case 3: builder.setSauce("kotchchi"); break;
            default:
                System.out.println("Invalid sauce!");
                return null;
        }

        // Toppings selection (150 LKR each)
        while (true) {
            System.out.println("\nAdd toppings (150 LKR each):");
            System.out.println("1. Pepperoni");
            System.out.println("2. Mushroom");
            System.out.println("3. Onion");
            System.out.println("4. Done adding toppings");
            
            int toppingChoice = scanner.nextInt();
            scanner.nextLine();
            
            if (toppingChoice == 4) break;
            
            switch (toppingChoice) {
                case 1: builder.addTopping("pepperoni"); break;
                case 2: builder.addTopping("mushroom"); break;
                case 3: builder.addTopping("onion"); break;
                default: System.out.println("Invalid topping!");
            }
        }

        // Extra cheese option (200 LKR)
        System.out.print("\nAdd extra cheese for 200 LKR? (y/n): ");
        boolean extraCheese = scanner.nextLine().toLowerCase().startsWith("y");
        builder.setExtraCheese(extraCheese);

        return builder.build();
    }

    private static void checkOrderStatus() {
        System.out.print("\nEnter order ID: ");
        String orderId = scanner.nextLine();
        
        Order order = activeOrders.get(orderId);
        if (order != null) {
            System.out.println("Current status: " + order.getCurrentState().getStatusMessage());
            System.out.print("Move to next state? (y/n): ");
            if (scanner.nextLine().toLowerCase().startsWith("y")) {
                order.nextState();
            }
        } else {
            System.out.println("Order not found!");
        }
    }

    private static void cancelOrder() {
        System.out.print("\nEnter order ID to cancel: ");
        String orderId = scanner.nextLine();
        
        Order order = activeOrders.get(orderId);
        if (order != null) {
            order.cancelOrder();
            System.out.println("Order cancelled successfully!");
        } else {
            System.out.println("Order not found!");
        }
    }
}