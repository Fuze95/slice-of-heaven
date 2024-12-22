package com.sliceofheaven;

import com.sliceofheaven.models.*;
import com.sliceofheaven.payment.*;
import com.sliceofheaven.states.*;
import java.util.*;
import java.util.stream.Collectors;

public class SliceOfHeaven {
    private static Scanner scanner = new Scanner(System.in);
    private static Admin admin = Admin.getInstance();
    private static Map<String, Order> activeOrders = new HashMap<>();

    private static boolean adminLogin() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("\n=== Admin Login ===");
    System.out.print("Username: ");
    String username = scanner.nextLine();
    System.out.print("Password: ");
    String password = scanner.nextLine();

    return admin.login(username, password);
}

    public static void main(String[] args) {
    System.out.println("\n=== Slice of Heaven Pizza ===");
    while (!adminLogin()) {
        System.out.println("Login failed! Please try again.");
    }
    System.out.println("Login successful!");

    while (true) {
        System.out.println("\n=== Slice of Heaven Pizza ===");
        System.out.println("1. Create New Customer");
        System.out.println("2. Place Order");
        System.out.println("3. Check Order Status");
        System.out.println("4. Cancel Order");
        System.out.println("5. Display All Customers");
        System.out.println("6. Display All Orders");
        System.out.println("7. Exit"); 
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        
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
                displayCustomers();
                break;
            case 6:
                displayOrders();         
                break;
            case 7:                     
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

            // Check for favorite pizzas
            List<Pizza> savedPizzas = customer.getSavedPizzas();
            if (!savedPizzas.isEmpty()) {
                System.out.print("Do you want to order from your favorite pizzas? [Y/N]: ");
                if (scanner.nextLine().toLowerCase().startsWith("y")) {
                    System.out.println("\n=== Saved Favorite Pizzas ===");
                    for (int i = 0; i < savedPizzas.size(); i++) {
                        Pizza pizza = savedPizzas.get(i);
                        System.out.println((i + 1) + ". " + pizza.getSpecialName() + 
                                         " - Size: " + pizza.getSize() + 
                                         ", Crust: " + pizza.getCrust() +
                                         ", Sauce: " + pizza.getSauce());
                    }

                    Order order = new Order(customer, false);
                    
                    while (true) {
                        System.out.print("\nSelect pizza number (0 to finish): ");
                        int choice = scanner.nextInt();
                        scanner.nextLine();
                        
                        if (choice == 0) break;
                        
                        if (choice > 0 && choice <= savedPizzas.size()) {
                            order.addPizza(savedPizzas.get(choice - 1));
                        } else {
                            System.out.println("Invalid choice!");
                        }
                        
                        System.out.print("Add another favorite pizza? [Y/N]: ");
                        if (!scanner.nextLine().toLowerCase().startsWith("y")) {
                            break;
                        }
                    }

                    // Ask for delivery after pizza selection
                    System.out.println("Available towns for delivery: " + admin.getTowns());
                    System.out.print("Is this for delivery? [Y/N]: ");
                    boolean isDelivery = scanner.nextLine().toLowerCase().startsWith("y");
                    order.setDelivery(isDelivery);

                    // Skip to payment processing
                    processPayment(order, customer);
                    return;
                }
            }

            // Normal ordering process
            System.out.println("Available towns for delivery: " + admin.getTowns());
            System.out.print("Is this for delivery? [Y/N]: ");
            boolean isDelivery = scanner.nextLine().toLowerCase().startsWith("y");
            Order order = new Order(customer, isDelivery);
            
            while (true) {
                Pizza pizza = createPizza(customer);
                if (pizza != null) {
                    order.addPizza(pizza);
                }
                System.out.print("Add another pizza? [Y/N]: ");
                if (!scanner.nextLine().toLowerCase().startsWith("y")) {
                    break;
                }
            }

            processPayment(order, customer);
        }

    private static void processPayment(Order order, Customer customer) {
        System.out.println("\nTotal Amount: " + order.getTotalAmount() + " LKR");
        System.out.println("Select payment method:");
        System.out.println("1. Credit Card");
        System.out.println("2. PayPal");
        System.out.println("3. KOKO Pay");
        System.out.print("Enter your choice: ");
        
        int paymentChoice = scanner.nextInt();
        scanner.nextLine();
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

    private static Pizza createPizza(Customer customer) {
            System.out.println("\n=== Create Pizza ===");
            Pizza.PizzaBuilder builder = new Pizza.PizzaBuilder();
            // Size selection
            System.out.println("Select size:");
            System.out.println("1. Personal (1000 LKR)");
            System.out.println("2. Medium (1800 LKR)");
            System.out.println("3. Large (2400 LKR)");
            System.out.print("Enter your choice: ");
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
            System.out.print("Enter your choice: ");
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
            System.out.print("Enter your choice: ");
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
                System.out.print("Enter your choice: ");
                
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
            System.out.print("\nAdd extra cheese for 200 LKR? [Y/N]: ");
            boolean extraCheese = scanner.nextLine().toLowerCase().startsWith("y");
            builder.setExtraCheese(extraCheese);
            
            Pizza pizza = builder.build();
            
            System.out.print("Add this pizza as favorite [Y/N]: ");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("Y")) {
                System.out.print("Enter Special name for the pizza: ");
                String specialName = scanner.nextLine();
                pizza.setSpecialName(specialName);
                
                customer.savePizza(pizza);
                System.out.println("Pizza saved as favorite with name: " + specialName);
            }
            
            return pizza;
    }

    private static void checkOrderStatus() {
        System.out.print("\nEnter order ID: ");
        String orderId = scanner.nextLine();
        
        Order order = activeOrders.get(orderId);
        if (order != null) {
            System.out.println("Current status: " + order.getCurrentState().getStatusMessage());
            System.out.print("Move to next state? [Y/N]: ");
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

    private static void displayCustomers() {
        Map<String, Customer> customers = admin.getCustomers();
        
        if (customers.isEmpty()) {
            System.out.println("\nNo customers registered yet!");
            return;
        }
        System.out.println("\n=== Customer List ===");
        // Print table header
        System.out.println("+" + "-".repeat(20) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(10) + "+" + "-".repeat(30) + "+");
        System.out.printf("| %-18s | %-13s | %-23s | %-8s | %-28s |\n", 
            "Name", "Mobile", "Email", "Points", "Favorite Pizzas");
        System.out.println("+" + "-".repeat(20) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(10) + "+" + "-".repeat(30) + "+");
        
        // Print each customer's details
        for (Map.Entry<String, Customer> entry : customers.entrySet()) {
            Customer customer = entry.getValue();

            List<Pizza> savedPizzas = customer.getSavedPizzas();
            String favoritePizzas = savedPizzas.isEmpty() ? "None" : 
                savedPizzas.stream()
                          .map(Pizza::getSpecialName)
                          .limit(2)
                          .filter(name -> name != null && !name.isEmpty())
                          .collect(Collectors.joining(", ")) + 
                (savedPizzas.size() > 2 ? ", ..." : "");
            
            System.out.printf("| %-18s | %-13s | %-23s | %-8d | %-28s |\n",
                customer.getName(),
                entry.getKey(),
                customer.getEmail(),
                customer.getLoyaltyPoints(),
                favoritePizzas
            );
        }
        
        // Print table footer
        System.out.println("+" + "-".repeat(20) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(10) + "+" + "-".repeat(30) + "+");
    }

    private static void displayOrders() {
        if (activeOrders.isEmpty()) {
            System.out.println("\nNo active orders!");
            return;
        }
        
        System.out.println("\n=== Active Orders ===");
        // Increased column widths, especially for Status
        System.out.println("+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(15) + "+" + "-".repeat(35) + "+");
        System.out.printf("| %-13s | %-23s | %-13s | %-33s |\n",
            "Order ID", "Customer Name", "Mobile", "Status");
        System.out.println("+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(15) + "+" + "-".repeat(35) + "+");
        
        // Print each order's details
        for (Map.Entry<String, Order> entry : activeOrders.entrySet()) {
            Order order = entry.getValue();
            Customer customer = order.getCustomer();
            
            System.out.printf("| %-13s | %-23s | %-13s | %-33s |\n",
                order.getOrderId(),
                customer.getName(),
                customer.getMobileNumber(),
                order.getCurrentState().getStatusMessage()
            );
        }
        
        // Print table footer
        System.out.println("+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(15) + "+" + "-".repeat(35) + "+");
    }
}