package com.agora.app.backend;

import java.util.Scanner;

public class UserCreateAndSaveDemo {

    public static void main (String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Case ID");
        String caseID = input.nextLine();
        System.out.println("Enter Legal First Name");
        String legalFirst = input.nextLine();
        System.out.println("Enter Preferred First Name (Leave blank to use legal first name as preferred name)");
        String preferredFirst = input.nextLine();
        preferredFirst = preferredFirst.equals("") ? legalFirst : preferredFirst;
        
    }
}
