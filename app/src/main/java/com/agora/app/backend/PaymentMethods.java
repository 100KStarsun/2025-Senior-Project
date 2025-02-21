package com.agora.app.backend;

public enum PaymentMethods {
    PAYPAL("PayPal"),
    ZELLE("Zelle"),
    CASHAPP("Cash App"),
    VENMO("Venmo"),
    APPLEPAY("Apple Pay"),
    SAMSUNGPAY("Samsung Pay"),
    GOOGLEPAY("Google Pay");

    private final String paymentMethodDescription;

    private PaymentMethods(String value) {
        paymentMethodDescription = value;
    }

    public String getPaymentMethodDescription() {
        return paymentMethodDescription;
    }
}
