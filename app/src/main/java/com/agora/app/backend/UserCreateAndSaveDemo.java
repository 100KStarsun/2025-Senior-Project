package com.agora.app.backend;

import java.io.Console;
import java.util.EnumMap;
import java.util.Scanner;

public class UserCreateAndSaveDemo {

    static String caseEmailDomain = "@case.edu";

    public static void main (String[] args) {
        Console console = System.console();
        String caseID = console.readLine("Enter Case ID: ");
        String legalFirst = console.readLine("Enter legal first name: ");
        String preferredFirst = console.readLine("Enter preferred first name (leave blank if none): ");
        preferredFirst = preferredFirst.isEmpty() ? legalFirst : preferredFirst;
        String last = console.readLine("Enter last name: ");
        EnumMap<PaymentMethods, Boolean> paymentMethodsSetup = new EnumMap<>(PaymentMethods.class);
        for (PaymentMethods method : PaymentMethods.values()) {
            paymentMethodsSetup.put(method, console.readLine("Do you have [%s] setup? [y/n]: ", method.getPaymentMethodDescription()).equalsIgnoreCase("y"));
        }
        User demoUser = new User(caseID, preferredFirst, legalFirst, last, caseID + caseEmailDomain, paymentMethodsSetup);

        System.out.println("Basic account details created.");

        String password;
        do {
            String password1 = String.valueOf(console.readPassword("Enter password: "));
            String password2 = String.valueOf(console.readPassword("Confirm password: "));
            password = password1.equals(password2) ? password1 : "";
            if (password.equals("")) {
                System.out.println("Passwords did not match. Try again.");
            }
        } while (password.equals(""));
        
    }
}
