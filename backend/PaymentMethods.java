/**
 * https://stackoverflow.com/questions/13291076/java-enum-why-use-tostring-instead-of-name
 * this is why there's more than just capital letters here lol
 * -- Levi
 */
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
