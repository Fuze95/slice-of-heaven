# Slice of Heaven - Pizza Ordering System

A Java-based pizza ordering system that implements multiple design patterns including Singleton, Observer, State, and Builder patterns. This system allows customers to create custom pizzas or choose from saved favorites, with flexible payment options (Card, PayPal, or KOKO) and real-time order tracking. The application features a comprehensive order management system that handles the entire process from pizza customization (size, crust, toppings) to delivery status updates. Customers can earn loyalty points, save their favorite pizzas, and receive SMS notifications about their order status. The system supports both takeaway and delivery options across different towns.

## Project Structure

```
slice-of-heaven/
├── src/
│   └── com/
│       └── sliceofheaven/
│           ├── models/
│           │   ├── Customer.java
│           │   ├── Order.java
│           │   ├── Pizza.java
│           │   └── PizzaBuilder.java
│           ├── observers/
│           │   ├── OrderObserver.java
│           │   └── OrderSubject.java
│           ├── payment/
│           │   ├── CreditCardPayment.java
│           │   ├── KOKOPayment.java
│           │   ├── PayPalPayment.java
│           │   └── PaymentStrategy.java
│           ├── states/
│           │   ├── CancelledState.java
│           │   ├── CompletedState.java
│           │   ├── CookingState.java
│           │   ├── DeliveryState.java
│           │   ├── OrderState.java
│           │   ├── PlacedState.java
│           │   ├── PreparingState.java
│           │   └── ReadyState.java
│           ├── Admin.java
│           └── SliceOfHeaven.java
└── bin/
```

The project follows a structured package organization:
- `models/`: Contains core business entities like Customer, Order, and Pizza classes
- `observers/`: Implements the Observer pattern for order status notifications
- `payment/`: Contains different payment strategy implementations
- `states/`: Manages various order states using the State pattern
- `bin/`: Contains compiled `.class` files

To compile and run the application, use the following commands:

```bash
javac -d bin src\com\sliceofheaven\observers\*.java src\com\sliceofheaven\models\*.java src\com\sliceofheaven\payment\*.java src\com\sliceofheaven\states\*.java src\com\sliceofheaven\*.java

java -cp bin com.sliceofheaven.SliceOfHeaven
```

The system uses several design patterns:
- Singleton: For Admin class
- Builder: For Pizza construction
- State: For order status management
- Strategy: For payment methods
- Observer: For order notifications

This structured approach ensures clean code organization, maintainability, and separation of concerns throughout the application.
