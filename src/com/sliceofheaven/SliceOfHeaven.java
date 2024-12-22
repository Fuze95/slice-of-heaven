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
    addDummyCustomers();// Added this for testing
    System.out.println("\n=== Slice of Heaven Pizza ===");
    while (!adminLogin()) {
        System.out.println("Login failed! Please try again.");
    }
    System.out.println("Login successful!");

    while (true) {
        System.out.println("\n=== Slice of Heaven Pizza ===\n1. Create Customer\n2. Place Order" +
                         "\n3. Check Order\n4. Cancel Order\n5. View Customers\n6. View Orders\n7. Exit"); 
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
        Customer customer = admin.getCustomer(scanner.nextLine());
        if (customer == null) {
            System.out.println("Customer not found!");
            return;
        }

        Order order = null;
        List<Pizza> savedPizzas = customer.getSavedPizzas();
        
        // Handle favorite pizzas
        if (!savedPizzas.isEmpty() && promptYesNo("Do you want to order from your favorite pizzas?")) {
            order = handleFavoritePizzas(customer, savedPizzas);
            if (order == null) return;
        }
        
        if (order == null) {
            order = handleNormalOrder(customer);
            if (order == null) return;
        }
        
        if (!confirmAndProcessOrder(order)) {
            System.out.println("Order cancelled!");
        }
    }

    private static boolean promptYesNo(String message) {
        System.out.print(message + " [Y/N]: ");
        return scanner.nextLine().toLowerCase().startsWith("y");
    }

    private static Order handleFavoritePizzas(Customer customer, List<Pizza> savedPizzas) {
        System.out.println("\n=== Saved Favorite Pizzas ===");
        for (int i = 0; i < savedPizzas.size(); i++) {
            Pizza pizza = savedPizzas.get(i);
            System.out.printf("%d. %s - Size: %s, Crust: %s, Sauce: %s%n", 
                i + 1, pizza.getSpecialName(), pizza.getSize(), pizza.getCrust(), pizza.getSauce());
        }

        Order order = new Order(customer, false);
        while (true) {
            System.out.print("\nSelect pizza number (0 to finish): ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice == 0) {
                return order.getPizzas().isEmpty() ? null : order;
            }
            
            if (choice > 0 && choice <= savedPizzas.size()) {
                order.addPizza(savedPizzas.get(choice - 1));
                if (!promptYesNo("Add another favorite pizza?")) break;
            } else {
                System.out.println("Invalid choice!");
            }
        }
        
        setupDelivery(order);
        return order;
    }

    private static Order handleNormalOrder(Customer customer) {
        Order order = new Order(customer, false);
        setupDelivery(order);
        
        do {
            Pizza pizza = createPizza(customer);
            if (pizza != null) {
                order.addPizza(pizza);
            }
        } while (promptYesNo("Add another pizza?"));
        
        return order;
    }

    private static void setupDelivery(Order order) {
        System.out.println("Available towns for delivery: " + admin.getTowns());
        boolean isDelivery = promptYesNo("Is this for delivery?");
        order.setDelivery(isDelivery);
        
        if (isDelivery) {
            System.out.print("Enter delivery town: ");
            String town = scanner.nextLine();
            order.setDeliveryTown(town);
            
            double deliveryFee = admin.getDeliveryFee(town);
            if (admin.getTowns().contains(town)) {
                System.out.printf("Delivery fee for %s: LKR %.2f%n", town, deliveryFee);
            } else {
                System.out.printf("Note: %s is outside our regular delivery area.%n", town);
                System.out.printf("Default delivery fee will be charged: LKR %.2f%n", deliveryFee);
                if (!promptYesNo("Do you want to proceed with this delivery?")) {
                    System.out.println("Please select a different town.");
                    setupDelivery(order);
                    return;
                }
            }
        }
    }

    private static boolean confirmAndProcessOrder(Order order) {
        displayOrderSummary(order);
        if (!promptYesNo("\nConfirm order?")) {
            return false;
        }
        processPayment(order, order.getCustomer());
        return true;
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
            order.notifyObservers(order.getCurrentState().getStatusMessage());
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
            OrderState currentState = order.getCurrentState();
            System.out.println("Current status: " + currentState.getStatusMessage());
            
            // Preview next state
            OrderState nextState = currentState.previewNextState();
            if (nextState != null) {
                System.out.println("Next state will be: " + nextState.getStatusMessage());
                
                System.out.print("Move to next state? [Y/N]: ");
                if (scanner.nextLine().toLowerCase().startsWith("y")) {
                    order.nextState();
                }
            } else {
                System.out.println("This is the final state.");
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

    private static void displayOrderSummary(Order order) {
        System.out.println("\n=== Order Summary ===");
        
        order.getPizzas().forEach(pizza -> {
            System.out.printf("\nPizza %d:%n--------------------%n", 
                order.getPizzas().indexOf(pizza) + 1);
            System.out.printf("Size: %s%nCrust: %s%nSauce: %s%n", 
                pizza.getSize(), pizza.getCrust(), pizza.getSauce());
            
            if (!pizza.getToppings().isEmpty()) {
                System.out.println("Toppings: " + String.join(", ", pizza.getToppings()));
            }
            if (pizza.hasExtraCheese()) {
                System.out.println("Extra Cheese: Yes");
            }
            System.out.printf("Price: %.2f LKR%n", pizza.getPrice());
        });

        double total = order.getPizzas().stream().mapToDouble(Pizza::getPrice).sum();
        System.out.println("\n-------------------");
        System.out.printf("Delivery: %s%n", order.isDelivery() ? "Yes" : "No");
        
        if (order.isDelivery()) {
            String town = order.getDeliveryTown();
            double deliveryFee = Admin.getInstance().getDeliveryFee(town);
            System.out.printf("Delivery Location: %s%nDelivery Charge: %.2f LKR%n", 
                town, deliveryFee);
            total += deliveryFee;
        }

        System.out.printf("%n-------------------%nTotal Amount: %.2f LKR%n", total);
    }

    private static void displayCustomers() {
        Map<String, Customer> customers = admin.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("\nNo customers registered yet!");
            return;
        }

        String border = "+" + "-".repeat(20) + "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(10) + "+" + "-".repeat(30) + "+";
        String format = "| %-18s | %-13s | %-23s | %-8s | %-28s |\n";
        
        System.out.println("\n=== Customer List ===");
        System.out.println(border);
        System.out.printf(format, "Name", "Mobile", "Email", "Points", "Favorite Pizzas");
        System.out.println(border);
        
        customers.forEach((mobile, customer) -> {
            String favoritePizzas = customer.getSavedPizzas().isEmpty() ? "None" : 
                customer.getSavedPizzas().stream()
                    .map(Pizza::getSpecialName)
                    .limit(2)
                    .filter(name -> name != null && !name.isEmpty())
                    .collect(Collectors.joining(", ")) + 
                    (customer.getSavedPizzas().size() > 2 ? ", ..." : "");
            
            System.out.printf(format, customer.getName(), mobile, customer.getEmail(), 
                customer.getLoyaltyPoints(), favoritePizzas);
        });
        
        System.out.println(border);
    }

    private static void displayOrders() {
        if (activeOrders.isEmpty()) {
            System.out.println("\nNo active orders!");
            return;
        }

        String border = "+" + "-".repeat(15) + "+" + "-".repeat(25) + "+" + "-".repeat(15) + "+" + "-".repeat(35) + "+";
        String format = "| %-13s | %-23s | %-13s | %-33s |\n";
        
        System.out.println("\n=== Active Orders ===");
        System.out.println(border);
        System.out.printf(format, "Order ID", "Customer Name", "Mobile", "Status");
        System.out.println(border);
        
        activeOrders.forEach((orderId, order) -> {
            Customer customer = order.getCustomer();
            System.out.printf(format, order.getOrderId(), customer.getName(), 
                customer.getMobileNumber(), order.getCurrentState().getStatusMessage());
        });
        
        System.out.println(border);
    }

    // TODO: REMOVE BEFORE PRODUCTION - Testing data only
    private static void addDummyCustomers() {
        // Dummy customer data
        String[][] dummyData = {
            {"John Doe", "john@email.com", "0779879948", "Bandarawela"},
            {"Jane Smith", "jane@email.com", "0719879948", "Badulla"},
            {"Mike Wilson", "mike@email.com", "0779879947", "Diyatalawa"},
            {"Sarah Brown", "sarah@email.com", "0779879944", "Welimada"},
            {"Tom Garcia", "tom@email.com", "0779879949", "Ella"}
        };

        for (String[] customer : dummyData) {
            admin.createCustomer(customer[0], customer[1], customer[2], customer[3]);
        }
        System.out.println("Dummy customers added successfully!");
    }
}